package com.example.xcomputers.leaps.homefeed.trainers;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.networking.feed.event.FeedFilterRequest;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.login.UserResponse;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.event.EventActivity;
import com.example.xcomputers.leaps.homefeed.FeedInsideTab;
import com.example.xcomputers.leaps.homefeed.HomeFeedContainer;
import com.example.xcomputers.leaps.homefeed.OnSearchDataCollectedListener;
import com.example.xcomputers.leaps.trainer.TrainerActivity;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.LoginResponseToUserTypeMapper;
import com.example.xcomputers.leaps.welcome.WelcomeActivity;

import java.util.Collections;
import java.util.List;

import rx.Subscription;

import static android.app.Activity.RESULT_OK;
import static com.example.xcomputers.leaps.homefeed.HomeFeedContainer.FEED_SEARCH_TRAINER_KEY;
import static com.example.xcomputers.leaps.trainer.TrainerActivity.ENTITY_KEY;

/**
 * Created by xComputers on 15/06/2017.
 */
@Layout(layoutId = R.layout.home_feed_trainers)
public class FeedTrainersView extends BaseView<FeedTrainersPresenter> implements FeedInsideTab,SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView trainersRecycler;
    private TextView header;
    private TextView filterHeaderText;
    private LinearLayoutManager layoutManager;
    private TextView filterCancel;
    private FeedTrainersFilterAdapter filterAdapter;
    private FeedFilterRequest request;
    private RecyclerView.OnScrollListener listener;
    private Entity trainer;
    private RelativeLayout emptyState;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int FEED_TRAINER_VIEW_REQUEST_CODE = 3;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyState = (RelativeLayout) view.findViewById(R.id.empty_state);
        trainersRecycler = (RecyclerView) view.findViewById(R.id.feed_trainers_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        header = (TextView) view.findViewById(R.id.feed_trainers_header);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeTrainers);
        swipeRefreshLayout.setOnRefreshListener(this);
        LayoutInflater factory = getLayoutInflater();

        View homeFeedContainerView = factory.inflate(R.layout.home_feed_container_view, null);
        filterCancel = (TextView) homeFeedContainerView.findViewById(R.id.feed_header_lbl);
        filterHeaderText = (TextView) view.findViewById(R.id.home_feed_header_name);

        if(HomeFeedContainer.getCurrentPosition() == 1) {
            HomeFeedContainer.getHomeView().findViewById(R.id.feed_header_lbl).setOnClickListener(v -> {
                showLoading();
                emptyState.setVisibility(View.GONE);
                trainersRecycler.setVisibility(View.VISIBLE);
                presenter.clearData();
                presenter.getTrainersNoFilter();
                filterHeaderText.setVisibility(View.INVISIBLE);
                if (getParentFragment() != null) {
                    ((OnSearchDataCollectedListener) getParentFragment()).resetSearch();
                }
                HomeFeedContainer.getHomeView().findViewById(R.id.homescreen_search_tv).setClickable(true);
                HomeFeedContainer.getHomeView().findViewById(R.id.feed_header_lbl).setVisibility(View.GONE);
            });
        }
        if (getArguments() != null) {
            if (FEED_SEARCH_TRAINER_KEY.equals(((OnSearchDataCollectedListener) getParentFragment()).getOrigin())) {
                setFilter(((OnSearchDataCollectedListener) getParentFragment()).onSearchDataCollected());
                HomeFeedContainer.getHomeView().findViewById(R.id.feed_header_lbl).setVisibility(View.VISIBLE);
            } else {
                showLoading();
                presenter.getTrainersNoFilter();
            }
        }

        FeedTrainersAdapter adapter = new FeedTrainersAdapter(Collections.emptyList(), trainer -> {
            if (TextUtils.isEmpty(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""))) {
                getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class).putExtra(ENTITY_KEY, trainer), FEED_TRAINER_VIEW_REQUEST_CODE);
                return;
            }
            Intent intent = new Intent(getContext(), EventActivity.class);
            intent.putExtra(ENTITY_KEY, trainer);
            startActivity(intent);
        });
        trainersRecycler.setAdapter(adapter);
        trainersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        trainersRecycler.addItemDecoration(decoration);

        //  filterHeaderRl = (RelativeLayout) view.findViewById(R.id.filter_header);
        // filterCancel = (TextView) filterHeaderRl.findViewById(R.id.feed_header_lbl);
        // filterHeaderRl.setVisibility(View.GONE);
    }

    @Override
    protected FeedTrainersPresenter createPresenter() {
        return new FeedTrainersPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getTrainersObservable().subscribe(entities -> {
            hideLoading();
            FeedTrainersAdapter adapter = new FeedTrainersAdapter(entities, trainer -> {
                if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null) {
                    this.trainer = trainer;
                    getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class), FEED_TRAINER_VIEW_REQUEST_CODE);
                    return;
                }
                Intent intent = new Intent(getContext(), TrainerActivity.class);
                intent.putExtra(ENTITY_KEY, trainer);
                startActivity(intent);
            });
          //  filterHeaderRl.setVisibility(View.GONE);
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
            header.setVisibility(View.VISIBLE);
            trainersRecycler.setAdapter(adapter);
            trainersRecycler.setLayoutManager(layoutManager);
        }));

        subscriptions.add(presenter.getTrainersFilterObservable()
                .subscribe(entities -> {
                    if (entities.getTrainers().isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        trainersRecycler.setVisibility(View.GONE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                        trainersRecycler.setVisibility(View.VISIBLE);

                        if (filterAdapter == null) {
                            filterAdapter = new FeedTrainersFilterAdapter(entities.getTrainers(), trainer -> {
                                if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null) {
                                    this.trainer = trainer;
                                    getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class), FEED_TRAINER_VIEW_REQUEST_CODE);
                                    return;
                                }
                                Intent intent = new Intent(getContext(), TrainerActivity.class);
                                intent.putExtra(ENTITY_KEY, trainer);
                                startActivity(intent);

                            });
                            filterHeaderText.setText("Trainers filtered " + String.valueOf(entities.getTotalResults()));
                            trainersRecycler.setAdapter(filterAdapter);
                        } else {
                            filterAdapter.setLoadingState(false);
                            filterAdapter.setData(entities.getTrainers());
                            if (!(trainersRecycler.getAdapter() instanceof FeedTrainersFilterAdapter)) {
                                trainersRecycler.setAdapter(filterAdapter);
                            }
                        }
                        if (listener == null) {
                            listener = getListener();
                        }
                        trainersRecycler.addOnScrollListener(listener);
                    }
                }));
        subscriptions.add(presenter.getGeneralErrorObservable().subscribe(aVoid -> hideLoading()));

        subscriptions.add(presenter.getUsersFollowing().subscribe(this::SuccessFollowing));



    }

    private void SuccessFollowing(UserResponse userResponse){
        LoginResponseToUserTypeMapper.map(userResponse);
        EntityHolder.getInstance().setEntity(userResponse);
    }

    @Override
    public void setFilter(FeedFilterRequest request) {
        if (request == null) {
            showLoading();
            presenter.getTrainersNoFilter();
            return;
        }
        this.request = request;
        showLoading();
        presenter.clearData();
      //  filterHeaderRl.setVisibility(View.VISIBLE);
        header.setVisibility(View.GONE);
        request.setLimit(20);
        request.setOffset(0);
        presenter.getTrainers(request);
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

                if (!filterAdapter.getLoadingState() && presenter.shouldLoadMore()) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        filterAdapter.setLoadingState(true);
                        presenter.getTrainers(request);
                    }
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FEED_TRAINER_VIEW_REQUEST_CODE) {
                Intent intent = new Intent(getContext(), TrainerActivity.class);
                intent.putExtra(ENTITY_KEY, trainer);
                startActivity(intent);
            }
        }
    }

    public void onRefresh() {
        showLoading();
        presenter.getTrainersNoFilter();
        presenter.getFollowing(User.getInstance().getUserId(),PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
        hideLoading();
    }

}
