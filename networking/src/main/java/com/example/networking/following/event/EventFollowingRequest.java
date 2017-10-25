package com.example.networking.following.event;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivan on 10/2/2017.
 */

public class EventFollowingRequest {

    @SerializedName("event_id")
    private long eventId;

    public EventFollowingRequest(long eventId) {
        this.eventId = eventId;
    }

}
