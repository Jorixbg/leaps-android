package com.example.networking.registration;

import com.example.networking.base.BaseService;
import com.example.networking.base.RetrofitInterface;

import retrofit2.Call;
import rx.Observable;

/**
 * Created by xComputers on 04/06/2017.
 */
@RetrofitInterface(retrofitApi = RegistrationRetrofitApi.class)
public class RegistrationService extends BaseService<RegistrationRetrofitApi> {

    public Call<RegistrationResponse> register(String email, String password, String firstName, String lastName, long birthDay, String fbId){

        return serviceApi.register(new RegistrationRequest(email, firstName, lastName, birthDay, password, fbId));
    }

    public Call<RegistrationResponse> register(String email, String password, String firstName, String lastName, long birthDay){
        return serviceApi.register(new RegistrationRequest(email, firstName, lastName, birthDay, password));
    }

    public Call<RegistrationResponse> register(String email, String password, String firstName, String lastName, String googleId, long birthDay){

        return serviceApi.register(new RegistrationRequest(email, firstName, lastName, googleId, birthDay, password));
    }
}
