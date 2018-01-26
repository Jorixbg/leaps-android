package com.example.networking.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 03/06/2017.
 */

public class LoginRequest {
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;
    @SerializedName("firebase_token")
    private String firebase_token;

    public LoginRequest(String name, String password, String firebase_token) {
        this.name = name;
        this.password = password;
        this.firebase_token = firebase_token;
    }
}
