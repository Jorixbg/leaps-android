package club.leaps.presentation.event.createEvent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import club.leaps.networking.feed.event.RealEvent;
import club.leaps.presentation.R;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;
import club.leaps.presentation.base.Layout;

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
                priceFrom.setError(getString(R.string.minimum_price_request));
                priceFrom.requestFocus();
            }else if(TextUtils.isEmpty(freeSlots.getText().toString())){
                freeSlots.setError(getString(R.string.number_of_slots_request));
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
