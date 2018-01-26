package com.example.networking.feed.event;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by xComputers on 14/06/2017.
 */

public interface Event extends Serializable {

    long eventId();
    long ownerId();
    String title();
    String description();
    Date date();
    Date timeFrom();
    Date timeTo();
    String ownerPicUrl();
    String ownerName();
    double latitude();
    double longtitude();
    float priceFrom();
    String address();
    int freeSlots();
    Date created();
    String imageUrl();
    String[] tags();
    List<EventImage> images();
    Attendee attendees();
    float rating();
    int reviews();
    float distance();




}
