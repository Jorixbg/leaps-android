package com.example.networking.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 13/07/2017.
 */

public class FacebookLoginRequest {

    @SerializedName("fb_id")
    String fbId;

    public FacebookLoginRequest(String fbId) {
        this.fbId = fbId;
    }
}
