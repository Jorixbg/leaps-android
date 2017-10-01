package com.example.xcomputers.leaps.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;

import java.util.List;

import rx.Subscription;

/**
 * Created by Ivan on 9/21/2017.
 */

@Layout(layoutId = R.layout.following_event_view)
public class FollowingEventView extends BaseView<EmptyPresenter> {

    private RecyclerView recyclerView;
    private RelativeLayout emptyState;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyState = (RelativeLayout) view.findViewById(R.id.following_event_empty_state);
        recyclerView = (RecyclerView) view.findViewById(R.id.following_event_recycler);



    }

    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {


    }




}