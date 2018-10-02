package club.leaps.presentation.profile.userProfile;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesPresenter;
import club.leaps.presentation.profile.becomeTrainer.BecomeTrainerActivity;
import club.leaps.presentation.profile.trainerProfile.TrainerProfileEditView;
import club.leaps.presentation.profile.trainerProfile.TrainerProfilePreview;
import club.leaps.presentation.trainer.UserEventsAdapter;
import club.leaps.networking.feed.event.AttendeeResponse;
import club.leaps.networking.feed.event.Event;
import club.leaps.networking.feed.event.RealEvent;
import club.leaps.networking.feed.trainer.Entity;
import club.leaps.networking.login.UserResponse;
import club.leaps.presentation.MainActivity;
import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.event.EventActivity;
import club.leaps.presentation.event.EventListingActivity;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesPresenter;
import club.leaps.presentation.profile.ProfileListPresenter;
import club.leaps.presentation.profile.SettingsView;
import club.leaps.presentation.profile.becomeTrainer.BecomeTrainerActivity;
import club.leaps.presentation.profile.trainerProfile.TrainerProfileEditView;
import club.leaps.presentation.profile.trainerProfile.TrainerProfilePreview;
import club.leaps.presentation.follow.FollowUserView;
import club.leaps.presentation.trainer.UserEventsAdapter;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.GlideInstance;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;

import static club.leaps.presentation.event.EventActivity.EVENT_KEY;
import static club.leaps.presentation.event.EventListingActivity.EVENT_PAST;

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
    private List<RealEvent> followingEvents;
    private RelativeLayout userViewRL;
    private RelativeLayout userViewSettingsRL;
    private TextView aboutMeTxt;
    private HomeFeedActivitiesPresenter homeFeedPresenter;
    private RelativeLayout editProfileRL;
    private RelativeLayout settingsRL;
    private RelativeLayout viewTutorialRL;
    private RelativeLayout logOutRL;
    private RelativeLayout inviteFriendsRL;
    private RelativeLayout giveFeedbackRL;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        user = EntityHolder.getInstance().getEntity();
        homeFeedPresenter = new HomeFeedActivitiesPresenter();
        followingEvents = new ArrayList<>();
        if(getArguments().getSerializable("user") !=null) {
            user = (Entity) getArguments().getSerializable("user");

            userViewRL.setVisibility(View.VISIBLE);
            userViewSettingsRL.setVisibility(View.GONE);
            becomeTrainerRl.setVisibility(View.GONE);

        }
        if (user != null) {
            initUser(user);
        }


        settingsRL.setOnClickListener(v -> openFragment(SettingsView.class, new Bundle(), true));
        becomeTrainerbtn.setOnClickListener(v -> getActivity().startActivityForResult(new Intent(getContext(), BecomeTrainerActivity.class), BECOME_TRAINER_REQUEST));
        Class editClass = User.getInstance().isTrainer() ? TrainerProfileEditView.class : UserEditProfileView.class;

        editProfileRL.setOnClickListener(v-> {
            openFragment(editClass, new Bundle(), true);
        });

        logOutRL.setOnClickListener(v -> {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().remove("Authorization").apply();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().remove("UserId").apply();
            startActivity(new Intent(getContext(), MainActivity.class));
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove("InboxUsers").apply();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove("ChatMessages").apply();
            LoginManager.getInstance().logOut();


            String currentUserId = String.valueOf(User.getInstance().getUserId());

            HashMap<String, String> logoutUser = new HashMap<>();
            logoutUser.put("device_token","");
            logoutUser.put("id",currentUserId);
            logoutUser.put("os","android");
            logoutUser.put("name",User.getInstance().getFirstName()+" " + User.getInstance().getLastName());
            logoutUser.put("image", User.getInstance().getImageUrl());

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

            userRef.setValue(logoutUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //  Toast.makeText(getApplicationContext(),"Successful.",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // Toast.makeText(getApplicationContext(),"ERROR.",Toast.LENGTH_SHORT).show();
                    }
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

            User.clear();
            EntityHolder.clear();

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
        editProfileRL = (RelativeLayout) view.findViewById(R.id.user_edit_profile_rl);
        settingsRL = (RelativeLayout) view.findViewById(R.id.user_settings_rl);
        inviteFriendsRL = (RelativeLayout) view.findViewById(R.id.user_invite_friends_rl);
        giveFeedbackRL = (RelativeLayout) view.findViewById(R.id.user_give_feedback_rl);
        viewTutorialRL = (RelativeLayout) view.findViewById(R.id.user_view_tutorial_rl);
        logOutRL = (RelativeLayout) view.findViewById(R.id.user_log_out_rl);

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
                followingBtn.setText(getString(R.string.followed));
                followingBtn.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                clicked = true;
                break;
            }
        }
        if(!clicked){
            followingBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_button_unclicked));
            followingBtn.setText(getString(R.string.follow));
            followingBtn.setTextColor(ContextCompat.getColor(getContext(),  R.color.light_blue));
        }

        showPastBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventListingActivity.class);
            intent.putExtra(EVENT_PAST, (Serializable) pastEvents);
            startActivity(intent);
        });
        UserEventsAdapter eventsAdapter = new UserEventsAdapter(events,followingEvents,homeFeedPresenter, event -> {
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
        subscriptions.add(presenter.getUserEntityObservable().subscribe(this::getUserSuccess));

    }

    private void getUserSuccess(UserResponse getUserResponse){
        initUser(getUserResponse);
    }

    private void userFollowingSuccess(UserResponse userResponse){
        hideLoading();
        followingBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_button_clicked));
        followingBtn.setText(getString(R.string.followed));
        followingBtn.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        initUser(userResponse);


    }

    private void onError(Throwable t){
        hideLoading();
        presenter.UnFollowUser(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""), user.userId());
    }

    private void userUnFollowingSuccess(UserResponse userResponse){
        initUser(userResponse);
        followingBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_button_unclicked));
        followingBtn.setText(getString(R.string.follow));
        followingBtn.setTextColor(ContextCompat.getColor(getContext(),  R.color.light_blue));
    }

    private void onErrorUnFollowing(Throwable t){
        hideLoading();
    }


    @Override
    public void onResume() {
        super.onResume();
        if(BecomeTrainerActivity.isFlag()) {

            Fragment newFragment = new TrainerProfilePreview();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.profile_container, newFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }

        presenter.getUser(User.getInstance().getUserId(),PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
    }
}
