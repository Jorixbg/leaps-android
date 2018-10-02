package club.leaps.presentation.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import club.leaps.presentation.event.EventActivity;
import club.leaps.presentation.homefeed.OnSearchDataCollectedListener;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesFilterAdapter;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesPresenter;
import club.leaps.networking.feed.event.Event;
import club.leaps.networking.feed.event.FeedFilterEventResponse;
import club.leaps.networking.feed.event.FeedFilterRequest;
import club.leaps.presentation.R;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.event.EventActivity;
import club.leaps.presentation.homefeed.OnSearchDataCollectedListener;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesFilterAdapter;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesPresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

import static club.leaps.presentation.event.EventActivity.EVENT_KEY;

/**
 * Created by Ivan on 10/16/2017.
 */

@Layout(layoutId = R.layout.home_feed_activities_view)
public class EventFilterView extends BaseView<HomeFeedActivitiesPresenter> implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView recyclerView;
    private List<String> sectionTitles;
    private HomeFeedActivitiesFilterAdapter filterAdapter;
    private TextView filterCancel;
    private TextView filterHeaderText;
    // private RelativeLayout filterHeader;
    private FeedFilterRequest request;
    private RecyclerView.OnScrollListener listener;
    private GridLayoutManager layoutManager;
    private RelativeLayout emptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FeedFilterEventResponse response;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //HomeScreenView.getTabLayout().setVisibility(View.VISIBLE);
        emptyState = (RelativeLayout) view.findViewById(R.id.empty_state);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_feed_activities_recycler);
        response = (FeedFilterEventResponse) getArguments().getSerializable("filterEvent");
        //    filterHeader = (RelativeLayout) view.findViewById(R.id.filter_header);
        //   filterHeader.setVisibility(View.GONE);
        //  filterHeaderText = (TextView) filterHeader.findViewById(R.id.home_feed_header_name);
        //  filterHeader.setVisibility(View.GONE);

        LayoutInflater factory = getLayoutInflater();

        View homeFeedContainerView = factory.inflate(R.layout.home_feed_container_view, null);

        filterCancel = (TextView) homeFeedContainerView.findViewById(R.id.feed_header_lbl);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeEvents);
        swipeRefreshLayout.setOnRefreshListener(this);

        filterCancel.setOnClickListener(v -> {
            showLoading();
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            presenter.getEventsNoFilter(sectionTitles);
            presenter.clearData();
            recyclerView.removeOnScrollListener(listener);
            if (getParentFragment() != null) {
                ((OnSearchDataCollectedListener) getParentFragment()).resetSearch();
            }
        });

        if (response != null) {
            hideLoading();
            if (filterAdapter != null) {
                filterAdapter.setLoadingState(false);
            }
            List<Event> list = new ArrayList<>();
            list.addAll(response.getEventList());
            if (response.getEventList().size() == 0 || response.getEventList().isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            } else {
                emptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                //filterHeaderText.setText(sectionedDataHolder.getHeaderForSection(0));
                if (filterAdapter == null) {
                    filterAdapter = new HomeFeedActivitiesFilterAdapter(list,presenter, event -> {
                        Intent intent = new Intent(getContext(), EventActivity.class);
                        intent.putExtra(EventActivity.EVENT_KEY, (Parcelable) event);
                        startActivity(intent);
                    });
                    layoutManager = new GridLayoutManager(getContext(), 1);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(filterAdapter);
                } else {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    filterAdapter.setLoadingState(false);
                    filterAdapter.setData(list);
                }
            }


        }
    }

    protected HomeFeedActivitiesPresenter createPresenter() {
        return new HomeFeedActivitiesPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }



    @Override
    public void onRefresh() {
        showLoading();
        presenter.getEventsNoFilter(sectionTitles);
        presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization",""));
        hideLoading();
    }

}

