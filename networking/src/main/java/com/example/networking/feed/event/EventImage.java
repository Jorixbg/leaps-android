package com.example.networking.feed.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xComputers on 16/07/2017.
 */

public class EventImage implements Serializable {

    private static final long serialVersionUID = -8408589490551005135L;

    @SerializedName("image_id")
    private long imageId;
    @SerializedName("image_url")
    private String imageUrl;


    public long getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
