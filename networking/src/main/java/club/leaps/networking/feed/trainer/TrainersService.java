package club.leaps.networking.feed.trainer;

import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;
import club.leaps.networking.feed.event.FeedFilterRequest;
import club.leaps.networking.feed.event.FeedFilterTrainerResponse;
import club.leaps.networking.login.UserResponse;
import club.leaps.networking.following.user.FollowedResponse;

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
