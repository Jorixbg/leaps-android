package com.example.networking.registration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by xComputers on 04/06/2017.
 */

public interface RegistrationRetrofitApi {

    @POST("/register")
    Call<RegistrationResponse> register(@Body RegistrationRequest registrationRequest);
}
