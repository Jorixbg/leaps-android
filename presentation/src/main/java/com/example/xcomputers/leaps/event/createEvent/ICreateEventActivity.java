package com.example.xcomputers.leaps.event.createEvent;

import android.net.Uri;

import com.example.networking.test.ChoosenDate;
import com.example.xcomputers.leaps.base.IActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by xComputers on 15/07/2017.
 */

public interface ICreateEventActivity extends IActivity {
    //TODO this method should accept an object of some kind and it should have overloads for the inner screens
    void collectData(Map<Integer, Uri> images, String title, String description, List<String> tags);
    void collectData(double latitude,double longitude,String address);
    void collectData(double priceFrom, int freeSlots);
    void collectData(long eventTime,long endTime);
    void collectData(String frequency, long startTime,long endTime,List<ChoosenDate> dates);
}
