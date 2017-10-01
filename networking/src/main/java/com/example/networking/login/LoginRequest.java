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

    public LoginRequest(String username, String password) {
        this.name = username;
        this.password = password;
    }
}
