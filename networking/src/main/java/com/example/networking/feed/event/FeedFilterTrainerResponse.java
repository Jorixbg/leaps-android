package com.example.networking.feed.event;

import com.example.networking.feed.trainer.Entity;
import com.example.networking.login.UserResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xComputers on 09/07/2017.
 */

public class FeedFilterTrainerResponse {

    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("trainers")
    private List<UserResponse> trainers;

    public int getTotalResults() {
        return totalResults;
    }

    public List<Entity> getTrainers() {
        List<Entity> list = new ArrayList<>();
        list.addAll(trainers);
        return list;
    }
}
