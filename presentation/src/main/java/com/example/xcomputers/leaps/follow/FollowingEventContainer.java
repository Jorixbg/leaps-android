package com.example.xcomputers.leaps.follow;

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
import android.widget.TextView;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.homefeed.activities.HomeFeedActivitiesView;
import com.example.xcomputers.leaps.homefeed.trainers.FeedTrainersView;
import com.example.xcomputers.leaps.test.TestView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Ivan on 9/21/2017.
 */

@Layout(layoutId = R.layout.following_events_tab)
public class FollowingEventContainer extends BaseView<EmptyPresenter>{

    private static final int POSITION_EVENTS = 0;
    private static final int POSITION_TRAINERS = 1;

    private ViewPager pager;
    private TabLayout tabs;
    private Map<Integer, Fragment> fragments;
    private int currentPosition;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragments = new HashMap<>();
        pager = (ViewPager) view.findViewById(R.id.following_feed_pager);
        tabs = (TabLayout) view.findViewById(R.id.following_feed_tabs);
        pager.setAdapter(new FollowingEventContainer.HomeFeedPagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(pager);
        addTabs();
        tabs.addOnTabSelectedListener(getTabsListener(pager));
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


    private class HomeFeedPagerAdapter extends FragmentPagerAdapter {

        public HomeFeedPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    Fragment myFragment = (Fragment) FeedTrainersView.instantiate(getContext(), FollowingPastEventView.class.getCanonicalName(), new Bundle());
                    fragments.put(position, myFragment);
                    return (Fragment) myFragment;
                case 0:
                default:
                    Fragment frag = (Fragment) HomeFeedActivitiesView.instantiate(getContext(), FollowingEventView.class.getCanonicalName(), new Bundle());
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
                    return "PAST";
                case 0:
                default:
                    return "UPCOMING";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (fragments.get(0) != null) {
            ((Fragment) fragments.get(0)).onActivityResult(requestCode, resultCode, data);
        }
        if (fragments.get(1) != null) {
            ((Fragment) fragments.get(1)).onActivityResult(requestCode, resultCode, data);
        }
    }
}
