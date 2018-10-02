package club.leaps.presentation.event.createEvent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.FilePathDescriptor;
import club.leaps.presentation.utils.LoginResponseToUserTypeMapper;
import club.leaps.networking.feed.event.CreateEventService;
import club.leaps.networking.feed.event.RealEvent;
import club.leaps.networking.login.LoginService;
import club.leaps.networking.test.ChoosenDate;
import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.IBaseView;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.FilePathDescriptor;
import club.leaps.presentation.utils.LoginResponseToUserTypeMapper;

import java.io.ByteArrayOutputStream;
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
    private double latitude;
    private double longitude;
    private long timeFrom;
    private long timeTo;
    private String address;
    private int freeSlots;
    private List<String> tags;
    private Map<Integer, Uri> images;
    private Map<Integer, Uri> defaultImages;
    private Map<Integer, Uri> imagesToDelete;
    private CreateEventService service;
    private int imageCounter = 1;
    private Bundle editBundle;
    private RealEvent event;
    private LoginService getUserService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultImages = new HashMap<>();
        setContentView(R.layout.activity_create_event);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        service = new CreateEventService();
        getUserService = new LoginService();
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        container = (FrameLayout) findViewById(R.id.create_event_container);
        editBundle = getIntent().getBundleExtra("Edit");
        if(editBundle!=null) {
            event = (RealEvent) editBundle.getSerializable("Event");
        }
        if(event!=null && editBundle !=null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("event",event);
            bundle.putString("Edit","edit");
            openFragment(CreateEventFirstView.class, bundle);
        }
        else {
            openFragment(CreateEventFirstView.class, new Bundle());
        }
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
    public void collectData(Map<Integer, Uri> images,Map<Integer, Uri> imagesToDelete,String title, String description, List<String> tags) {
        this.images = images;
        this.imagesToDelete = imagesToDelete;
        this.title = title;
        this.description = description;
        this.tags = tags;
        if(event!=null && editBundle !=null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("event",event);
            bundle.putString("Edit","edit");
            openFragment(CreateEventSecondView.class, bundle);
        }
        else {
            openFragment(CreateEventSecondView.class, new Bundle());
        }
    }

    @Override
    public void collectData(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        if(event!=null && editBundle !=null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("event",event);
            bundle.putString("Edit","edit");
            openFragment(CreateEventThirdView.class, bundle);
        }
        else {
            openFragment(CreateEventThirdView.class, new Bundle());
        }
    }

    @Override
    public void collectData(double priceFrom, int freeSlots) {
        this.priceFrom = priceFrom;
        this.freeSlots = freeSlots;
        if(event!=null && editBundle !=null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("event",event);
            bundle.putString("Edit","edit");
            openFragment(CreateEventFourthView.class, bundle);
        }
        else {
            openFragment(CreateEventFourthView.class, new Bundle());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void collectData(long eventTime,long endTime) {
        if(editBundle!=null && event!=null){
            long eventId = event.eventId();
            this.timeFrom = eventTime;
            this.timeTo = endTime;
            showLoading();
            Map<String, RequestBody> params = new HashMap<>();
            MultipartBody.Part pic1 = null;
            if(images.get(1)!=null) {
                File photo = new File(FilePathDescriptor.getPath(this, images.get(1)));
                RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), photo);
                //TODO images needs to be event_image_url
                pic1 = MultipartBody.Part.createFormData("image", photo.getName(), mainImageBody);

            }
            service.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));

            MultipartBody.Part finalPic = pic1;
            service.editEvent(eventId,title, description, eventTime, eventTime, 0, User.getInstance().getUserId(),
                    latitude, longitude, priceFrom, address, freeSlots, new Date().getTime(), tags)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createEventResponse -> {
                        if(finalPic!=null) {
                            uploadMainImage(eventId, finalPic);
                        }
                    }, throwable -> {
                        hideLoading();
                        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            this.timeFrom = eventTime;
            this.timeTo = endTime;
            showLoading();
            Map<String, RequestBody> params = new HashMap<>();
            MultipartBody.Part pic ;
            if(images.get(1)!=null) {
                File photo = new File(FilePathDescriptor.getPath(this, images.get(1)));
                RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), photo);
                pic = MultipartBody.Part.createFormData("image", photo.getName(), mainImageBody);
            }
            else {
                Drawable drawable = this.getResources().getDrawable(R.drawable.logotype);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Uri uri = getImageUri(this,bitmap);
                defaultImages.put(1,uri);
                File defaultPhoto = new File(FilePathDescriptor.getPath(this,defaultImages.get(1)));
                RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), defaultPhoto);
                pic = MultipartBody.Part.createFormData("image", defaultPhoto.getName(), mainImageBody);
            }
            //TODO images needs to be event_image_url
            service.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
            service.createEvent(title, description, eventTime, eventTime, 0, User.getInstance().getUserId(),
                    latitude, longitude, priceFrom, address, freeSlots, new Date().getTime(), tags)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createEventResponse -> {
                        uploadMainImage(createEventResponse.getEventId(), pic);
                    }, throwable -> {
                        hideLoading();
                        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void collectData(String frequency, long startTime, long endTime, List<ChoosenDate> dates) {
        //todo editRecurringEvent
            this.timeFrom = startTime;
            this.timeTo = endTime;
            showLoading();
            Map<String, RequestBody> params = new HashMap<>();
            File photo = new File(FilePathDescriptor.getPath(this, images.get(1)));
            RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), photo);
            //TODO images needs to be event_image_url
            MultipartBody.Part pic = MultipartBody.Part.createFormData("image", photo.getName(), mainImageBody);
            service.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
            service.createRecurringEvent(title,true, description,frequency ,dates,startTime, endTime,
                    latitude, longitude, priceFrom, address, freeSlots, tags)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createEventResponse -> {
                        uploadMainImage(createEventResponse.getEventId(), pic);
                    }, throwable -> {
                        hideLoading();
                        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                        for (Map.Entry<Integer, Uri> e : imagesToDelete.entrySet()) {
                            this.getContentResolver().delete(e.getValue(), null, null);

                        }
                    }, throwable -> {
                        service.removeHeader("Authorization");
                        hideLoading();
                        setResult(RESULT_OK);
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("Event", "Event").apply();
                        finish();
                    });
            break;
        }
        if(keyToRemove == -1){
            service.removeHeader("Authorization");
            hideLoading();
            setResult(RESULT_OK);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("Event", "Event").apply();
            finish();
            return;
        }
        images.remove(keyToRemove);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadMainImage(long eventId, MultipartBody.Part pic){
        service.uploadMainImage(eventId, pic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> uploadImage(eventId)
                        , throwable -> {
                    service.removeHeader("Authorization");
                    hideLoading();
                    setResult(RESULT_OK);
                    finish();
                });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getUserService.getUser(User.getInstance().getUserId(),PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    if(userResponse.userId() == User.getInstance().getUserId()){
                        LoginResponseToUserTypeMapper.map(userResponse);
                        EntityHolder.getInstance().setEntity(userResponse);
                    }
                    service.removeHeader("Authorization");
                },(throwable) -> {
                    service.removeHeader("Authorization");
                    hideLoading();
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
