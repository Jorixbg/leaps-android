package com.example.xcomputers.leaps.comment;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.Toast;

import com.example.networking.feed.event.Event;
import com.example.networking.test.EventRatingResponse;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by Ivan on 9/21/2017.
 */
@Layout(layoutId = R.layout.comment_event_page)
public class EventCommentPage extends BaseView<EventRatingPresenter> {

    private RecyclerView commentRecyclerView;
    private CommentEventAdapter adapter;
    private List<EventRatingResponse> comments;



    private Event event;
    private LinearLayoutManager layoutManager;
    private int pageNumber =1;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        event = (Event) getArguments().getSerializable("eventObj");
        commentRecyclerView = (RecyclerView) view.findViewById(R.id.comment_recyclerview);
        comments  = new ArrayList<>();

        layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        comments = (List<EventRatingResponse>) getArguments().getSerializable("eventComment");
        if( comments!=null && comments.size() !=0 && event!=null){
            adapter = new CommentEventAdapter(getContext(),presenter,event.eventId(),comments);
            SnapHelper helper = new LinearSnapHelper();
            helper.attachToRecyclerView(commentRecyclerView);
            commentRecyclerView.setAdapter(adapter);
            commentRecyclerView.setLayoutManager(layoutManager);
        }
        else {

            presenter.getComment(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""),
                    event.eventId(), pageNumber, 20);
        }

    }


    @Override
    protected EventRatingPresenter createPresenter() {
        return new EventRatingPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getCommentObservable().subscribe(this::commentSuccess));
        subscriptions.add(presenter.getErrorCommentObservable().subscribe(this::onError));
    }

    private void commentSuccess(List<EventRatingResponse> eventRatingResponses){
        hideLoading();
        if(adapter == null) {
            comments = eventRatingResponses;
            adapter = new CommentEventAdapter(getContext(), presenter, event.eventId(), comments);
            commentRecyclerView.setAdapter(adapter);
            commentRecyclerView.setLayoutManager(layoutManager);
        }
       CommentEventAdapter.setComments(eventRatingResponses);
        if(commentRecyclerView !=null) {
            commentRecyclerView.getAdapter().notifyDataSetChanged();
        }


    }

    private void onError(Throwable t){

        hideLoading();
        Toast.makeText(getContext(), getString(R.string.lbl_general_error), Toast.LENGTH_SHORT).show();
    }


}
