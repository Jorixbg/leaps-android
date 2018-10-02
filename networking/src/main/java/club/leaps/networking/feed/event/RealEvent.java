package club.leaps.networking.feed.event;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by xComputers on 15/06/2017.
 */

public class RealEvent implements Event {

    private static final long serialVersionUID = -5020478936928446634L;

    @SerializedName("title")
    private String title;
    @SerializedName("event_id")
    private long eventId;
    @SerializedName("description")
    private String description;
    @SerializedName("date")
    private long date;
    @SerializedName("time_from")
    private long timeFrom;
    @SerializedName("time_to")
    private long timeTo;
    @SerializedName("owner_image_url")
    private String ownerPicUrl;
    @SerializedName("event_image_url")
    private String imageUrl;
    @SerializedName("specialities")
    private String[] tags;
    @SerializedName("owner_name")
    private String ownerName;
    @SerializedName("owner_id")
    private long ownerId;
    @SerializedName("coord_lat")
    private double latitude;
    @SerializedName("coord_lnt")
    private double longtitude;
    @SerializedName("price_from")
    private float priceFrom;
    @SerializedName("address")
    private String address;
    @SerializedName("free_slots")
    private int freeSlots;
    @SerializedName("date_created")
    private long created;
    @SerializedName("attending")
    private Attendee attendees;
    @SerializedName("rating")
    private float rating;
    @SerializedName("reviews")
    private int reviews;
    @SerializedName("images")
    private List<EventImage> imageUrls;
    @SerializedName("position")
    private int position;
    @SerializedName("distance")
    private float distance;



    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public long ownerId(){return ownerId;}

    @Override
    public long eventId() {
        return eventId;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Date date() {
        return new Date(date);
    }

    @Override
    public Date timeFrom() {
        return new Date(timeFrom);
    }

    @Override
    public Date timeTo() {
        return new Date(timeTo);
    }

    @Override
    public String ownerPicUrl() {
        return ownerPicUrl;
    }

    @Override
    public String ownerName() {
        return ownerName;
    }

    @Override
    public double latitude() {
        return latitude;
    }

    @Override
    public double longtitude() {
        return longtitude;
    }

    @Override
    public float priceFrom() {
        return priceFrom;
    }

    @Override
    public String address() {
        return address;
    }

    @Override
    public int freeSlots() {
        return freeSlots;
    }

    @Override
    public Date created() {
        return new Date(created);
    }

    @Override
    public String imageUrl() {
        return imageUrl;
    }

    @Override
    public String[] tags(){
        return tags;
    }

    @Override
    public List<EventImage> images() {
        return imageUrls;
    }

    public Attendee attendees() {
        Attendee followersObj= attendees;
        return followersObj;
    }

    @Override
    public float rating() {
        return rating;
    }

    @Override
    public int reviews() {
        return reviews;
    }

    public float distance() {
        return distance;
    }

    public void setAttendee(Attendee attendees) {
        this.attendees = attendees;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealEvent realEvent = (RealEvent) o;

        return eventId == realEvent.eventId;

    }

}
