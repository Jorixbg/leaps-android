package com.example.networking.calendar;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;
import com.example.networking.feed.event.RealEvent;


import java.util.Calendar;
import java.util.List;

import rx.Observable;

/**
 * Created by xComputers on 18/06/2017.
 */
@RetrofitInterface(retrofitApi = CalendarApi.class)
public class CalendarService extends BaseService<CalendarApi> {

    public Observable<List<RealEvent>> getAttendingPast(String auth, EventCalendarRequest request){
        addHeader("Authorization" , auth);
        return serviceApi.getEventsPast("attending", request);
    }

    public Observable<List<RealEvent>> getAttendingFuture(String auth, EventCalendarRequest request){
        addHeader("Authorization" , auth);
        return serviceApi.getEventsFuture("attending", request);
    }
    public Observable<List<RealEvent>> getHostingPast(String auth, EventCalendarRequest request){
        addHeader("Authorization" , auth);
        return serviceApi.getEventsPast("hosting", request);
    }
    public Observable<List<RealEvent>> getHostingFuture(String auth, EventCalendarRequest request){
        addHeader("Authorization" , auth);
        return serviceApi.getEventsFuture("hosting", request);
    }
}
