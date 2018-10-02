package club.leaps.presentation.profile;

import club.leaps.networking.following.FollowingService;
import club.leaps.networking.login.LoginService;
import club.leaps.networking.login.UserResponse;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BasePresenter;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.LoginResponseToUserTypeMapper;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 31/07/2017.
 */

public class ProfileListPresenter extends BasePresenter {



    private FollowingService followingService;
    private Subject<UserResponse, UserResponse> followingUserSubject;
    private Subject<Throwable, Throwable> errorFollowingUserSubject;
    private Subject<UserResponse, UserResponse> unFollowingUserSubject;
    private Subject<Throwable, Throwable> errorUnFollowingUserSubject;
    private Subject<UserResponse, UserResponse> userEntitySubject;
    private Subject<Void, Void> userSubject;
    private Subject<Void, Void> errorSubject;

    private LoginService service;

    public ProfileListPresenter(){
        userSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
        service = new LoginService();
        followingService = new FollowingService();
        followingUserSubject = PublishSubject.create();
        errorFollowingUserSubject = PublishSubject.create();
        unFollowingUserSubject = PublishSubject.create();
        errorUnFollowingUserSubject = PublishSubject.create();
        userEntitySubject = PublishSubject.create();
    }

    public void FollowingUser(String auth, long userId){
        followingService.addHeader("Authorization", auth);
        followingService.FollowingUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    followingUserSubject.onNext(userResponse);
                    followingService.removeHeader("Authorization");
                }, throwable -> {
                    followingService.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorFollowingUserSubject.onNext(null);
                });


    }

    public void UnFollowUser(String auth, long userId){
        followingService.addHeader("Authorization", auth);
        followingService.UnFollowingUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    unFollowingUserSubject.onNext(userResponse);
                    followingService.removeHeader("Authorization");
                }, throwable -> {
                    followingService.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorUnFollowingUserSubject.onNext(null);
                });
    }

    public void getUser(long userId, String auth){
        service.getUser(userId, auth).subscribe(userResponse -> {
            EntityHolder.getInstance().setEntity(userResponse);
            //I had forgotten about this... when I receive the new user i overwrite
            // the old one and I forgot about the fact that this also deletes the old coordinates from the User object.
            // The fix is to retain the coordinates in variables and apply them to the new User instance
            double lat = User.getInstance().getLattitude();
            double longitude = User.getInstance().getLongtitude();
            User.clear();
            LoginResponseToUserTypeMapper.map(userResponse);
            User.getInstance().setLattitude(lat);
            User.getInstance().setLongtitude(longitude);
            userSubject.onNext(null);
            userEntitySubject.onNext(userResponse);
        },(throwable) -> {
            errorHandler().call(throwable);
            errorSubject.onNext(null);
        });
    }



    public Observable<UserResponse> getUnFollowingUserSubject(){
        return unFollowingUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorUnFollowingUserSubject(){
        return errorUnFollowingUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UserResponse> getFollowingUserSubject(){
        return followingUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Throwable> getErrorFollowingUserSubject(){
        return errorFollowingUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> getUserObservable(){
        return userSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UserResponse> getUserEntityObservable(){
        return userEntitySubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> getErrorObservable(){
        return errorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public FollowingService getFollowingService() {
        return followingService;
    }


}
