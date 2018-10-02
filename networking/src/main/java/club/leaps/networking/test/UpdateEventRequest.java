package club.leaps.networking.test;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ivan on 1/5/2018.
 */

public class UpdateEventRequest {

    @SerializedName("event_id")
    private long eventId;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("date")
    private long date;
    @SerializedName("time_from")
    private long timeFrom;
    @SerializedName("time_to")
    private long timeTo;
    @SerializedName("coord_lat")
    private Double lattitude;
    @SerializedName("coord_lnt")
    private Double longtitude;
    @SerializedName("price_from")
    private double priceFrom;
    @SerializedName("address")
    private String address;
    @SerializedName("free_slots")
    private int freeSlots;
    @SerializedName("tags")
    private List<String> tags;

    public UpdateEventRequest(long eventId, String title, String description, long date, long timeFrom, long timeTo, Double lattitude, Double longtitude, double priceFrom, String address, int freeSlots, List<String> tags) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.lattitude = lattitude;
        this.longtitude = longtitude;
        this.priceFrom = priceFrom;
        this.address = address;
        this.freeSlots = freeSlots;
        this.tags = tags;
    }

    public long getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDate() {
        return date;
    }

    public long getTimeFrom() {
        return timeFrom;
    }

    public long getTimeTo() {
        return timeTo;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public double getPriceFrom() {
        return priceFrom;
    }

    public String getAddress() {
        return address;
    }

    public int getFreeSlots() {
        return freeSlots;
    }

    public List<String> getTags() {
        return tags;
    }
}
