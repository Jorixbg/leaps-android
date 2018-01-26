package com.example.xcomputers.leaps.trainer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.networking.feed.event.AttendeeResponse;
import com.example.networking.feed.event.Event;
import com.example.networking.feed.event.FeedEventsService;
import com.example.networking.feed.event.RealEvent;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.feed.trainer.Image;
import com.example.networking.following.FollowingService;
import com.example.networking.profile.EntityService;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.IActivity;
import com.example.xcomputers.leaps.base.IBaseView;
import com.example.xcomputers.leaps.event.EventActivity;
import com.example.xcomputers.leaps.event.EventListingActivity;
import com.example.xcomputers.leaps.event.EventViewPagerAdapter;
import com.example.xcomputers.leaps.follow.FollowUserView;
import com.example.xcomputers.leaps.homefeed.activities.HomeFeedActivitiesPresenter;
import com.example.xcomputers.leaps.test.ChatView;
import com.example.xcomputers.leaps.utils.CustomTextView;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.GlideInstance;
import com.example.xcomputers.leaps.utils.LoginResponseToUserTypeMapper;
import com.example.xcomputers.leaps.utils.TagView;
import com.google.android.flexbox.FlexboxLayout;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.example.xcomputers.leaps.event.EventActivity.EVENT_KEY;
import static com.example.xcomputers.leaps.event.EventListingActivity.EVENT_PAST;

/**
 * Created by xComputers on 10/07/2017.
 */

public class TrainerActivity extends AppCompatActivity implements IActivity, SwipeRefreshLayout.OnRefreshListener  {
    public static final String ENTITY_KEY = "TrainerView.ENTITY_KEY";
    public static final String ENTITY_ID_KEY = "TrainerView.ENTITY_ID_KEY";

