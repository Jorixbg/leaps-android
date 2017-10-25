package com.example.xcomputers.leaps.event;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networking.base.HttpError;
import com.example.networking.feed.event.Attendee;
import com.example.networking.feed.event.AttendeeResponse;
import com.example.networking.feed.event.Event;
import com.example.networking.feed.event.EventAttendService;
import com.example.networking.feed.event.EventImage;
import com.example.networking.feed.event.FeedEventsService;
import com.example.networking.feed.event.RealEvent;
import com.example.networking.following.FollowingService;
import com.example.networking.test.EventRateService;
import com.example.networking.test.EventRatingResponse;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.IActivity;
import com.example.xcomputers.leaps.base.IBaseView;
import com.example.xcomputers.leaps.event.createEvent.CreateEventActivity;
import com.example.xcomputers.leaps.test.EventCommentPage;
import com.example.xcomputers.leaps.test.EventRatingView;
import com.example.xcomputers.leaps.test.FollowUserView;
import com.example.xcomputers.leaps.trainer.TrainerActivity;
import com.example.xcomputers.leaps.utils.CustomTextView;
import com.example.xcomputers.leaps.utils.GlideInstance;
import com.example.xcomputers.leaps.utils.TagView;
import com.example.xcomputers.leaps.welcome.WelcomeActivity;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.flexbox.FlexboxLayout;
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
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.example.xcomputers.leaps.trainer.TrainerActivity.ENTITY_ID_KEY;

/**
 * Created by xComputers on 10/07/2017.
 */

