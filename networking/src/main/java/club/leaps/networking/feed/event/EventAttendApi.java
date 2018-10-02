package club.leaps.networking.feed.event;


import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by xComputers on 15/07/2017.
 */

public interface EventAttendApi {


    @POST("/event/attend")
    Observable<RealEvent> attendEvent(@Body EventAttendRequest request);

    @POST("/event/unattend")
    Observable<Void> unattendEvent(@Body EventAttendRequest request);

    @GET("/event/{event_id}")
    Observable<List<RealEvent>> getEventId(@Path("event_id") long eventId);
}