    private ImageView mainPic;
    private CustomTextView nameTv;
    private TextView usernameTv;
    private TextView eventsNumber;
    private FlexboxLayout tagsContainer;
    private TextView aboutTv;
    private TextView showPastBtn;
    private ViewPager imagesRecycler;
    private RecyclerView eventsRecycler;
    private TextView addressTv;
    private TextView ageTv;
    private List<Event> pastEvents;
    private List<RealEvent> followingEvents;
    private ProgressBar loading;
    private RelativeLayout container;
    private EntityService service;
    private RelativeLayout emptyState;
    private Button followingBtn;
    private Entity trainer;
    private Entity trainer2;
    private FollowingService followingService;
    private TextView followersTV;
    private TextView followingTV;
    private IBaseView currentFragment;
    private FrameLayout followersContainer;
    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing;
    private CirclePageIndicator pageIndicator;
    private RatingBar rating;
    private TextView ratingCounter;
    private TextView ratingViewers;
    private Button messageBtn;
    private HomeFeedActivitiesPresenter homeFeedPresenter;
    private FeedEventsService followingEventService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainer_view);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        initFields();

        homeFeedPresenter = new HomeFeedActivitiesPresenter();

        swipeRefreshLayout.setOnRefreshListener(this);
        service = new EntityService();
        followingService = new FollowingService();
        followingEventService = new FeedEventsService();

        followingEvents = new ArrayList<>();
        getFollowFutureEvent();
        findViewById(R.id.profile_list_profile_btn).setVisibility(View.INVISIBLE);

        if(getIntent().getSerializableExtra(ENTITY_KEY)!= null) {
         trainer = (Entity) getIntent().getSerializableExtra(ENTITY_KEY);
            showLoading();
            takeUserInfo(trainer);
        }
        else{
            showLoading();
            service.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
            service.getEntity(getIntent().getLongExtra(ENTITY_ID_KEY, -1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userResponse -> {
                        hideLoading();
                        displayTrainer(userResponse);
                        trainer2 = userResponse;
                    }, throwable -> {
                        hideLoading();
                        finish();
                    });
        }


        followingBtn.setOnClickListener(v->{
            if(trainer2 !=null){
                followFromEvent();
            }
            else {
                follow();
            }

        });

        followersTV.setOnClickListener(v-> {

                Bundle bundle = new Bundle();
                if(trainer!=null) {
                    bundle.putSerializable("followers", (ArrayList<AttendeeResponse>) trainer.followers().getFollowers());
                    bundle.putSerializable("following", null);
                    bundle.putSerializable("service", followingService);
                }
                else if(trainer2!=null){
                    bundle.putSerializable("followers", (ArrayList<AttendeeResponse>) trainer2.followers().getFollowers());
                    bundle.putSerializable("following", null);
                    bundle.putSerializable("service", followingService);
                }
                openFragment(FollowUserView.class, bundle,true);
                hideLoadingFollow();

        });

        followingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if(trainer!=null) {
                    bundle.putSerializable("followers", null);
                    bundle.putSerializable("following", (ArrayList<AttendeeResponse>) trainer.followers().getFollowingUsers());
                    bundle.putSerializable("service", followingService);
                }
                else if(trainer2!=null){
                    bundle.putSerializable("followers", null);
                    bundle.putSerializable("following", (ArrayList<AttendeeResponse>) trainer.followers().getFollowingUsers());
                    bundle.putSerializable("service", followingService);
                }
                openFragment(FollowUserView.class, bundle);
                hideLoadingFollow();
            }
        });

        messageBtn.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            if(trainer!=null){
                String userFirstName = trainer.firstName() ;
                String userSecondName = trainer.lastName();
                String fullName = userFirstName +" "+ userSecondName;
                bundle.putSerializable("userId",trainer.userId()+"");
                bundle.putSerializable("userImage",trainer.profileImageUrl());
                bundle.putSerializable("userFullName",fullName);
            }
            else if(trainer2!=null){
                String userFirstName = trainer2.firstName() ;
                String userSecondName = trainer2.lastName();
                String fullName = userFirstName +" "+ userSecondName;
                bundle.putSerializable("userId",trainer2.userId()+"");
                bundle.putSerializable("userImage",trainer2.profileImageUrl());
                bundle.putSerializable("userFullName",fullName);
            }

            openFragment(ChatView.class, bundle);
            hideLoadingFollow();

        });


    }


    private void displayTrainer(Entity trainer){

        if(trainer == null){
            throw new IllegalArgumentException("The trainer is null in " + getClass().getCanonicalName());
        }
        if(trainer.followers().getFollowingUsers().isEmpty() || trainer.followers().getFollowingUsers() == null){
            followingTV.setText("0");
        }
        else {
            followingTV.setText(trainer.followers().getFollowingUsers().size()+"");
        }
        if(trainer.followers().getFollowers().isEmpty() || trainer.followers().getFollowers() == null){
            followersTV.setText("0");
        }
        else {

            followersTV.setText(trainer.followers().getFollowers().size() + "");
        }

            boolean clicked = false;
            for(int i = 0; i<trainer.followers().getFollowers().size(); i++){
                if(User.getInstance().getUserId() == trainer.followers().getFollowers().get(i).getUserId()){
                    followingBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.following_button_clicked));
                    followingBtn.setText("FOLLOWED");
                    followingBtn.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    clicked = true;
                    break;
                }
            }
            if(!clicked){
                followingBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.following_button_unclicked));
                followingBtn.setText("FOLLOW");
                followingBtn.setTextColor(ContextCompat.getColor(this,  R.color.light_blue));
            }



        if(User.getInstance().getUserId() == trainer.userId()){
            followingBtn.setVisibility(View.GONE);
            messageBtn.setVisibility(View.GONE);
        }
        else {
            followingBtn.setVisibility(View.VISIBLE);
            messageBtn.setVisibility(View.VISIBLE);
        }

        List<String> imageUrls = new ArrayList<>();
        for(Image image : trainer.images()){
            imageUrls.add(image.getImageUrl());
        }
        if(imageUrls.isEmpty()){
            //Basically we want to set a placeholder if there are no images to load into the images recyclerView otherwise we end up with a white space
            findViewById(R.id.recycler_placeholder).setBackground(ContextCompat.getDrawable(this, R.drawable.event_placeholder));
        }else {
         //   EventAdapter adapter = new EventAdapter(imageUrls);
            EventViewPagerAdapter adapter = new EventViewPagerAdapter(getApplicationContext(),getSupportFragmentManager(),imageUrls);
            imagesRecycler.setAdapter(adapter);
            pageIndicator.setViewPager(imagesRecycler);
            pageIndicator.setCurrentItem(0);
          //  imagesRecycler.setAdapter(adapter);
          //  imagesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
           // SnapHelper helper = new LinearSnapHelper();
          //  imagesRecycler.setOnFlingListener(null);
          //  helper.attachToRecyclerView(imagesRecycler);
        }
        GlideInstance.loadImageCircle(this, trainer.profileImageUrl(), mainPic, R.drawable.profile_placeholder);

        eventsNumber.setText(String.valueOf(trainer.hosting().size()));
        List<String> tags = trainer.specialities();
        tagsContainer.removeAllViews();
        for (String tag : tags) {
            TagView tagView = new TagView(this);
            tagView.setBackground(ContextCompat.getDrawable(this, R.drawable.event_tag_shape));
            tagView.setTextColor(ContextCompat.getColor(this, R.color.primaryBlue));
            tagView.setText(tag);
            tagsContainer.addView(tagView);
        }

        aboutTv.setText(trainer.longDescription());
        Date now = new Date(System.currentTimeMillis());
        List<Event> events = new ArrayList<>();
        pastEvents = new ArrayList<>();
        for(Event event : trainer.hosting()){
            if(event.date().after(now)){
                events.add(event);
            }else{
                pastEvents.add(event);
            }
        }

        showPastBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventListingActivity.class);
            intent.putExtra(EVENT_PAST, (Serializable) pastEvents);
            startActivity(intent);
        });

        if(events.isEmpty()){
            emptyState.setVisibility(View.VISIBLE);
            eventsRecycler.setVisibility(View.INVISIBLE);
        }else {
            emptyState.setVisibility(View.GONE);
            eventsRecycler.setVisibility(View.VISIBLE);
            UserEventsAdapter eventsAdapter = new UserEventsAdapter(events,followingEvents,homeFeedPresenter, event -> {
                Intent intent = new Intent(this, EventActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(EVENT_KEY, event);
                startActivity(intent);
            });
            eventsRecycler.setAdapter(eventsAdapter);
            eventsRecycler.setLayoutManager(new LinearLayoutManager(this));
            if(swipeRefreshLayout.isRefreshing()){
                isRefreshing = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        }
        usernameTv.setText(trainer.username());
        nameTv.setText(trainer.firstName() + " " + trainer.lastName());
        ageTv.setText(trainer.age() + "");
        addressTv.setText(trainer.address());

        LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_ATOP);

        if(trainer.rating()==0 && trainer.reviews() == 0){
            ratingCounter.setText(0.0f+"");
            ratingViewers.setText("("+0+""+" reviews)");
        }
        else {

            DecimalFormat df = new DecimalFormat("#.0");
            String ratingEvent = String.valueOf(df.format(trainer.rating()));
            rating.setRating(trainer.rating());
            ratingCounter.setText(ratingEvent+"");
            ratingViewers.setText("("+String.valueOf(trainer.reviews()+""+" reviews)"));
        }


    }

    private void initFields(){
        emptyState = (RelativeLayout) findViewById(R.id.empty_state);
        container = (RelativeLayout) findViewById(R.id.container);
        loading = (ProgressBar) findViewById(R.id.loading);
        mainPic = (ImageView) findViewById(R.id.trainer_pic);
        nameTv = (CustomTextView) findViewById(R.id.trainer_name);
        usernameTv = (TextView) findViewById(R.id.trainer_title);
        ageTv = (TextView) findViewById(R.id.trainer_age);
        addressTv = (TextView) findViewById(R.id.trainer_location);
        eventsNumber = (TextView) findViewById(R.id.events_number_tv);
        tagsContainer = (FlexboxLayout) findViewById(R.id.trainer_tags_container);
        aboutTv = (TextView) findViewById(R.id.trainer_about_tv);
        showPastBtn = (TextView) findViewById(R.id.trainer_show_past_btn);
        eventsRecycler = (RecyclerView) findViewById(R.id.trainer_events_recycler);
        imagesRecycler = (ViewPager) findViewById(R.id.trainer_image_horizontall_scroll);
        followingBtn = (Button) findViewById(R.id.follow_trainer_btn);
        followersTV = (TextView) findViewById(R.id.user_profile_followers_tv);
        followingTV = (TextView) findViewById(R.id.user_profile_following_tv);
        followersContainer = (FrameLayout) findViewById(R.id.followers_container);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_trainer_view);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.event_indicator);
        rating = (RatingBar) findViewById(R.id.trainer_rating_bar);
        ratingCounter = (TextView) findViewById(R.id.rating_counter_tv);
        ratingViewers = (TextView) findViewById(R.id.rating_review_counter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeTrainerEvents);
        messageBtn = (Button) findViewById(R.id.trainer_msg_btn);
    }

    private void takeUserInfo(Entity trainer){
        service.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        service.getEntity(trainer.userId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    hideLoading();
                    this.trainer = userResponse;
                    displayTrainer(userResponse);
                }, throwable -> {
                    hideLoading();
                    finish();
                });
    }

    private void follow(){
        followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        followingService.FollowingUser(trainer.userId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    hideLoading();
                    takeUserInfo(trainer);
                    getUser();
                }, throwable -> {
                    followingService.removeHeader("Authorization");
                    unfollow();
                });

    }

    private void unfollow(){
        followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        followingService.UnFollowingUser(trainer.userId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    takeUserInfo(trainer);
                    getUser();
                    hideLoading();
                }, throwable -> {
                    followingService.removeHeader("Authorization");
                });
    }

    private void followFromEvent(){
        followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        followingService.FollowingUser(trainer2.userId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    hideLoading();
                    takeUserInfo(trainer2);
                    getUser();
                }, throwable -> {
                    followingService.removeHeader("Authorization");
                    unfollowFromEvent();
                });

    }

    private void unfollowFromEvent(){
        followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        followingService.UnFollowingUser(trainer2.userId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    getUser();
                    takeUserInfo(trainer2);
                    hideLoading();
                }, throwable -> {
                    followingService.removeHeader("Authorization");
                });
    }

    private void getUser(){
        followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        followingService.getUser(User.getInstance().getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    LoginResponseToUserTypeMapper.map(userResponse);
                    EntityHolder.getInstance().setEntity(userResponse);
                    if(isRefreshing){
                        displayTrainer(userResponse);
                        if(swipeRefreshLayout.isRefreshing()){
                            isRefreshing = false;
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                    followingService.removeHeader("Authorization");
                },(throwable) -> {
                    followingService.removeHeader("Authorization");
                });


    }

    public void getFollowFutureEvent(){
        followingEventService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        followingEventService.getFollowFutureEvent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realEvent->{
                    service.removeHeader("Authorization");
                    followingEvents=realEvent;
                    eventsRecycler.getAdapter().notifyDataSetChanged();
                }, throwable -> {
                    service.removeHeader("Authorization");
                });


    }



    @Override
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
                        .replace(R.id.followers_container, fragmentToOpen, name)
                        .addToBackStack(name).commitAllowingStateLoss();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.followers_container, fragmentToOpen, name)
                        .disallowAddToBackStack().commitAllowingStateLoss();
            }
            getSupportFragmentManager().executePendingTransactions();
            currentFragment = (IBaseView) fragmentToOpen;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showLoadingFollow();
        if(trainer!=null) {
            displayTrainer(trainer);
        }
        /*if (currentFragment !=null && currentFragment.onBack()) {

            return;
        }
        FragmentManager fm = getSupportFragmentManager();

            if(fm.getBackStackEntryCount()>0) {

                fm.popBackStack();

        } else {
            supportFinishAfterTransition();
        }*/
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

    public void showLoadingFollow() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        followersContainer.setVisibility(View.GONE);
    }

    public void hideLoadingFollow() {
        swipeRefreshLayout.setVisibility(View.GONE);
        followersContainer.setVisibility(View.VISIBLE);
    }

    public void showLoading() {

        loading.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }

    public void hideLoading() {

        loading.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    public void onRefresh() {
        showLoading();
        isRefreshing=true;
        getUser();
        hideLoading();
    }
}
