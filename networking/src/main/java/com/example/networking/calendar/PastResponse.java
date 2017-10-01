package com.example.networking.calendar;

import com.example.networking.feed.event.RealEvent;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xComputers on 16/07/2017.
 */

public class PastResponse {

    @SerializedName("past")
    List<RealEvent> pastList;

    public List<RealEvent> getPastList() {
        return pastList;
    }
}
