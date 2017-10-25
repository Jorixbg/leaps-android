package com.example.xcomputers.leaps.profile.userProfile;

import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networking.feed.event.AttendeeResponse;
import com.example.networking.feed.event.Event;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.login.UserResponse;
import com.example.xcomputers.leaps.MainActivity;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.event.EventActivity;
import com.example.xcomputers.leaps.event.EventListingActivity;
import com.example.xcomputers.leaps.profile.ProfileListPresenter;
import com.example.xcomputers.leaps.profile.SettingsView;
import com.example.xcomputers.leaps.profile.becomeTrainer.BecomeTrainerActivity;
import com.example.xcomputers.leaps.profile.trainerProfile.TrainerProfileEditView;
import com.example.xcomputers.leaps.test.FollowUserView;
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

    private static final int BECOME_TRAINER_REQUEST = 4567;

    private ImageView userProfilePic;
    private TextView namesTV;
    private TextView userNameTV;
    private RecyclerView recyclerView;
    private TextView followingTv;
    private TextView followersTv;
    private Button followingBtn;
    private Entity user;
    private TextView settings;
    private TextView viewTutorial;
    private TextView logOut;
    private TextView editProfile;
    private Button becomeTrainerbtn;
    private RelativeLayout becomeTrainerRl;
    private TextView attendedTv;
    private TextView descriptionTv;
    private TextView showPastBtn;
    private List<Event> pastEvents;
    private RelativeLayout userViewRL;
    private RelativeLayout userViewSettingsRL;
    private TextView aboutMeTxt;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        user = EntityHolder.getInstance().getEntity();
        if(getArguments().getSerializable("user") !=null) {
            user = (Entity) getArguments().getSerializable("user");

            userViewRL.setVisibility(View.VISIBLE);
            userViewSettingsRL.setVisibility(View.GONE);
            becomeTrainerRl.setVisibility(View.GONE);

        }
        if (user != null) {
            initUser(user);
        }


        settings.setOnClickListener(v -> openFragment(SettingsView.class, new Bundle(), true));
        becomeTrainerbtn.setOnClickListener(v -> getActivity().startActivityForResult(new Intent(getContext(), BecomeTrainerActivity.class), BECOME_TRAINER_REQUEST));
        Class editClass = User.getInstance().isTrainer() ? TrainerProfileEditView.class : UserEditProfileView.class;

        editProfile.setOnClickListener(v-> {
            openFragment(editClass, new Bundle(), true);
        });

        logOut.setOnClickListener(v -> {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().remove("Authorization").apply();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().remove("UserId").apply();
            User.clear();
            EntityHolder.clear();
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        });



    }

    private void initFields(View view){
        aboutMeTxt = (TextView) view.findViewById(R.id.about_me_txt);
        userViewSettingsRL = (RelativeLayout) view.findViewById(R.id.profile_view_settings_rl);
        userViewRL = (RelativeLayout) view.findViewById(R.id.user_profile_view_rl);
        editProfile = (TextView) view.findViewById(R.id.edit_profile_txt); 
        settings = (TextView) view.findViewById(R.id.profile_list_settings);
        viewTutorial = (TextView) view.findViewById(R.id.profile_list_tutorial);
        logOut = (TextView) view.findViewById(R.id.profile_list_log_out);
        becomeTrainerbtn = (Button) view.findViewById(R.id.profile_listing_become_trainer_btn);
        becomeTrainerbtn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Typoforge Studio - Cervo-Medium.otf"));
        becomeTrainerRl = (RelativeLayout) view.findViewById(R.id.profile_list_become_trainer_rl);
        userProfilePic = (ImageView) view.findViewById(R.id.user_profile_image);
        namesTV = (TextView) view.findViewById(R.id.user_profile_names);
        userNameTV = (TextView) view.findViewById(R.id.user_profile_username);
        attendedTv = (TextView) view.findViewById(R.id.user_profile_attended_tv);
        descriptionTv = (TextView) view.findViewById(R.id.user_profile_description);
        showPastBtn = (TextView) view.findViewById(R.id.trainer_show_past_btn);
        recyclerView = (RecyclerView) view.findViewById(R.id.trainer_events_recycler);
        followingTv = (TextView) view.findViewById(R.id.user_profile_following_tv);
        followersTv = (TextView) view.findViewById(R.id.user_profile_followers_tv);
        followingBtn = (Button) view.findViewById(R.id.follow_user_btn);

        followingBtn.setOnClickListener(v->{
            presenter.FollowingUser(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""), user.userId());
            presenter.getUser(User.getInstance().getUserId(),PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
        });

    }


    private void initUser(Entity initUser){
        if(User.getInstance().getUserId() == initUser.userId()){
            followingBtn.setVisibility(View.GONE);
            userViewRL.setVisibility(View.GONE);
            userViewSettingsRL.setVisibility(View.VISIBLE);
            becomeTrainerRl.setVisibility(View.VISIBLE);
        }
        else {
            followingBtn.setVisibility(View.VISIBLE);
        }

        GlideInstance.loadImageCircle(getContext(), initUser.profileImageUrl(), userProfilePic, R.drawable.profile_placeholder);
        Date now = new Date(System.currentTimeMillis());
        List<Event> events = new ArrayList<>();
        List<User> users = new ArrayList<>();
        pastEvents = new ArrayList<>();
        for (Event event : initUser.attending()) {
            if (event.date().after(now)) {
                events.add(event);
            } else {
                pastEvents.add(event);
            }
        }


        followersTv.setOnClickListener(v-> {

            Bundle bundle = new Bundle();
            if(user!=null) {
                bundle.putSerializable("followers", (ArrayList<AttendeeResponse>) user.followers().getFollowers());
                bundle.putSerializable("following", null);
                bundle.putSerializable("service", presenter.getFollowingService());
                bundle.putString("user","");
            }
            Log.e("UserView ", user.followers().getFollowers().size()+"");
            openFragment(FollowUserView.class, bundle,true);

        });


        followingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if(user!=null) {
                    bundle.putSerializable("followers", null);
                    bundle.putSerializable("following", (ArrayList<AttendeeResponse>) user.followers().getFollowingUsers());
                    bundle.putSerializable("service", presenter.getFollowingService());
                    bundle.putString("user","");
                }
                openFragment(FollowUserView.class, bundle);
            }
        });


        boolean clicked = false;
        for(int i = 0; i<initUser.followers().getFollowers().size(); i++){
            if(User.getInstance().getUserId() == initUser.followers().getFollowers().get(i).getUserId()){
                followingBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_button_clicked));
                followingBtn.setText("FOLLOWED");
                followingBtn.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                clicked = true;
                break;
            }
        }
        if(!clicked){
            followingBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_button_unclicked));
            followingBtn.setText("FOLLOW");
            followingBtn.setTextColor(ContextCompat.getColor(getContext(),  R.color.light_blue));
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
        recyclerView.setAdapter(eventsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        namesTV.setText(initUser.firstName() + " " + initUser.lastName());
        userNameTV.setText(initUser.username());
        attendedTv.setText(String.valueOf(initUser.attendedEvents()));
        followingTv.setText(initUser.followers().getFollowingUsers().size()+"");
        followersTv.setText(initUser.followers().getFollowers().size()+"");
        descriptionTv.setText(initUser.description());
        aboutMeTxt.setText(initUser.longDescription());

    }


    @Override
    protected ProfileListPresenter createPresenter() {
        return new ProfileListPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getFollowingUserSubject().subscribe(this::userFollowingSuccess));
        subscriptions.add(presenter.getErrorFollowingUserSubject().subscribe(this::onError));
        subscriptions.add(presenter.getUnFollowingUserSubject().subscribe(this::userUnFollowingSuccess));
        subscriptions.add(presenter.getErrorUnFollowingUserSubject().subscribe(this::onErrorUnFollowing));
    }

    private void userFollowingSuccess(UserResponse userResponse){
        hideLoading();
        followingBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_button_clicked));
        followingBtn.setText("FOLLOWED");
        followingBtn.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        Toast.makeText(getContext(),"userFollowingSuccess!",Toast.LENGTH_SHORT).show();
        initUser(userResponse);


    }

    private void onError(Throwable t){
        hideLoading();
        presenter.UnFollowUser(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""), user.userId());
    }

    private void userUnFollowingSuccess(UserResponse userResponse){
        initUser(userResponse);
        followingBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_button_unclicked));
        followingBtn.setText("FOLLOW");
        followingBtn.setTextColor(ContextCompat.getColor(getContext(),  R.color.light_blue));
    }

    private void onErrorUnFollowing(Throwable t){
        hideLoading();
        Toast.makeText(getContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
    }


}
