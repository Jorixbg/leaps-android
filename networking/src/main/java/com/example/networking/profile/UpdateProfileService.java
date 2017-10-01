package com.example.networking.profile;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;
import com.example.networking.feed.trainer.Image;

import okhttp3.MultipartBody;
import retrofit2.Call;
import rx.Observable;

/**
 * Created by xComputers on 23/07/2017.
 */
@RetrofitInterface(retrofitApi = UpdateProfileApi.class)
public class UpdateProfileService extends BaseService<UpdateProfileApi> {

    public Observable<Void> updateUser(UpdateProfileRequest request){
        return serviceApi.updateUser(request);
    }

    public Observable<Image> uploadImage(long eventId, MultipartBody.Part photo){
        return serviceApi.uploadImage(photo, eventId);
    }

    public Observable<Image> uploadMainImage(long eventId, MultipartBody.Part photo){
        return serviceApi.uploadMainImage(photo, eventId);
    }

    public Call<Void> deleteImage(long imageId){
        return serviceApi.deleteImage(new DeletePicRequest(imageId));
    }
}
