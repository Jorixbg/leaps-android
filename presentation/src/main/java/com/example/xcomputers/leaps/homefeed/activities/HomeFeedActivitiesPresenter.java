package com.example.xcomputers.leaps.homefeed.activities;


import com.example.networking.feed.event.FeedEventsResponse;
import com.example.networking.feed.event.FeedEventsService;
import com.example.networking.feed.event.FeedFilterRequest;
import com.example.networking.feed.event.RealEvent;
import com.example.networking.test.FollowingService;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BasePresenter;
import com.example.xcomputers.leaps.utils.SectionedDataHolder;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 14/06/2017.
 */

public class HomeFeedActivitiesPresenter extends BasePresenter {

    //New code
    private FollowingService followService;
    private Subject<RealEvent, RealEvent> followingSubject;
    private Subject<Throwable, Throwable> errorFollowingSubject;
    private Subject<FeedEventsResponse, FeedEventsResponse> getEventMapSubject;
    private Subject<Throwable, Throwable> errorEventMapSubject;

    private FeedEventsService service = new FeedEventsService();
    private Subject<SectionedDataHolder, SectionedDataHolder> dataSubject;
    private Subject<SectionedDataHolder, SectionedDataHolder> filterSubject;
    private Subject<Void,Void> filterErrorSubject;
    private Subject<Void, Void> eventsErrorSubject;
    private List<String> sectionTitles;
    private SectionedDataHolder dataHolder;
    private boolean shouldLoadMore = true;
    private int page = 1;

    public HomeFeedActivitiesPresenter() {

        //New code
        followService = new FollowingService();
        followingSubject = PublishSubject.create();
        errorFollowingSubject = PublishSubject.create();
        getEventMapSubject = PublishSubject.create();
        errorEventMapSubject = PublishSubject.create();

        dataSubject = PublishSubject.create();
        filterSubject = PublishSubject.create();
        filterErrorSubject = PublishSubject.create();
        eventsErrorSubject = PublishSubject.create();
        dataHolder = new SectionedDataHolder();
    }

    //Method for following events
    public void followingEvent(String auth, long eventId){
        followService.addHeader("Authorization", auth);
        followService.FollowingEvent(User.getInstance().getUserId(),eventId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent->{
                    service.removeHeader("Authorization");
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorFollowingSubject.onNext(null);
                });
    }



    //This is need to reset the state of the presenter when a new search is applied
    public void setShouldLoadMore(boolean shouldLoadMore){
        this.shouldLoadMore = shouldLoadMore;
        page = 1;
    }

    public void getEvents(String headerTitle, FeedFilterRequest request) {
        //this check was absent before and the shouldLoadMore variable was kind of redunant...
        if(!shouldLoadMore){
            filterErrorSubject.onNext(null);
        }
        request.setLimit(20);
        request.setOffset(page);
        service.getEventsFilter(request).subscribe(feedFilterEventResponse -> {
            if (!feedFilterEventResponse.getEventList().isEmpty()) {
                dataHolder.addSection(feedFilterEventResponse.getTotalResults() + " " + headerTitle, feedFilterEventResponse.getEventList());
            }else{
                shouldLoadMore = false;
            }
            page++;
            filterSubject.onNext(dataHolder);
        }, throwable -> filterErrorSubject.onNext(null));
    }

    public void clearData(){
        this.dataHolder = new SectionedDataHolder();
    }

    public boolean shouldLoadMore(){
        return shouldLoadMore;
    }


    //method to be fixed
    public void getEventsMap(double lat,double lon){
        service.getEvents(String.valueOf(lat),String.valueOf(lon))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent ->{
                    getEventMapSubject.onNext(realEvent);
                    service.removeHeader("Authorization");
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorEventMapSubject.onNext(null);
                });
    }

    public void getEventsNoFilter(List<String> sectionTitles) {
        this.sectionTitles = sectionTitles;
        String latitude = User.getInstance().getLattitude() == 0 ? "na.na" : String.valueOf(User.getInstance().getLattitude());
        String longtitude = User.getInstance().getLongtitude() == 0 ? "na.na" : String.valueOf(User.getInstance().getLongtitude());
        service.getEvents(latitude, longtitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onFailure);
    }

    private void onSuccess(FeedEventsResponse response) {
        SectionedDataHolder dataHolder = new SectionedDataHolder();
        dataHolder.addSection(sectionTitles.get(0), response.getPopular());
        dataHolder.addSection(sectionTitles.get(1), response.getNearBy());
        dataHolder.addSection(sectionTitles.get(2), response.getSuited());
        dataSubject.onNext(dataHolder);
        service.removeHeader("Authorization");
    }

    private void onFailure(Throwable t) {
        errorHandler().call(t);
        eventsErrorSubject.onNext(null);
        service.removeHeader("Authorization");
    }

    //Observable for following an event
    public Observable<RealEvent> getFollowingObservable(){
        return followingSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    //Observable for error form following an event
    public Observable<Throwable> getErrorFollowingObservable(){
        return errorFollowingSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
    public Observable<FeedEventsResponse> getEventMapObservable(){
        return getEventMapSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    //Observable for error form following an event
    public Observable<Throwable> getErrorEventMapObservable(){
        return errorEventMapSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SectionedDataHolder> getEventsObservable() {

        return dataSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SectionedDataHolder> getFilterObservable() {
        return filterSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> getFilterErrorObservable(){
        return filterErrorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> getEventsErrorObservable(){
        return eventsErrorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
