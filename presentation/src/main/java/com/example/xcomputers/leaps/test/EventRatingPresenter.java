package com.example.xcomputers.leaps.test;

import com.example.networking.feed.trainer.Image;
import com.example.networking.test.EventRateService;
import com.example.networking.test.EventRatingResponse;
import com.example.networking.test.RateId;
import com.example.xcomputers.leaps.base.BasePresenter;

import java.util.List;

import okhttp3.MultipartBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Ivan on 10/2/2017.
 */

public class EventRatingPresenter extends BasePresenter {

    private EventRateService service;
    private Subject<RateId, RateId> eventRateSubject;
    private Subject<Throwable, Throwable> errorEventRateSubject;
    private Subject<List<EventRatingResponse>, List<EventRatingResponse>> eventCommentSubject;
    private Subject<Throwable, Throwable> errorEventCommentSubject;
    private Subject<Image, Image> uploadPicSubject;
    private Subject<Throwable, Throwable> generalErrorSubject;


    public EventRatingPresenter(){
        service = new EventRateService();
        eventRateSubject = PublishSubject.create();
        errorEventRateSubject = PublishSubject.create();
        eventCommentSubject = PublishSubject.create();
        errorEventCommentSubject = PublishSubject.create();
        uploadPicSubject = PublishSubject.create();
        generalErrorSubject = PublishSubject.create();
    }

    public void rateEvent(String auth, long eventId,int rating, String comment, long date){
        service.addHeader("Authorization", auth);
        service.rateEvent(eventId, rating,  comment, date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rateId->{
                    service.removeHeader("Authorization");
                    eventRateSubject.onNext(rateId);
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorEventRateSubject.onNext(null);
                });
    }

    public void getComment(String auth,long eventId, int page, int limit){
        service.addHeader("Authorization",auth);
        service.getComment(eventId,page,limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventRatingResponses -> {
                    eventCommentSubject.onNext(eventRatingResponses);
                    service.removeHeader("Authorization");
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorEventCommentSubject.onNext(null);
                });
    }


    public void uploadMainImage(String auth, long rateId, MultipartBody.Part pic) {
        service.addHeader("Authorization", auth);
        service.uploadMainImage(rateId, pic)
                .subscribe(image -> {
                    uploadPicSubject.onNext(image);
                    service.removeHeader("Authorization");
                }, throwable -> {
                    errorHandler().call(throwable);
                    service.removeHeader("Authorization");
                    generalErrorSubject.onNext(null);
                });
    }


    public Observable<List<EventRatingResponse>> getCommentObservable(){
        return eventCommentSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<Image> getImageObservable(){
        return uploadPicSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<Throwable> getErrorCommentObservable(){
        return errorEventCommentSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RateId> getRateIdObservable(){
        return eventRateSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
    public Observable<Throwable> getErrorRateIdObservable(){
        return errorEventRateSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
