package com.example.networking.feed.event;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xComputers on 09/07/2017.
 */

public class FeedFilterEventResponse {

    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("events")
    private List<RealEvent> eventList;

    public int getTotalResults() {
        return totalResults;
    }

    public List<RealEvent> getEventList() {
        return eventList;
    }
}
