package com.example.xcomputers.leaps.homecalendar;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.networking.feed.event.Event;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.event.EventActivity;
import com.example.xcomputers.leaps.event.createEvent.CreateEventActivity;
import com.example.xcomputers.leaps.utils.SectionedDataHolder;

import java.util.List;

import rx.Subscription;

import static com.example.xcomputers.leaps.event.EventActivity.EVENT_KEY;


/**
 * Created by xComputers on 18/06/2017.
 */
@Layout(layoutId = R.layout.home_calendar_view)
public class HomeCalendarUserView extends BaseView<HomeCalendarPresenter> {

    public static final String UPCOMING_ARGUMENT = "HomeCalendarUserView.UPCOMING_ARGUMENT";
    public static final String PAST_ARGUMENT = "HomeCalendarUserView.PAST_ARGUMENT";
    public static final String ATTENDING_ARGUMENT = "hosting";
    public static final String HOSTING_ARGUMENT = "attending";
    private static final String TYPE_ARGUMENT = "HomeCalendarUserView.TYPE_ARGUMENT";
    private static final int CREATE_EVENT_REQUEST = 7;
    private RecyclerView recyclerView;
    private HomeCalendarAdapter adapter;
    private String timeFrame;
    private ImageView createEventBtn;
    private LinearLayoutManager layoutManager;
    private String type;
    private TextView noEvetText;

    public static HomeCalendarUserView newInstance(String type, String timeFrame) {
        if (TextUtils.isEmpty(type)) {
            throw new IllegalArgumentException("You need to supply a valid type argument for HomeCalendarUserView");
        }
        HomeCalendarUserView view = new HomeCalendarUserView();
        Bundle bundle = new Bundle();
        bundle.putString(type, "");
        bundle.putString(TYPE_ARGUMENT, timeFrame);
        view.setArguments(bundle);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noEvetText = (TextView) view.findViewById(R.id.no_event_text);
        noEvetText.setVisibility(View.GONE);
        createEventBtn = (ImageView) view.findViewById(R.id.home_cal_create_event_btn);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_cal_recycler);
        String auth = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", "");
        showLoading();
        if( User.getInstance().getUserId() == 0){
            //Todo check if getUserId is 0
        }

        if (!User.getInstance().isTrainer()) {
            createEventBtn.setVisibility(View.GONE);
        } else {
            createEventBtn.setVisibility(View.VISIBLE);
            createEventBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), CreateEventActivity.class);
                getActivity().startActivityForResult(intent, CREATE_EVENT_REQUEST);
            });
        }
        if (getArguments().containsKey(UPCOMING_ARGUMENT)) {
            timeFrame = UPCOMING_ARGUMENT;
            type = getArguments().getString(TYPE_ARGUMENT);
            presenter.getEvents(type, UPCOMING_ARGUMENT, auth);
        } else if (getArguments().containsKey(PAST_ARGUMENT)) {
            timeFrame = PAST_ARGUMENT;
            type = getArguments().getString(TYPE_ARGUMENT);
            presenter.getEvents(type, PAST_ARGUMENT, auth);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Event", "").isEmpty()) {
            String auth = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", "");
            showLoading();
            if (getArguments().containsKey(UPCOMING_ARGUMENT)) {
                timeFrame = UPCOMING_ARGUMENT;
                type = getArguments().getString(TYPE_ARGUMENT);
                presenter.getEvents(type, UPCOMING_ARGUMENT, auth);
            } else if (getArguments().containsKey(PAST_ARGUMENT)) {
                timeFrame = PAST_ARGUMENT;
                type = getArguments().getString(TYPE_ARGUMENT);
                presenter.getEvents(type, timeFrame, auth);
            }
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().remove("Event").apply();
        }
    }

    void setupRecycler(SectionedDataHolder holder) {

        layoutManager = new LinearLayoutManager(getContext());
        adapter = new HomeCalendarAdapter(holder);
        adapter.shouldShowHeadersForEmptySections(false);
        adapter.shouldShowFooters(false);
        adapter.registerItemClickListener((sectionIndex, relativePosition) -> {
            Event event = holder.getItem(sectionIndex, relativePosition);
            Intent intent = new Intent(getContext(), EventActivity.class);
            intent.putExtra(EVENT_KEY, event);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.addOnScrollListener(getListener());
    }

    @Override
    protected HomeCalendarPresenter createPresenter() {
        return new HomeCalendarPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getAttendingObservable()
                .subscribe(holder -> {
                    if (holder.isEmpty()) {
                        hideLoading();
                        noEvetText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        return;
                    }
                    if (adapter == null) {
                        setupRecycler(holder);
                    } else {
                        adapter.setData(holder);
                    }
                    hideLoading();

                }));

        subscriptions.add(presenter.getHostingObservable()
                .subscribe(sectionedDataHolder -> {
                    if (sectionedDataHolder.isEmpty()) {
                        hideLoading();
                        noEvetText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        return;
                    }
                    if (adapter == null) {
                        setupRecycler(sectionedDataHolder);
                    } else {
                        adapter.setData(sectionedDataHolder);
                    }
                    hideLoading();
                }));
        subscriptions.add(presenter.getGeneralErrorObservable().subscribe(aVoid -> hideLoading()));
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
                        presenter.getEvents(type, timeFrame, PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
                    }
                }
            }
        };
    }
}
