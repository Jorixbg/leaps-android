package club.leaps.networking.feed.event;


import club.leaps.networking.test.CreateRecurringEventRequest;
import club.leaps.networking.test.CreateRecurringEventResponse;
import club.leaps.networking.test.UpdateEventRequest;

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

    @PUT("/event/create/repeat")
    Observable<CreateRecurringEventResponse> createRecurringEvent(@Body CreateRecurringEventRequest request);

    @Multipart
    @PUT("/pic/event")
    Observable<Void> uploadImage(@Part MultipartBody.Part photo, @Part("event_id") long eventId);

    @Multipart
    @PUT("/pic/event/main")
    Observable<Void> uploadMainImage(@Part MultipartBody.Part photo, @Part("event_id") long eventId);


    @POST("/event/update")
    Observable<Integer> editEvent(@Body UpdateEventRequest request);

}
