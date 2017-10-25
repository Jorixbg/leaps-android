package com.example.networking.feed.event;

import java.util.ArrayList;

/**
 * Created by xComputers on 14/06/2017.
 */

public class DefaultFeedEventRequest extends FeedFilterRequest {

    public DefaultFeedEventRequest(){
        super("THIS IS A TEST !!!!", 123.12312, 123.12312,10,new ArrayList<>(), System.currentTimeMillis(), System.currentTimeMillis(), DateSelection.NEXT3,20,1);
    }
}
