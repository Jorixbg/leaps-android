package com.example.networking.feed.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xComputers on 02/07/2017.
 */

public class Attendee implements Serializable {

    private static final long serialVersionUID = 2887315518377172621L;

    @SerializedName("user_id")
    private long userId;
    @SerializedName("user_name")
    private String name;
    @SerializedName("user_image_url")
    private String imageUrl;

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Attendee(long userId, String name, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}

