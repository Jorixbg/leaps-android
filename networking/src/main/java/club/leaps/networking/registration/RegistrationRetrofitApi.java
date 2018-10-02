package club.leaps.networking.registration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by xComputers on 04/06/2017.
 */

public interface RegistrationRetrofitApi {

    @POST("/register")
    Call<RegistrationResponse> register(@Body RegistrationRequest registrationRequest);
}
