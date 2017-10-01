package com.example.networking.base;


/**
 * Created by xComputers on 09/05/2017.
 */

import retrofit2.Retrofit;

/**
 * A base implementation of a service which all service classes should extend
 * @param <Api> the Retrofit interface to make the server calls
 */

public abstract class BaseService<Api>{

    protected Api serviceApi;

    protected BaseService() {
        serviceApi = provideServiceApi();
    }

    @SuppressWarnings("unchecked")
    protected Api provideServiceApi() {

        if (getClass().getAnnotation(RetrofitInterface.class) == null) {
            throw new RuntimeException(String.format("You don't have an retrofitApi annotation on %s", this.getClass().getCanonicalName()));
        }
        return provideRetrofit().create((Class<Api>) getClass().getAnnotation(RetrofitInterface.class).retrofitApi());
    }

    private Retrofit provideRetrofit() {
        return RetrofitManager.getInstance().provideRetrofit();
    }

    public void removeHeader(String headerKey) {
        RetrofitManager.getInstance().removeRequestHeader(headerKey);
    }

    public void addHeader(String headerKey, String headerValue) {
        RetrofitManager.getInstance().addRequestHeader(headerKey, headerValue);
    }

}
