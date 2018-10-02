package club.leaps.presentation.event;

import club.leaps.networking.feed.event.Event;
import club.leaps.networking.feed.event.FeedEventsService;
import club.leaps.presentation.User;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 12/07/2017.
 */

public class EventsListingPresenter {

    private static final String POPULAR = "popular";
    private static final String NEARBY = "nearby";
    private static final String SUITED = "suited";

    public enum Type {POPULAR, SUITED, NEARBY, TRAINER}

    private List<Event> data;
    private int page;
    private boolean shouldLoadMore = true;
    private Subject<List<Event>, List<Event>> eventSubject;
    private Subject<Void, Void> eventsErrorSubject;
    private FeedEventsService service;

    EventsListingPresenter(){
        data = new ArrayList<>();
        eventSubject = PublishSubject.create();
        eventsErrorSubject = PublishSubject.create();
        service = new FeedEventsService();
        page = 1;
    }

    void getEvents(Type type){
        String latitude = User.getInstance().getLattitude() == 0 ? "na.na" : String.valueOf(User.getInstance().getLattitude());
        String longtitude = User.getInstance().getLongtitude() == 0 ? "na.na" : String.valueOf(User.getInstance().getLongtitude());
        switch (type){
            case POPULAR:
                service.getPopular(page).subscribe(realEvents -> {
                    if(realEvents.isEmpty()){
                        this.shouldLoadMore = false;
                    }
                    data.addAll(realEvents);
                    eventSubject.onNext(data);
                    page++;
                }, throwable -> eventsErrorSubject.onNext(null));
                break;
            case SUITED:
                service.getSuited(page).subscribe(realEvents -> {
                    if(realEvents.isEmpty()){
                        this.shouldLoadMore = false;
                    }
                    data.addAll(realEvents);
                    eventSubject.onNext(data);
                    page++;
                }, throwable -> eventsErrorSubject.onNext(null));
                break;
            case NEARBY:
                service.getEvents(NEARBY, page, latitude, longtitude).subscribe(realEvents -> {
                    if(realEvents.isEmpty()){
                        this.shouldLoadMore = false;
                    }
                    data.addAll(realEvents);
                    eventSubject.onNext(data);
                    page++;
                }, throwable -> eventsErrorSubject.onNext(null));
                break;
            case TRAINER:
                //TODO awaiting API call...
                break;
        }
    }

    boolean shouldLoadMore(){
        return shouldLoadMore;
    }

    Observable<List<Event>> getEventsObservable(){
        return eventSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<Void> getEventsErrorObservable(){
        return eventsErrorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

}
