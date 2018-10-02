package club.leaps.networking.calendar;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 08/07/2017.
 */

public class EventCalendarRequest {

    @SerializedName("user_id")
    private long userId;
    @SerializedName("limit")
    private int limit;
    @SerializedName("page")
    private int offset;


    public EventCalendarRequest(long userId, int limit, int offset) {
        this.userId = userId;
        this.limit = limit;
        this.offset = offset;
    }
}
