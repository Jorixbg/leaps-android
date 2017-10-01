package com.example.xcomputers.leaps.homecalendar;


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
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

import static android.view.View.GONE;
import static com.example.xcomputers.leaps.homecalendar.HomeCalendarUserView.ATTENDING_ARGUMENT;
import static com.example.xcomputers.leaps.homecalendar.HomeCalendarUserView.HOSTING_ARGUMENT;
import static com.example.xcomputers.leaps.homecalendar.HomeCalendarUserView.PAST_ARGUMENT;
import static com.example.xcomputers.leaps.homecalendar.HomeCalendarUserView.UPCOMING_ARGUMENT;

/**
 * Created by xComputers on 18/06/2017.
 */
@Layout(layoutId = R.layout.home_cal_container_view)
public class HomeCalendarContainer extends BaseView<EmptyPresenter> {

    private ViewPager pager;
    private TabLayout tabs;
    private HomeCalendarPagerAdapter adapter;
    private AppCompatSpinner spinner;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = (ViewPager) view.findViewById(R.id.home_cal_pager);
        tabs = (TabLayout) view.findViewById(R.id.home_cal_tabs);
        spinner = (AppCompatSpinner) view.findViewById(R.id.calendar_spinner);
        if (!User.getInstance().isTrainer()) {
            view.findViewById(R.id.calendar_spinner).setVisibility(GONE);
        }
        List<String> list = new ArrayList<>();
        list.add("Attending");
        list.add("Hosting");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        changeSpinnerArrowColor();
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                adapter = new HomeCalendarPagerAdapter(getChildFragmentManager());
                pager.setAdapter(adapter);
                tabs.setupWithViewPager(pager);
                addTabs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Empty
            }
        });
        spinner.setSelection(0, false);
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
                if(tab.getCustomView() != null) {
                    ((TextView) tab.getCustomView()).setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
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
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return HomeCalendarUserView.newInstance(PAST_ARGUMENT, spinner.getSelectedItemPosition() == 0 ? ATTENDING_ARGUMENT : HOSTING_ARGUMENT);
                case 0:
                default:
                    return HomeCalendarUserView.newInstance(UPCOMING_ARGUMENT, spinner.getSelectedItemPosition() == 0 ? ATTENDING_ARGUMENT : HOSTING_ARGUMENT);
            }
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
}