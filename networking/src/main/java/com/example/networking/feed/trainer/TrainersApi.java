package com.example.networking.feed.trainer;

import com.example.networking.feed.event.FeedFilterRequest;
import com.example.networking.feed.event.FeedFilterTrainerResponse;
import com.example.networking.login.UserResponse;
import com.example.networking.test.FollowedResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by xComputers on 15/06/2017.
 */

public interface TrainersApi {

    @POST("/user/trainer/filter")
    Observable<FeedFilterTrainerResponse> getTrainers(@Body FeedFilterRequest request);

    @GET("/user/{user_id}")
    Observable<List<UserResponse>> getFollowing(@Path("userId") long userId );

    @GET("/user/trainer/feed")
    Observable<List<FollowedResponse>> getTrainersNoFilter();
}
