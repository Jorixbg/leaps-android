package club.leaps.networking.following;

import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;
import club.leaps.networking.feed.event.RealEvent;
import club.leaps.networking.following.event.EventFollowingRequest;
import club.leaps.networking.following.user.FollowingUserRequest;
import club.leaps.networking.login.UserResponse;

import java.io.Serializable;

import rx.Observable;

/**
 * Created by Ivan on 9/1/2017.
 */
@RetrofitInterface(retrofitApi = EventFollowingApi.class)
public class FollowingService extends BaseService<EventFollowingApi> implements Serializable {

    public Observable <UserResponse> FollowingUser (long userId) {
        return serviceApi.FollowingUser(new FollowingUserRequest(userId));
    }
    public Observable <UserResponse> UnFollowingUser (long userId) {
        return serviceApi.UnFollowingUser(new FollowingUserRequest(userId));
    }

    public Observable <RealEvent> FollowingEvent (long eventId) {
        return serviceApi.FollowingEvent(new EventFollowingRequest(eventId));
    }

    public Observable <RealEvent> UnfollowingEvent (long eventId) {
        return serviceApi.UnfollowingEvent(new EventFollowingRequest(eventId));
    }

    public Observable<UserResponse> getUser(long userId){
        return serviceApi.getUser(userId);
    }
}
