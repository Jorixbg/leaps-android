package com.example.xcomputers.leaps.welcome;

import com.example.networking.base.HttpError;
import com.example.networking.login.LoginResponse;
import com.example.networking.login.LoginService;
import com.example.xcomputers.leaps.base.BasePresenter;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.LoginResponseToUserTypeMapper;

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
 * Created by xComputers on 25/05/2017.
 */

public class WelcomePresenter extends BasePresenter {

    private LoginService service = new LoginService();
    private Subject<String, String> authSubject;
    private Subject<Void, Void> userNotFoundSubject;
    private Subject<String, String> errorSubject;
    private String auth;

    WelcomePresenter(){
        authSubject = PublishSubject.create();
        userNotFoundSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
    }

    public void loginFacebook(String fbId){
        service.facebookLogin(fbId).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    auth = response.headers().get("Authorization");
                    login(response.body().getUserId());
                }else if(response.code() == 401){
                    userNotFoundSubject.onNext(null);
                }else{
                    onError(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                errorSubject.onNext("");
            }
        });
    }

    public void loginGoogle(String googleId){
        service.googleLogin(googleId).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    auth = response.headers().get("Authorization");
                    login(response.body().getUserId());
                }else if(response.code() == 401){
                    userNotFoundSubject.onNext(null);
                }else{
                    onError(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                errorHandler().call(t);
                errorSubject.onNext("");
            }
        });
    }

    private void login(long userId){
        service.getUser(userId, auth).subscribe(userResponse -> {
            EntityHolder.getInstance().setEntity(userResponse);
            LoginResponseToUserTypeMapper.map(userResponse);
            authSubject.onNext(auth);
        }, throwable -> authSubject.onNext(auth));
    }

    private void onError(Response response){
        Converter<ResponseBody, HttpError> converter = (Converter<ResponseBody, HttpError>) GsonConverterFactory.create()
                .responseBodyConverter(HttpError.class, new Annotation[0],null);
        HttpError error1 = null;
        String message = "Oops... Something went wrong";
        try{
            error1 = converter.convert(response.errorBody());
            message = error1.getMessage();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        errorSubject.onNext(message);
    }

    Observable<String> getAuthObservable(){
        return authSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<Void> getSubjectNotFoundObservable(){
        return userNotFoundSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<String> getErrorObservable(){
        return errorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
