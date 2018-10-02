package club.leaps.networking.following.user;

import club.leaps.networking.feed.event.AttendeeResponse;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 9/28/2017.
 */

public class Followed implements Serializable {
    @SerializedName("following")
    private List<AttendeeResponse> followingUsers;
    @SerializedName("others")
    private List<AttendeeResponse> followers;

    private List<AttendeeResponse> allUsers;

    public List<AttendeeResponse> getFollowingUsers() {
        return followingUsers;
    }

    public void setFollowingUsers(List<AttendeeResponse> followingUsers) {
        this.followingUsers = followingUsers;
    }

    public List<AttendeeResponse> getFollowers() {
        return followers;
    }

    public void setFollowers(List<AttendeeResponse> followers) {
        this.followers = followers;
    }


    public List<AttendeeResponse> getAllUsers() {


        allUsers = new ArrayList<>();
        allUsers.addAll(followingUsers);
        allUsers.addAll(followers);

        return allUsers;
    }
    public void setAllUsers(List<AttendeeResponse> allUsers) {
        this.allUsers = allUsers;
    }

}
