package club.leaps.presentation.utils;

import club.leaps.networking.feed.trainer.Entity;
import club.leaps.presentation.User;

/**
 * Created by xComputers on 03/06/2017.
 */

public class LoginResponseToUserTypeMapper {

    public static void map(Entity response){

        if(response == null){
            return;
        }
        User user = User.getInstance();
        user.setUserId(response.userId());
        user.setUsername(response.username());
        user.setEmail(response.email());
        user.setAge(response.age());
        user.setGender(response.gender());
        user.setAddress(response.address());
        user.setMaxDistance(response.maxDistance());
        user.setFirstName(response.firstName());
        user.setLastName(response.lastName());
        user.setBirthDay(response.birthDay());
        user.setDescription(response.description());
        user.setHasFreeEvent(response.hasFreeEvent());
        user.setTrainer(response.isTrainer());
        user.setImageUrl(response.profileImageUrl());
        user.setFollowed(response.followers());
        user.setHostingEvents(response.hosting());
        if(response.isTrainer()){
            user.setSpecs(response.specialities());
            user.setPhoneNumber(response.phoneNumber());
            user.setYearsOfTraining(response.yearsOfTraining());
            user.setSesionPrice(response.sesionPrice());
            user.setLongDescription(response.longDescription());
        }
    }
}
