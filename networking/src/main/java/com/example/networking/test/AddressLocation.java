package com.example.networking.test;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ivan on 10/16/2017.
 */

public class AddressLocation implements Serializable {

    @SerializedName("address")
    private String address;

    public AddressLocation(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
