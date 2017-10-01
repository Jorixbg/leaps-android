package com.example.networking.forgotpass;

import com.example.networking.base.ApiResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by xComputers on 04/06/2017.
 */

interface ForgotPasswordApi {
    @POST("/user/resetPassword")
    Observable<ApiResponse> sendPasswordEmail(@Body ForgotPassRequest request);
}
