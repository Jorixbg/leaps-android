package com.example.xcomputers.leaps.homefeed;

import com.example.networking.feed.event.FeedFilterRequest;

/**
 * Created by xComputers on 18/06/2017.
 */

public interface OnSearchDataCollectedListener {
    FeedFilterRequest onSearchDataCollected();
    String getOrigin();
    void resetSearch();
}
