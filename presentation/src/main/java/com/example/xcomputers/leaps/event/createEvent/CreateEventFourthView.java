package com.example.xcomputers.leaps.event.createEvent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import rx.Subscription;

/**
 * Created by xComputers on 16/07/2017.
 */
@Layout(layoutId = R.layout.create_event_fourth_view)
public class CreateEventFourthView extends BaseView<EmptyPresenter> {

    private TextView dateTv;
    private TextView timeTv;
    private Calendar calendar;
    private Button publishBtn;
    private Calendar date;
    private Calendar time;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dateTv = (TextView) view.findViewById(R.id.create_event_date_tv);
        timeTv = (TextView) view.findViewById(R.id.create_event_time_tv);
        calendar = Calendar.getInstance();
        publishBtn = (Button) view.findViewById(R.id.publish_event_btn);
        publishBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round_blue_button_shape));
        publishBtn.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        publishBtn.setOnClickListener(v -> {
            if (date == null) {
                Toast.makeText(getContext(), R.string.errro_create_event_date, Toast.LENGTH_SHORT).show();
            } else if (time == null) {
                Toast.makeText(getContext(), R.string.error_create_event_time, Toast.LENGTH_SHORT).show();
            } else {
                calendar.setTimeInMillis(date.getTimeInMillis());
                calendar.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                ((ICreateEventActivity) getActivity()).collectData(calendar.getTimeInMillis());
            }
        });
        dateTv.setOnClickListener(v ->
            new DatePickerDialog(getContext(), (datePicker, year1, month1, day1) -> {
                Calendar eventTime = Calendar.getInstance();
                eventTime.set(year1, month1, day1);
                if (Calendar.getInstance().after(eventTime)) {
                    Toast.makeText(getContext(), "You can only schedule events for the future", Toast.LENGTH_SHORT).show();
                    return;
                }

                dateTv.setText(String.format(Locale.getDefault(), "%d.%d.%d", day1, month1 + 1, year1));
                date = eventTime;
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

        timeTv.setOnClickListener(v ->
            new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                if(time == null){
                    time = Calendar.getInstance();
                }
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);
                timeTv.setText(String.format(Locale.getDefault(), "%d:%02d",hourOfDay, minute));
            }, 12, 0, true).show());
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }
}
