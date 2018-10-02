package club.leaps.networking.following;


import club.leaps.networking.feed.event.RealEvent;
import club.leaps.networking.following.event.EventFollowingRequest;
import club.leaps.networking.following.user.FollowingUserRequest;
import club.leaps.networking.login.UserResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Ivan on 9/1/2017.
 */

public interface EventFollowingApi {

    @POST("user/follow")
    Observable<UserResponse> FollowingUser(@Body FollowingUserRequest request);

    @POST("/user/unfollow")
    Observable<UserResponse> UnFollowingUser(@Body FollowingUserRequest request);

    @POST("event/follow")
    Observable<RealEvent> FollowingEvent(@Body EventFollowingRequest request);

    @POST("event/unfollow")
    Observable<RealEvent> UnfollowingEvent(@Body EventFollowingRequest request);

    @GET("/user/{user_id}")
    Observable<UserResponse> getUser(@Path("user_id") long userId);

}
