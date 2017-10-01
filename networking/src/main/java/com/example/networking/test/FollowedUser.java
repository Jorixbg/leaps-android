package com.example.networking.test;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivan on 9/28/2017.
 */

public class FollowedUser implements Parcelable {


    @SerializedName("user_id")
    private long userId;
    @SerializedName("full_name")
    private String username;
    @SerializedName("user_profile_picture")
    private String profileImage;


    protected FollowedUser(Parcel in) {
        userId = in.readLong();
        username = in.readString();
        profileImage = in.readString();
    }

    public static final Creator<FollowedUser> CREATOR = new Creator<FollowedUser>() {
        @Override
        public FollowedUser createFromParcel(Parcel in) {
            return new FollowedUser(in);
        }

        @Override
        public FollowedUser[] newArray(int size) {
            return new FollowedUser[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(userId);
        parcel.writeString(username);
        parcel.writeString(profileImage);
    }
}
