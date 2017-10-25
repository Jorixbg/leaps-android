package com.example.xcomputers.leaps.base;

import com.example.networking.base.HttpError;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.ConnectException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 22/05/2017.
 */

public abstract class BasePresenter {

    private final Subject<String, String> errorSubject;

    protected BasePresenter() {

        errorSubject = PublishSubject.create();
    }

    protected Action1<Throwable> errorHandler() {

        return (this::handleError);
    }

    private void handleError(Throwable error) {
        String message = "Oops... Something went wrong!";
        if (error instanceof UnknownHostException || error instanceof ConnectException) {
            message = "This action requires an internet connection!";
        } else if (error instanceof HttpException) {
            HttpException exception = (HttpException) error;
            Response response = exception.response();
            Converter<ResponseBody, HttpError> converter = (Converter<ResponseBody, HttpError>) GsonConverterFactory.create()
                    .responseBodyConverter(HttpError.class, new Annotation[0], null);
            try {
                message = converter.convert(response.errorBody()).getMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        errorSubject.onNext(message);
    }

    public Observable<String> errorObservable() {

        return errorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
