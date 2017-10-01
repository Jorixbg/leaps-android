package com.example.networking.feed.event;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by xComputers on 14/06/2017.
 */

public interface FeedEventsApi {

    @GET("/event/feed/{latitude}/{longitude}")
    Observable<FeedEventsResponse> getEvents(@Path("latitude") String latitude, @Path("longitude") String longitude);

    @POST("/event/filter")
    Observable<FeedFilterEventResponse> getEventsFilter(@Body FeedFilterRequest request);

    @GET("/event/{type}/{page}/{latitude}/{longitude}")
    Observable<List<RealEvent>> getEvents(@Path("page") int page, @Path("type") String type,@Path("latitude") String latitude, @Path("longitude") String longitude);

    @GET("/event/popular/{page}")
    Observable<List<RealEvent>> getPopular(@Path("page") int page);

    @GET("/event/suited/{page}")
    Observable<List<RealEvent>> getSuited(@Path("page") int page);



}
