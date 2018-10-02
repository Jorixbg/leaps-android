package club.leaps.networking.profile;

import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;
import club.leaps.networking.feed.trainer.Image;

import okhttp3.MultipartBody;
import retrofit2.Call;
import rx.Observable;

/**
 * Created by xComputers on 23/07/2017.
 */
@RetrofitInterface(retrofitApi = UpdateProfileApi.class)
public class UpdateProfileService extends BaseService<UpdateProfileApi> {

    public Observable<Integer> updateUser(UpdateProfileRequest request){
        return serviceApi.updateUser(request);
    }

    public Observable<Image> uploadImage(long eventId, MultipartBody.Part photo){
        return serviceApi.uploadImage(photo, eventId);
    }

    public Observable<Integer> uploadMainImage(long eventId, MultipartBody.Part photo){
        return serviceApi.uploadMainImage(photo, eventId);
    }

    public Call<Void> deleteImage(long imageId){
        return serviceApi.deleteImage(new DeletePicRequest(imageId));
    }

    public Observable<Integer> updateFirebaseToken(FirebaseToken firebaseToken){
        return serviceApi.updateFirebaseToken(firebaseToken);
    }
}
