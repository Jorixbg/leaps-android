package com.example.networking.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 13/07/2017.
 */

public class GoogleLoginRequest {

    @SerializedName("google_id")
    String googleId;

    public GoogleLoginRequest(String googleId) {
        this.googleId = googleId;
    }
}
