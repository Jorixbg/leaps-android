package club.leaps.networking.test;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivan on 10/2/2017.
 */

public class EventRateRequest {

    @SerializedName("event_id")
    private long eventId;
    @SerializedName("rating")
    private int rating;
    @SerializedName("comment")
    private String comment;
    @SerializedName("date_created")
    private long date;

    public EventRateRequest(long eventId, int rating, String comment, long date) {
        this.eventId = eventId;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }
}
