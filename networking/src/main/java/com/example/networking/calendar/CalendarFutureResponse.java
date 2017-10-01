package com.example.networking.calendar;

import com.example.networking.feed.event.Event;
import com.example.networking.feed.event.RealEvent;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xComputers on 16/07/2017.
 */

public class CalendarFutureResponse {


    @SerializedName("future")
    List<RealEvent> futureList;



    public List<RealEvent> getFutureList() {
        return futureList;
    }
}
