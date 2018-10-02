package club.leaps.presentation.profile.trainerProfile;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import club.leaps.networking.feed.event.AttendeeResponse;
import club.leaps.networking.feed.trainer.Entity;
import club.leaps.networking.feed.trainer.Image;
import club.leaps.networking.login.UserResponse;
import club.leaps.presentation.MainActivity;
import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.profile.ProfileListPresenter;
import club.leaps.presentation.profile.SettingsView;
import club.leaps.presentation.profile.tutorial.TutorialActivity;
import club.leaps.presentation.profile.userProfile.UserEditProfileView;
import club.leaps.presentation.event.EventViewPagerAdapter;
import club.leaps.presentation.follow.FollowUserView;
import club.leaps.presentation.utils.CustomTextView;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.GlideInstance;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 23/07/2017.
 */
@Layout(layoutId = R.layout.trainer_profile_preview)
public class TrainerProfilePreview extends BaseView<ProfileListPresenter> {

    private static final int BECOME_TRAINER_REQUEST = 4567;

    private ImageView mainPic;
    private CustomTextView nameTv;
    private TextView usernameTv;
    private ViewPager imagesRecycler;
    private TextView settings;
    private TextView viewTutorial;
    private TextView logOut;
    private TextView editProfile;
    private TextView followingTv;
    private TextView followersTV;
    private TextView eventTV;
    private Entity trainer2;
    private RatingBar rating;
    private TextView ratingCounter;
    private TextView ratingViewers;
    private View view;
    private CirclePageIndicator pageIndicator;
    private RelativeLayout editProfileRL;
    private RelativeLayout settingsRL;
    private RelativeLayout viewTutorialRL;
    private RelativeLayout logOutRL;
    private RelativeLayout inviteFriendsRL;
    private RelativeLayout giveFeedbackRL;
  //  private Button becomeTrainerbtn;
    //private RelativeLayout becomeTrainerRl;
   // private List<Event> pastEvents;
   // private TextView eventsNumber;
   // private FlexboxLayout tagsContainer;
   // private TextView aboutTv;
  //  private TextView showPastBtn;
   // private RecyclerView eventsRecycler;
  //  private TextView addressTv;
  //  private TextView ageTv;
   // private ImageView editProfileBtn;
   // private Button followBtn;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        this.view = view;
    }

    private void initFields(View view){
        editProfile = (TextView) view.findViewById(R.id.edit_profile_txt);
        mainPic = (ImageView) view.findViewById(R.id.trainer_pic);
        nameTv = (CustomTextView) view.findViewById(R.id.trainer_name);
        usernameTv = (TextView) view.findViewById(R.id.trainer_title);
        imagesRecycler = (ViewPager) view.findViewById(R.id.trainer_image_horizontall_scroll);
        settings = (TextView) view.findViewById(R.id.profile_list_settings);
        viewTutorial = (TextView) view.findViewById(R.id.profile_list_tutorial);
        logOut = (TextView) view.findViewById(R.id.profile_list_log_out);
        followingTv = (TextView)  view.findViewById(R.id.trainer_following_tv);
        followersTV = (TextView)  view.findViewById(R.id.trainer_followers_tv);
        eventTV = (TextView)  view.findViewById(R.id.trainer_events_number_tv);
        rating = (RatingBar) view.findViewById(R.id.trainer_rating_bar);
        ratingCounter = (TextView) view.findViewById(R.id.rating_counter_tv);
        ratingViewers = (TextView) view.findViewById(R.id.rating_review_counter);
        pageIndicator = (CirclePageIndicator) view.findViewById(R.id.event_indicator);
        editProfileRL = (RelativeLayout) view.findViewById(R.id.edit_profile_rl);
        settingsRL = (RelativeLayout) view.findViewById(R.id.settings_rl);
        inviteFriendsRL = (RelativeLayout) view.findViewById(R.id.invite_friends_rl);
        giveFeedbackRL = (RelativeLayout) view.findViewById(R.id.give_feedback_rl);
        viewTutorialRL = (RelativeLayout) view.findViewById(R.id.view_tutorial_rl);
        logOutRL = (RelativeLayout) view.findViewById(R.id.log_out_rl);

        //  editProfileBtn = (ImageView) view.findViewById(R.id.profile_list_profile_btn);
       //  ageTv = (TextView) view.findViewById(R.id.trainer_age);
       //  addressTv = (TextView) view.findViewById(R.id.trainer_location);
       // eventsNumber = (TextView) view.findViewById(R.id.events_number_tv);
       // tagsContainer = (FlexboxLayout) view.findViewById(R.id.trainer_tags_container);
       // aboutTv = (TextView) view.findViewById(R.id.trainer_about_tv);
       // showPastBtn = (TextView) view.findViewById(R.id.trainer_show_past_btn);
       // eventsRecycler = (RecyclerView) view.findViewById(R.id.trainer_events_recycler);

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

        subscriptions.add(presenter.getUserEntityObservable().subscribe(this::success));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BECOME_TRAINER_REQUEST){
            showLoading();
            presenter.getUser(User.getInstance().getUserId(), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
        }
    }

    private void success(UserResponse userResponse){


        showTrainer(userResponse);

    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.getUser(User.getInstance().getUserId(),PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
    }

    private void showTrainer(Entity trainer){
        if(trainer == null){
            throw new IllegalArgumentException("The trainer is null in " + getClass().getCanonicalName());
        }


        List<String> imageUrls = new ArrayList<>();
        for(Image image : trainer.images()){
            imageUrls.add(image.getImageUrl());
        }
        if(imageUrls.isEmpty()){
            view.findViewById(R.id.recycler_placeholder).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_placeholder));
        }else {
            EventViewPagerAdapter adapter = new EventViewPagerAdapter(view.getContext(),getChildFragmentManager(),imageUrls);
            imagesRecycler.setAdapter(adapter);
            pageIndicator.setViewPager(imagesRecycler);
            pageIndicator.setCurrentItem(0);
            pageIndicator.setFillColor(ContextCompat.getColor(getContext(), R.color.light_blue));
            pageIndicator.setPageColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            pageIndicator.setStrokeColor(ContextCompat.getColor(getContext(), R.color.light_blue));
            pageIndicator.setRadius((int) (4 * Resources.getSystem().getDisplayMetrics().density));
        }

        GlideInstance.loadImageCircle(getContext(), trainer.profileImageUrl(), mainPic, R.drawable.event_placeholder);

        viewTutorialRL.setOnClickListener(v -> startActivity(new Intent(getContext(), TutorialActivity.class)));
        settingsRL.setOnClickListener(v -> openFragment(SettingsView.class, new Bundle(), true));
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


        usernameTv.setText(trainer.username());
        nameTv.setText(trainer.firstName() + " " + trainer.lastName());
        String following = String.valueOf(trainer.followers().getFollowingUsers().size());
        String followers = String.valueOf(trainer.followers().getFollowers().size());


        followingTv.setText(following);

        followingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if(trainer!=null) {
                    bundle.putSerializable("followers", null);
                    bundle.putSerializable("following", (ArrayList<AttendeeResponse>) trainer.followers().getFollowingUsers());
                    bundle.putSerializable("service", presenter.getFollowingService());
                    bundle.putString("user","");
                }
                openFragment(FollowUserView.class, bundle);
            }
        });


        followersTV.setText(followers);

        followersTV.setOnClickListener(v-> {

            Bundle bundle = new Bundle();
            if(trainer!=null) {
                bundle.putSerializable("followers", (ArrayList<AttendeeResponse>) trainer.followers().getFollowers());
                bundle.putSerializable("following", null);
                bundle.putSerializable("service", presenter.getFollowingService());
                bundle.putString("user","");
            }
            openFragment(FollowUserView.class, bundle,true);

        });

        eventTV.setText(trainer.hosting().size()+"");

        LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_ATOP);

        if(trainer.rating()==0 && trainer.reviews() == 0){
            ratingCounter.setText(0.0f+"");
            ratingViewers.setText("("+0+""+getString(R.string.number_reviews));
        }
        else {

            DecimalFormat df = new DecimalFormat("#.0");
            String ratingEvent = String.valueOf(df.format(trainer.rating()));
            rating.setRating(trainer.rating());
            ratingCounter.setText(ratingEvent+"");
            ratingViewers.setText("("+String.valueOf(trainer.reviews()+""+getString(R.string.number_reviews)));
        }

        //  editProfileBtn.setOnClickListener(v-> openFragment(ProfileListView.class, new Bundle(), true));

