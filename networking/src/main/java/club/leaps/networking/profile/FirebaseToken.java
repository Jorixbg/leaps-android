package club.leaps.networking.profile;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by IvanGachmov on 2/23/2018.
 */

public class FirebaseToken implements Serializable {

    @SerializedName("firebase_token")
    private String firebase_token;

    public FirebaseToken(String firebase_token) {
        this.firebase_token = firebase_token;
    }

    public String getFirebase_token() {
        return firebase_token;
    }

    public void setFirebase_token(String firebase_token) {
        this.firebase_token = firebase_token;
    }
}