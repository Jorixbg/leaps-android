package com.example.xcomputers.leaps.trainer;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.networking.feed.event.Event;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.feed.trainer.Image;
import com.example.networking.profile.EntityService;
import com.example.networking.test.FollowedUser;
import com.example.networking.test.FollowingService;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.IActivity;
import com.example.xcomputers.leaps.base.IBaseView;
import com.example.xcomputers.leaps.event.EventActivity;
import com.example.xcomputers.leaps.event.EventAdapter;
import com.example.xcomputers.leaps.event.EventListingActivity;
import com.example.xcomputers.leaps.homefeed.SearchView;
import com.example.xcomputers.leaps.test.FollowUserView;
import com.example.xcomputers.leaps.utils.CustomTextView;
import com.example.xcomputers.leaps.utils.GlideInstance;
import com.example.xcomputers.leaps.utils.TagView;
import com.google.android.flexbox.FlexboxLayout;

import java.io.Serializable;
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

public class TrainerActivity extends AppCompatActivity implements IActivity {
    public static final String ENTITY_KEY = "TrainerView.ENTITY_KEY";
    public static final String ENTITY_ID_KEY = "TrainerView.ENTITY_ID_KEY";

    private ImageView mainPic;
    private CustomTextView nameTv;
    private TextView usernameTv;
    private TextView eventsNumber;
    private FlexboxLayout tagsContainer;
    private TextView aboutTv;
    private TextView showPastBtn;
    private RecyclerView imagesRecycler;
    private RecyclerView eventsRecycler;
    private TextView addressTv;
    private TextView ageTv;
    private List<Event> pastEvents;
    private ProgressBar loading;
    private RelativeLayout container;
    private EntityService service;
    private RelativeLayout emptyState;
    private Button followingBtn;
    private Entity trainer;
    private FollowingService followingService;
    private TextView followersTV;
    private TextView followingTV;
    private IBaseView currentFragment;
    private FrameLayout followersContainer;
    private NestedScrollView nestedScrollView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainer_view);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        initFields();
        service = new EntityService();
        followingService = new FollowingService();
        findViewById(R.id.profile_list_profile_btn).setVisibility(View.INVISIBLE);

        if(getIntent().getSerializableExtra(ENTITY_KEY)!= null) {
         trainer = (Entity) getIntent().getSerializableExtra(ENTITY_KEY);
            showLoading();
            takeUserInfo();

        }


        followingBtn.setOnClickListener(v->{
                follow();
        });

        followersTV.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("followers", (ArrayList<FollowedUser>) trainer.followers().getFollowers());
            bundle.putParcelableArrayList("following", (ArrayList<FollowedUser>) trainer.followers().getFollowingUsers());
            openFragment(FollowUserView.class, bundle);
            nestedScrollView.setVisibility(View.GONE);
            followersContainer.setVisibility(View.VISIBLE);

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
            boolean clicked = false;
            followersTV.setText(trainer.followers().getFollowers().size() + "");
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
        }

        List<String> imageUrls = new ArrayList<>();
        for(Image image : trainer.images()){
            imageUrls.add(image.getImageUrl());
        }
        if(imageUrls.isEmpty()){
            //Basically we want to set a placeholder if there are no images to load into the images recyclerView otherwise we end up with a white space
            findViewById(R.id.recycler_placeholder).setBackground(ContextCompat.getDrawable(this, R.drawable.event_placeholder));
        }else {
            EventAdapter adapter = new EventAdapter(imageUrls);
            imagesRecycler.setAdapter(adapter);
            imagesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            SnapHelper helper = new LinearSnapHelper();
            imagesRecycler.setOnFlingListener(null);
            helper.attachToRecyclerView(imagesRecycler);
        }
        GlideInstance.loadImageCircle(this, trainer.profileImageUrl(), mainPic, R.drawable.profile_placeholder);

        eventsNumber.setText(String.valueOf(trainer.hosting().size()));
        List<String> tags = trainer.specialities();
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
            UserEventsAdapter eventsAdapter = new UserEventsAdapter(events, event -> {
                Intent intent = new Intent(this, EventActivity.class);
                intent.putExtra(EVENT_KEY, event);
                startActivity(intent);
            });
            eventsRecycler.setAdapter(eventsAdapter);
            eventsRecycler.setLayoutManager(new LinearLayoutManager(this));
        }
        usernameTv.setText(trainer.username());
        nameTv.setText(trainer.firstName() + " " + trainer.lastName());
        ageTv.setText(trainer.age() + "");
        addressTv.setText(trainer.address());
    }

    public void showLoading() {

        loading.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }

    public void hideLoading() {

        loading.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
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
        imagesRecycler = (RecyclerView) findViewById(R.id.trainer_image_horizontall_scroll);
        followingBtn = (Button) findViewById(R.id.follow_trainer_btn);
        followersTV = (TextView) findViewById(R.id.user_profile_followers_tv);
        followingTV = (TextView) findViewById(R.id.user_profile_following_tv);
        followersContainer = (FrameLayout) findViewById(R.id.followers_container);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_trainer_view);
    }

    private void takeUserInfo(){
        service.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        service.getEntity(trainer.userId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    hideLoading();
                    displayTrainer(userResponse);
                    trainer = userResponse;
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
                    takeUserInfo();
                }, throwable -> {
                    followingService.removeHeader("Authorization");
                    unfollow();
                });

    }

    private void unfollow()
    {
        followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization", ""));
        followingService.UnFollowingUser(trainer.userId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    hideLoading();
                }, throwable -> {
                    followingService.removeHeader("Authorization");
                    takeUserInfo();
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
        if (currentFragment != null && currentFragment.getClass().getCanonicalName().equals(name)) {
            return;
        }
        if(currentFragment != null && currentFragment.getClass().getName().equals(SearchView.class.getName())){


        }
        if (getSupportFragmentManager().findFragmentByTag(name) != null) {
            currentFragment = popBackStack(getSupportFragmentManager(), name, arguments);
        } else {
            Fragment fragmentToOpen = Fragment.instantiate(this, name, arguments);

            if (addToBackStack) {

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.followers_container, fragmentToOpen, name)
                            .addToBackStack(name).commitAllowingStateLoss();
            }

            else {
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

        nestedScrollView.setVisibility(View.VISIBLE);
        followersContainer.setVisibility(View.GONE);

        if (currentFragment !=null && currentFragment.onBack()) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();


            if(fm.getBackStackEntryCount()>0) {
                fm.popBackStack();
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



}
