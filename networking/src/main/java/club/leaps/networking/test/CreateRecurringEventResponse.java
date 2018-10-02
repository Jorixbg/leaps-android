package club.leaps.networking.test;

import club.leaps.networking.feed.event.Attendee;
import club.leaps.networking.feed.event.EventImage;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
<<<<<<< HEAD
 * Created by IvanGachmov on 12/22/2017.
=======
 * Created by Ivan on 12/21/2017.
>>>>>>> 2a2801496905df4f2f7347c4991d057231b6e633
 */

public class CreateRecurringEventResponse {

    @SerializedName("event_id")
    private long eventId;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("date")
    private long date;
    @SerializedName("time_from")
    private long start;
    @SerializedName("time_to")
    private long end;
    @SerializedName("owner_id")
    private long ownerId;
    @SerializedName("owner_name")
    private String owner_name;
    @SerializedName("owner_image_url")
    private String owner_image_url;
    @SerializedName("distance")
    private long distance;
    @SerializedName("can_rate")
    private boolean can_rate;
    @SerializedName("rating")
    private float rating;
    @SerializedName("reviews")
    private int reviews;
    @SerializedName("specialties")
    private List<String> specialties;
    @SerializedName("attending")
    private Attendee attendees;
    @SerializedName("event_image_url")
    private String event_image_url;
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
    @SerializedName("date_created")
    private long created;
    @SerializedName("images")
    private List<EventImage> imageUrls;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwner_image_url() {
        return owner_image_url;
    }

    public void setOwner_image_url(String owner_image_url) {
        this.owner_image_url = owner_image_url;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public boolean isCan_rate() {
        return can_rate;
    }

    public void setCan_rate(boolean can_rate) {
        this.can_rate = can_rate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public Attendee getAttendees() {
        return attendees;
    }

    public void setAttendees(Attendee attendees) {
        this.attendees = attendees;
    }

    public String getEvent_image_url() {
        return event_image_url;
    }

    public void setEvent_image_url(String event_image_url) {
        this.event_image_url = event_image_url;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public double getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(int freeSlots) {
        this.freeSlots = freeSlots;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public List<EventImage> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<EventImage> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
