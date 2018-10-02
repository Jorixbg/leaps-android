package club.leaps.presentation.test;

import club.leaps.networking.feed.event.FeedEventsService;
import club.leaps.networking.feed.event.FeedFilterEventResponse;
import club.leaps.networking.feed.event.FeedFilterRequest;
import club.leaps.presentation.base.BasePresenter;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Ivan on 10/16/2017.
 */

public class EventFilterPresenter extends BasePresenter {

    private FeedEventsService service;
    private Subject<FeedFilterEventResponse, FeedFilterEventResponse> eventFilterSubject;
    private Subject<Throwable, Throwable> errorEventFilterSubject;

    public EventFilterPresenter(){
        service = new FeedEventsService();
        eventFilterSubject = PublishSubject.create();
        errorEventFilterSubject = PublishSubject.create();
    }

    public void getEventFilter(FeedFilterRequest feedFilterRequest){
        service.getEventsFilter(feedFilterRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feedFilterEventResponse->{
                    service.removeHeader("Authorization");
                    eventFilterSubject.onNext(feedFilterEventResponse);
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorEventFilterSubject.onNext(null);
                });
    }




    public Observable<FeedFilterEventResponse> getEventFilterObservable(){
        return eventFilterSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorEventFilterObservable(){
        return errorEventFilterSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }








}
