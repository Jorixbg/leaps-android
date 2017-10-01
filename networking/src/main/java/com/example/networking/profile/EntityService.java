package com.example.networking.profile;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;
import com.example.networking.login.UserResponse;

import rx.Observable;

/**
 * Created by xComputers on 29/07/2017.
 */
@RetrofitInterface(retrofitApi = EntityApi.class)
public class EntityService extends BaseService<EntityApi> {

    public Observable<UserResponse> getEntity(long userId){
        return serviceApi.getEntity(userId);
    }
}
