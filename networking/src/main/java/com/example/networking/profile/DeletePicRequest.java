package com.example.networking.profile;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xComputers on 23/07/2017.
 */

public class DeletePicRequest {

    @SerializedName("image_id")
    long imageId;

    public DeletePicRequest(long imageId) {
        this.imageId = imageId;
    }
}
