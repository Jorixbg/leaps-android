package club.leaps.presentation.splash;

import android.text.TextUtils;

import club.leaps.networking.login.LoginService;
import club.leaps.networking.tags.TagsService;
import club.leaps.presentation.TagsHolder;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BasePresenter;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.LoginResponseToUserTypeMapper;

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
