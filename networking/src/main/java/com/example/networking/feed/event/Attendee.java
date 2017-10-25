package com.example.networking.feed.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xComputers on 02/07/2017.
 */

public class Attendee implements Serializable {


    @SerializedName("followed")
    private List<AttendeeResponse> followed;
    @SerializedName("others")
    private List<AttendeeResponse> others;

    private List<AttendeeResponse> allUsers;



    public Attendee(List<AttendeeResponse> followed, List<AttendeeResponse> others) {
        this.followed = followed;
        this.others = others;
    }

    public List<AttendeeResponse> getFollowed() {
        return followed;
    }

    public void setFollowed(List<AttendeeResponse> followed) {
        this.followed = followed;
    }

    public List<AttendeeResponse> getOthers() {
        return others;
    }

    public void setOthers(List<AttendeeResponse> others) {
        this.others = others;
    }

    public List<AttendeeResponse> getAllUsers() {


        allUsers = new ArrayList<>();
        allUsers.addAll(followed);
        allUsers.addAll(others);

        return allUsers;
    }
    public void setAllUsers(List<AttendeeResponse> allUsers) {
        this.allUsers = allUsers;
    }
}

