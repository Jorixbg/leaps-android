package club.leaps.presentation.profile.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import club.leaps.presentation.R;
import club.leaps.presentation.base.IActivity;
import club.leaps.presentation.base.IBaseView;

/**
 * Created by xComputers on 23/07/2017.
 */

public class TutorialActivity extends AppCompatActivity implements IActivity{

    private ViewPager viewPager;
    private ScreenSlidePagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        viewPager = (ViewPager) findViewById(R.id.walkthrough_pager);
        adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            finish();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        }
    }


    public void setPage(int page) {
        viewPager.setCurrentItem(page, true);
    }

    @Override
    public <View extends IBaseView> void openFragment(Class<View> viewClass, Bundle arguments) {
        //Empty
    }

    @Override
    public <View extends IBaseView> void openFragment(Class<View> viewClass, Bundle arguments, boolean addToBackStack) {
        //Empty
    }

    @Override
    public void showLoading() {
        //Empty
    }

    @Override
    public void hideLoading() {
        //Empty
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 5;
        private static final int FIRST_PAGE = 0;
        private static final int SECOND_PAGE = 1;
        private static final int THIRD_PAGE = 2;
        private static final int FOURTH_PAGE = 3;
        private static final int FIFTH_PAGE = 4;

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FIRST_PAGE:
                default:
                    return new TutorialFirstScreen();
                case SECOND_PAGE:
                    return new TutorialSecondScreen();
                case THIRD_PAGE:
                    return new TutorialThirdScreen();
                case FOURTH_PAGE:
                    return new TutorialFourthScreen();
                case FIFTH_PAGE:
                    return new TutorialFifthScreen();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
