package com.example.xcomputers.leaps.splash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.homescreen.HomeScreenView;
import com.example.xcomputers.leaps.welcome.ILoginContainer;
import com.example.xcomputers.leaps.welcome.InsideView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.List;

import rx.Subscription;

import static android.app.Activity.RESULT_OK;
import static com.example.xcomputers.leaps.splash.WalkthroughContainerView.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.example.xcomputers.leaps.utils.Contants.HAS_SKIPPED_WALKTHROUGH;
import static com.example.xcomputers.leaps.utils.Contants.SHARED_PREFS;

/**
 * Created by xComputers on 01/06/2017.
 */
@Layout(layoutId = R.layout.splash_view)
public class SplashView extends BaseView<SplashPresenter> implements InsideView{

    public static final String HAS_SEEN_TUTORIAL = "SplashView.HAS_SEEN_TUTORIAL";
    public static final int REQUEST_CHECK_SETTINGS = 9292;

    private ILoginContainer container;
    private FusedLocationProviderClient mFusedLocationClient;
    private ProgressDialog locationDialog;
    private LocationRequest request;
    private LocationCallback callback;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null){
            new Handler().postDelayed(() -> {
                openFragment(WalkthroughContainerView.class, new Bundle(), false);
            }, 2500);
        }else {
            String auth = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", "");
            String userIdString = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("UserId", "");
            long userId = TextUtils.isEmpty(userIdString) ? 0 : Long.valueOf(userIdString);
            presenter.getTags(auth, userId);
        }
    }

    @Override
    protected SplashPresenter createPresenter() {
        return new SplashPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getTagsObservable()
                .subscribe(aVoid -> checkTutorial(),
                        throwable -> checkTutorial()));
    }

    private void checkTutorial(){
        if(PreferenceManager.getDefaultSharedPreferences(getContext()).contains(HAS_SEEN_TUTORIAL)){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            if(checkLocationPermission()){
                obtainLocationAndOpenNextView();
            }
        }else{
            new Handler().postDelayed(() -> {
                openFragment(WalkthroughContainerView.class, new Bundle(), false);
            }, 2500);
        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.lbl_location_permission_title)
                        .setMessage(R.string.lbl_location_permission_message)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    obtainLocationAndOpenNextView();
                }else{
                    getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(HAS_SKIPPED_WALKTHROUGH, "").apply();
                    openFragment(HomeScreenView.class, new Bundle(), true);
                }
            }
        }
    }

    private void obtainLocationAndOpenNextView() {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            User.getInstance().setLongtitude(location.getLongitude());
                            User.getInstance().setLattitude(location.getLatitude());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(HAS_SKIPPED_WALKTHROUGH, "").apply();
                                    openFragment(HomeScreenView.class, new Bundle(), true);
                                }
                            }, 2500);
                        }else{
                            showLocationProgressDialog();
                            request = new LocationRequest();
                            request.setNumUpdates(1);
                            request.setFastestInterval(100);
                            request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                    .addLocationRequest(request);

                            SettingsClient client = LocationServices.getSettingsClient(getContext());
                            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

                            task.addOnSuccessListener(getActivity(), locationSettingsResponse -> setupLocationCallback());

                            task.addOnFailureListener(getActivity(), e -> {
                                int statusCode = ((ApiException) e).getStatusCode();
                                switch (statusCode) {
                                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                                        // Location settings are not satisfied, but this can be fixed
                                        // by showing the user a dialog.
                                        try {
                                            // Show the dialog by calling startResolutionForResult(),
                                            // and check the result in onActivityResult().
                                            ResolvableApiException resolvable = (ResolvableApiException) e;
                                            resolvable.startResolutionForResult(getActivity(),
                                                    REQUEST_CHECK_SETTINGS);
                                        } catch (IntentSender.SendIntentException sendEx) {
                                            // Ignore the error.
                                            getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(HAS_SKIPPED_WALKTHROUGH, "").apply();
                                            openFragment(HomeScreenView.class, new Bundle(), true);
                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        // Location settings are not satisfied. However, we have no way
                                        // to fix the settings so we won't show the dialog.
                                        getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(HAS_SKIPPED_WALKTHROUGH, "").apply();
                                        openFragment(HomeScreenView.class, new Bundle(), true);
                                        break;
                                }
                            });
                        }
                    });
        } else {
            //Permission denied
            getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(HAS_SKIPPED_WALKTHROUGH, "").apply();
            openFragment(HomeScreenView.class, new Bundle(), true);
        }
    }


    private void setupLocationCallback(){
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null || locationResult.getLocations() == null || locationResult.getLocations().isEmpty()){
                    hideLocationProgressDialog();
                    mFusedLocationClient.removeLocationUpdates(callback);
                    getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(HAS_SKIPPED_WALKTHROUGH, "").apply();
                    openFragment(HomeScreenView.class, new Bundle(), true);
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if(location != null){
                        hideLocationProgressDialog();
                        User.getInstance().setLongtitude(location.getLongitude());
                        User.getInstance().setLattitude(location.getLatitude());
                        getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(HAS_SKIPPED_WALKTHROUGH, "").apply();
                        openFragment(HomeScreenView.class, new Bundle(), true);
                        mFusedLocationClient.removeLocationUpdates(callback);
                        return;
                    }
                }
            }
        };  if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(request, callback, null);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == RESULT_OK){
                setupLocationCallback();
            }
        }
    }

    private void showLocationProgressDialog() {
        if (locationDialog == null) {
            locationDialog = new ProgressDialog(getContext());
            locationDialog.setMessage(getString(R.string.lbl_location_obtainint));
            locationDialog.setIndeterminate(true);
            locationDialog.setCancelable(false);
            locationDialog.setCanceledOnTouchOutside(false);
        }

        locationDialog.show();
    }

    private void hideLocationProgressDialog() {
        if (locationDialog != null && locationDialog.isShowing()) {
            locationDialog.hide();
        }
    }

    @Override
    public void setContainer(ILoginContainer container) {

        this.container = container;
    }
}
