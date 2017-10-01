package com.example.networking.test;


import com.example.networking.feed.event.EventAttendRequest;
import com.example.networking.login.UserResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Ivan on 9/1/2017.
 */

public interface EventFollowingApi {

    @POST("user/follow")
    Observable<UserResponse> FollowingUser(@Body FollowingUserRequest request);


    @POST("/user/unfollow")
    Observable<UserResponse> UnFollowingUser(@Body FollowingUserRequest request);


    @POST("event/follow/dummy")
    Observable<Integer> FollowingEvent(@Body EventAttendRequest request);

}
