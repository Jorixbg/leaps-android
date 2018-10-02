package club.leaps.networking.feed.trainer;

import club.leaps.networking.feed.event.FeedFilterRequest;
import club.leaps.networking.feed.event.FeedFilterTrainerResponse;
import club.leaps.networking.login.UserResponse;
import club.leaps.networking.following.user.FollowedResponse;

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
    Observable<UserResponse> getFollowing(@Path("user_id") long userId );

    @GET("/user/trainer/feed")
    Observable<List<FollowedResponse>> getTrainersNoFilter();
}
