package com.example.xcomputers.leaps.follow;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.networking.feed.event.RealEvent;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;

import java.util.List;

import rx.Subscription;

/**
 * Created by IvanGachmov on 12/15/2017.
 */

@Layout(layoutId = R.layout.following_event_view)
public class FollowingPastEventView extends BaseView<FollowingEventPresenter> implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView followingEventRecyclerView;
    private RelativeLayout emptyState;
    private LinearLayoutManager layoutManager;
    private FollowingEventAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyState = (RelativeLayout) view.findViewById(R.id.following_event_empty_state);
        followingEventRecyclerView = (RecyclerView) view.findViewById(R.id.following_event_recycler);
        presenter.getFollowPastEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization",""));
        layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeFollowingEvents);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    protected FollowingEventPresenter createPresenter() {
        return new FollowingEventPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getFollowingPastEventObservable().subscribe(this::followingSuccess));
        subscriptions.add(presenter.getErrorFollowingPastEventObservable().subscribe(this::onError));
    }

    private void followingSuccess(List<RealEvent> realEvents){
        adapter = new FollowingEventAdapter(getContext(), realEvents,presenter);
        followingEventRecyclerView.setAdapter(adapter);
        followingEventRecyclerView.setLayoutManager(layoutManager);
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    private void onError(Throwable t){
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        hideLoading();
        Toast.makeText(getContext(), getString(R.string.lbl_general_error), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRefresh() {
        showLoading();
        presenter.getFollowPastEvent(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization",""));
        hideLoading();
    }
}
