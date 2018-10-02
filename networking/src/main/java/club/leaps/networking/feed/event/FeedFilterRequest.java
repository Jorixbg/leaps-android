package club.leaps.networking.feed.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xComputers on 14/06/2017.
 */

public class FeedFilterRequest implements Serializable{

    @SerializedName("key_words")
    protected String address;
    @SerializedName("my_lnt")
    protected Double longtitude;
    @SerializedName("my_lat")
    protected Double latitude;
    @SerializedName("distance")
    protected int distance;
    @SerializedName("tags")
    protected List<String> tags;
    @SerializedName("min_start_date")
    protected long minStartTime;
    @SerializedName("max_start_date")
    protected long maxStartDate;
    @SerializedName("limit")
    private int limit;
    @SerializedName("page")
    private int offset;
    private DateSelection time;

    public enum DateSelection {TODAY, NEXT3, NEXT5,ALL}

    public FeedFilterRequest(String address, Double longtitude, Double latitude, int distance,List<String> tags, long minStartTime, long maxStartDate, DateSelection time,
                               int limit, int offset) {
        this.address = address;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.distance = distance;
        this.tags = tags;
        this.minStartTime = minStartTime;
        this.maxStartDate = maxStartDate;
        this.time = time;
        this.limit = limit;
        this.offset = offset;
    }






    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getAddress() {
        return address;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public int getDistance() {
        return distance;
    }

    public List<String> getTags() {
        return tags;
    }

    public long getMinStartTime() {
        return minStartTime;
    }

    public long getMaxStartDate() {
        return maxStartDate;
    }

    public DateSelection getTime(){
        return time;
    }
}
