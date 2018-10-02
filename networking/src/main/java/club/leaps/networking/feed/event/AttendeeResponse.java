package club.leaps.networking.feed.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ivan on 10/7/2017.
 */

public class AttendeeResponse implements Serializable {


    @SerializedName("user_id")
    private long userId;
    @SerializedName("user_name")
    private String username;
    @SerializedName("user_image_url")
    private String profileImage;





    public long getUserId(){
        return userId;
    }

    public String getUsername(){
        return username;
    }

    public String getProfileImage(){
        return profileImage;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }


}

