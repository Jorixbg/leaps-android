package com.example.xcomputers.leaps.profile.trainerProfile;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.networking.feed.event.AttendeeResponse;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.feed.trainer.Image;
import com.example.networking.login.UserResponse;
import com.example.xcomputers.leaps.MainActivity;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.profile.ProfileListPresenter;
import com.example.xcomputers.leaps.profile.SettingsView;
import com.example.xcomputers.leaps.profile.tutorial.TutorialActivity;
import com.example.xcomputers.leaps.profile.userProfile.UserEditProfileView;
import com.example.xcomputers.leaps.event.EventViewPagerAdapter;
import com.example.xcomputers.leaps.follow.FollowUserView;
import com.example.xcomputers.leaps.utils.CustomTextView;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.GlideInstance;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
        Entity trainer1 = EntityHolder.getInstance().getEntity();
        showTrainer(trainer1);

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

        trainer2 = userResponse;

    }


    @Override
    public void onResume() {
        super.onResume();
        Entity trainer1 = EntityHolder.getInstance().getEntity();
        showTrainer(trainer1);


    }

    private void showTrainer(Entity trainer){
        if(trainer == null){
            throw new IllegalArgumentException("The trainer is null in " + getClass().getCanonicalName());
        }

        presenter.getUser(User.getInstance().getUserId(),PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));

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
        }
        GlideInstance.loadImageCircle(getContext(), trainer.profileImageUrl(), mainPic, R.drawable.event_placeholder);

        viewTutorial.setOnClickListener(v -> startActivity(new Intent(getContext(), TutorialActivity.class)));
        settings.setOnClickListener(v -> openFragment(SettingsView.class, new Bundle(), true));
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
            ratingViewers.setText("("+0+""+" reviews)");
        }
        else {

            DecimalFormat df = new DecimalFormat("#.0");
            String ratingEvent = String.valueOf(df.format(trainer.rating()));
            rating.setRating(trainer.rating());
            ratingCounter.setText(ratingEvent+"");
            ratingViewers.setText("("+String.valueOf(trainer.reviews()+""+" reviews)"));
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


