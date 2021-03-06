package club.leaps.presentation.event;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesFilterAdapter;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesPresenter;
import club.leaps.networking.feed.event.Event;
import club.leaps.presentation.R;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesFilterAdapter;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesPresenter;

import java.util.ArrayList;
import java.util.List;

import static club.leaps.presentation.event.EventActivity.EVENT_KEY;


/**
 * Created by xComputers on 12/07/2017.
 */

public class EventListingActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    public static final String EVENT_POPULAR = "EventListingActivity.EVENT_POPULAR";
    public static final String EVENT_SUITED = "EventListingActivity.EVENT_SUITED";
    public static final String EVENT_NEARBY = "EventListingActivity.EVENT_NEARBY";
    public static final String EVENT_PAST = "EventListingActivity.EVENT_PAST";

    private RecyclerView recyclerView;
    private TextView header;
    private HomeFeedActivitiesFilterAdapter adapter;
    private EventsListingPresenter presenter;
    private HomeFeedActivitiesPresenter presenter2;
    private EventsListingPresenter.Type type;
    private ProgressBar loadingView;
    private RelativeLayout mainContainer;
    private LinearLayoutManager layoutManager;
    private RelativeLayout emptyState;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_listing_activity);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        loadingView = (ProgressBar) findViewById(R.id.progress_bar);
        emptyState = (RelativeLayout) findViewById(R.id.empty_state);
        mainContainer = (RelativeLayout) findViewById(R.id.event_listing_container);
        presenter = new EventsListingPresenter();
        presenter2 = new HomeFeedActivitiesPresenter();
        recyclerView = (RecyclerView) findViewById(R.id.event_listing_recycler);
        header = (TextView) findViewById(R.id.event_listing_header);
        layoutManager = new LinearLayoutManager(this);
        if (savedInstanceState != null) {
            return;
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeEvents);
        swipeRefreshLayout.setOnRefreshListener(this);
        presenter2.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization",""));

        //noinspection unchecked
        List<Event> eventsList = (List<Event>) getIntent().getSerializableExtra(EVENT_PAST);
        if (eventsList != null) {
            header.setText(R.string.lbl_past_events);
            setupRecycler(eventsList);
            return;
        }

        showLoading();
        setupPresenterCallbacks();
        eventType();
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

                if (!adapter.getLoadState() && presenter.shouldLoadMore()) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        adapter.setLoadingState(true);
                        recyclerView.post(() -> adapter.notifyDataSetChanged());
                        presenter.getEvents(type);
                    }
                }
            }
        };
    }

    private void eventType(){
        if (getIntent().getStringExtra(EVENT_POPULAR) != null) {
            this.type = EventsListingPresenter.Type.POPULAR;
            header.setText(getString(R.string.lbl_popular_events));
        } else if (getIntent().getStringExtra(EVENT_SUITED) != null) {
            header.setText(getString(R.string.lbl_selected_events));
            this.type = EventsListingPresenter.Type.SUITED;
        } else if (getIntent().getStringExtra(EVENT_NEARBY) != null) {
            this.type = EventsListingPresenter.Type.NEARBY;
            header.setText(getString(R.string.lbl_events_nearby));
        }
        presenter.getEvents(type);
    }

    private void setupPresenterCallbacks(){
        presenter.getEventsObservable().subscribe(events -> {
            hideLoading();
            if (adapter == null) {
                List<Event> list = new ArrayList<Event>();
                list.addAll(events);
                setupRecycler(list);
                recyclerView.addOnScrollListener(getListener());
            } else {
                adapter.setLoadingState(false);
                List<Event> data = new ArrayList<Event>();
                data.addAll(events);
                setupRecycler(data);
            }
        });
        presenter.getEventsErrorObservable().subscribe(aVoid -> {
            if (adapter != null) {
                adapter.setLoadingState(false);
            }
            hideLoading();
        });
    }

    private void setupRecycler(List<Event> events) {
        if(events.isEmpty()){
            recyclerView.setVisibility(View.INVISIBLE);
            emptyState.setVisibility(View.VISIBLE);
        }else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new HomeFeedActivitiesFilterAdapter(events,presenter2,event -> {
                Intent intent = new Intent(EventListingActivity.this, EventActivity.class);
                intent.putExtra(EVENT_KEY, event);
                startActivity(intent);

            });
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
        }

        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        mainContainer.setVisibility(View.GONE);
    }

    private void hideLoading() {
        mainContainer.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }


    @Override
    public void onRefresh() {
        showLoading();
        eventType();
        presenter2.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(this).getString("Authorization",""));
        setupPresenterCallbacks();
        hideLoading();
    }

}
