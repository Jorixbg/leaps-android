package com.example.networking.test;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CreateRecurringEventRequest {


    @SerializedName("dates")
    private List<ChoosenDate> dates;
    @SerializedName("repeat")
    private boolean repeat;
    @SerializedName("start")
    private long start;
    @SerializedName("end")
    private long end;
    @SerializedName("frequency")
    private String frequency;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("address")
    private String address;
    @SerializedName("coord_lat")
    private Double lattitude;
    @SerializedName("coord_lnt")
    private Double longtitude;
    @SerializedName("price_from")
    private double priceFrom;
    @SerializedName("free_slots")
    private int freeSlots;
    @SerializedName("tags")
    private List<String> tags;

    public CreateRecurringEventRequest(List<ChoosenDate> dates, boolean repeat, long start, long end, String frequency, String title, String description, String address, Double lattitude, Double longtitude, double priceFrom, int freeSlots, List<String> tags) {
        this.dates = dates;
        this.repeat = repeat;
        this.start = start;
        this.end = end;
        this.frequency = frequency;
        this.title = title;
        this.description = description;
        this.address = address;
        this.lattitude = lattitude;
        this.longtitude = longtitude;
        this.priceFrom = priceFrom;
        this.freeSlots = freeSlots;
        this.tags = tags;
    }

    public List<ChoosenDate> getDates() {
        return dates;
    }

    public void setDates(List<ChoosenDate> dates) {
        this.dates = dates;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(int freeSlots) {
        this.freeSlots = freeSlots;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
