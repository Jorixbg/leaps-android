package com.example.networking.following.user;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ivan on 9/28/2017.
 */

public class FollowedUser implements Serializable {


    @SerializedName("user_id")
    private long userId;
    @SerializedName("user_name")
    private String username;
    @SerializedName("user_image_url")
    private String profileImage;


    protected FollowedUser(Parcel in) {
        userId = in.readLong();
        username = in.readString();
        profileImage = in.readString();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
