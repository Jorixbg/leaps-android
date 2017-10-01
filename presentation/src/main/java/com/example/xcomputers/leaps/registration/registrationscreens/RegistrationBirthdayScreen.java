package com.example.xcomputers.leaps.registration.registrationscreens;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.registration.IRegistrationContainer;
import com.example.xcomputers.leaps.registration.IRegistrationInsideView;
import com.example.xcomputers.leaps.utils.CustomTextView;

import java.util.Calendar;
import java.util.List;

import rx.Subscription;

import static com.example.xcomputers.leaps.registration.RegistrationContainerView.BIRTHDAY_KEY;


/**
 * Created by xComputers on 04/06/2017.
 */
@Layout(layoutId = R.layout.registration_birthday_view)
public class RegistrationBirthdayScreen extends BaseView<EmptyPresenter> implements IRegistrationInsideView{

    private static final int DEFAULT_YEAR = 1990;
    private static final int DEFAULT_MONTH = 1;
    private static final int DEFAULT_DAY = 10;

    private Button cancelBtn;
    private Button nextBtn;
    private CustomTextView calendarTV;
    private String birthDay;
    private IRegistrationContainer container;
    private long birthDayTimeStamp;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelBtn = (Button) view.findViewById(R.id.reg_birthday_cancel_btn);
        nextBtn = (Button) view.findViewById(R.id.reg_birthday_action_btn);
        calendarTV = (CustomTextView) view.findViewById(R.id.reg_birthday_calendarBtn);
        if(savedInstanceState == null && getArguments() != null){
            if(getArguments().containsKey(BIRTHDAY_KEY)){
                birthDay = getArguments().getString(BIRTHDAY_KEY);
                birthDayTimeStamp = getArguments().getLong("BIRTHDAY_STAMP");
                if(birthDay != null && !birthDay.isEmpty()) {
                    calendarTV.setText(birthDay);
                }
            }
        }else if(savedInstanceState != null && savedInstanceState.containsKey(BIRTHDAY_KEY)){
            birthDay = savedInstanceState.getString(BIRTHDAY_KEY);
            birthDayTimeStamp = getArguments().getLong("BIRTHDAY_STAMP");
            if(birthDay != null && !birthDay.isEmpty()) {
                calendarTV.setText(birthDay);
            }
        }
        calendarTV.setOnClickListener(v -> showCalendar(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY));
        nextBtn.setOnClickListener(v -> {
           if(birthDay == null || birthDay.isEmpty()){
               Toast.makeText(getContext(), "Please provide a valid date of birth", Toast.LENGTH_SHORT).show();
           } else{
               container.gatherData(String.valueOf(birthDayTimeStamp));
           }
        });

        cancelBtn.setOnClickListener(v -> {
            getActivity().finish();
        });
    }

    @Override
    public boolean onBack() {
        return container.onBack();
    }

    private void showCalendar(int year, int month, int day){

        new DatePickerDialog(getContext(), (datePicker, year1, month1, day1) -> {
            Calendar userAge = Calendar.getInstance();
            userAge.set(year1, month1, day1);
            Calendar minAdultAge = Calendar.getInstance();
            minAdultAge.add(Calendar.YEAR, -18);
            if (minAdultAge.before(userAge)) {
                Toast.makeText(getContext(), getString(R.string.error_not_adult), Toast.LENGTH_SHORT).show();
                return;
            }
            birthDay = (String.format("%s %s %s", String.valueOf(year1), String.valueOf(month1 + 1), String.valueOf(day1)));
            birthDayTimeStamp = userAge.getTimeInMillis();
            calendarTV.setText(birthDay);
        }, year, month, day).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(birthDay!= null && !birthDay.isEmpty()){
            outState.putString(BIRTHDAY_KEY, birthDay);
            outState.putLong("BIRTHDAY_STAMP", birthDayTimeStamp);
        }
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {}

    @Override
    public void setContainer(IRegistrationContainer container) {
        this.container = container;
    }
}
