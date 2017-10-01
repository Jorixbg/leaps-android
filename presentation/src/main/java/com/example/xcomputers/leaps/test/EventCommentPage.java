package com.example.xcomputers.leaps.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by Ivan on 9/21/2017.
 */
@Layout(layoutId = R.layout.comment_event_page)
public class EventCommentPage extends BaseView<EmptyPresenter> {

    private RecyclerView commentRecyclerView;
    private CommentEventAdapter adapter;
    private List<EventComment> comments = new ArrayList<>();

    private LinearLayoutManager layoutManager;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        commentRecyclerView = (RecyclerView) view.findViewById(R.id.comment_recyclerview);


        layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        adapter = new CommentEventAdapter(getContext(),comments);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(commentRecyclerView);
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(layoutManager);

    }


    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }
}
