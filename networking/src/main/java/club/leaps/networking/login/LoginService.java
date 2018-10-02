package club.leaps.networking.login;

import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;

import retrofit2.Call;
import rx.Observable;

/**
 * Created by xComputers on 03/06/2017.
 */
@RetrofitInterface(retrofitApi = LoginRetrofitService.class)
public class LoginService extends BaseService<LoginRetrofitService>{

    public Call<UserResponse> login(LoginRequest request){
        return serviceApi.login(request);
    }

    public Observable<UserResponse> getUser(long userId, String auth){
        addHeader("Authorization", auth);
        return serviceApi.getUser(userId);
    }

    public Call<LoginResponse> facebookLogin(String fbId){
        return serviceApi.loginFacebook(new FacebookLoginRequest(fbId));
    }

    public Call<LoginResponse> googleLogin(String googleId){
        return serviceApi.googleLogin(new GoogleLoginRequest(googleId));
    }

}
