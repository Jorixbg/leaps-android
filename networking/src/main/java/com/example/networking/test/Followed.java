package com.example.networking.test;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ivan on 9/28/2017.
 */

public class Followed {
    @SerializedName("following")
    private List<FollowedUser> followingUsers;
    @SerializedName("others")
    private List<FollowedUser> followers;

    public List<FollowedUser> getFollowingUsers() {
        return followingUsers;
    }

    public void setFollowingUsers(List<FollowedUser> followingUsers) {
        this.followingUsers = followingUsers;
    }

    public List<FollowedUser> getFollowers() {
        return followers;
    }

    public void setFollowers(List<FollowedUser> followers) {
        this.followers = followers;
    }
}
