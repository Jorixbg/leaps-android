package club.leaps.presentation.follow;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import club.leaps.networking.feed.event.RealEvent;
import club.leaps.presentation.R;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;

import java.util.List;

import rx.Subscription;

/**
 * Created by Ivan on 9/21/2017.
 */

@Layout(layoutId = R.layout.following_event_view)
public class FollowingEventView extends BaseView<FollowingEventPresenter> implements SwipeRefreshLayout.OnRefreshListener{

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
        presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization",""));
        layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeFollowingEvents);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    protected FollowingEventPresenter createPresenter() {
        return new FollowingEventPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getFollowingFutureEventObservable().subscribe(this::followingSuccess));
        subscriptions.add(presenter.getErrorFollowingFutureEventObservable().subscribe(this::onError));
    }

    private void followingSuccess(List<RealEvent> realEvents){
        showLoading();
        adapter = new FollowingEventAdapter(getContext(), realEvents,presenter);
        followingEventRecyclerView.setAdapter(adapter);
        followingEventRecyclerView.setLayoutManager(layoutManager);
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        followingEventRecyclerView.getAdapter().notifyDataSetChanged();
        hideLoading();
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
        presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization",""));

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization",""));
    }
}