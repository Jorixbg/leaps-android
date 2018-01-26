package com.example.xcomputers.leaps.homefeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.networking.feed.event.Event;
import com.example.networking.feed.event.FeedFilterRequest;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.event.EventActivity;
import com.example.xcomputers.leaps.event.EventListingActivity;
import com.example.xcomputers.leaps.event.createEvent.CreateEventActivity;
import com.example.xcomputers.leaps.homefeed.FeedInsideTab;
import com.example.xcomputers.leaps.homefeed.HomeFeedContainer;
import com.example.xcomputers.leaps.homefeed.OnSearchDataCollectedListener;
import com.example.xcomputers.leaps.homescreen.HomeScreenView;
import com.example.xcomputers.leaps.utils.SectionedDataHolder;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

import static com.example.xcomputers.leaps.event.EventActivity.EVENT_KEY;
import static com.example.xcomputers.leaps.event.EventListingActivity.EVENT_NEARBY;
import static com.example.xcomputers.leaps.event.EventListingActivity.EVENT_POPULAR;
import static com.example.xcomputers.leaps.event.EventListingActivity.EVENT_SUITED;
import static com.example.xcomputers.leaps.homefeed.HomeFeedContainer.FEED_SEARCH_EVENT_KEY;

/**
 * Created by xComputers on 10/06/2017.
 */
