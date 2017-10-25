package com.example.xcomputers.leaps.homefeed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.networking.feed.event.FeedFilterRequest;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.homefeed.activities.HomeFeedActivitiesView;
import com.example.xcomputers.leaps.homefeed.trainers.FeedTrainersView;
import com.example.xcomputers.leaps.homescreen.HomeScreenView;
import com.example.xcomputers.leaps.map.GoogleMapContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

import static com.example.xcomputers.leaps.homefeed.SearchView.SEARCH_RESULT_KEY;

/**
 * Created by xComputers on 10/06/2017.
 */
@Layout(layoutId = R.layout.home_feed_container_view)
public class HomeFeedContainer extends BaseView<EmptyPresenter> implements OnSearchDataCollectedListener {
    public static final String FEED_SEARCH_TRAINER_KEY = "HomeScreenView.FEED_SEARCH_TRAINER_KEY";
    public static final String FEED_SEARCH_EVENT_KEY = "HomeScreenView.FEED_SEARCH_EVENT_KEY";
    public static final String FEED_SEARCH_ORIGIN_KEY = "HomeFeedContainer.FEED_SEARCH_ORIGIN_KEY";

    private static final int POSITION_EVENTS = 0;
    private static final int POSITION_TRAINERS = 1;

    private ViewPager pager;
    private TabLayout tabs;
    private Map<Integer, FeedInsideTab> fragments;
    private FeedFilterRequest request;
    private int currentPosition;
    private TextView searchView;
    private ImageView showMapBtn;
    private static View view1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view1 = view;
        fragments = new HashMap<>();
        request = (FeedFilterRequest) getArguments().getSerializable(SEARCH_RESULT_KEY);
        searchView = (TextView) view.findViewById(R.id.homescreen_search_tv);
        getArguments().remove(SEARCH_RESULT_KEY);
        showMapBtn = (ImageView) view.findViewById(R.id.show_map_btn);
        showMapBtn.setOnClickListener(v->{

            Intent intent = new Intent(getActivity(),GoogleMapContainer.class);
            startActivity(intent);

        });

        pager = (ViewPager) view.findViewById(R.id.home_feed_pager);
        tabs = (TabLayout) view.findViewById(R.id.home_feed_tabs);
        pager.setAdapter(new HomeFeedPagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(pager);
        addTabs();
        tabs.addOnTabSelectedListener(getTabsListener(pager));
        if (getArguments() != null) {
            if (FEED_SEARCH_EVENT_KEY.equals(getArguments().getString(FEED_SEARCH_ORIGIN_KEY))) {
                tabs.getTabAt(POSITION_EVENTS).select();
            } else if (FEED_SEARCH_TRAINER_KEY.equals(getArguments().getString(FEED_SEARCH_ORIGIN_KEY))) {
                tabs.getTabAt(POSITION_TRAINERS).select();
            }
        }
        if (request != null) {
            String upTo = getString(R.string.lbl_up_to) + " " + request.getDistance() + getString(R.string.lbl_km);
            String tags = request.getTags().size() > 0 ? request.getTags().get(0) : null;
            if (tags != null) {
                searchView.setText(upTo + " • " + tags + " • " + getTime(request.getTime()));
            } else {
                searchView.setText(upTo + " • " + getTime(request.getTime()));
            }
        } else {
            searchView.setText(getString(R.string.homescreen_search_bar_lbl));
        }

       view.findViewById(R.id.feed_header_lbl).setVisibility(View.GONE);


       searchView.setOnClickListener(v -> {

           searchView.setClickable(false);
            Bundle bundle = new Bundle();
                String value = "";
                if (tabs.getSelectedTabPosition() == POSITION_EVENTS) {
                    value = FEED_SEARCH_EVENT_KEY;
                } else if (tabs.getSelectedTabPosition() == POSITION_TRAINERS) {
                    value = FEED_SEARCH_TRAINER_KEY;
                }
            bundle.putString(FEED_SEARCH_ORIGIN_KEY, value);

            HomeScreenView.getTabLayout().setVisibility(View.GONE);
            openFragment(SearchView.class, bundle);
       });




    }

    public static View getHomeView(){
        return view1;
    }

    private String getTime(FeedFilterRequest.DateSelection dateSelection) {
        String time = "";
        switch (dateSelection) {
            case TODAY:
                time = getString(R.string.lbl_today);
                break;
            case NEXT3:
                time = getString(R.string.lbl_next3);
                break;
            case NEXT5:
                time = getString(R.string.lbl_next5);
                break;
            case ALL:
                time = getString(R.string.lbl_all);
                break;
        }
        return time;
    }

    private TabLayout.ViewPagerOnTabSelectedListener getTabsListener(ViewPager pager) {


        return new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                currentPosition = tab.getPosition();
                if (tab.getCustomView() != null)
                    ((TextView) tab.getCustomView()).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                if (tab.getCustomView() != null)
                    ((TextView) tab.getCustomView()).setTextColor(ContextCompat.getColor(getContext(), R.color.inactive_blue));
            }
        };
    }

    private void addTabs() {
        for (int i = 0; i < 2; i++) {
            TabLayout.Tab created = tabs.getTabAt(i);
            if (created != null) {
                created.setCustomView(R.layout.custom_tab);
                if (i == 0) {
                    if (created.getCustomView() != null)
                        ((TextView) created.getCustomView()).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
                }
            }
        }
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
    }

    @Override
    public FeedFilterRequest onSearchDataCollected() {
        return request;
    }

    @Override
    public String getOrigin() {
        String value = "";
        if (currentPosition == POSITION_EVENTS) {
            if (FEED_SEARCH_EVENT_KEY.equals(getArguments().get(FEED_SEARCH_ORIGIN_KEY))) {
                value = FEED_SEARCH_EVENT_KEY;
            }
        } else if (tabs.getSelectedTabPosition() == POSITION_TRAINERS) {
            if (FEED_SEARCH_TRAINER_KEY.equals(getArguments().get(FEED_SEARCH_ORIGIN_KEY))) {
                value = FEED_SEARCH_TRAINER_KEY;
            }
        }
        return value;
    }

    @Override
    public void resetSearch() {
        searchView.setText(getString(R.string.homescreen_search_bar_lbl));
    }

    private class HomeFeedPagerAdapter extends FragmentPagerAdapter {

        public HomeFeedPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    FeedInsideTab myFragment = (FeedInsideTab) FeedTrainersView.instantiate(getContext(), FeedTrainersView.class.getCanonicalName(), new Bundle());
                    fragments.put(position, myFragment);
                    return (Fragment) myFragment;
                case 0:
                default:
                    FeedInsideTab frag = (FeedInsideTab) HomeFeedActivitiesView.instantiate(getContext(), HomeFeedActivitiesView.class.getCanonicalName(), new Bundle());
                    fragments.put(position, frag);
                    return (Fragment) frag;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            fragments.remove(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return "TRAINERS";
                case 0:
                default:
                    return "ACTIVITIES";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (fragments.get(0) != null && data !=null) {
            ((Fragment) fragments.get(0)).onActivityResult(requestCode, resultCode, data);
        }
        if (fragments.get(1) != null && data != null) {
            ((Fragment) fragments.get(1)).onActivityResult(requestCode, resultCode, data);
        }
    }

}
