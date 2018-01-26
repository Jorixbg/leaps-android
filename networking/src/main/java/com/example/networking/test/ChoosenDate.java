package com.example.networking.test;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by IvanGachmov on 12/20/2017.
 */

public class ChoosenDate implements Serializable {

    @SerializedName("period")
    private String period;
    @SerializedName("start")
    private String start;
    @SerializedName("end")
    private String end;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
