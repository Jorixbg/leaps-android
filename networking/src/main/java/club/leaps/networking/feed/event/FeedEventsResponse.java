package club.leaps.networking.feed.event;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xComputers on 14/06/2017.
 */

public class FeedEventsResponse {

    @SerializedName("popular")
    List<RealEvent> popular;
    @SerializedName("nearby")
    List<RealEvent> nearBy;
    @SerializedName("suited")
    List<RealEvent> suited;


    public List<RealEvent> getNearBy() {
        return nearBy;
    }

    public List<RealEvent> getPopular() {
        return popular;
    }

    public List<RealEvent> getSuited() {
        return suited;
    }
}
