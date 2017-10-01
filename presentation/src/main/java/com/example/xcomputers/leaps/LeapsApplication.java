package com.example.xcomputers.leaps;

import android.app.Application;

import com.example.networking.base.RetrofitManager;
import com.testfairy.TestFairy;


/**
 * Created by xComputers on 22/05/2017.
 */

public class LeapsApplication extends Application {

    public static final String BASE_URL = "http://ec2-35-157-240-40.eu-central-1.compute.amazonaws.com:8888";

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitManager.getInstance().init(BASE_URL);
        TestFairy.begin(this, "7b4838e8517c84ef3f0639c7944261928e28b4eb");
    }
}
