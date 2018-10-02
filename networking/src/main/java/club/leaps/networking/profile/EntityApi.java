package club.leaps.networking.profile;

import club.leaps.networking.login.UserResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by xComputers on 29/07/2017.
 */

public interface EntityApi {

    @GET("user/{user_id}")
    Observable<UserResponse> getEntity(@Path("user_id") long userId);
}
