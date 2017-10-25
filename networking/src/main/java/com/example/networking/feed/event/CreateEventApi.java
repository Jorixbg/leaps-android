package com.example.networking.feed.event;



import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by xComputers on 16/07/2017.
 */

public interface CreateEventApi {


    @PUT("/event/create")
    Observable<CreateEventResponse> createEvent(@Body CreateEventRequest request);

    @Multipart
    @PUT("/pic/event")
    Observable<Void> uploadImage(@Part MultipartBody.Part photo, @Part("event_id") long eventId);

    @Multipart
    @PUT("/pic/event/main")
    Observable<Void> uploadMainImage(@Part MultipartBody.Part photo, @Part("event_id") long eventId);


    @POST("/event/update")
    Observable<Integer> editEvent(@Body CreateEventResponse response,@Body CreateEventRequest request);

}
