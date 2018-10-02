package club.leaps.networking.registration;

import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;

import retrofit2.Call;

/**
 * Created by xComputers on 04/06/2017.
 */
@RetrofitInterface(retrofitApi = RegistrationRetrofitApi.class)
public class RegistrationService extends BaseService<RegistrationRetrofitApi> {

    public Call<RegistrationResponse> register(String email, String password, String firstName, String lastName, long birthDay, String fbId,String firebaseToken){

        return serviceApi.register(new RegistrationRequest(email, firstName, lastName, birthDay, password, fbId,firebaseToken));
    }


    public Call<RegistrationResponse> register(String email, String password, String firstName, String lastName, long birthDay, String firebaseToken,boolean device){
        return serviceApi.register(new RegistrationRequest(email, firstName, lastName, birthDay, firebaseToken, password,device));
    }

    public Call<RegistrationResponse> register(String email, String password, String firstName, String lastName, String googleId, long birthDay,String firebaseToken){

        return serviceApi.register(new RegistrationRequest(email, firstName, lastName, googleId, birthDay, password,firebaseToken));
    }
}
