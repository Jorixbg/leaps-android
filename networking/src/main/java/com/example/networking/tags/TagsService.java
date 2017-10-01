package com.example.networking.tags;

import android.util.Log;
import android.widget.Toast;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;

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
