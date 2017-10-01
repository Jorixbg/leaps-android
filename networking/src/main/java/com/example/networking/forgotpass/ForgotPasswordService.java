package com.example.networking.forgotpass;

import com.example.networking.base.ApiResponse;
import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;

import rx.Observable;

/**
 * Created by xComputers on 04/06/2017.
 */
@RetrofitInterface(retrofitApi = ForgotPasswordApi.class)
public class ForgotPasswordService extends BaseService<ForgotPasswordApi> {

    public Observable<ApiResponse> sendForgotPassEmail(String email){
        return serviceApi.sendPasswordEmail(new ForgotPassRequest(email));
    }
}
