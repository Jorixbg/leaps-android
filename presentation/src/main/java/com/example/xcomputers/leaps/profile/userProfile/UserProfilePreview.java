package com.example.xcomputers.leaps.profile.userProfile;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networking.feed.event.Event;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.login.UserResponse;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.event.EventActivity;
import com.example.xcomputers.leaps.event.EventListingActivity;
import com.example.xcomputers.leaps.profile.ProfileListPresenter;
import com.example.xcomputers.leaps.profile.ProfileListView;
import com.example.xcomputers.leaps.trainer.UserEventsAdapter;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.GlideInstance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscription;

import static com.example.xcomputers.leaps.event.EventActivity.EVENT_KEY;
import static com.example.xcomputers.leaps.event.EventListingActivity.EVENT_PAST;

/**
 * Created by xComputers on 24/07/2017.
 */
@Layout(layoutId = R.layout.user_profile_view)
public class UserProfilePreview extends BaseView<ProfileListPresenter> {

    private ImageView editProfileBtn;
    private ImageView userProfilePic;
    private TextView namesTV;
    private TextView userNameTV;
    private TextView attendedTv;
    private TextView descriptionTv;
    private TextView showPastBtn;
    private TextView followingTv;
    private TextView followersTv;
    private RecyclerView recyclerView;
    private List<Event> pastEvents;
    private Button followUserBtn;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        Entity user = EntityHolder.getInstance().getEntity();
        GlideInstance.loadImageCircle(getContext(), user.profileImageUrl(), userProfilePic, R.drawable.profile_placeholder);
        Date now = new Date(System.currentTimeMillis());
        List<Event> events = new ArrayList<>();
        List<User> users = new ArrayList<>();
        pastEvents = new ArrayList<>();
        for(Event event : user.attending()){
            if(event.date().after(now)){
                events.add(event);
            }else{
                pastEvents.add(event);
            }
        }
        //Todo for loop for users List

        editProfileBtn.setOnClickListener(v -> openFragment(ProfileListView.class, new Bundle(), true));



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
        recyclerView.setAdapter(eventsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        namesTV.setText(user.firstName() + " " + user.lastName());
        userNameTV.setText(user.username());
        attendedTv.setText(String.valueOf(user.attendedEvents()));
        //Todo setText for following and followers
        descriptionTv.setText(user.description());
    }


    private void initFields(View view){
        editProfileBtn = (ImageView) view.findViewById(R.id.edit_profile_btn);
        userProfilePic = (ImageView) view.findViewById(R.id.user_profile_image);
        namesTV = (TextView) view.findViewById(R.id.user_profile_names);
        userNameTV = (TextView) view.findViewById(R.id.user_profile_username);
        attendedTv = (TextView) view.findViewById(R.id.user_profile_attended_tv);
        descriptionTv = (TextView) view.findViewById(R.id.user_profile_description);
        showPastBtn = (TextView) view.findViewById(R.id.trainer_show_past_btn);
        recyclerView = (RecyclerView) view.findViewById(R.id.trainer_events_recycler);
        followingTv = (TextView) view.findViewById(R.id.user_profile_following_tv);
        followersTv = (TextView) view.findViewById(R.id.user_profile_followers_tv);
        followUserBtn = (Button) view.findViewById(R.id.follow_user_btn);

        followUserBtn.setOnClickListener(v->{
            //122 is userId
            presenter.FollowingUser(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""), 122);
        });

    }


    @Override
    protected ProfileListPresenter createPresenter() {
        return new ProfileListPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
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
