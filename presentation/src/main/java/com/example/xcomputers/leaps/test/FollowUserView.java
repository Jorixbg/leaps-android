package com.example.xcomputers.leaps.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

import com.example.networking.test.FollowedUser;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by Ivan on 9/29/2017.
 */
@Layout(layoutId = R.layout.follow_users_page)
public class FollowUserView  extends BaseView<EmptyPresenter> {

    private RecyclerView followingUserRecyclerView;
    private RecyclerView followersUserRecyclerView;
    private FollowUserAdapter followingAdapter;
    private FollowUserAdapter followersAdapter;

    private List<FollowedUser> followedUsers = new ArrayList<>();
    private List<FollowedUser> followingUsers = new ArrayList<>();

    private LinearLayoutManager layoutManager;
    private LinearLayoutManager layoutManager2;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        followedUsers = getArguments().getParcelableArrayList("followers");
        followingUsers = getArguments().getParcelableArrayList("following");

        followingUserRecyclerView = (RecyclerView) view.findViewById(R.id.following_recyclerview);
        followersUserRecyclerView = (RecyclerView) view.findViewById(R.id.followers_recyclerview);

        layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager2  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        //Set adapter for users that are following
        followingAdapter = new FollowUserAdapter(getContext(),followingUsers);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(followingUserRecyclerView);
        followingUserRecyclerView.setAdapter(followingAdapter);
        followingUserRecyclerView.setLayoutManager(layoutManager);

        //Set adapter for user that are followers
        followersAdapter = new FollowUserAdapter(getContext(),followedUsers);
        helper.attachToRecyclerView(followingUserRecyclerView);
        followersUserRecyclerView.setAdapter(followersAdapter);
        followersUserRecyclerView.setLayoutManager(layoutManager2);

    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }



}
