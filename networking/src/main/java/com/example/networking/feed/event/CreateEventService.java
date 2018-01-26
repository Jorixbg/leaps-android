package com.example.networking.feed.event;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;
import com.example.networking.test.ChoosenDate;
import com.example.networking.test.CreateRecurringEventRequest;
import com.example.networking.test.CreateRecurringEventResponse;
import com.example.networking.test.UpdateEventRequest;

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

    //todo method to be fixed as needed

    public Observable<CreateRecurringEventResponse> createRecurringEvent(String title, boolean repeat, String description, String frequency, List<ChoosenDate> dates, long start, long end,
                                                                         double lattitude, double longtitude, double priceFrom, String address, int freeSlots, List<String> tags){
        return serviceApi.createRecurringEvent(new CreateRecurringEventRequest(dates,repeat,start,end,frequency,title,description,address,lattitude,longtitude,priceFrom,freeSlots,tags));
    }


        public Observable<Void> uploadImage(long eventId, MultipartBody.Part photo){
        return serviceApi.uploadImage(photo, eventId);
    }

    public Observable<Void> uploadMainImage(long eventId, MultipartBody.Part photo){
        return serviceApi.uploadMainImage(photo, eventId);
    }


    public Observable<Integer> editEvent(long eventId,String title, String description, long date, long timeFrom, long timeTo, long ownerId,
                                         double lattitude, double longtitude, double priceFrom, String address, int freeSlots, long dateCreated, List<String> tags){
        return serviceApi.editEvent(new UpdateEventRequest(eventId,title, description,
                date, timeFrom, timeTo, lattitude, longtitude,
                priceFrom , address, freeSlots, tags));
    }




}
