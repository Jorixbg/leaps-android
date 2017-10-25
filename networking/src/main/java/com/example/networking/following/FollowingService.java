package com.example.networking.following;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;
import com.example.networking.feed.event.RealEvent;
import com.example.networking.following.event.EventFollowingRequest;
import com.example.networking.following.user.FollowingUserRequest;
import com.example.networking.login.UserResponse;

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
