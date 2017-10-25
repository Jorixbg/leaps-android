package com.example.networking.test;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;
import com.example.networking.feed.event.CreateEventResponse;
import com.example.networking.feed.trainer.Image;

import java.util.List;

import okhttp3.MultipartBody;
import rx.Observable;

/**
 * Created by Ivan on 10/2/2017.
 */
@RetrofitInterface(retrofitApi = EventRateApi.class)
public class EventRateService  extends BaseService<EventRateApi> {

    public Observable<RateId> rateEvent (long eventId, int rating, String comment, long date) {
        return serviceApi.RateEvent(new EventRateRequest(eventId,rating,comment,date));
    }

    public Observable<List<EventRatingResponse>> getComment (long event_id, int page, int limit) {
        return serviceApi.getComment(event_id,page,limit);
    }


    public Observable<Integer> deleteEvent (long event_id) {
        return serviceApi.deleteEvent(new CreateEventResponse(event_id));
    }

    public Observable<Image> uploadMainImage(long rateId, MultipartBody.Part photo){
        return serviceApi.uploadMainImage(photo, rateId);
    }





}