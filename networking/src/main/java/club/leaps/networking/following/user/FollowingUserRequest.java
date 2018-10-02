package club.leaps.networking.following.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivan on 9/3/2017.
 */

public class FollowingUserRequest {

    @SerializedName("user_id")
    long userId;

    public FollowingUserRequest(long userId){
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
