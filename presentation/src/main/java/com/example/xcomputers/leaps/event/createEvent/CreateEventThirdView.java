package com.example.xcomputers.leaps.event.createEvent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.networking.feed.event.RealEvent;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;

import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 16/07/2017.
 */
@Layout(layoutId = R.layout.create_event_third_view)
public class CreateEventThirdView extends BaseView<EmptyPresenter> {

    private EditText priceFrom;
    private EditText freeSlots;
    private ImageView nextBtn;
    private RealEvent event;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        priceFrom = (EditText) view.findViewById(R.id.create_event_price_from_et);
        freeSlots = (EditText) view.findViewById(R.id.create_event_free_slots_et);
        nextBtn = (ImageView) view.findViewById(R.id.create_event_next_btn);
        nextBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(priceFrom.getText().toString())) {
                priceFrom.setError("Please provide a minimum price");
                priceFrom.requestFocus();
            }else if(TextUtils.isEmpty(freeSlots.getText().toString())){
                freeSlots.setError("Please provide the number of free slots");
                freeSlots.requestFocus();
            }else{
                ((ICreateEventActivity)getActivity()).collectData(Double.valueOf(priceFrom.getText().toString()), Integer.valueOf(freeSlots.getText().toString()));
            }
        });

        event = (RealEvent) getArguments().getSerializable("event");
        if(event !=null){
            loadVies();
        }


    }

    private void loadVies() {
        priceFrom.setText(String.valueOf(event.priceFrom()));
        freeSlots.setText(String.valueOf(event.freeSlots()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        priceFrom = null;
        freeSlots = null;
        nextBtn = null;
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }
}
