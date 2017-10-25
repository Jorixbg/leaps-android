package com.example.xcomputers.leaps.homefeed.trainers;

import com.example.networking.feed.event.FeedFilterRequest;
import com.example.networking.feed.event.FeedFilterTrainerResponse;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.feed.trainer.TrainersService;
import com.example.networking.login.UserResponse;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 15/06/2017.
 */

class FeedTrainersPresenter extends BasePresenter {

    private TrainersService service = new TrainersService();
    private Subject<List<Entity>, List<Entity>> trainersSubject;
    private Subject<FeedFilterTrainerResponse, FeedFilterTrainerResponse> trainersFilterSubject;
    private Subject<UserResponse, UserResponse> followingSubject;
    private Subject<Void, Void> generalErrorSubject;
    private List<Entity> trainers;
    private boolean shouldLoadMore = true;

    FeedTrainersPresenter(){

        trainersSubject = PublishSubject.create();
        trainersFilterSubject = PublishSubject.create();
        generalErrorSubject = PublishSubject.create();
        followingSubject = PublishSubject.create();
        trainers = new ArrayList<>();
    }

    void clearData(){
        this.trainers = new ArrayList<>();
    }

    boolean shouldLoadMore(){
        return shouldLoadMore;
    }

    void getTrainers(FeedFilterRequest request){
        request.setLimit(20);
        request.setOffset(trainers.size());
        service.getTrainers(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    if(userResponse.getTrainers().isEmpty()){
                        shouldLoadMore = false;
                    }
                    trainersFilterSubject.onNext(userResponse);
                    service.removeHeader("Authorization");
                }, throwable -> {
                    errorHandler().call(throwable);
                    generalErrorSubject.onNext(null);
                    service.removeHeader("Authorization");
                });
    }

    void getTrainersNoFilter(){
        service.getTrainersNoFilter()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    List<Entity> list = new ArrayList<>();
                    list.addAll(userResponse);
                    trainersSubject.onNext(list);
                    service.removeHeader("Authorization");
                }, throwable -> {
                    errorHandler().call(throwable);
                    generalErrorSubject.onNext(null);
                    service.removeHeader("Authorization");
                });
    }



    void getFollowing (long userId,String auth){
        service.addHeader("Authorization", auth);
        service.getUsersFollowing(User.getInstance().getUserId(),auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    followingSubject.onNext(userResponse);
                    service.removeHeader("Authorization");
                }, throwable -> {
                    errorHandler().call(throwable);
                    generalErrorSubject.onNext(null);
                    service.removeHeader("Authorization");
                });
    }


    Observable<List<Entity>> getTrainersObservable(){
        return trainersSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<FeedFilterTrainerResponse> getTrainersFilterObservable(){
        return trainersFilterSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<UserResponse> getUsersFollowing(){
        return followingSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<Void> getGeneralErrorObservable(){
        return generalErrorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

}
