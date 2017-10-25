package com.example.networking.test;

import com.example.networking.feed.event.CreateEventResponse;
import com.example.networking.feed.trainer.Image;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Ivan on 10/2/2017.
 */

public interface EventRateApi {

    @POST("/event/rate")
    Observable<RateId> RateEvent(@Body EventRateRequest request);

    @Multipart
    @POST("/pic/rate")
    Observable<Image> uploadMainImage(@Part MultipartBody.Part photo, @Part("rate_id") long rateId);

    @GET("/event/comments/{event_id}/{page}/{limit}")
    Observable<List<EventRatingResponse>> getComment(@Path("event_id") long event_id, @Path("page") int page, @Path("limit") int limit);

    @HTTP(method = "DELETE", path = "/event/delete", hasBody = true)
    Observable<Integer> deleteEvent(@Body CreateEventResponse response);





}
