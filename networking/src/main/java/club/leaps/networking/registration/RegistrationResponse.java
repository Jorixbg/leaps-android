package club.leaps.networking.registration;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 04/06/2017.
 */

public class RegistrationResponse {

    @SerializedName("user_id")
    private int userId;

    public int getUserId() {
        return userId;
    }
}
