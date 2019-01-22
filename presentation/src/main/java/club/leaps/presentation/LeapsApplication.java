package club.leaps.presentation;

import android.app.Application;

import club.leaps.networking.base.RetrofitManager;
import com.testfairy.TestFairy;


/**
 * Created by xComputers on 22/05/2017.
 */

public class LeapsApplication extends Application {

//    public static final String BASE_URL = "http://91.238.251.154:8888";
    public static final String BASE_URL = "http://91.238.251.125:8888";

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitManager.getInstance().init(BASE_URL);
        TestFairy.begin(this, "7b4838e8517c84ef3f0639c7944261928e28b4eb");
    }
}
