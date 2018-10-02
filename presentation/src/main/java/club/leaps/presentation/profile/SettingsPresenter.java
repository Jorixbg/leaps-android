package club.leaps.presentation.profile;

import club.leaps.networking.feed.trainer.Entity;
import club.leaps.networking.login.UserResponse;
import club.leaps.networking.profile.UpdateProfileRequest;
import club.leaps.networking.profile.UpdateProfileService;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BasePresenter;
import club.leaps.presentation.utils.EntityHolder;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 23/07/2017.
 */

public class SettingsPresenter extends BasePresenter {

    private UpdateProfileService service;
    private Subject<Void, Void> updateUserSubject;
    private Subject<Void, Void> errorSubject;
    private UpdateProfileRequest request;

    public SettingsPresenter(){
        service = new UpdateProfileService();
        updateUserSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
    }

    void updateUser(String auth, int maxDistanceSetting){
        Entity entity = EntityHolder.getInstance().getEntity();
        service.addHeader("Authorization", auth);
        this.request = new UpdateProfileRequest(entity.userId(), entity.username(), entity.email(), entity.gender(), entity.address(),
                maxDistanceSetting, entity.firstName(), entity.lastName(), entity.birthDay(), entity.description(), entity.longDescription(),
                entity.yearsOfTraining(), entity.phoneNumber(), entity.sesionPrice(), entity.isTrainer(),entity.specialities());
        service.updateUser(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    UserResponse userResponse = (UserResponse) EntityHolder.getInstance().getEntity();
                    User.getInstance().setMaxDistance(request.getMaxDistanceSetting());
                    userResponse.setMaxDistance(request.getMaxDistanceSetting());
                    service.removeHeader("Authorization");
                    updateUserSubject.onNext(null);
                }, throwable -> {
                    service.removeHeader("Authorization");
                    errorHandler().call(throwable);
                    errorSubject.onNext(null);
                });
    }

    public Observable<Void> getUpdateUserObservable(){
        return updateUserSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> getErrorObservable(){
        return errorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
