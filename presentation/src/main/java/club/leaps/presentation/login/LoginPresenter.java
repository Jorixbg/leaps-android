package club.leaps.presentation.login;

import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.LoginResponseToUserTypeMapper;
import club.leaps.networking.base.HttpError;
import club.leaps.networking.login.LoginRequest;
import club.leaps.networking.login.LoginService;
import club.leaps.networking.login.UserResponse;
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
 * Created by xComputers on 03/06/2017.
 */

class LoginPresenter extends BasePresenter {

    private LoginService service = new LoginService();
    private Subject<String, String> loginSubject;
    private Subject<String, String> errorValidationSubject;
    private Subject<Throwable,Throwable> generalErroSubject;
    private String auth;

    LoginPresenter() {
        loginSubject = PublishSubject.create();
        errorValidationSubject = PublishSubject.create();
        generalErroSubject = PublishSubject.create();
    }

    void login(String username, String password, String firebaseToken) {
        if (username == null || username.isEmpty()) {
            errorValidationSubject.onNext("Please provide a non empty username or email!");
            return;
        } else if (password == null || password.isEmpty()) {
            errorValidationSubject.onNext("The password can not be empty!");
            return;
        }

        service.login(new LoginRequest(username, password,firebaseToken)).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    auth = response.headers().get("Authorization");
                    service.getUser(response.body().userId(), auth)
                            .subscribe(userResponse -> {
                                onLoginSuccess(userResponse, auth);
                                service.removeHeader("Authorization");
                            }, throwable -> {
                                errorHandler().call(throwable);
                                service.removeHeader("Authorization");
                                generalErroSubject.onNext(throwable);
                            });

                } else {
                    onError(response);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

                errorHandler().call(t);
                generalErroSubject.onNext(t);
            }
        });
    }

    private void onLoginSuccess(UserResponse response, String auth) {
        EntityHolder.getInstance().setEntity(response);
        LoginResponseToUserTypeMapper.map(response);

        loginSubject.onNext(auth);
        //another subject to carry the userId to the view
    }

    private void onError(Response response) {
        Converter<ResponseBody, HttpError> converter = (Converter<ResponseBody, HttpError>) GsonConverterFactory.create()
                .responseBodyConverter(HttpError.class, new Annotation[0], null);
        HttpError error1 = null;
        String message = "Oops... Something went wrong";
        try {
            error1 = converter.convert(response.errorBody());
            message =error1.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorValidationSubject.onNext(message);
    }

    Observable<String> getLoginObservable() {

        return loginSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<String> getErrorValidationSubject() {

        return errorValidationSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<Throwable> getLoginErrorObservable(){
        return generalErroSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
