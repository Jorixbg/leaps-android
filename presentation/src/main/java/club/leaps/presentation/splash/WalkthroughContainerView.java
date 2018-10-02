package club.leaps.presentation.splash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.homescreen.HomeScreenView;
import club.leaps.presentation.welcome.ILoginContainer;
import club.leaps.presentation.welcome.InsideView;
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
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import club.leaps.presentation.homescreen.HomeScreenView;
import club.leaps.presentation.welcome.ILoginContainer;
import club.leaps.presentation.welcome.InsideView;
import rx.Subscription;

import static android.app.Activity.RESULT_OK;
import static club.leaps.presentation.utils.Contants.HAS_SKIPPED_WALKTHROUGH;
import static club.leaps.presentation.utils.Contants.SHARED_PREFS;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by xComputers on 01/06/2017.
 */
@Layout(layoutId = R.layout.walkthrough_container)
public class WalkthroughContainerView extends BaseView<EmptyPresenter> implements IWalkthroughContainer, InsideView {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int REQUEST_CHECK_SETTINGS = 6932;


    private ViewPager walkthroughPager;
    private ScreenSlidePagerAdapter adapter;
    private ILoginContainer container;
    private LocationRequest request;
    private LocationCallback callback;
    private ProgressDialog locationDialog;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.walkthrough_container,container,false);
        walkthroughPager = (ViewPager) root.findViewById(R.id.walkthrough_pager);
        adapter = new ScreenSlidePagerAdapter(getApplicationContext(),getChildFragmentManager());
        CirclePageIndicator pageIndicator = (CirclePageIndicator) root.findViewById(R.id.indicator);
        walkthroughPager.setAdapter(adapter);
        pageIndicator.setViewPager(walkthroughPager);
        pageIndicator.setCurrentItem(0);
        pageIndicator.setFillColor(ContextCompat.getColor(getContext(), R.color.full_indicator));
        pageIndicator.setPageColor(ContextCompat.getColor(getContext(), R.color.empty_indicator));
        pageIndicator.setRadius((int) (4 * Resources.getSystem().getDisplayMetrics().density));
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    @Override
    public boolean onBack() {
        if (walkthroughPager.getCurrentItem() == 0) {
            getActivity().finish();
        } else {
            walkthroughPager.setCurrentItem(walkthroughPager.getCurrentItem() - 1, true);
        }
        return true;
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
    }

    @Override
    public void setPage(int page) {
        walkthroughPager.setCurrentItem(page, true);
    }

    @Override
    public void openWelcomeScreen() {
        if (checkLocationPermission()) {
            obtainLocationAndOpenNextView();
        }
    }

    @Override
    public void setContainer(ILoginContainer container) {
        this.container = container;
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
                        .setTitle(getView().getContext().getString(R.string.lbl_location_permission_title))
                        .setMessage(getView().getContext().getString(R.string.lbl_location_permission_message))
                        .setPositiveButton(getView().getContext().getString(R.string.ok), (dialogInterface, i) -> {
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
                    return;
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
                            getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(HAS_SKIPPED_WALKTHROUGH, "").apply();
                            openFragment(HomeScreenView.class, new Bundle(), true);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == RESULT_OK){
                setupLocationCallback();
            }
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

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        private Context context;

        private static final int NUM_PAGES = 5;
        private static final int FIRST_PAGE = 0;
        private static final int SECOND_PAGE = 1;
        private static final int THIRD_PAGE = 2;
        private static final int FOURTH_PAGE = 3;
        private static final int FIFTH_PAGE = 4;

        ScreenSlidePagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FIRST_PAGE:
                default:
                    return new WalkthroughFirstScreen();
                case SECOND_PAGE:
                    return new WalkThroughSecondScreen();
                case THIRD_PAGE:
                    return new WalkthroughThirdScreen();
                case FOURTH_PAGE:
                    return new WalkthroughFourthScreen();
                case FIFTH_PAGE:
                    return new WalkthroughFifthScreen();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
