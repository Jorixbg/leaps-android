package com.example.xcomputers.leaps.event.createEvent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        priceFrom = (EditText) view.findViewById(R.id.create_event_price_from_et);
        freeSlots = (EditText) view.findViewById(R.id.create_event_free_slots_et);
        nextBtn = (ImageView) view.findViewById(R.id.create_event_next_btn);
        nextBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(priceFrom.getText().toString())) {
                Toast.makeText(getContext(), R.string.error_create_event_price_from, Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(freeSlots.getText().toString())){
                Toast.makeText(getContext(), R.string.error_create_event_free_slots, Toast.LENGTH_SHORT).show();
            }else{
                ((ICreateEventActivity)getActivity()).collectData(Double.valueOf(priceFrom.getText().toString()), Integer.valueOf(freeSlots.getText().toString()));
            }
        });
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
