package com.example.xcomputers.leaps.profile.trainerProfile;

import com.example.networking.base.HttpError;
import com.example.networking.feed.trainer.Image;
import com.example.networking.login.UserResponse;
import com.example.networking.profile.UpdateProfileRequest;
import com.example.networking.profile.UpdateProfileService;
import com.example.networking.test.FollowingService;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BasePresenter;
import com.example.xcomputers.leaps.utils.EntityHolder;

import java.io.IOException;
import java.lang.annotation.Annotation;

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
    private Subject<Void, Void> updateUserSubject;
    private UpdateProfileRequest request;
    private Subject<Void, Void> deletePicSubject;
    private Subject<Image, Image> uploadPicSubject;
    private Subject<Image, Image> uploadMainPicSubject;
    private Subject<String, String> generalErrorSubject;

    public EditProfilePresenter() {
        service = new UpdateProfileService();
        updateUserSubject = PublishSubject.create();
        deletePicSubject = PublishSubject.create();
        uploadPicSubject = PublishSubject.create();
        uploadMainPicSubject = PublishSubject.create();
        generalErrorSubject = PublishSubject.create();

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
                           int price) {
        this.request = new UpdateProfileRequest(userId, userName, email, gender, location,
                maxDistanceSetting, firstName, lastName, birthDay, desc, longDesc, yearsOfTraining, phoneNumber, price, EntityHolder.getInstance().getEntity().isTrainer());
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
                    service.removeHeader("Authorization");
                    updateUserSubject.onNext(null);
                }, throwable -> {
                    errorHandler().call(throwable);
                    service.removeHeader("Authorization");
                    generalErrorSubject.onNext("");
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
                    generalErrorSubject.onNext("");
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
                    generalErrorSubject.onNext("");
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
                generalErrorSubject.onNext("");
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
        generalErrorSubject.onNext(message);
    }




    public Observable<Image> getUploadMainPicObservable() {
        return uploadMainPicSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> getDeletePicObservable() {
        return deletePicSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Image> getUploadImageObservable() {
        return uploadPicSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> getUpdateUserObservable() {
        return updateUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getGeneralErrorObservable(){
        return generalErrorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
