package com.example.networking.registration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 04/06/2017.
 */

public class RegistrationRequest {

    @SerializedName("email_address")
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("birthday")
    private long birthDay;
    @SerializedName("password")
    private String password;
    @SerializedName("fb_id")
    private String fBId;
    @SerializedName("gogle_id")
    private String googleId;

    public RegistrationRequest(@NonNull String email,
                               @Nullable String firstName,
                               @Nullable String lastName,
                               @Nullable long birthDay,
                               @NonNull String password,
                               String fbId) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.password = password;
        this.fBId = fbId;
    }

    public RegistrationRequest(@NonNull String email,
                               @Nullable String firstName,
                               @Nullable String lastName,
                               long birthDay,
                               @NonNull String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.password = password;
    }

    public RegistrationRequest(@NonNull String email,
                               @Nullable String firstName,
                               @Nullable String lastName,
                               String googleId,
                               long birthDay,
                               @NonNull String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.password = password;
        this.googleId = googleId;
    }

}
