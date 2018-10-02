package club.leaps.presentation.test;

import club.leaps.presentation.base.BasePresenter;

/**
 * Created by Ivan on 9/2/2017.
 */

public class TestPresenter extends BasePresenter {

   /* private EventAttendService service;
    private FollowingService service1;
    private Subject<RealEvent, RealEvent> followingSubject;
    private Subject<Throwable, Throwable> errorFollowingSubject;
    private Subject<UserResponse, UserResponse> followingUserSubject;
    private Subject<Throwable, Throwable> errorFollowingUserSubject;

    public TestPresenter(){
        service = new EventAttendService();
        service1 = new FollowingService();
        followingSubject = PublishSubject.create();
        followingUserSubject = PublishSubject.create();
        errorFollowingSubject = PublishSubject.create();
        errorFollowingUserSubject = PublishSubject.create();
    }

    public void getFollowingEvent(String auth, long eventId){
        service.addHeader("Authorization", auth);
        service.getFollowingEvent(User.getInstance().getUserId(),eventId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent->{
                    service.removeHeader("Authorization");
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorFollowingSubject.onNext(null);
                });
    }

    public void getFollowingUser(String auth, long userId){
        service1.addHeader("Authorization", auth);
        service1.getFollowingUser(User.getInstance().getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    followingUserSubject.onNext(userResponse);
                    service1.removeHeader("Authorization");
                }, throwable -> {
                     service1.removeHeader("Authorization");
                     errorHandler().call(throwable);
                    errorFollowingUserSubject.onNext(null);
                });


    }


    public Observable<RealEvent> getFollowingObservable(){
        return followingSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorFollowingObservable(){
        return errorFollowingSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UserResponse> getFollowingUserSubject(){
        return followingUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorFollowingUserSubject(){
        return errorFollowingUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }*/

}
