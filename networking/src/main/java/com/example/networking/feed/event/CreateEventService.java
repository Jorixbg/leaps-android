package com.example.networking.feed.event;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;


import java.util.List;

import okhttp3.MultipartBody;

import rx.Observable;

/**
 * Created by xComputers on 16/07/2017.
 */
@RetrofitInterface(retrofitApi = CreateEventApi.class)
public class CreateEventService extends BaseService<CreateEventApi> {

    public Observable<CreateEventResponse> createEvent(String title, String description, long date, long timeFrom, long timeTo, long ownerId,
                                                       double lattitude, double longtitude, double priceFrom, String address, int freeSlots, long dateCreated, List<String> tags){
        return serviceApi.createEvent(new CreateEventRequest(title, description,
                date, timeFrom, timeTo, ownerId, lattitude, longtitude,
                priceFrom , address, freeSlots, dateCreated, tags));
    }

    public Observable<Void> uploadImage(long eventId, MultipartBody.Part photo){
        return serviceApi.uploadImage(photo, eventId);
    }

    public Observable<Void> uploadMainImage(long eventId, MultipartBody.Part photo){
        return serviceApi.uploadMainImage(photo, eventId);
    }
}
