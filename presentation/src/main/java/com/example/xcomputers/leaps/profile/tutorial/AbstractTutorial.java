package com.example.xcomputers.leaps.profile.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.utils.CustomTextView;

import java.util.List;

import rx.Subscription;


/**
 * Created by xComputers on 23/07/2017.
 */

public class AbstractTutorial extends BaseView<EmptyPresenter> {

    private Button skipBtn;
    protected Button nextBtn;
    protected RelativeLayout backgroundLayout;
    protected CustomTextView headerTv;
    protected TextView contentTV;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        skipBtn = (Button) view.findViewById(R.id.walkthrough_skip_btn);
        nextBtn = (Button) view.findViewById(R.id.walkthrough_next_btn);
        backgroundLayout = (RelativeLayout) view.findViewById(R.id.walkthrough_background);
        headerTv = (CustomTextView) view.findViewById(R.id.walkthrough_header_tv);
        contentTV = (TextView) view.findViewById(R.id.walkthrough_explanation_tv);

        skipBtn.setOnClickListener(v-> getActivity().finish());
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }
}
