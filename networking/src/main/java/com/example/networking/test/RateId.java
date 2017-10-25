package com.example.networking.test;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ivan on 10/23/2017.
 */

public class RateId implements Serializable {

    @SerializedName("rate_id")
    private int rateId;

    public int getRateId() {
        return rateId;
    }

    public void setRateId(int rateId) {
        this.rateId = rateId;
    }
}
