package com.example.networking.base;

import android.util.Log;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Created by xComputers on 09/05/2017.
 */

public class RetrofitManager{

    private static final int TIMEOUT = 30;
    private static RetrofitManager retrofitManager;
    private Retrofit retrofit;
    private static Headers requestHeaders;

    private RetrofitManager(){}

    public static RetrofitManager getInstance() {
        if (retrofitManager == null) {
            retrofitManager = new RetrofitManager();
        }
        return retrofitManager;
    }

    Retrofit provideRetrofit() {
        return retrofit;
    }


    public void init(String url) {
        retrofit = provideRetrofit(url, provideOkHttpClient());
    }

    private void createRetrofit(String url){

        retrofit = provideRetrofit(url, provideOkHttpClient());
    }

    void setRequestHeaders(Headers headers) {
        requestHeaders = headers;
    }

    private Retrofit provideRetrofit(String endpoint, OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(endpoint)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    private OkHttpClient provideOkHttpClient() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    if (requestHeaders != null) {
                        requestBuilder.headers(requestHeaders);
                    }
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    void updateBaseUrl(String url){

        createRetrofit(url);
    }

    void addRequestHeader(String key, String value) {
        if (requestHeaders == null) {
            requestHeaders = Headers.of(key,value);
        }
        requestHeaders = requestHeaders.newBuilder()
                .removeAll(key)
                .add(key,value)
                .build();
    }

    void removeRequestHeader(String key) {
        if (requestHeaders == null) {
            Log.e(RetrofitManager.class.getCanonicalName(), "No request headers attached");
            return;
        }
        requestHeaders = requestHeaders.newBuilder()
                .removeAll(key)
                .build();
    }
}