public class EventActivity extends AppCompatActivity implements IActivity, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    public static final String EVENT_KEY = "EventView.EVENT_KEY";
    public static final int EVENT_REQUEST_CODE = 5;
    public static final int LOGIN_REQUEST_CODE = 9593;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private TextView dateTimeTV;
    private ImageView ownerPic;
    private TextView ownerName;
    private CustomTextView eventTitle;
    private FlexboxLayout tagsContainer;
    private TextView aboutTv;
    private TextView eventLocation;
    private ImageView eventLocationMockup;
    private ImageView attendaceMockup;
    private RelativeLayout attendaceContainer;
    private TextView attendeesNumber;
    private TextView freeSlots;
    private ImageView atendeeImage1;
    private ImageView atendeeImage2;
    private ImageView atendeeImage3;
    private ImageView atendeeImage4;
    private ImageView atendeeImage5;
    private ImageView atendeeImage6;
    private ImageView atendeeImage7;
    private ImageView footer;
    private RecyclerView imagesRecycler;
    private RelativeLayout ownerRL;
    private TextView footerTV;
    private Button footerBtn;
    private EventAttendService service;
    private EventRateService commentService;
    private RelativeLayout containerLayout;
    private FrameLayout container;
    private ProgressBar progressBar;
    private Event event;
    private RatingBar rating;
    private IBaseView currentFragment;
    private LinearLayout attendesImages;
    private List<EventRatingResponse> eventComments;
    private Button editBtn;
    private Button deleteBtn;
    private ImageView followBtn;
    private ImageView shareBtn;
    private boolean isClicked;
    private FollowingService followingService;
    private FeedEventsService feedEventsService;
    private List<RealEvent> realEventList;
    private GoogleMap mMap;
    private GoogleApiClient mLocationClient;
    private Marker marker;
    private Geocoder geocoder;
    boolean hasCommeted;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);
        followingService = new FollowingService();
        feedEventsService = new FeedEventsService();
        commentService = new EventRateService();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        followingFutureEvent();
        initViews();
        service = new EventAttendService();
        event = (Event) getIntent().getSerializableExtra(EVENT_KEY);
        eventComments = new ArrayList<>();
        getEvent();
        getComment();
        createMap();
        //initRating();

        attendesImages.setOnClickListener(v->{
            Bundle bundle = new Bundle();
                if(event.attendees().getOthers()!=null) {
                    bundle.putSerializable("attendeeOther", (Serializable) event.attendees().getOthers());
                    bundle.putString("event","event");
                }
                if(event.attendees().getFollowed() !=null) {
                    bundle.putSerializable("attendeeFollowed", (Serializable) event.attendees().getFollowed());
                    bundle.putString("event","event");
                }

            openFragment(FollowUserView.class, bundle);
            hideLoading();
        });

        followBtn.setOnClickListener(v->{
            followingEventFromActivity();
        });





        shareBtn.setOnClickListener(v->{
            String imgUrl = event.imageUrl();

            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentTitle(event.title())
                    .setContentDescription(event.description())
                    .setContentUrl(Uri.parse("http://ec2-35-157-240-40.eu-central-1.compute.amazonaws.com:8888/"+imgUrl))
                    .build();

            ShareDialog.show(this, shareLinkContent);
        });


        String[] tags = event.tags();
        for (String tag : tags) {
            TagView tagView = new TagView(this);
            tagView.setBackground(ContextCompat.getDrawable(this, R.drawable.event_tag_shape));
            tagView.setTextColor(ContextCompat.getColor(this, R.color.primaryBlue));
            tagView.setText(tag);
            tagsContainer.addView(tagView);
        }


        if(!PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", "").isEmpty()) {
            for (int i = 0; i < User.getInstance().getHostingEvents().size(); i++) {
                if (event.eventId() == User.getInstance().getHostingEvents().get(i).eventId()) {
                    editBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }

        editBtn.setOnClickListener(v->{
            editEvent();
        });

        deleteBtn.setOnClickListener(v->{
            deleteEvent();
            onBackPressed();

        });

    }


    private void createMap(){

        geocoder = new Geocoder(this);

        if(servicesOK()) {
            initMap();
        }

        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationClient.connect();

    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.event_map);
        mapFragment.getMapAsync(this);

    }

    public boolean servicesOK() {

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "CAN'T CONNECT TO MAPPING SERVICE", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {


                return  false;

            }
        });
    }


    private void gotoLocation(double lan, double lng, float zoom) {
        LatLng latLng = new LatLng(lan, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        gotoLocation(event.latitude(),event.longtitude(),12);


            LatLng latLng = new LatLng(event.latitude(), event.longtitude());
            if (marker != null) {
                marker.remove();
            }


            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            marker = mMap.addMarker(options);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.moveCamera(update);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void followingEventFromActivity(){

        followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        followingService.FollowingEvent(event.eventId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent->{
                    service.removeHeader("Authorization");

                    followingFutureEvent();
                    Log.e("Tags2","Follow");
                }, throwable -> {
                    service.removeHeader("Authorization");
                    unfollowingEventFromActivity();
                    Log.e("Tags2","Error Follow");
                });



    }

    private void unfollowingEventFromActivity(){
        followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        followingService.UnfollowingEvent(event.eventId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(realEvent->{
                        service.removeHeader("Authorization");
                        Log.e("Tags2","UNFollow");
                    }, throwable -> {
                        service.removeHeader("Authorization");
                        followingFutureEvent();
                        Log.e("Tags2","Error UNFollow");
                    });

    }

    private void followingFutureEvent(){
        feedEventsService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        feedEventsService.getFollowFutureEvent()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(realEvent->{
                        service.removeHeader("Authorization");
                        realEventList=realEvent;
                        checkFollowingEvent();
                    }, throwable -> {
                        service.removeHeader("Authorization");
                    });



    }

    private void checkFollowingEvent(){
        boolean clicked = false;
        if(realEventList !=null) {
            for (int i = 0; i < realEventList.size(); i++) {
                if (event.eventId() == realEventList.get(i).eventId()) {
                    followBtn.setImageResource(R.drawable.follow_event);
                    clicked = true;
                    break;
                }
            }
        }
        if (!clicked) {
            followBtn.setImageResource(R.drawable.unfollow_event);
        }
    }

    private void initRating(){
        if(hasCommeted){
            rating.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("eventObj", event);
                    bundle.putSerializable("eventComment", (ArrayList<EventRatingResponse>)eventComments);
                    openFragment(EventCommentPage.class, bundle);
                    hideLoading();
                    return false;
                }
            });

        }
        else {
            rating.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("eventObj", event);
                    openFragment(EventRatingView.class, bundle);
                    hideLoading();
                    return false;
                }
            });
        }
    }

    private boolean searchForUser() {
        for(int i =0;i<eventComments.size();i++){
            if(eventComments.get(i).getUserId() == User.getInstance().getUserId()){
                return true;
            }
        }



        return false;

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showLoading();
        getEvent();
    }

    private void setupEvent(Event event) {

        if(event.date().before(new Date(System.currentTimeMillis()))){
            findViewById(R.id.attend_layout).setVisibility(View.INVISIBLE);
        }

        List<String> images = new ArrayList<>();
        images.add(event.imageUrl());
        List<String> imageUrls = new ArrayList<>();
        for (EventImage image : event.images()) {
            imageUrls.add(image.getImageUrl());
        }
        images.addAll(imageUrls);


        setupFooter();

        //new Code


        ownerRL.setOnClickListener(v -> {
            if(PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", "").isEmpty()){
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                return;
            }
            Intent intent = new Intent(this, TrainerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ENTITY_ID_KEY, event.ownerId());
            startActivity(intent);
        });

        EventAdapter adapter = new EventAdapter(images);
        imagesRecycler.setAdapter(adapter);
        imagesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper helper = new LinearSnapHelper();
        imagesRecycler.setOnFlingListener(null);
        helper.attachToRecyclerView(imagesRecycler);
        GlideInstance.loadImageCircle(this, event.ownerPicUrl(), ownerPic, R.drawable.profile_placeholder);
        eventLocation.setText(event.address());
        eventTitle.setText(event.title());
        ownerName.setText(event.ownerName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.date());
        DateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateTimeTV.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                + ", "
                + calendar.get(Calendar.DATE)
                + " "
                + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                + ", "
                + format.format(event.timeFrom())
                + " - "
                + format.format(event.timeTo()));
        aboutTv.setText(event.description());

        if (PreferenceManager.getDefaultSharedPreferences(this).contains(getString(R.string.auth_label))) {
            attendaceMockup.setVisibility(View.GONE);
            eventLocationMockup.setVisibility(View.GONE);
            attendaceContainer.setVisibility(View.VISIBLE);
            Attendee atendees = event.attendees();
            attendeesNumber.setText(atendees.getAllUsers().size() + "");
            freeSlots.setText(event.freeSlots() + "");
            for (int i = 0; i < atendees.getAllUsers().size(); i++) {
                if (i == 7) {
                    break;
                }
                AttendeeResponse attendee = atendees.getAllUsers().get(i);
                if (attendee == null) {
                    continue;
                }
                GlideInstance.loadImageCircle(this, attendee.getProfileImage(), getImageForAttendee(i), R.drawable.profile_placeholder);
            }
        } else {
            attendaceMockup.setVisibility(View.VISIBLE);
            eventLocationMockup.setVisibility(View.VISIBLE);
            attendaceContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void setupFooter() {

        if (PreferenceManager.getDefaultSharedPreferences(this).contains(getString(R.string.auth_label))) {
            boolean attending = false;
            for (int i = 0; i < event.attendees().getAllUsers().size(); i++) {
                AttendeeResponse attendee = event.attendees().getAllUsers().get(i);
                if (User.getInstance().getUserId() == attendee.getUserId()) {
                    attending = true;
                    break;
                }
            }
            footerTV.setText(attending ? getString(R.string.lbl_reject_spot) : getString(R.string.lbl_get_spot));
            footerBtn.setText(attending ? getString(R.string.lbl_reject) : getString(R.string.lbl_attend));
            final boolean finalAttending = attending;
            footerBtn.setOnClickListener(v -> {
                if (finalAttending) {
                    unattendEvent();
                } else {
                    attendEvent();
                }
            });
        } else {
            footerTV.setText(R.string.lbl_try_free);
            footerBtn.setText(R.string.lbl_sign_up);
            footerBtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivityForResult(intent, EVENT_REQUEST_CODE);
            });
        }
    }


    private void initViews() {
        ownerRL = (RelativeLayout) findViewById(R.id.ownerLL);
        dateTimeTV = (TextView) findViewById(R.id.event_date_time_text);
        ownerPic = (ImageView) findViewById(R.id.event_owner_pic);
        ownerName = (TextView) findViewById(R.id.event_owner_name);
        eventTitle = (CustomTextView) findViewById(R.id.event_title);
        tagsContainer = (FlexboxLayout) findViewById(R.id.event_tags_container);
        aboutTv = (TextView) findViewById(R.id.event_about_tv);
        eventLocation = (TextView) findViewById(R.id.event_location_tv);
        eventLocationMockup = (ImageView) findViewById(R.id.event_location_mockup_image);
        attendaceMockup = (ImageView) findViewById(R.id.event_attendance_mockup_image);
        attendaceContainer = (RelativeLayout) findViewById(R.id.attendance_logged_container);
        attendeesNumber = (TextView) findViewById(R.id.event_people_number);
        freeSlots = (TextView) findViewById(R.id.event_free_slots_number);
        atendeeImage1 = (ImageView) findViewById(R.id.event_attendee1);
        atendeeImage2 = (ImageView) findViewById(R.id.event_attendee2);
        atendeeImage3 = (ImageView) findViewById(R.id.event_attendee3);
        atendeeImage4 = (ImageView) findViewById(R.id.event_attendee4);
        atendeeImage5 = (ImageView) findViewById(R.id.event_attendee5);
        atendeeImage6 = (ImageView) findViewById(R.id.event_attendee6);
        atendeeImage7 = (ImageView) findViewById(R.id.event_attendee7);
        footer = (ImageView) findViewById(R.id.event_action_footer);
        imagesRecycler = (RecyclerView) findViewById(R.id.event_image_horizontall_scroll);
        footerBtn = (Button) findViewById(R.id.event_footer_action_button);
        footerTV = (TextView) findViewById(R.id.event_footer_text_view);
        containerLayout = (RelativeLayout) findViewById(R.id.event_container);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        container = (FrameLayout) findViewById(R.id.container);
        attendesImages = (LinearLayout) findViewById(R.id.event_attendance_image_container);
        editBtn = (Button) findViewById(R.id.edit_button_event);
        deleteBtn = (Button) findViewById(R.id.delete_button_event);
        followBtn = (ImageView) findViewById(R.id.feed_recycler_follow_button);
        shareBtn = (ImageView) findViewById(R.id.feed_recycler_share_button);
    }



    private ImageView getImageForAttendee(int index) {
        switch (index) {
            case 0:
            default:
                return atendeeImage1;
            case 1:
                return atendeeImage2;
            case 2:
                return atendeeImage3;
            case 3:
                return atendeeImage4;
            case 4:
                return atendeeImage5;
            case 5:
                return atendeeImage6;
            case 6:
                return atendeeImage7;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ownerRL = null;
        dateTimeTV = null;
        imagesRecycler = null;
        ownerPic = null;
        ownerName = null;
        eventTitle = null;
        tagsContainer = null;
        aboutTv = null;
        eventLocation = null;
        eventLocationMockup = null;
        attendaceMockup = null;
        attendaceContainer = null;
        attendeesNumber = null;
        freeSlots = null;
        atendeeImage1 = null;
        atendeeImage2 = null;
        atendeeImage3 = null;
        atendeeImage4 = null;
        atendeeImage5 = null;
        atendeeImage6 = null;
        atendeeImage7 = null;
        footer = null;
        footerBtn = null;
        footerTV = null;
        containerLayout = null;
        progressBar = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == EVENT_REQUEST_CODE) {
                setupEvent(event);
            }else if(requestCode == LOGIN_REQUEST_CODE){
                Intent intent = new Intent(this, TrainerActivity.class);
                intent.putExtra(ENTITY_ID_KEY, event.ownerId());
                startActivity(intent);
            }
        }
        if(currentFragment != null){
            ((Fragment)currentFragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getEvent(){
        hideLoading();
        service.getEventId(event.eventId(),
                PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.auth_label), ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent -> {
                    service.removeHeader("Authorization");
                    this.event = realEvent.get(0);
                    setupEvent(event);
                    showLoading();
                }, throwable -> {
                   Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    service.removeHeader(getString(R.string.auth_label));
                    showLoading();
                });
    }


    private void editEvent(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Event",event);
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.putExtra("Edit", bundle);
        startActivity(intent);
    }


    private void deleteEvent(){
        String auth =  PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.auth_label), "");
        service.addHeader("Authorization",auth);
        commentService.deleteEvent(event.eventId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventRatingResponses -> {
                    service.removeHeader("Authorization");
                }, throwable -> {
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    service.removeHeader(getString(R.string.auth_label));
                });


    }


    private void getComment(){
        String auth =  PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.auth_label), "");
        service.addHeader("Authorization",auth);
        commentService.getComment(event.eventId(),1,20)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventRatingResponses -> {
                    eventComments = eventRatingResponses;
                    hasCommeted = searchForUser();
                    initRating();
                    service.removeHeader("Authorization");
                }, throwable -> {
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    service.removeHeader(getString(R.string.auth_label));
                });
    }

    private void attendEvent() {
        service.attendEvent(User.getInstance().getUserId(),
                event.eventId(),
                PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.auth_label), ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent -> {
                    service.removeHeader(getString(R.string.auth_label));
                    Toast.makeText(this, R.string.success_lbl, Toast.LENGTH_SHORT).show();
                    this.event = realEvent;
                    this.tagsContainer.removeAllViewsInLayout();
                    getEvent();
                }, throwable -> {
                    if (throwable instanceof HttpException) {
                        HttpException exception = (HttpException) throwable;
                        Response response = exception.response();
                        Converter<ResponseBody, HttpError> converter = (Converter<ResponseBody, HttpError>) GsonConverterFactory.create()
                                .responseBodyConverter(HttpError.class, new Annotation[0], null);
                        HttpError error1 = null;
                        try {
                            error1 = converter.convert(response.errorBody());
                            Toast.makeText(this, error1.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    service.removeHeader(getString(R.string.auth_label));
                });
    }

    private void clearAttendees(){
        atendeeImage1.setImageDrawable(null);
        atendeeImage2.setImageDrawable(null);
        atendeeImage3.setImageDrawable(null);
        atendeeImage4.setImageDrawable(null);
        atendeeImage5.setImageDrawable(null);
        atendeeImage6.setImageDrawable(null);
        atendeeImage7.setImageDrawable(null);
    }

    private void unattendEvent() {
        clearAttendees();
        service.unattendEvent(User.getInstance().getUserId(),
                event.eventId(),
                PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.auth_label), ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent -> {
                    List<AttendeeResponse> list = new ArrayList<AttendeeResponse>();
                    for(int i = 0; i < event.attendees().getAllUsers().size() ; i++){
                        AttendeeResponse attendee = event.attendees().getAllUsers().get(i);
                        if(attendee == null){
                            continue;
                        }
                        if(attendee.getUserId() != User.getInstance().getUserId()){
                            list.add(attendee);
                        }
                    }
                    event.attendees().setAllUsers(list);
                    service.removeHeader(getString(R.string.auth_label));
                    this.tagsContainer.removeAllViewsInLayout();
                    getEvent();
                }, throwable -> {
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    service.removeHeader(getString(R.string.auth_label));
                });
    }

    public <View extends IBaseView> void openFragment(Class<View> clazz, Bundle arguments) {

        openFragment(clazz, arguments, true);
    }

    @Override
    public <View extends IBaseView> void openFragment(Class<View> clazz, Bundle arguments, boolean addToBackStack) {

        String name = clazz.getCanonicalName();
        if (name == null) {
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

    protected IBaseView popBackStack(android.support.v4.app.FragmentManager fragmentManager, String fragmentViewName, Bundle args) {

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentViewName);
        if (fragment != null && fragment.getArguments() != null && args != null) {
            fragment.getArguments().putAll(args);
        }
        fragmentManager.popBackStack(fragmentViewName, 0);
        fragmentManager.executePendingTransactions();
        return (IBaseView) fragment;
    }

    public void showLoading() {
        containerLayout.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }

    public void hideLoading() {
        containerLayout.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }


}
