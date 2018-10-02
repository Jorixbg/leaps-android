package club.leaps.presentation.registration;



import android.util.Log;

import club.leaps.networking.base.HttpError;
import club.leaps.networking.login.LoginService;
import club.leaps.networking.registration.RegistrationResponse;
import club.leaps.networking.registration.RegistrationService;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BasePresenter;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.LoginResponseToUserTypeMapper;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 04/06/2017.
 */

class RegistrationPresenter extends BasePresenter {

    private RegistrationService service = new RegistrationService();
    private LoginService loginService = new LoginService();
    private Subject<String, String> registerSubject;
    private Subject<String, String> errorSubject;
    private String auth = "";

    RegistrationPresenter(){

        registerSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
    }

    void register(String email, String password, String firstName, String lastName, long birthDay, String fbId, String googleId,String firebaseToken){
        if(fbId != null) {
            service.register(email, password, firstName, lastName, birthDay, fbId,firebaseToken).enqueue(new Callback<RegistrationResponse>() {
                @Override
                public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                    onRegSuccess(response);
                }

                @Override
                public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                    registerSubject.onError(null);
                }
            });
        }else if(googleId != null){
            service.register(email, password, firstName, lastName,googleId, birthDay,firebaseToken).enqueue(new Callback<RegistrationResponse>() {
                @Override
                public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                    onRegSuccess(response);
                }

                @Override
                public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                    registerSubject.onError(null);
                }
            });
        }else if (firebaseToken!=null){
            service.register(email, password, firstName, lastName, birthDay,firebaseToken,true).enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                onRegSuccess(response);
            }
            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                errorHandler().call(t);
                errorSubject.onNext("");
            }
        });

        }

    }

    public void onRegSuccess(Response<RegistrationResponse> response){
        if (response.isSuccessful()) {
            User.getInstance().setUserId(response.body().getUserId());
            auth = response.headers().get("Authorization");
            loginService.getUser(response.body().getUserId(), auth).subscribe(userResponse -> {
                EntityHolder.getInstance().setEntity(userResponse);
                LoginResponseToUserTypeMapper.map(userResponse);
                registerSubject.onNext(auth);

            }, throwable -> errorSubject.onNext(throwable.getMessage()));
        } else {
            onError(response);
        }
    }

    private void onError(Response response) {
        Converter<ResponseBody, HttpError> converter = (Converter<ResponseBody, HttpError>) GsonConverterFactory.create()
                .responseBodyConverter(HttpError.class, new Annotation[0], null);
        HttpError error1 = null;
        String message = "Oops... Something went wrong";
        try {
            error1 = converter.convert(response.errorBody());
            message = error1.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorSubject.onNext(message);
    }

    Observable<String> getRegistrationObservable(){
        return registerSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<String> getErrorSubject(){
        return errorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
