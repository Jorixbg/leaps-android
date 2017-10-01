package com.example.xcomputers.leaps.splash;

import android.text.TextUtils;

import com.example.networking.login.LoginService;
import com.example.networking.tags.TagsService;
import com.example.xcomputers.leaps.TagsHolder;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BasePresenter;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.LoginResponseToUserTypeMapper;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 09/07/2017.
 */

public class SplashPresenter extends BasePresenter {

    private Subject<Void, Void> tagsSubject;
    private TagsService service = new TagsService();
    private LoginService loginService = new LoginService();

    SplashPresenter(){
        tagsSubject = PublishSubject.create();
    }

    void getTags(String auth, long userId){
        service.getTags().subscribe(tagsResponse -> {
            TagsHolder.getInstance().setTags(tagsResponse);
            getUser(auth, userId);
        }, throwable -> {
            getUser(auth, userId);
        });
    }

    private void getUser(String auth, long userId){
        if(TextUtils.isEmpty(auth) || userId == 0){
            tagsSubject.onNext(null);
        }else{
            loginService.getUser(userId, auth).subscribe(userResponse -> {
                double lat = User.getInstance().getLattitude();
                double longitude = User.getInstance().getLongtitude();
                EntityHolder.getInstance().setEntity(userResponse);
                User.clear();
                LoginResponseToUserTypeMapper.map(userResponse);
                User.getInstance().setLattitude(lat);
                User.getInstance().setLongtitude(longitude);
                tagsSubject.onNext(null);
            },throwable -> {
                tagsSubject.onError(new Throwable());
            });
        }
    }

    Observable<Void> getTagsObservable(){
        return tagsSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
