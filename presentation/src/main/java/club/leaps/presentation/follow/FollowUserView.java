package club.leaps.presentation.follow;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import club.leaps.presentation.profile.userProfile.UserProfilePreview;
import club.leaps.presentation.trainer.TrainerActivity;
import club.leaps.networking.feed.event.AttendeeResponse;
import club.leaps.networking.feed.trainer.Entity;
import club.leaps.networking.following.FollowingService;
import club.leaps.networking.login.UserResponse;
import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.profile.userProfile.UserProfilePreview;
import club.leaps.presentation.trainer.TrainerActivity;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.LoginResponseToUserTypeMapper;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

import static club.leaps.presentation.trainer.TrainerActivity.ENTITY_KEY;

/**
 * Created by Ivan on 9/29/2017.
 */
@Layout(layoutId = R.layout.follow_users_page)
public class FollowUserView  extends BaseView<GetUserPresenter> implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView followingUserRecyclerView;
    private TextView emptyState;
    private FollowUserAdapter followingAdapter;
    private FollowUserAdapter followersAdapter;
    private FollowAttendeeAdapter attendeeOtherAdapter;


    private SwipeRefreshLayout swipeRefreshLayout;
    private String event;
    private String userBundle;
    private boolean swipe;


    private List<AttendeeResponse> othersForAdapterTrainer;
    private List<AttendeeResponse> friendsForAdapterTrainer;
    private List<AttendeeResponse> friendsForAdapterEvent;
    private List<AttendeeResponse> othersForAdapterEvent;


    private List<AttendeeResponse> followedUsers = new ArrayList<>();
    private List<AttendeeResponse> followingUsers = new ArrayList<>();
    private List<AttendeeResponse> attendesFollowed = new ArrayList<>();
    private List<AttendeeResponse> attendesOther = new ArrayList<>();


    private LinearLayoutManager layoutManager;
    private Entity user;
    private FollowingService service;
    private SnapHelper helper;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeEvents);
        emptyState = view.findViewById(R.id.empty_state_text);
        followingUserRecyclerView = (RecyclerView) view.findViewById(R.id.following_recyclerview);

        swipeRefreshLayout.setOnRefreshListener(this);

        event = getArguments().getString("event");
        userBundle = getArguments().getString("user");
        service = new FollowingService();
        if(event!=null && !event.isEmpty() && userBundle == null) {
            foundFriendsEvent();
        }
        else {
            foundFriendsTrainer();
        }
        if(userBundle !=null && !userBundle.isEmpty()){

            foundFriendsTrainer();
        }





        layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        helper = new LinearSnapHelper();
      if(attendeeOtherAdapter!=null && attendeeOtherAdapter!=null){
            helper.attachToRecyclerView(followingUserRecyclerView);
            followingUserRecyclerView.setAdapter(attendeeOtherAdapter);
            followingUserRecyclerView.setLayoutManager(layoutManager);

            //Set adapter for user that are followers
           /* helper.attachToRecyclerView(followingUserRecyclerView);
            followersUserRecyclerView.setAdapter(followersAdapter);
            followersUserRecyclerView.setLayoutManager(layoutManager2);*/
        }
        if(followersAdapter !=null && followersAdapter !=null) {

            helper.attachToRecyclerView(followingUserRecyclerView);
            followingUserRecyclerView.setAdapter(followersAdapter);
            followingUserRecyclerView.setLayoutManager(layoutManager);


           /* helper.attachToRecyclerView(followingUserRecyclerView);
            followersUserRecyclerView.setAdapter(attendeeOtherAdapter);
           followersUserRecyclerView.setLayoutManager(layoutManager2);*/

        }





    }


    @Override
    protected GetUserPresenter createPresenter() {
        return new GetUserPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getUserSubject().subscribe(this::followingSuccess));
        subscriptions.add(presenter.getErrorUserSubject().subscribe(this::onError));
    }

    private void followingSuccess(UserResponse userResponse) {
        hideLoading();
        if(userResponse.userId() == User.getInstance().getUserId() && swipe == true){
            LoginResponseToUserTypeMapper.map(userResponse);
            EntityHolder.getInstance().setEntity(userResponse);
            swipe = false;
        }
        else {
            user = (Entity) userResponse;
            if (user.isTrainer()) {
                Intent intent = new Intent(getContext(), TrainerActivity.class);
                intent.putExtra(TrainerActivity.ENTITY_KEY, user);
                startActivity(intent);
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                openFragment(UserProfilePreview.class, bundle, true);
            }
        }

    }

    private void onError(Throwable t){

        hideLoading();
        Toast.makeText(getContext(), getString(R.string.lbl_general_error), Toast.LENGTH_SHORT).show();
    }


    private void followedFriendsTrainer() {

        othersForAdapterTrainer = new ArrayList<>();
        friendsForAdapterTrainer = new ArrayList<>();
        if(followedUsers !=null && followedUsers.size() !=0) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
                for (int i = 0; i < followedUsers.size(); i++) {
                    boolean inFriends = false;

                    for (int j = 0; j < User.getInstance().getFollowed().getFollowingUsers().size(); j++) {
                        if (User.getInstance().getFollowed().getFollowingUsers().get(j).getUserId() ==
                                followedUsers.get(i).getUserId()) {
                            friendsForAdapterTrainer.add(followedUsers.get(i));
                            inFriends = true;
                        }
                    }
                    if (othersForAdapterTrainer.size() == 0 && !inFriends) {
                        othersForAdapterTrainer.add(followedUsers.get(i));
                    }
                    if (othersForAdapterTrainer.size() != 0 && othersForAdapterTrainer != null && !inFriends) {
                        boolean isIn = false;
                        for (int k = 0; k < othersForAdapterTrainer.size(); k++) {
                            if (followedUsers.get(i).getUserId() == othersForAdapterTrainer.get(k).getUserId()) {
                                isIn = true;
                            }
                        }
                        if (!isIn) {
                            othersForAdapterTrainer.add(followedUsers.get(i));
                        }
                    }

                }
           } else {
            swipeRefreshLayout.setVisibility(View.GONE);
            emptyState.setText(getString(R.string.empty_followers));
            emptyState.setVisibility(View.VISIBLE);
        }

    }

    private void followingFriendsTrainer() {

        othersForAdapterTrainer = new ArrayList<>();
        friendsForAdapterTrainer = new ArrayList<>();
        if(followingUsers !=null && followingUsers.size() !=0) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            for (int i = 0; i < followingUsers.size(); i++) {
                boolean inFriends = false;
                for (int j = 0; j < User.getInstance().getFollowed().getFollowingUsers().size(); j++) {
                    if (User.getInstance().getFollowed().getFollowingUsers().get(j).getUserId() ==
                            followingUsers.get(i).getUserId()) {
                        friendsForAdapterTrainer.add(followingUsers.get(i));
                        inFriends = true;
                    }
                }
                if (othersForAdapterTrainer.size() == 0 && !inFriends) {
                    othersForAdapterTrainer.add(followingUsers.get(i));
                }
                if (othersForAdapterTrainer.size() != 0 && othersForAdapterTrainer != null && !inFriends) {
                    boolean isIn = false;
                    for (int k = 0; k < othersForAdapterTrainer.size(); k++) {
                        if (followingUsers.get(i).getUserId() == othersForAdapterTrainer.get(k).getUserId()) {
                            isIn = true;
                        }
                    }
                    if (!isIn) {
                        othersForAdapterTrainer.add(followingUsers.get(i));
                    }
                }

            }
        }
        else {
            swipeRefreshLayout.setVisibility(View.GONE);
            emptyState.setText(getString(R.string.empty_followers));
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    private void followedFriendsEvent() {

        othersForAdapterEvent = new ArrayList<>();
        friendsForAdapterEvent = new ArrayList<>();
        if(attendesOther !=null && attendesOther.size() !=0) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            for (int i = 0; i < attendesOther.size(); i++) {
                boolean inFriends = false;

                for (int j = 0; j < User.getInstance().getFollowed().getFollowingUsers().size(); j++) {
                    if (User.getInstance().getFollowed().getFollowingUsers().get(j).getUserId() ==
                            attendesOther.get(i).getUserId()) {
                        friendsForAdapterEvent.add(attendesOther.get(i));
                        inFriends = true;
                    }
                }
                if (othersForAdapterEvent.size() == 0 && !inFriends) {
                    othersForAdapterEvent.add(attendesOther.get(i));
                }
                if (othersForAdapterEvent.size() != 0 && othersForAdapterEvent != null && !inFriends) {
                    boolean isIn = false;
                    for (int k = 0; k < othersForAdapterEvent.size(); k++) {
                        if (attendesOther.get(i).getUserId() == othersForAdapterEvent.get(k).getUserId()) {
                            isIn = true;
                        }
                    }
                    if (!isIn) {
                        othersForAdapterEvent.add(attendesOther.get(i));
                    }
                }

            }
        }
        else {
            swipeRefreshLayout.setVisibility(View.GONE);
            emptyState.setText(getString(R.string.empty_followers));
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    private void foundFriendsEvent(){
        attendesFollowed = (List<AttendeeResponse>) getArguments().getSerializable("attendeeFollowed");
        attendesOther = (List<AttendeeResponse>) getArguments().getSerializable("attendeeOther");

        List<AttendeeResponse> followedForAdapterEvent = new ArrayList<>();
        followedFriendsEvent();
        followedForAdapterEvent.addAll(attendesFollowed);
        followedForAdapterEvent.addAll(attendesOther);
        attendeeOtherAdapter = new FollowAttendeeAdapter(getContext(), followedForAdapterEvent,othersForAdapterEvent,service,attendee -> {
            presenter.getUser(attendee.getUserId(), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
        });
        if(swipeRefreshLayout.isRefreshing()){

            helper.attachToRecyclerView(followingUserRecyclerView);
            followingUserRecyclerView.setAdapter(attendeeOtherAdapter);
            followingUserRecyclerView.setLayoutManager(layoutManager);
            swipeRefreshLayout.setRefreshing(false);
        }

         /*   else {
                followingFriendsEvent();
                List<AttendeeResponse> followingForAdapterEvent = new ArrayList<>();
                followingForAdapterEvent.addAll(friendsForAdapterEvent);
                followingForAdapterEvent.addAll(othersForAdapterEvent);
                attendeeOtherAdapter = new FollowAttendeeAdapter(getContext(), followingForAdapterEvent,othersForAdapterEvent,service,attendee -> {
                    presenter.getUser(attendee.getUserId(), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
                });
            }*/
    }

    private void foundFriendsTrainer(){
        followedUsers = (List<AttendeeResponse>) getArguments().getSerializable("followers");
        followingUsers = (List<AttendeeResponse>) getArguments().getSerializable("following");

        if(followedUsers!=null && followedUsers.size() !=0) {
            followedFriendsTrainer();
            List<AttendeeResponse> followedForAdapterTrainer  = new ArrayList<>();
            followedForAdapterTrainer.addAll(friendsForAdapterTrainer);
            followedForAdapterTrainer.addAll(othersForAdapterTrainer);
            followersAdapter = new FollowUserAdapter(getContext(), followedForAdapterTrainer,othersForAdapterTrainer,service,followedUser -> {
                presenter.getUser(followedUser.getUserId(), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
            });
        }

        else {
            followingFriendsTrainer();
            List<AttendeeResponse> followingForAdapterTrainer = new ArrayList<>();
            followingForAdapterTrainer.addAll(friendsForAdapterTrainer);
            followingForAdapterTrainer.addAll(othersForAdapterTrainer);
            followersAdapter = new FollowUserAdapter(getContext(), followingForAdapterTrainer,othersForAdapterTrainer,service,followedUser -> {
                presenter.getUser(followedUser.getUserId(), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
            });
        }

        if(swipeRefreshLayout.isRefreshing()){
            helper.attachToRecyclerView(followingUserRecyclerView);
            followingUserRecyclerView.setAdapter(followersAdapter);
            followingUserRecyclerView.setLayoutManager(layoutManager);
            swipeRefreshLayout.setRefreshing(false);
        }


        //Log.e("Loop ","Masiv " +followedUsers.size()+"");
        //  Log.e("Loop ","Friends: " + friendsForAdapter.size()+"");
        // Log.e("Loop ","Others: " + othersForAdapter.size()+"");
        // Log.e("Loop ","User Friends: " + User.getInstance().getFollowed().getFollowingUsers().size()+"");

    }

    @Override
    public void onRefresh() {
        showLoading();
        if(event!=null && !event.isEmpty()) {
            foundFriendsEvent();
        }
        else {
            foundFriendsTrainer();

        }

        hideLoading();
        swipe = true;
        presenter.getUser(User.getInstance().getUserId(),PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
    }


}
