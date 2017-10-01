package com.example.xcomputers.leaps.map;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.networking.feed.event.FeedEventsResponse;
import com.example.networking.feed.event.RealEvent;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.homefeed.activities.HomeFeedActivitiesPresenter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.Subscription;

/**
 * Created by Ivan on 9/25/2017.
 */
@Layout(layoutId = R.layout.event_map_view)
public class GoogleMapView extends BaseView<HomeFeedActivitiesPresenter> implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap mMap;
    private GoogleApiClient mLocationClient;
    private LocationListener mListener;
    private Marker marker;
    private Marker previousMarker;
    private Location currentLocation;
    private Geocoder geocoder;
    private Button searchBtn;
    private LinearLayoutManager layoutManager;
    private RecyclerView eventRecycler;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private List<RealEvent> eventList;
    private MyAdapter adapter;

    private List<String> sectionTitles;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchBtn = (Button) view.findViewById(R.id.search_btn);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        eventRecycler = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        geocoder = new Geocoder(getContext());
        eventList = new ArrayList<>();



        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventRecycler.getContext(),
                layoutManager.getOrientation());
        eventRecycler.addItemDecoration(dividerItemDecoration);

        if(servicesOK()) {
            initMap();
        }
        mLocationClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationClient.connect();

        searchBtn.setOnClickListener(v -> {

        });

    }





    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    public boolean servicesOK() {

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, getActivity(), ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "CAN'T CONNECT TO MAPPING SERVICE", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        gotoLocation(23.3219,42.6977,15);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                showClickedMarker(marker);
                return  false;

            }
        });

    }


    private void showClickedMarker(Marker marker){
        if(previousMarker!=null){
            previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        previousMarker=marker;
        for (int i =0 ; i<eventList.size();i++){
            if (marker.getTitle().equalsIgnoreCase(eventList.get(i).title())){
                layoutManager.scrollToPosition(i);
                break;
            }
        }
    }


    private void gotoLocation(double lan, double lng, float zoom) {
        LatLng latLng = new LatLng(lan, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);

        presenter.getEventsMap(currentLocation.getLatitude(),currentLocation.getLatitude());

       /* if(currentLocation!=null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Log.e("LAT",currentLocation.getLatitude()+"");
            Log.e("LAT",currentLocation.getLongitude()+"");
            if (marker != null) {
                marker.remove();
            }

            List<Address> listAddresses = null;
            try {
                listAddresses = gc.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String state = listAddresses.get(0).getAdminArea();

            MarkerOptions options = new MarkerOptions()
                    .title(state)
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            options.snippet(listAddresses.get(0).getCountryName());

            marker = mMap.addMarker(options);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.moveCamera(update);
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void showAllEvents() throws IOException {
        if (marker != null) {
            marker.remove();
        }

        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());


        List<LatLng> listAddresses = new ArrayList<>();


        for(int i=0;i<eventList.size()-1;i++){
            double latitude = eventList.get(i).latitude();
            double longitude = eventList.get(i).longtitude();
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            listAddresses.add(new LatLng(latitude,longitude));
            MarkerOptions options = new MarkerOptions()
                    .title(eventList.get(i).title())
                    .position(listAddresses.get(i))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            options.snippet(addresses.get(0).getCountryName());

            marker = mMap.addMarker(options);
            mMap.addMarker(options);
        }





    }


    @Override
    protected HomeFeedActivitiesPresenter createPresenter() {
        return new HomeFeedActivitiesPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getEventMapObservable().subscribe(this::followingSuccess));
        subscriptions.add(presenter.getErrorEventMapObservable().subscribe(this::onError));
    }

    private void followingSuccess(FeedEventsResponse realEvent){
        eventList = realEvent.getNearBy();


        Log.e("Tag",eventList.get(0).ownerName()+"");
        Log.e("Tag",eventList.get(1).ownerName()+"");
        Log.e("Tag",eventList.get(2).ownerName()+"");

        adapter = new MyAdapter(getContext(),eventList);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(eventRecycler);
        eventRecycler.setAdapter(adapter);
        eventRecycler.setLayoutManager(layoutManager);

        try {
            showAllEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void onError(Throwable t){

        Toast.makeText(getContext(), "Something whent wrong", Toast.LENGTH_SHORT).show();
    }

}
