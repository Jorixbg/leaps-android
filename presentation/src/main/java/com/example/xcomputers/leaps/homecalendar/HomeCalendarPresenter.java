package com.example.xcomputers.leaps.homecalendar;

import com.example.networking.calendar.CalendarService;
import com.example.networking.calendar.EventCalendarRequest;
import com.example.networking.feed.event.RealEvent;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BasePresenter;
import com.example.xcomputers.leaps.utils.SectionedDataHolder;

import java.util.Calendar;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import static com.example.xcomputers.leaps.homecalendar.HomeCalendarUserView.ATTENDING_ARGUMENT;
import static com.example.xcomputers.leaps.homecalendar.HomeCalendarUserView.HOSTING_ARGUMENT;
import static com.example.xcomputers.leaps.homecalendar.HomeCalendarUserView.PAST_ARGUMENT;

/**
 * Created by xComputers on 18/06/2017.
 */

public class HomeCalendarPresenter extends BasePresenter {

    private CalendarService service = new CalendarService();
    private Subject<SectionedDataHolder,SectionedDataHolder> attendingSubject;
    private Subject<SectionedDataHolder,SectionedDataHolder> hostingSubject;
    private Subject<Void, Void> generalErrorSubject;
    private int page = 1;
    private boolean shouldLoadMore = true;
    private Calendar calendar;
    private SectionedDataHolder holder;

    public HomeCalendarPresenter() {

        attendingSubject = PublishSubject.create();
        hostingSubject = PublishSubject.create();
        generalErrorSubject = PublishSubject.create();
        calendar = Calendar.getInstance();
        holder = new SectionedDataHolder();
    }

    public void getEvents(String type, String timeFrame, String auth) {
        if (type.equals(HOSTING_ARGUMENT)) {
            getHosting(auth, timeFrame);
        } else if (type.equals(ATTENDING_ARGUMENT)) {
            getAttending(auth, timeFrame);
        }
    }

    private void getHosting(String auth, String timeFrame) {
        if (timeFrame.equals(PAST_ARGUMENT)) {
            service.getHostingPast(auth, new EventCalendarRequest(User.getInstance().getUserId(), 20, page))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                       for (RealEvent event : response) {
                           if(response.size() < 20){
                               shouldLoadMore = false;
                           }
                            handleReduce(event, holder);
                        }
                        page++;
                        hostingSubject.onNext(holder);
                        service.removeHeader("Authorization");
                    }, this::onHostingFailure);
        } else {
            service.getHostingFuture(auth, new EventCalendarRequest(User.getInstance().getUserId(), 20, page))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        for (RealEvent event : response) {
                            if(response.size() < 20){
                                shouldLoadMore = false;
                            }
                            handleReduce(event, holder);
                        }
                        page++;
                        hostingSubject.onNext(holder);
                        service.removeHeader("Authorization");
                    }, this::onHostingFailure);
        }
    }

    private void getAttending(String auth, String timeFrame) {
        if (timeFrame.equals(PAST_ARGUMENT)) {
            service.getAttendingPast(auth, new EventCalendarRequest(User.getInstance().getUserId(), 20, page))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        for (RealEvent event : response) {
                            if(response.size() < 20){
                                shouldLoadMore = false;
                            }
                            handleReduce(event, holder);
                        }
                        page++;
                        attendingSubject.onNext(holder);
                        service.removeHeader("Authorization");
                    }, this::onAttendingFailure);
        } else {
            service.getAttendingFuture(auth, new EventCalendarRequest(User.getInstance().getUserId(), 20, page))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        for (RealEvent event : response) {
                            if(response.size() < 20){
                                shouldLoadMore = false;
                            }
                            handleReduce(event, holder);
                        }
                        page++;
                        attendingSubject.onNext(holder);
                        service.removeHeader("Authorization");
                    }, this::onAttendingFailure);
        }
    }

    public boolean shouldLoadMore() {
        return shouldLoadMore;
    }

    private void handleReduce(RealEvent event, SectionedDataHolder holder) {

        calendar.setTime(event.date());
        String title = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                + " "
                + calendar.get(Calendar.DAY_OF_MONTH);
        holder.addEvent(title, event);
    }

    private void onAttendingFailure(Throwable t) {
        errorHandler().call(t);
        generalErrorSubject.onNext(null);
    }

    private void onHostingFailure(Throwable t) {
        errorHandler().call(t);
        generalErrorSubject.onNext(null);
    }

    public Observable<SectionedDataHolder> getAttendingObservable() {

        return attendingSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SectionedDataHolder> getHostingObservable() {

        return hostingSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> getGeneralErrorObservable(){
        return generalErrorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}