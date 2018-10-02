package club.leaps.networking.profile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xComputers on 23/07/2017.
 */

public class UpdateProfileRequest {
    @SerializedName("user_id")
    long userId;
    @SerializedName("username")
    String userName;
    @SerializedName("email_address")
    String email;
    @SerializedName("gender")
    String gender;
    @SerializedName("location")
    String location;
    @SerializedName("max_distance_setting")
    int maxDistanceSetting;
    @SerializedName("first_name")
    String firstName;
    @SerializedName("last_name")
    String lastName;
    @SerializedName("birthday")
    long birthDay;
    @SerializedName("description")
    String desc;
    @SerializedName("long_description")
    String longDesc;
    @SerializedName("years_of_training")
    int yearsOfTraining;
    @SerializedName("phone_number")
    String phoneNumber;
    @SerializedName("price_for_session")
    int price;
    @SerializedName("is_trainer")
    boolean isTrainer;
    @SerializedName("specialties")
    List<String> specialties;


    public UpdateProfileRequest(long userId,
                                String userName,
                                String email,
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
                                int price,
                                boolean isTrainer,
                                List<String> specialties) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.location = location;
        this.maxDistanceSetting = maxDistanceSetting;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.desc = desc;
        this.longDesc = longDesc;
        this.yearsOfTraining = yearsOfTraining;
        this.phoneNumber = phoneNumber;
        this.price = price;
        this.isTrainer = isTrainer;
        this.specialties = specialties;
    }

    public boolean getIsTrainer(){
        return this.isTrainer;
    }
    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getLocation() {
        return location;
    }

    public int getMaxDistanceSetting() {
        return maxDistanceSetting;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getBirthDay() {
        return birthDay;
    }

    public String getDesc() {
        return desc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public int getYearsOfTraining() {
        return yearsOfTraining;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getPrice() {
        return price;
    }
    
    public boolean isTrainer() {
        return isTrainer;
    }

    public List<String> getSpecialties() {
        return specialties;
    }
}
