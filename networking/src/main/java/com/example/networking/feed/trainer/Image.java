package com.example.networking.feed.trainer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xComputers on 15/07/2017.
 */

public class Image implements Serializable{

    private static final long serialVersionUID = 7333709424895633879L;

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
