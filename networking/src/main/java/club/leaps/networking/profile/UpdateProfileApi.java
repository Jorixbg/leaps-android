package club.leaps.networking.profile;

import club.leaps.networking.feed.trainer.Image;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by xComputers on 23/07/2017.
 */

public interface UpdateProfileApi {

    @POST("/user/update")
    Observable<Integer> updateUser(@Body UpdateProfileRequest request);

    @POST("/user/update/firebase_token")
    Observable<Integer> updateFirebaseToken(@Body FirebaseToken firebaseToken);

    @Multipart
    @PUT("/pic/user")
    Observable<Image> uploadImage(@Part MultipartBody.Part photo, @Part("user_id") long userId);

    @Multipart
    @PUT("/pic/user/main")
    Observable<Integer> uploadMainImage(@Part MultipartBody.Part photo, @Part("user_id") long userId);

    @HTTP(method = "DELETE", path = "/pic/user", hasBody = true)
    Call<Void> deleteImage(@Body DeletePicRequest request);
}
