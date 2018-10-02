package club.leaps.presentation.event;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 10/30/2017.
 */

public class EventViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private List<String> images = new ArrayList<>();

    private int NUM_PAGES = 0;
    private static final int FIRST_PAGE = 0;
    private static final int SECOND_PAGE = 1;
    private static final int THIRD_PAGE = 2;
    private static final int FOURTH_PAGE = 3;
    private static final int FIFTH_PAGE = 4;

    public EventViewPagerAdapter(Context context, FragmentManager fm, List<String> images) {
        super(fm);
        this.context = context;
        this.images = images;
        NUM_PAGES = images.size();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FIRST_PAGE:
            default:
                return new ViewPagerImage(images.get(FIRST_PAGE));
            case SECOND_PAGE:
                return new ViewPagerImage(images.get(SECOND_PAGE));
            case THIRD_PAGE:
                return new ViewPagerImage(images.get(THIRD_PAGE));
            case FOURTH_PAGE:
                return  new ViewPagerImage(images.get(FOURTH_PAGE));
            case FIFTH_PAGE:
                return  new ViewPagerImage(images.get(FIFTH_PAGE));
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}

