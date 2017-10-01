package com.example.xcomputers.leaps.event.createEvent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;

import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 16/07/2017.
 */
@Layout(layoutId = R.layout.create_event_second_view)
public class CreateEventSecondView extends BaseView<EmptyPresenter> {


    private ImageView nextBtn;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        nextBtn = (ImageView) view.findViewById(R.id.create_event_next_btn);
        nextBtn.setOnClickListener(v -> ((ICreateEventActivity)getActivity()).collectData());
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }
}
