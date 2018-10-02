package club.leaps.networking.tags;

import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;

import java.util.List;

import rx.Observable;

/**
 * Created by xComputers on 09/07/2017.
 */
@RetrofitInterface(retrofitApi = TagsApi.class)
public class TagsService extends BaseService<TagsApi> {

    public Observable<List<String>> getTags(){
        return serviceApi.getTags();
    }
}
