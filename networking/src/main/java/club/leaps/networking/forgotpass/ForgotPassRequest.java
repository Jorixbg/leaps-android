package club.leaps.networking.forgotpass;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 04/06/2017.
 */

class ForgotPassRequest {
    @SerializedName("email_address")
    String email;

    ForgotPassRequest(String email) {
        this.email = email;
    }
}