@Layout(layoutId = R.layout.home_feed_activities_view)
public class HomeFeedActivitiesView extends BaseView<HomeFeedActivitiesPresenter> implements FeedInsideTab,SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private List<String> sectionTitles;
    private HomeFeedActivitiesFilterAdapter filterAdapter;
    private TextView filterCancel;
    private TextView filterHeaderText;
    private RelativeLayout filterHeader;
    private FeedFilterRequest request;
    private RecyclerView.OnScrollListener listener;
    private GridLayoutManager layoutManager;
    private RelativeLayout emptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView createEventBtn;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HomeScreenView.getTabLayout().setVisibility(View.VISIBLE);
        presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization", ""));
        emptyState = (RelativeLayout) view.findViewById(R.id.empty_state);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_feed_activities_recycler);
        filterHeader = (RelativeLayout) view.findViewById(R.id.filter_header);
        filterHeader.setVisibility(View.GONE);
        filterCancel = (TextView) filterHeader.findViewById(R.id.feed_header_lbl);
        filterHeaderText = (TextView) filterHeader.findViewById(R.id.home_feed_header_name);
        createEventBtn = (ImageView) view.findViewById(R.id.home_cal_create_event_btn);
        LayoutInflater factory = getLayoutInflater();

        View homeFeedContainerView = factory.inflate(R.layout.home_feed_container_view, null);

        //NEW CODE
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeEvents);
        swipeRefreshLayout.setOnRefreshListener(this);

        sectionTitles = new ArrayList<>();
        sectionTitles.add(getString(R.string.lbl_popular_events));
        sectionTitles.add(getString(R.string.lbl_events_nearby));
        sectionTitles.add(getString(R.string.lbl_selected_events));

        showLoading();

        if (!User.getInstance().isTrainer()) {
            createEventBtn.setVisibility(View.GONE);
        } else {
            createEventBtn.setVisibility(View.VISIBLE);
            createEventBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), CreateEventActivity.class);
                startActivity(intent);
            });
        }


        if (getArguments() != null) {
            if (FEED_SEARCH_EVENT_KEY.equals(((OnSearchDataCollectedListener) getParentFragment()).getOrigin())) {
                HomeFeedContainer.getHomeView().findViewById(R.id.feed_header_lbl).setVisibility(View.VISIBLE);
                presenter.setShouldLoadMore(true);
                setFilter(((OnSearchDataCollectedListener) getParentFragment()).onSearchDataCollected());
            } else {
                presenter.getEventsNoFilter(sectionTitles);
            }
        }
        if (HomeFeedContainer.getCurrentPosition() == 0) {
            HomeFeedContainer.getHomeView().findViewById(R.id.feed_header_lbl).setOnClickListener(v -> {
                showLoading();
                emptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                presenter.clearData();
                presenter.getEventsObservable().subscribe(this::onSuccess);
                presenter.getEventsNoFilter(sectionTitles);
                filterHeader.setVisibility(View.GONE);
                recyclerView.removeOnScrollListener(listener);
                if (getParentFragment() != null) {
                    ((OnSearchDataCollectedListener) getParentFragment()).resetSearch();
                }
                HomeFeedContainer.getHomeView().findViewById(R.id.homescreen_search_tv).setClickable(true);
                HomeFeedContainer.getHomeView().findViewById(R.id.feed_header_lbl).setVisibility(View.GONE);
            });


        }
    }

    protected HomeFeedActivitiesPresenter createPresenter() {
        return new HomeFeedActivitiesPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getEventsObservable().subscribe(this::onSuccess));
        subscriptions.add(presenter.getFilterObservable().subscribe(sectionedDataHolder -> {
            if(filterAdapter != null){
                filterAdapter.setLoadingState(false);
            }
            List<Event> list = new ArrayList<>();
            list.addAll(sectionedDataHolder.getItemsForSection(0));
            if(sectionedDataHolder.isEmpty()){
                emptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            }
            if(list.isEmpty()){
                emptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else{
                emptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                filterHeaderText.setText(sectionedDataHolder.getHeaderForSection(0));
                if (filterAdapter == null) {
                    filterAdapter = new HomeFeedActivitiesFilterAdapter(list,presenter, event -> {
                        Intent intent = new Intent(getContext(), EventActivity.class);
                        intent.putExtra(EVENT_KEY, event);
                        startActivity(intent);
                    });
                    layoutManager = new GridLayoutManager(getContext(), 1);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(filterAdapter);
                } else {
                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    filterAdapter.setLoadingState(false);
                    filterAdapter.setData(list);
                }
            }
        }));

        subscriptions.add(presenter.getFilterErrorObservable().subscribe(aVoid -> {
            if (filterAdapter != null) {
                filterAdapter.setLoadingState(false);
                recyclerView.post(() -> filterAdapter.notifyDataSetChanged());
            }
        }));

        subscriptions.add(presenter.getEventsErrorObservable().subscribe(aVoid -> {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyState.setVisibility(View.VISIBLE);
            hideLoading();
        }));


        subscriptions.add(presenter.getErrorFollowFuturEventObservable().subscribe(this::onError));



    }

    private void onError(Throwable t){

        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("Authorization", null).apply();


    }



    private void onSuccess(SectionedDataHolder holder) {
        filterHeader.setVisibility(View.GONE);
        hideLoading();
        HomeFeedActivitiesAdapter adapter = new HomeFeedActivitiesAdapter(holder,presenter, event -> {
            Intent intent = new Intent(getContext(), EventActivity.class);
            intent.putExtra(EVENT_KEY, event);
            startActivity(intent);

        }, index -> {
            String type;
            switch (index) {
                case 0:
                default:
                    //Popular
                    type = EVENT_POPULAR;
                    break;
                case 1:
                    //Nearby
                    type = EVENT_NEARBY;
                    break;
                case 2:
                    //Suited
                    type = EVENT_SUITED;
                    break;
            }
            Intent intent = new Intent(getContext(), EventListingActivity.class);
            intent.putExtra(type, "");
            startActivity(intent);

        });
        layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.removeOnScrollListener(listener);
        adapter.setLayoutManager(layoutManager);
        adapter.shouldShowHeadersForEmptySections(false);
        adapter.shouldShowFooters(false);
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setFilter(FeedFilterRequest request) {
        showLoading();
        if (request == null) {
            presenter.getEventsNoFilter(sectionTitles);
            return;
        }
        presenter.clearData();
        filterHeader.setVisibility(View.VISIBLE);
        request.setLimit(20);
        request.setOffset(0);
        this.request = request;
        filterAdapter = null;
        if (this.listener == null) {
            this.listener = getListener();
        }
        recyclerView.addOnScrollListener(listener);
        presenter.getEvents(getString(R.string.lbl_events_filtered), request);
    }

    private RecyclerView.OnScrollListener getListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!filterAdapter.getLoadState() && presenter.shouldLoadMore()) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        filterAdapter.setLoadingState(true);
                        recyclerView.post(() -> filterAdapter.notifyDataSetChanged());
                        presenter.getEvents(getString(R.string.lbl_events_filtered), HomeFeedActivitiesView.this.request);
                    }
                }
            }
        };
    }

    @Override
    public void onRefresh() {
        showLoading();
        presenter.getEventsObservable().subscribe(this::onSuccess);
        presenter.getEventsNoFilter(sectionTitles);
        presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization",""));
        hideLoading();
    }


    @Override
    public void onResume() {
        super.onResume();
        if(HomeScreenView.getString() != null && HomeScreenView.getString().equalsIgnoreCase("null")){

        }
        else {

        }
    }

}

