package club.leaps.networking.base;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 07/08/2017.
 */

public class HttpError {

    @SerializedName("status")
    private int status;
    @SerializedName("error")
    private String error;
    @SerializedName("message")
    private String message;

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
