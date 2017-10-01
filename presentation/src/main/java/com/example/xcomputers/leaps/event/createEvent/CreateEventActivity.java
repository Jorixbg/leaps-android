package com.example.xcomputers.leaps.event.createEvent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.networking.feed.event.CreateEventService;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.IBaseView;
import com.example.xcomputers.leaps.utils.FilePathDescriptor;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xComputers on 15/07/2017.
 */

public class CreateEventActivity extends AppCompatActivity implements ICreateEventActivity {

    private ProgressBar progressBar;
    private FrameLayout container;
    private IBaseView currentFragment;
    private String title;
    private String description;
    private double priceFrom;
    private long timeFrom;
    private long timeTo;
    private String address;
    private int freeSlots;
    private List<String> tags;
    private Map<Integer, Uri> images;
    private CreateEventService service;
    private int imageCounter = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        service = new CreateEventService();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        container = (FrameLayout) findViewById(R.id.create_event_container);
        openFragment(CreateEventFirstView.class, new Bundle());

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
                        .replace(R.id.create_event_container, fragmentToOpen, name)
                        .addToBackStack(name).commitAllowingStateLoss();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.create_event_container, fragmentToOpen, name)
                        .disallowAddToBackStack().commitAllowingStateLoss();
            }
            getSupportFragmentManager().executePendingTransactions();
            currentFragment = (IBaseView) fragmentToOpen;
        }
    }


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


    public void showLoading() {

        progressBar.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }


    public void hideLoading() {

        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((Fragment) currentFragment).onActivityResult(requestCode, resultCode, data);
    }

    public void hideKeyboard() {

        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }


    @Override
    public void collectData(Map<Integer, Uri> images, String title, String description, List<String> tags) {
        this.images = images;
        this.title = title;
        this.description = description;
        this.tags = tags;
        openFragment(CreateEventSecondView.class, new Bundle());
    }

    @Override
    public void collectData() {
        openFragment(CreateEventThirdView.class, new Bundle());
    }

    @Override
    public void collectData(double priceFrom, int freeSlots) {
        this.priceFrom = priceFrom;
        this.freeSlots = freeSlots;
        openFragment(CreateEventFourthView.class, new Bundle());
    }

    @Override
    public void collectData(long eventTime) {
        this.timeFrom = eventTime;
        showLoading();
        Map<String, RequestBody> params = new HashMap<>();
        File photo = new File(FilePathDescriptor.getPath(this, images.get(1)));
        RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), photo);
        //TODO images needs to be event_image_url
        MultipartBody.Part pic = MultipartBody.Part.createFormData("image", photo.getName(), mainImageBody);
        service.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        service.createEvent(title, description, eventTime, eventTime, 0, User.getInstance().getUserId(),
                User.getInstance().getLattitude(), User.getInstance().getLongtitude(), priceFrom, "Sofia", freeSlots, new Date().getTime(), tags)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createEventResponse -> {

                    uploadMainImage(createEventResponse.getEventId(), pic);

                }, throwable -> {
                    hideLoading();
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImage(long eventId) {
        Integer keyToRemove = new Integer(-1);
        for(Map.Entry<Integer, Uri> entry : images.entrySet()){
            keyToRemove = entry.getKey();
            File photo = new File(FilePathDescriptor.getPath(this, images.get(keyToRemove)));
            RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), photo);
            MultipartBody.Part pic = MultipartBody.Part.createFormData("image", photo.getName(), mainImageBody);
            service.uploadImage(eventId, pic)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aVoid -> {
                        imageCounter++;
                        uploadImage(eventId);
                    }, throwable -> {
                        service.removeHeader("Authorization");
                        hideLoading();
                        Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("Event", "Event").apply();
                        finish();
                    });
            break;
        }
        if(keyToRemove == -1){
            service.removeHeader("Authorization");
            hideLoading();
            Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("Event", "Event").apply();
            finish();
            return;
        }
        images.remove(keyToRemove);
    }

    private void uploadMainImage(long eventId, MultipartBody.Part pic){
        service.uploadMainImage(eventId, pic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> uploadImage(eventId)
                        , throwable -> {
                    service.removeHeader("Authorization");
                    hideLoading();
                    Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
    }
}