//        eventsNumber.setText(String.valueOf(trainer.hosting().size()));
       /* List<String> tags = trainer.specialities();
        for (String tag : tags) {
            TagView tagView = new TagView(getContext());
            tagView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_tag_shape));
            tagView.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
            tagView.setText(tag);
            tagsContainer.addView(tagView);
        }

//        aboutTv.setText(trainer.longDescription());
        Date now = new Date(System.currentTimeMillis());
        List<Event> events = new ArrayList<>();
        pastEvents = new ArrayList<>();
        for(Event event : trainer.attending()){
            if(event.date().after(now)){
                events.add(event);
            }else{
                pastEvents.add(event);
            }
        }*/

      /*  showPastBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventListingActivity.class);
            intent.putExtra(EVENT_PAST, (Serializable) pastEvents);
            startActivity(intent);
        });
        UserEventsAdapter eventsAdapter = new UserEventsAdapter(events, event -> {
            Intent intent = new Intent(getContext(), EventActivity.class);
            intent.putExtra(EVENT_KEY, event);
            startActivity(intent);
        });*/

        //followBtn.setOnClickListener(v -> followSetup());
        // eventsRecycler.setAdapter(eventsAdapter);
        //  eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //ageTv.setText(trainer.age() + "");
        // addressTv.setText(trainer.address());
    }



}


