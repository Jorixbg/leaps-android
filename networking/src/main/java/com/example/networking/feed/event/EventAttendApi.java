package com.example.networking.feed.event;


import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by xComputers on 15/07/2017.
 */

public interface EventAttendApi {


    @POST("/event/attend")
    Observable<RealEvent> attendEvent(@Body EventAttendRequest request);

    @POST("/event/unattend")
    Observable<Void> unattendEvent(@Body EventAttendRequest request);
}
