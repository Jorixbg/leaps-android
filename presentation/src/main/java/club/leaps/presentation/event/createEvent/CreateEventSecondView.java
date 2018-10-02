package club.leaps.presentation.event.createEvent;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import club.leaps.networking.feed.event.RealEvent;
import club.leaps.networking.test.AddressLocation;
import club.leaps.networking.test.Coordinates;
import club.leaps.presentation.R;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 16/07/2017.
 */
@Layout(layoutId = R.layout.create_event_second_view)
public class CreateEventSecondView extends BaseView<CoordinatesPresenter> implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private ImageView nextBtn;
    private SearchEditText searchAddress;
    private double latitude;
    private double longitude;
    private String address = "Sofia";
    private TextView showAddress;

    private GoogleMap mMap;
    private GoogleApiClient mLocationClient;
    private Marker marker;
    private Geocoder geocoder;
    private Location currentLocation;
    private AddressLocation finalAddress;
    private AddressLocation finalAddressEdit;
    private RealEvent event;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {



        geocoder = new Geocoder(getContext());
        if(servicesOK()) {
            initMap();
        }
        mLocationClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationClient.connect();


        nextBtn = (ImageView) view.findViewById(R.id.create_event_next_btn);
        showAddress = (TextView) view.findViewById(R.id.create_event_et);
        nextBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(searchAddress.getText().toString()) ){
                searchAddress.setError(getString(R.string.empty_event_address));
                searchAddress.requestFocus();
            }
            else {
                ((ICreateEventActivity) getActivity()).collectData(latitude,longitude,searchAddress.getText()+"");
            }
        });

        searchAddress = (SearchEditText) view.findViewById(R.id.create_event_location_tv);
        searchAddress.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (searchAddress.getText() != null && !searchAddress.equals("")&& !TextUtils.isEmpty(searchAddress.getText().toString())) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        finalAddress = new AddressLocation(searchAddress.getText().toString());
                        presenter.getCoordinates(finalAddress);
                    }
                    return false;
                }
                else {
                    searchAddress.setError(getString(R.string.empty_address));
                }

                return false;
            }
        });


        searchAddress.setKeyImeChangeListener(new SearchEditText.KeyImeChange(){
            @Override
            public void onKeyIme(int keyCode, KeyEvent event)
            {
                if (searchAddress.getText() != null && !searchAddress.equals("") && !TextUtils.isEmpty(searchAddress.getText().toString())) {
                    if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
                        finalAddress = new AddressLocation(searchAddress.getText().toString());
                        presenter.getCoordinates(finalAddress);
                    }
                }
            }});

        event = (RealEvent) getArguments().getSerializable("event");
        if(event !=null){
            loadVies();
        }
    }

    private void loadVies() {
        searchAddress.setText(event.address());
        finalAddressEdit = new AddressLocation(searchAddress.getText().toString());
        presenter.getCoordinates(finalAddressEdit);
    }


    private void findLocation(String address) {
        //mMap.clear();
        LatLng latLng = getLocationFromAddress(getContext(),address );
        showAddress.setText(address);
       // marker = mMap.addMarker(new MarkerOptions().position(latLng).title(address)
       //         .icon(BitmapDescriptorFactory.fromResource(R.drawable.clicked_event_location)));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gotoLocation(latLng.latitude,latLng.longitude,15);
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
            Toast.makeText(getContext(), getString(R.string.map_connection_lost), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);




        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng latLng = map.getCameraPosition().target;
                latitude = latLng.latitude;
                longitude = latLng.longitude;

                presenter.getAddresss(latitude,longitude);

            }
        });


        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {


                return  false;

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                latitude = point.latitude;
                longitude = point.longitude;

                    presenter.getAddresss(latitude,longitude);

            }
        });




        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker2) {



                address = marker2.getTitle().toString();

                Toast.makeText(getContext(),address+" is saved",Toast.LENGTH_SHORT).show();
            }
        });*/




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

        gotoLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),15);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }


    @Override
    protected CoordinatesPresenter createPresenter() {
        return new CoordinatesPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getCoordinatesObservable().subscribe(this::coordinatesSuccess));
        subscriptions.add(presenter.getErrorCoordinatesObservable().subscribe(this::onError));
        subscriptions.add(presenter.getAddressSubject().subscribe(this::addressSuccess));
        subscriptions.add(presenter.getErrorAddressSubject().subscribe(this::onError));
    }

    private void coordinatesSuccess(Coordinates coordinates){
        hideLoading();
        latitude = coordinates.getLatitude();
        longitude = coordinates.getLongitude();
        if(finalAddressEdit!=null){
            address = finalAddressEdit.getAddress();
        }
        else {
            address = finalAddress.getAddress();
        }
        findLocation(address);

    }

    private void addressSuccess(AddressLocation addressLocation){
        hideLoading();
        this.finalAddress = addressLocation;
        StringBuffer sb = new StringBuffer(finalAddress.getAddress());
        int index1 = sb.indexOf(",");
        int index2 = sb.indexOf(",",index1+1);
        sb.delete(index1,index2);

        this.address = sb.toString();
        showAddress.setText(address);

        if(!showAddress.getText().toString().isEmpty()){
            showAddress.setOnClickListener(v->{
                searchAddress.setText(showAddress.getText()+"");
            });

        }
    }

    private void onError(Throwable t){

        hideLoading();
        Toast.makeText(getContext(), getString(R.string.lbl_general_error), Toast.LENGTH_SHORT).show();
    }
}
