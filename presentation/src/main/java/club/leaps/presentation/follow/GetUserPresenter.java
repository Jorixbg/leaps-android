package club.leaps.presentation.follow;

import club.leaps.networking.login.LoginService;
import club.leaps.networking.login.UserResponse;
import club.leaps.presentation.base.BasePresenter;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Ivan on 10/9/2017.
 */

public class GetUserPresenter extends BasePresenter {
    private Subject<UserResponse, UserResponse> getUserSubject;
    private Subject<Throwable, Throwable> getUserErrorSubject;
    private LoginService service;

    public GetUserPresenter() {

        getUserSubject = PublishSubject.create();
        getUserErrorSubject = PublishSubject.create();
        service = new LoginService();
    }

    public void getUser(long userId, String auth){
        service.getUser(userId, auth).subscribe(userResponse -> {
            getUserSubject.onNext(userResponse);
            service.removeHeader("Authorization");
        },(throwable) -> {
            service.removeHeader("Authorization");
            errorHandler().call(throwable);
            getUserErrorSubject.onNext(null);
        });
    }
    public Observable<UserResponse> getUserSubject(){
        return getUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorUserSubject(){
        return getUserErrorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }


}
