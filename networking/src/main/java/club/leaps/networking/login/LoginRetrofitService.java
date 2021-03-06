package club.leaps.networking.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by xComputers on 03/06/2017.
 */

interface LoginRetrofitService {

    @POST("/login")
    Call<UserResponse> login(@Body LoginRequest request);

    @GET("/user/{user_id}")
    Observable<UserResponse> getUser(@Path("user_id") long userId);

    @POST("/login/fb")
    Call<LoginResponse> loginFacebook(@Body FacebookLoginRequest request);

    @POST("/login/google")
    Call<LoginResponse> googleLogin(@Body GoogleLoginRequest request);
}
