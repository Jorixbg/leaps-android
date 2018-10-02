package club.leaps.networking.feed.event;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 15/07/2017.
 */

public class EventAttendRequest {

    @SerializedName("user_id")
    private long userId;
    @SerializedName("event_id")
    private long eventId;

    public EventAttendRequest(long userId, long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

}
