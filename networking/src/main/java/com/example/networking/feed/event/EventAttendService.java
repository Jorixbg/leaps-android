package com.example.networking.feed.event;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;

import rx.Observable;

/**
 * Created by xComputers on 15/07/2017.
 */
@RetrofitInterface(retrofitApi = EventAttendApi.class)
public class EventAttendService extends BaseService<EventAttendApi> {


    public Observable<RealEvent> attendEvent(long userId, long eventId, String auth) {
        addHeader("Authorization", auth);
        return serviceApi.attendEvent(new EventAttendRequest(userId, eventId));
    }

    public Observable<Void> unattendEvent(long userId, long eventId, String auth) {
        addHeader("Authorization", auth);
        return serviceApi.unattendEvent(new EventAttendRequest(userId, eventId));
    }


}
