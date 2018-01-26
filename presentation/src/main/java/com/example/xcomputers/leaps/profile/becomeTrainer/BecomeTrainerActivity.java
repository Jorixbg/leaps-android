package com.example.xcomputers.leaps.profile.becomeTrainer;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.networking.base.HttpError;
import com.example.networking.becomeTrainer.BecomeTrainerService;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.login.UserResponse;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.IActivity;
import com.example.xcomputers.leaps.base.IBaseView;
import com.example.xcomputers.leaps.utils.EntityHolder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.ConnectException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xComputers on 23/07/2017.
 */

public class BecomeTrainerActivity extends AppCompatActivity implements IActivity{

    private IBaseView currentFragment;
    private int years;
    private int price;
    private String phoneNumber;
    private BecomeTrainerService service;
    private ProgressBar progressBar;
    private FrameLayout container;
    private static boolean flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.become_trainer_activity);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        service = new BecomeTrainerService();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        container = (FrameLayout) findViewById(R.id.container);
        openFragment(BecomeTrainerYears.class, new Bundle());
    }


    public <View extends IBaseView> void openFragment(Class<View> clazz, Bundle arguments) {

        openFragment(clazz, arguments, true);
    }


    public <View extends IBaseView> void openFragment(Class<View> clazz, Bundle arguments, boolean addToBackStack) {

        String name = clazz.getCanonicalName();
        if (name == null) {
            return;
        }
        if (currentFragment != null && currentFragment.getClass().getCanonicalName().equals(name)) {
            return;
        }
        if (getSupportFragmentManager().findFragmentByTag(name) != null) {
            currentFragment = popBackStack(getSupportFragmentManager(), name, arguments);
        } else {
            Fragment fragmentToOpen = Fragment.instantiate(this, name, arguments);
            if (addToBackStack) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragmentToOpen, name)
                        .addToBackStack(name).commitAllowingStateLoss();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragmentToOpen, name)
                        .disallowAddToBackStack().commitAllowingStateLoss();
            }
            getSupportFragmentManager().executePendingTransactions();
            currentFragment = (IBaseView) fragmentToOpen;
        }
    }

    @Override
    public void onBackPressed() {

        if (currentFragment.onBack()) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 1) {
            int topEntryIndex = fm.getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(topEntryIndex - 1);
            for (int i = topEntryIndex; i >= 0; i--) {
                if (fm.getBackStackEntryAt(i) != null && backStackEntry.getName() != null) {
                    currentFragment = popBackStack(fm, backStackEntry.getName(), null);
                    break;
                }
            }
        } else {
            supportFinishAfterTransition();
        }
    }

    protected IBaseView popBackStack(FragmentManager fragmentManager, String fragmentViewName, Bundle args) {

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentViewName);
        if (fragment != null && fragment.getArguments() != null && args != null) {
            fragment.getArguments().putAll(args);
        }
        fragmentManager.popBackStack(fragmentViewName, 0);
        fragmentManager.executePendingTransactions();

        return (IBaseView) fragment;
    }

    void setYears(int years) {
        this.years = years;
        openFragment(BecomeTrainerSessionPrice.class, new Bundle());
    }

    void setSessionPrice(int sessionPrice) {
        this.price = sessionPrice;
        openFragment(BecomeTrainerPhoneNumber.class, new Bundle());
    }

    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        showLoading();
        service.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        Entity entity = EntityHolder.getInstance().getEntity();
        service.becomeTrainer(entity.userId(), entity.email(), entity.username(), entity.gender(), entity.address(),
                User.getInstance().getMaxDistance(), entity.firstName(), entity.lastName(), entity.birthDay(), entity.description(),
                entity.longDescription(), years, phoneNumber, price)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    service.removeHeader("Authorization");
                    User.getInstance().setTrainer(true);
                    User.getInstance().setYearsOfTraining(years);
                    User.getInstance().setSesionPrice(price);
                    User.getInstance().setPhoneNumber(phoneNumber);
                    UserResponse userResponse = (UserResponse) EntityHolder.getInstance().getEntity();
                    userResponse.setYearsOfTraining(years);
                    userResponse.setSesionPrice(price);
                    userResponse.setPhoneNumber(phoneNumber);
                    userResponse.setTrainer(true);
                    hideLoading();
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    flag = true;
                    finish();
                }, throwable -> {
                    service.removeHeader("Authorization");
                    hideLoading();
                    if (throwable instanceof HttpException) {
                        HttpException exception = (HttpException) throwable;
                        Response response = exception.response();
                        Converter<ResponseBody, HttpError> converter = (Converter<ResponseBody, HttpError>) GsonConverterFactory.create()
                                .responseBodyConverter(HttpError.class, new Annotation[0], null);
                        HttpError error1 = null;
                        try {
                            error1 = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(this, error1.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    setResult(RESULT_CANCELED);
                    finish();
                });
    }

    public void showLoading() {

        progressBar.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }


    public void hideLoading() {

        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    private String getErrorMessage(Throwable error) {
        String message;
        if (error instanceof UnknownHostException || error instanceof ConnectException) {
            message = "This action requires an internet connection!";
        } else if (error instanceof IOException) {
            message = "Oops... Something went wrong!";
        } else {
            message = error.getMessage();
        }
        return message;
    }

    public static boolean isFlag() {
        return flag;
    }
}
