package club.leaps.networking.feed.event;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 16/07/2017.
 */

public class CreateEventResponse {

    @SerializedName("event_id")
    private long eventId;

    public CreateEventResponse(long eventId) {
        this.eventId = eventId;
    }

    public long getEventId() {
        return eventId;
    }
}
