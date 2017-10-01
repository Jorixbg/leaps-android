package com.example.networking.test;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;
import com.example.networking.feed.event.EventAttendRequest;
import com.example.networking.login.UserResponse;

import rx.Observable;

/**
 * Created by Ivan on 9/1/2017.
 */
@RetrofitInterface(retrofitApi = EventFollowingApi.class)
public class FollowingService extends BaseService<EventFollowingApi> {

    public Observable <UserResponse> FollowingUser (long userId) {
        return serviceApi.FollowingUser(new FollowingUserRequest(userId));
    }
    public Observable <UserResponse> UnFollowingUser (long userId) {
        return serviceApi.UnFollowingUser(new FollowingUserRequest(userId));
    }


    public Observable <Integer> FollowingEvent (long userId, long eventId) {
        return serviceApi.FollowingEvent(new EventAttendRequest(userId, eventId));
    }
}
