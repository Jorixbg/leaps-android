package club.leaps.networking.calendar;

import club.leaps.networking.feed.event.RealEvent;
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
