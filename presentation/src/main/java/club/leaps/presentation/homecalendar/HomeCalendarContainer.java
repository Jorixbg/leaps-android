package club.leaps.presentation.homecalendar;


import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.TextView;

import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;
import club.leaps.presentation.base.Layout;

import java.util.List;

import rx.Subscription;

import static android.view.View.GONE;
import static club.leaps.presentation.homecalendar.HomeCalendarUserView.ATTENDING_ARGUMENT;
import static club.leaps.presentation.homecalendar.HomeCalendarUserView.HOSTING_ARGUMENT;
import static club.leaps.presentation.homecalendar.HomeCalendarUserView.PAST_ARGUMENT;
import static club.leaps.presentation.homecalendar.HomeCalendarUserView.UPCOMING_ARGUMENT;

/**
 * Created by xComputers on 18/06/2017.
 */
@Layout(layoutId = R.layout.home_cal_container_view)
public class HomeCalendarContainer extends BaseView<EmptyPresenter> implements SwipeRefreshLayout.OnRefreshListener {

    private ViewPager pager;
    private TabLayout tabs;
    private HomeCalendarPagerAdapter adapter;
    private AppCompatSpinner spinner;
    private String type ;
    private int pos;
    private TextView hostingTV;
    private TextView attendingTV;
    private boolean clicked;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = (ViewPager) view.findViewById(R.id.home_cal_pager);
        tabs = (TabLayout) view.findViewById(R.id.home_cal_tabs);
        spinner = (AppCompatSpinner) view.findViewById(R.id.calendar_spinner);
        hostingTV = (TextView) view.findViewById(R.id.hosting_tv);
        attendingTV = (TextView) view.findViewById(R.id.attending_tv);
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipeCalendarAttendingEvents);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (!User.getInstance().isTrainer()) {
            view.findViewById(R.id.calendar_spinner).setVisibility(GONE);
        }

        attendingTV.setOnClickListener(v->{
            attendingTV.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.round_white_button_shape_right));
            attendingTV.setTextColor(ContextCompat.getColor(getContext(),R.color.primaryBlue));
            hostingTV.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.round_transparent_button_left));
            hostingTV.setTextColor(ContextCompat.getColor(getContext(),R.color.colorWhite));
            adapter = new HomeCalendarPagerAdapter(getChildFragmentManager());
            clicked = false;
            pager.setAdapter(adapter);
            tabs.setupWithViewPager(pager);


        });

        hostingTV.setOnClickListener(v->{
            hostingTV.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.round_white_button_shape_left));
            hostingTV.setTextColor(ContextCompat.getColor(getContext(),R.color.primaryBlue));
            attendingTV.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.round_transparent_button_right));
            attendingTV.setTextColor(ContextCompat.getColor(getContext(),R.color.colorWhite));
            clicked = true;
            adapter = new HomeCalendarPagerAdapter(getChildFragmentManager());
            pager.setAdapter(adapter);
            tabs.setupWithViewPager(pager);

        });

        attendingTV.performClick();
        addTabs();

    }

    private void changeSpinnerArrowColor(){
        Drawable background = spinner.getBackground();
        if(background != null){
            Drawable.ConstantState constantState = background.getConstantState();
            if(constantState != null){
                Drawable spinnerDrawable = spinner.getBackground().getConstantState().newDrawable();

                spinnerDrawable.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    spinner.setBackground(spinnerDrawable);
                }else{
                    spinner.setBackgroundDrawable(spinnerDrawable);
                }
            }
        }
    }

    private void addTabs() {
        for (int i = 0; i < 2; i++) {
            TabLayout.Tab created = tabs.getTabAt(i);
            if (created != null) {
                created.setCustomView(R.layout.custom_tab);
                if(i == 0){
                    if(created.getCustomView() != null)
                    ((TextView)created.getCustomView()).setTextColor(ContextCompat.getColor(getContext(),R.color.primaryBlue));
                }
            }
        }
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if(tab.getPosition() == 0){
                    type = UPCOMING_ARGUMENT;
                }
                else {
                    type = PAST_ARGUMENT;
                }
                if(tab.getCustomView() != null) {
                    ((TextView) tab.getCustomView()).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                pos = tab.getPosition();
                if(tab.getCustomView() != null) {
                    ((TextView) tab.getCustomView()).setTextColor(ContextCompat.getColor(getContext(), R.color.inactive_blue));
                }
            }
        });
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }

    private class HomeCalendarPagerAdapter extends FragmentStatePagerAdapter {

        public HomeCalendarPagerAdapter(FragmentManager fm) {
            super(fm);
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        }



        @Override
        public Fragment getItem(int position) {

                switch (position) {
                    case 1:
                        return HomeCalendarUserView.newInstance(PAST_ARGUMENT, clicked == false ? ATTENDING_ARGUMENT : HOSTING_ARGUMENT);
                    case 0:
                    default:
                        return HomeCalendarUserView.newInstance(UPCOMING_ARGUMENT, clicked == false ? ATTENDING_ARGUMENT : HOSTING_ARGUMENT);
                }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getString(R.string.past_event_title);
                case 0:
                default:
                    return getString(R.string.upcoming_event_title);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }


    }

   @Override
    public void onResume() {
        super.onResume();
            if(type!=null && type.equalsIgnoreCase(PAST_ARGUMENT)){
                pos = 1;
            }
            if(type!=null && type.equalsIgnoreCase(UPCOMING_ARGUMENT)){
                pos = 0;
            }
            adapter = new HomeCalendarPagerAdapter(getChildFragmentManager());
            pager.setAdapter(adapter);

            TabLayout.Tab tab = tabs.getTabAt(pos);
            tab.select();
    }

    @Override
    public void onRefresh() {
        if(type!=null && type.equalsIgnoreCase(PAST_ARGUMENT)){
            pos = 1;
        }
        if(type!=null && type.equalsIgnoreCase(UPCOMING_ARGUMENT)){
            pos = 0;
        }
        adapter = new HomeCalendarPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);

        TabLayout.Tab tab = tabs.getTabAt(pos);
        tab.select();

    }

}