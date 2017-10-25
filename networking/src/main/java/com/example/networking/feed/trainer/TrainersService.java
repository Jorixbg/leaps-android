package com.example.networking.feed.trainer;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;
import com.example.networking.feed.event.FeedFilterRequest;
import com.example.networking.feed.event.FeedFilterTrainerResponse;
import com.example.networking.login.UserResponse;
import com.example.networking.following.user.FollowedResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by xComputers on 15/06/2017.
 */
@RetrofitInterface(retrofitApi = TrainersApi.class)
public class TrainersService extends BaseService<TrainersApi> {

    public Observable<FeedFilterTrainerResponse> getTrainers(FeedFilterRequest request){
        return serviceApi.getTrainers(request);
    }

    public Observable<List<FollowedResponse>> getTrainersNoFilter(){
        return serviceApi.getTrainersNoFilter();
    }

    public Observable<UserResponse> getUsersFollowing(long userId, String auth){
        addHeader("Authorization", auth);
        return serviceApi.getFollowing(userId);
    }



}
