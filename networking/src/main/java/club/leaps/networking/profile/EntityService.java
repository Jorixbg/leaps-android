package club.leaps.networking.profile;

import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;
import club.leaps.networking.login.UserResponse;

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
