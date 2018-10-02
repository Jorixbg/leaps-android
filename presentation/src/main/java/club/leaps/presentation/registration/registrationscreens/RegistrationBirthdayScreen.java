package club.leaps.presentation.registration.registrationscreens;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import club.leaps.presentation.R;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.registration.IRegistrationContainer;
import club.leaps.presentation.registration.IRegistrationInsideView;
import club.leaps.presentation.utils.CustomTextView;

import java.util.Calendar;
import java.util.List;

import rx.Subscription;

import static club.leaps.presentation.registration.RegistrationContainerView.BIRTHDAY_KEY;


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
    private Calendar calendar = Calendar.getInstance();


    private DatePickerDialog birthdayDialog;
    private DatePickerDialog.OnDateSetListener mBirthdaySetListener;

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
        calendarTV.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            birthdayDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mBirthdaySetListener, year, month, day);
            birthdayDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            birthdayDialog.show();
        });
        nextBtn.setOnClickListener(v -> {
           if(birthDay == null || birthDay.isEmpty()){
               Toast.makeText(getContext(), getString(R.string.date_of_birth_request), Toast.LENGTH_SHORT).show();
           } else{
               container.gatherData(String.valueOf(birthDayTimeStamp));
           }
        });

        cancelBtn.setOnClickListener(v -> {
            getActivity().finish();
        });


        mBirthdaySetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar userAge = Calendar.getInstance();
                userAge.set(year, month++, day);
                Calendar minAdultAge = Calendar.getInstance();
                minAdultAge.add(Calendar.YEAR, -13);
                if (minAdultAge.before(userAge)) {
                    Toast.makeText(getContext(), R.string.error_not_adult, Toast.LENGTH_SHORT).show();
                    return;
                }
                //birthDay = (String.format("%s %s %s", String.valueOf(year1), String.valueOf(month1), String.valueOf(day1)));
                birthDayTimeStamp = userAge.getTimeInMillis();
                birthDay = (String.format("%s.%s.%s",  String.valueOf(day),String.valueOf(month),String.valueOf(year)));
                calendarTV.setText(birthDay);
            }
        };


    }

    @Override
    public boolean onBack() {
        return container.onBack();
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
