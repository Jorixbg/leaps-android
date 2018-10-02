package club.leaps.presentation.profile.trainerProfile;

import club.leaps.networking.base.HttpError;
import club.leaps.networking.feed.trainer.Image;
import club.leaps.networking.login.UserResponse;
import club.leaps.networking.profile.FirebaseToken;
import club.leaps.networking.profile.UpdateProfileRequest;
import club.leaps.networking.profile.UpdateProfileService;
import club.leaps.presentation.base.BasePresenter;
import club.leaps.presentation.utils.EntityHolder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 23/07/2017.
 */

public class EditProfilePresenter extends BasePresenter {

    private UpdateProfileService service;
    private Subject<Integer, Integer> updateUserSubject;
    private UpdateProfileRequest request;
    private Subject<Void, Void> deletePicSubject;
    private Subject<Image, Image> uploadPicSubject;
    private Subject<Integer, Integer> uploadMainPicSubject;
    private Subject<Integer,Integer> updateFirebaseTokenSubject;
    private Subject<Throwable, Throwable> generalErrorSubject;

    public EditProfilePresenter() {
        service = new UpdateProfileService();
        updateUserSubject = PublishSubject.create();
        deletePicSubject = PublishSubject.create();
        uploadPicSubject = PublishSubject.create();
        uploadMainPicSubject = PublishSubject.create();
        generalErrorSubject = PublishSubject.create();
        updateFirebaseTokenSubject = PublishSubject.create();
    }


    public void updateFirebaseToken(String auth,FirebaseToken firebaseToken){
        service.addHeader("Authorization", auth);
        service.updateFirebaseToken(firebaseToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(number -> {
                    service.removeHeader("Authorization");
                    updateFirebaseTokenSubject.onNext(null);
                }, throwable -> {
                    errorHandler().call(throwable);
                    service.removeHeader("Authorization");
                    generalErrorSubject.onNext(throwable);
                });
    }

    public void updateUser(String auth, long userId,
                           String email,
                           String userName,
                           String gender,
                           String location,
                           int maxDistanceSetting,
                           String firstName,
                           String lastName,
                           long birthDay,
                           String desc,
                           String longDesc,
                           int yearsOfTraining,
                           String phoneNumber,
                           int price,
                           List<String> specialties) {
        this.request = new UpdateProfileRequest(userId, userName, email, gender, location,
                maxDistanceSetting, firstName, lastName, birthDay, desc, longDesc, yearsOfTraining, phoneNumber, price, EntityHolder.getInstance().getEntity().isTrainer(),specialties);
        service.addHeader("Authorization", auth);
        service.updateUser(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    UserResponse userResponse = (UserResponse) EntityHolder.getInstance().getEntity();
                    userResponse.setUsername(request.getUserName());
                    userResponse.setFirstName(request.getFirstName());
                    userResponse.setLastName(request.getLastName());
                    userResponse.setGender(request.getGender());
                    userResponse.setBirthDay(request.getBirthDay());
                    userResponse.setAddress(request.getLocation());
                    userResponse.setLongDescription(request.getLongDesc());
                    userResponse.setYearsOfTraining(request.getYearsOfTraining());
                    userResponse.setSesionPrice(request.getPrice());
                    userResponse.setTags(request.getSpecialties());
                    EntityHolder.getInstance().setEntity(userResponse);
                    service.removeHeader("Authorization");
                    updateUserSubject.onNext(aVoid);
                }, throwable -> {
                    errorHandler().call(throwable);
                    service.removeHeader("Authorization");
                    generalErrorSubject.onNext(throwable);
                });
    }

    public void uploadMainImage(String auth, long userId, MultipartBody.Part pic) {
        service.addHeader("Authorization", auth);
        service.uploadMainImage(userId, pic)
                .subscribe(image -> {
                    uploadMainPicSubject.onNext(image);
                    service.removeHeader("Authorization");
                }, throwable -> {
                    errorHandler().call(throwable);
                    service.removeHeader("Authorization");
                    generalErrorSubject.onNext(throwable);
                });
    }

    public void uploadImage(String auth, long userId, MultipartBody.Part pic) {
        service.addHeader("Authorization", auth);
        service.uploadImage(userId, pic)
                .subscribe(image -> {
                    uploadPicSubject.onNext(image);
                    service.removeHeader("Authorization");
                }, throwable -> {
                    errorHandler().call(throwable);
                    service.removeHeader("Authorization");
                    generalErrorSubject.onNext(throwable);
                });
    }

    public void deleteImage(String auth, long imageId) {
        service.addHeader("Authorization", auth);
        service.deleteImage(imageId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    deletePicSubject.onNext(null);
                } else {
                    deletePicSubject.onError(new Throwable());
                }
                service.removeHeader("Authorization");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorHandler().call(t);
                generalErrorSubject.onNext(t);
                service.removeHeader("Authorization");
            }
        });
    }

    private void onError(Response response){
        Converter<ResponseBody, HttpError> converter = (Converter<ResponseBody, HttpError>) GsonConverterFactory.create()
                .responseBodyConverter(HttpError.class, new Annotation[0], null);
        HttpError error1 = null;
        String message = "Oops... Something went wrong";
        try{
            error1 = converter.convert(response.errorBody());
            message = error1.getMessage();
        }
        catch (IOException e){
            e.printStackTrace();
        }
       // generalErrorSubject.onNext(message);
    }



    public Observable<Integer> getUpdateFirebaseTokenObservable() {
        return updateFirebaseTokenSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
    public Observable<Integer> getUploadMainPicObservable() {
        return uploadMainPicSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> getDeletePicObservable() {
        return deletePicSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Image> getUploadImageObservable() {
        return uploadPicSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Integer> getUpdateUserObservable() {
        return updateUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getGeneralErrorObservable(){
        return generalErrorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
