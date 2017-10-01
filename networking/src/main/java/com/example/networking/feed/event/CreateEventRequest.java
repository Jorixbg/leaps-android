package com.example.networking.feed.event;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by xComputers on 16/07/2017.
 */

public class CreateEventRequest {

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
    @SerializedName("owner_id")
    private long ownerId;
    @SerializedName("coord_lat")
    private Double lattitude;
    @SerializedName("coord_lnt")
    private Double longtitude;
    @SerializedName("address")
    private String address;
    @SerializedName("price_from")
    private double priceFrom;
    @SerializedName("free_slots")
    private int freeSlots;
    @SerializedName("date_created")
    private long dateCreated;
    @SerializedName("tags")
    private List<String> tags;

    public CreateEventRequest(String title,
                              String description,
                              long date,
                              long timeFrom,
                              long timeTo,
                              long ownerId,
                              double lattitude,
                              double longtitude,
                              double priceFrom,
                              String address,
                              int freeSlots,
                              long dateCreated,
                              List<String> tags) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.priceFrom = priceFrom;
        this.ownerId = ownerId;
        this.lattitude = lattitude;
        this.longtitude = longtitude;
        this.address = address;
        this.freeSlots = freeSlots;
        this.dateCreated = dateCreated;
        this.tags = tags;
    }
}
