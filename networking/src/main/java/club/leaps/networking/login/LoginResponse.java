package club.leaps.networking.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 15/07/2017.
 */

public class LoginResponse {

    @SerializedName("user_id")
    private long userId;

    public long getUserId() {
        return userId;
    }
}
