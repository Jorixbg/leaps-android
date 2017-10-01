package com.example.xcomputers.leaps.homefeed;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.networking.feed.event.FeedFilterRequest;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.TagsHolder;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.homescreen.HomeScreenView;
import com.example.xcomputers.leaps.utils.CustomWhiteButton;
import com.example.xcomputers.leaps.utils.TagView;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Subscription;

import static com.example.xcomputers.leaps.homefeed.HomeFeedContainer.FEED_SEARCH_ORIGIN_KEY;


/**
 * Created by xComputers on 18/06/2017.
 */
@Layout(layoutId = R.layout.search_view)
public class SearchView extends BaseView<EmptyPresenter> {

    //Gesture code
    private GestureDetector gesture;

    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    //





    public static final String SEARCH_RESULT_KEY = "SearchView.SEARCH_RESULT_KEY";

    private CustomWhiteButton searchBtn;
    private FlexboxLayout flowLayout;
    private TextView seekProgressView;
    private Button todayBtn;
    private Button next3Btn;
    private Button next5Btn;
    private SeekBar seekBar;
    private EditText searchET;
    private ScrollView scrollView;

    private FeedFilterRequest.DateSelection dateSelection;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        gesture = new GestureDetector(getActivity(), new MyGestureListener());
        //

        scrollView =(ScrollView) view.findViewById(R.id.scroll_search_view);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gesture.onTouchEvent(event);
                return true;
            }
        });


        seekProgressView = (TextView) view.findViewById(R.id.search_distance_tv);
        seekBar = (SeekBar) view.findViewById(R.id.search_seek_bar);
        seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.light_blue), PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }
                seekProgressView.setText(progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Empty
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Empty
            }
        });
        searchBtn = (CustomWhiteButton) view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(l -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(SEARCH_RESULT_KEY, collectSearchResults());
            bundle.putString(FEED_SEARCH_ORIGIN_KEY, getArguments().getString(FEED_SEARCH_ORIGIN_KEY));
            openFragment(HomeScreenView.class, bundle);
        });
        flowLayout = (FlexboxLayout) view.findViewById(R.id.search_flow_layout);
        List<String> tags = TagsHolder.getInstance().getTags();
        for (String tag : tags) {
            TagView tagView = new TagView(getContext());
            tagView.setText(tag);
            tagView.setOnClickListener(v -> tagView.toggle());
            flowLayout.addView(tagView);
        }
        todayBtn = (Button) view.findViewById(R.id.search_today_btn);
        todayBtn.setOnClickListener(v -> {

            todayBtn.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            next5Btn.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteAlpha));
            next3Btn.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteAlpha));
            this.dateSelection = FeedFilterRequest.DateSelection.TODAY;
        });
        next3Btn = (Button) view.findViewById(R.id.search_next_3_btn);
        next3Btn.setOnClickListener(v -> {
            todayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteAlpha));
            next5Btn.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteAlpha));
            next3Btn.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            this.dateSelection = FeedFilterRequest.DateSelection.NEXT3;
        });
        next5Btn = (Button) view.findViewById(R.id.search_next_5_btn);
        next5Btn.setOnClickListener(v -> {
            todayBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteAlpha));
            next5Btn.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            next3Btn.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteAlpha));
            this.dateSelection = FeedFilterRequest.DateSelection.NEXT5;
        });
        todayBtn.performClick();
        view.findViewById(R.id.search_reset_btn).setOnClickListener(v -> resetAll());
        searchET = (EditText) view.findViewById(R.id.search_tv);
    }

    private void resetAll() {
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            TagView tagView = (TagView) flowLayout.getChildAt(i);
            if (tagView.isSelected()) {
                tagView.toggle();
            }
        }
        seekBar.setProgress(20);
        todayBtn.performClick();
        searchET.setText("");
    }

    private FeedFilterRequest collectSearchResults() {

        List<String> selectedTags = new ArrayList<>();
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            TagView tagView = (TagView) flowLayout.getChildAt(i);
            if (tagView.isSelected()) {
                selectedTags.add((String) tagView.getText());
            }
        }
        String distanceValue = seekProgressView.getText().toString().split(" ")[0];
        int distance = Integer.valueOf(distanceValue);
        long beginDate = Calendar.getInstance().getTimeInMillis();
        long endDate = constructDate();
        String seachTVString = searchET.getText().toString();

        //TODO min/max price
        return new FeedFilterRequest(seachTVString, User.getInstance().getLongtitude(), User.getInstance().getLattitude(), distance, selectedTags, beginDate, endDate, dateSelection);
    }


    private long constructDate() {
        Calendar calendar = Calendar.getInstance();
        long date = 0;
        switch (dateSelection) {
            case TODAY:
                date = calendar.getTimeInMillis();
                break;
            case NEXT3:
                calendar.add(Calendar.DATE, 3);
                date = calendar.getTimeInMillis();
                break;
            case NEXT5:
                calendar.add(Calendar.DATE, 5);
                date = calendar.getTimeInMillis();
                break;
        }
        return date;
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (e1.getY() - e2.getY() > SWIPE_MAX_OFF_PATH  && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
                    getActivity().onBackPressed();
                    return true;
                }
                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }

            } catch (Exception e) {

            }
            return false;
        }

    }


}
