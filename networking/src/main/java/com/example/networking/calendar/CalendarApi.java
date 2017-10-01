package com.example.networking.calendar;


import com.example.networking.feed.event.RealEvent;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by xComputers on 18/06/2017.
 */

public interface CalendarApi {

    @POST("/user/events/{type}/future")
    Observable<List<RealEvent>> getEventsFuture(@Path("type") String type, @Body EventCalendarRequest request);

    @POST("/user/events/{type}/past")
    Observable<List<RealEvent>> getEventsPast(@Path("type") String type, @Body EventCalendarRequest request);

    @POST("/user/events/{type}/{timeFrame}")
    Observable<List<RealEvent>> getEvents(@Path("type") String type, @Path("timeFrame") String timeFrame, @Body EventCalendarRequest request);

}
