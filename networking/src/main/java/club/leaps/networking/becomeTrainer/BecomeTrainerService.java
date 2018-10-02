package club.leaps.networking.becomeTrainer;

import club.leaps.networking.base.BaseService;
import club.leaps.networking.base.RetrofitInterface;
import club.leaps.networking.profile.UpdateProfileApi;
import club.leaps.networking.profile.UpdateProfileRequest;

import rx.Observable;

/**
 * Created by xComputers on 23/07/2017.
 */
@RetrofitInterface(retrofitApi = UpdateProfileApi.class)
public class BecomeTrainerService extends BaseService<UpdateProfileApi> {

    private UpdateProfileRequest request;

    public Observable<Integer> becomeTrainer(long userId,
                                          String email,
                                          String userName,
                                          String gender,
                                          String location,
                                          int maxDistanceSetting,
                                          String firstName,
                                          String lastName,
                                          long birthDay,
                                          String desc,
                                          String longDesc,
                                          int yearsOfTraining,
                                          String phoneNumber,
                                          int price) {

        return serviceApi.updateUser(new UpdateProfileRequest(userId, userName, email, gender, location,
                maxDistanceSetting, firstName, lastName, birthDay, desc, longDesc, yearsOfTraining, phoneNumber, price, true,null));
    }
}
