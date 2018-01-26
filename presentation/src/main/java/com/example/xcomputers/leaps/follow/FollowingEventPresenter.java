package com.example.xcomputers.leaps.follow;

import android.util.Log;

import com.example.networking.feed.event.FeedEventsService;
import com.example.networking.feed.event.RealEvent;
import com.example.networking.following.FollowingService;
import com.example.xcomputers.leaps.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Ivan on 10/11/2017.
 */

public class FollowingEventPresenter extends BasePresenter {


    private FeedEventsService service;
    private FollowingService followingService;
    private Subject<List<RealEvent>, List<RealEvent>> getFollowFutureEventSubject;
    private Subject<List<RealEvent>, List<RealEvent>> getFollowPastEventSubject;
    private Subject<Throwable, Throwable> errorFollowPastEventSubject;
    private Subject<Throwable, Throwable> errorFollowFutureEventSubject;
    private Subject<RealEvent, RealEvent> followingFutureSubject;
    private Subject<Throwable, Throwable> errorFollowingFutureSubject;
    private Subject<RealEvent, RealEvent> unfollowingFutureSubject;
    private Subject<Throwable, Throwable> errorUnfollowingFutureSubject;

    private static List<RealEvent> realEventList;

    public FollowingEventPresenter(){
        service = new FeedEventsService();
        followingService = new FollowingService();
        getFollowFutureEventSubject = PublishSubject.create();
        errorFollowFutureEventSubject = PublishSubject.create();
        followingFutureSubject = PublishSubject.create();
        errorFollowingFutureSubject = PublishSubject.create();
        unfollowingFutureSubject = PublishSubject.create();
        errorUnfollowingFutureSubject = PublishSubject.create();
        getFollowPastEventSubject= PublishSubject.create();
        errorFollowPastEventSubject= PublishSubject.create();
        realEventList = new ArrayList<>();
    }

    public void getFollowFutureEvent(String auth){
        service.addHeader("Authorization", auth);
        service.getFollowFutureEvent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent->{
                    service.removeHeader("Authorization");
                    getFollowFutureEventSubject.onNext(realEvent);
                    realEventList=realEvent;
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorFollowFutureEventSubject.onNext(null);
                });


    }
    public void getFollowPastEvent(String auth){
        service.addHeader("Authorization", auth);
        service.getFollowFuturePastEvent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent->{
                    service.removeHeader("Authorization");
                    getFollowPastEventSubject.onNext(realEvent);
                    realEventList=realEvent;
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorFollowPastEventSubject.onNext(null);
                });
    }

    //Method for following events
    public void followingEvent(String auth, long eventId){
        followingService.addHeader("Authorization", auth);
        followingService.FollowingEvent(eventId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent->{
                    service.removeHeader("Authorization");
                    Log.e("Tags2","Follow");
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    unfollowingEvent(auth,eventId);
                    errorFollowingFutureSubject.onNext(null);

                    Log.e("Tags2","Error Follow");
                });
    }

    public void unfollowingEvent(String auth, long eventId){
        followingService.addHeader("Authorization", auth);
        followingService.UnfollowingEvent(eventId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent->{
                    service.removeHeader("Authorization");
                    Log.e("Tags2","UNFollow");
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorUnfollowingFutureSubject.onNext(null);
                    Log.e("Tags2","Error UNFollow");
                });
    }

    public List<RealEvent> getRealEventList() {
        return realEventList;
    }


    public Observable<List<RealEvent>> getFollowingFutureEventObservable(){
        return getFollowFutureEventSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorFollowingFutureEventObservable(){
        return errorFollowFutureEventSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<RealEvent>> getFollowingPastEventObservable(){
        return getFollowPastEventSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorFollowingPastEventObservable(){
        return errorFollowPastEventSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }







}
