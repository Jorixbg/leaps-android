package com.example.xcomputers.leaps.profile.trainerProfile;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networking.feed.event.Event;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.feed.trainer.Image;
import com.example.networking.login.UserResponse;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.event.EventActivity;
import com.example.xcomputers.leaps.event.EventAdapter;
import com.example.xcomputers.leaps.event.EventListingActivity;
import com.example.xcomputers.leaps.profile.ProfileListPresenter;
import com.example.xcomputers.leaps.profile.ProfileListView;
import com.example.xcomputers.leaps.trainer.UserEventsAdapter;
import com.example.xcomputers.leaps.utils.CustomTextView;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.GlideInstance;
import com.example.xcomputers.leaps.utils.TagView;
import com.google.android.flexbox.FlexboxLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscription;

import static com.example.xcomputers.leaps.event.EventActivity.EVENT_KEY;
import static com.example.xcomputers.leaps.event.EventListingActivity.EVENT_PAST;

/**
 * Created by xComputers on 23/07/2017.
 */
@Layout(layoutId = R.layout.trainer_view)
public class TrainerProfilePreview extends BaseView<ProfileListPresenter> {

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
    private ImageView editProfileBtn;
    private Button followBtn;
    private List<Event> pastEvents;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        Entity trainer = EntityHolder.getInstance().getEntity();
        if(trainer == null){
            throw new IllegalArgumentException("The trainer is null in " + getClass().getCanonicalName());
        }
        editProfileBtn.setOnClickListener(v-> openFragment(ProfileListView.class, new Bundle(), true));
        List<String> imageUrls = new ArrayList<>();
        for(Image image : trainer.images()){
            imageUrls.add(image.getImageUrl());
        }
        if(imageUrls.isEmpty()){
            view.findViewById(R.id.recycler_placeholder).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_placeholder));
        }else {
            EventAdapter adapter = new EventAdapter(imageUrls);
            imagesRecycler.setAdapter(adapter);
            imagesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        GlideInstance.loadImageCircle(getContext(), trainer.profileImageUrl(), mainPic, R.drawable.event_placeholder);
        eventsNumber.setText(String.valueOf(trainer.hosting().size()));
        List<String> tags = trainer.specialities();
        for (String tag : tags) {
            TagView tagView = new TagView(getContext());
            tagView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_tag_shape));
            tagView.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
            tagView.setText(tag);
            tagsContainer.addView(tagView);
        }

        aboutTv.setText(trainer.longDescription());
        Date now = new Date(System.currentTimeMillis());
        List<Event> events = new ArrayList<>();
        pastEvents = new ArrayList<>();
        for(Event event : trainer.attending()){
            if(event.date().after(now)){
                events.add(event);
            }else{
                pastEvents.add(event);
            }
        }

        showPastBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventListingActivity.class);
            intent.putExtra(EVENT_PAST, (Serializable) pastEvents);
            startActivity(intent);
        });
        UserEventsAdapter eventsAdapter = new UserEventsAdapter(events, event -> {
            Intent intent = new Intent(getContext(), EventActivity.class);
            intent.putExtra(EVENT_KEY, event);
            startActivity(intent);
        });

        followBtn.setOnClickListener(v -> followSetup());

        eventsRecycler.setAdapter(eventsAdapter);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        usernameTv.setText(trainer.username());
        nameTv.setText(trainer.firstName() + " " + trainer.lastName());
        ageTv.setText(trainer.age() + "");
        addressTv.setText(trainer.address());
    }

    private void followSetup(){
        presenter.FollowingUser(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""),
                User.getInstance().getUserId());

    }

    private void initFields(View view){
        editProfileBtn = (ImageView) view.findViewById(R.id.profile_list_profile_btn);
        mainPic = (ImageView) view.findViewById(R.id.trainer_pic);
        nameTv = (CustomTextView) view.findViewById(R.id.trainer_name);
        usernameTv = (TextView) view.findViewById(R.id.trainer_title);
        ageTv = (TextView) view.findViewById(R.id.trainer_age);
        addressTv = (TextView) view.findViewById(R.id.trainer_location);
        eventsNumber = (TextView) view.findViewById(R.id.events_number_tv);
        tagsContainer = (FlexboxLayout) view.findViewById(R.id.trainer_tags_container);
        aboutTv = (TextView) view.findViewById(R.id.trainer_about_tv);
        showPastBtn = (TextView) view.findViewById(R.id.trainer_show_past_btn);
        eventsRecycler = (RecyclerView) view.findViewById(R.id.trainer_events_recycler);
        imagesRecycler = (RecyclerView) view.findViewById(R.id.trainer_image_horizontall_scroll);
        followBtn = (Button) view.findViewById(R.id.follow_trainer_btn);
    }

    @Override
    protected ProfileListPresenter createPresenter() {
        return new ProfileListPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getUserObservable().subscribe(aVoid -> {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("UserId", String.valueOf(User.getInstance().getUserId())).apply();
            hideLoading();
        }));
        subscriptions.add(presenter.getErrorObservable().subscribe(aVoid -> {
            hideLoading();
        }));


        subscriptions.add(presenter.getFollowingUserSubject().subscribe(this::userFollowingSuccess));
        subscriptions.add(presenter.getErrorFollowingUserSubject().subscribe(this::onError));
    }

    private void userFollowingSuccess(UserResponse userResponse){
        hideLoading();
    }


    private void onError(Throwable t){
        hideLoading();
        Toast.makeText(getContext(), getString(R.string.lbl_general_error), Toast.LENGTH_SHORT).show();
    }


}


