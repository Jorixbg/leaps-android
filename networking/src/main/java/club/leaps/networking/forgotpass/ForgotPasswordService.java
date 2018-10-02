package club.leaps.networking.forgotpass;

import club.leaps.networking.base.ApiResponse;
import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;

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
