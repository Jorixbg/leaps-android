package com.example.xcomputers.leaps.event.createEvent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.networking.feed.event.RealEvent;
import com.example.networking.test.ChoosenDate;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Subscription;

/**
 * Created by xComputers on 16/07/2017.
 */
@Layout(layoutId = R.layout.create_event_fourth_view)
public class CreateEventFourthView extends BaseView<EmptyPresenter> {


    private Calendar calendar;
    private Calendar calendar2;
    private Calendar eventStartDate;
    private Calendar eventEndDate;
    private Calendar eventStartTime;
    private DatePickerDialog startDateDialog;
    private TimePickerDialog startTimeDialog;
    private DatePickerDialog endDateDialog;
    private TimePickerDialog endTimeDialog;
    private TimePickerDialog dayTimeDialog;
    private DatePickerDialog.OnDateSetListener mStartDateSetListener;
    private TimePickerDialog.OnTimeSetListener mStartOnTimeSetListener;
    private DatePickerDialog.OnDateSetListener mEndDateSetListener;
    private TimePickerDialog.OnTimeSetListener mEndOnTimeSetListener;
    private TimePickerDialog.OnTimeSetListener mDayStartOnTimeSetListener;
    private TimePickerDialog.OnTimeSetListener mDayEndOnTimeSetListener;
    private String stringDate;
    private String start_date;
    private String end_date;
    private String endTime;
    private String startTime;
    private boolean repeat;
    private String frequency_text="daily";
    private String setStartTime;
    private String setEndTime;


    private SnapHelper helper;
    private LinearLayoutManager layoutManager;


    private RecyclerView mondayRV;
    private RecyclerView tuesdayRV;
    private RecyclerView wednesdayRV;
    private RecyclerView thursdayRV;
    private RecyclerView fridayRV;
    private RecyclerView saturdayRV;
    private RecyclerView sundayRV;
    private RecyclerView dayRV;
    private DateAdapter mondayAdapter;
    private DateAdapter tuesdayAdapter;
    private DateAdapter wednesdayAdapter;
    private DateAdapter thursdayAdapter;
    private DateAdapter fridayAdapter;
    private DateAdapter saturdayAdapter;
    private DateAdapter sundayAdapter;
    private DateAdapter dateAdapter;


    private Switch repeatBtn;
    private TextView startDate;
    private TextView endDate;
    private TextView frequency;
    private TextView startTV;
    private TextView endTV;
    private Button publishBtn;
    private RelativeLayout weekRL;
    private RelativeLayout frequencyRL;
    private RelativeLayout startRL;
    private RelativeLayout endRL;
    private RelativeLayout mondayRL;
    private RelativeLayout tuesdayRL;
    private RelativeLayout wednesdayRL;
    private RelativeLayout thursdayRL;
    private RelativeLayout fridayRL;
    private RelativeLayout saturdayRL;
    private RelativeLayout sundayRL;
    private RelativeLayout dailyRL;
    private RelativeLayout dayRL;

    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private boolean day;


    private RealEvent event;
    private int number = 0;

    private List<ChoosenDate> mondayTime;
    private List<ChoosenDate> tuesdayTime;
    private List<ChoosenDate> wednesdayTime;
    private List<ChoosenDate> thursdayTime;
    private List<ChoosenDate> fridayTime;
    private List<ChoosenDate> saturdayTime;
    private List<ChoosenDate> sundayTime;
    private List<ChoosenDate> dailyTime;
    private Paint p = new Paint();
    private Paint p2 = new Paint();

    private boolean checkForClick;
    private boolean checkForAdd;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        initializeViews(view);
        initSwipe();
        calendar = Calendar.getInstance();
        calendar2 = Calendar.getInstance();

        publishBtn = (Button) view.findViewById(R.id.publish_event_btn);
        publishBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round_blue_button_shape));
        publishBtn.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        publishBtn.setOnClickListener(v -> {
            if(!repeat) {
                if (startDate.getText() == null || startDate.getText() == "") {
                    Toast.makeText(getContext(), "Please provide a string Date for your event", Toast.LENGTH_SHORT).show();
                } else if (endDate.getText() == null || endDate.getText() == "") {
                    Toast.makeText(getContext(), "Please provide a end Date  for your event", Toast.LENGTH_SHORT).show();
                } else {
                    calendar.setTimeInMillis(eventStartDate.getTimeInMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, eventStartDate.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, eventStartDate.get(Calendar.MINUTE));

                    calendar2.setTimeInMillis(eventEndDate.getTimeInMillis());
                    calendar2.set(Calendar.HOUR_OF_DAY, eventEndDate.get(Calendar.HOUR_OF_DAY));
                    calendar2.set(Calendar.MINUTE, eventEndDate.get(Calendar.MINUTE));

                    ((ICreateEventActivity) getActivity()).collectData(calendar.getTimeInMillis(),calendar2.getTimeInMillis());
                }
            }
            else {

                if (startDate.getText() == null || startDate.getText() == "") {
                    startDate.setError("Please provide a stringDate for your event");
                } else if (endDate.getText() == null || endDate.getText() == "") {
                    endDate.setError("Please provide a starting time for your event");
                } else {
                    List<ChoosenDate> period = new ArrayList<>();
                    if (frequency_text.equalsIgnoreCase("weekly")) {
                        period.addAll(mondayTime);
                        period.addAll(tuesdayTime);
                        period.addAll(wednesdayTime);
                        period.addAll(thursdayTime);
                        period.addAll(fridayTime);
                        period.addAll(saturdayTime);
                        period.addAll(sundayTime);
                    } else {
                        period.addAll(dailyTime);
                    }
                    calendar.setTimeInMillis(eventStartDate.getTimeInMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, eventStartDate.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, eventStartDate.get(Calendar.MINUTE));

                    calendar2.setTimeInMillis(eventEndDate.getTimeInMillis());
                    calendar2.set(Calendar.HOUR_OF_DAY, eventEndDate.get(Calendar.HOUR_OF_DAY));
                    calendar2.set(Calendar.MINUTE, eventEndDate.get(Calendar.MINUTE));


                    ((ICreateEventActivity) getActivity()).collectData(frequency_text, calendar.getTimeInMillis(), calendar2.getTimeInMillis(), period);
                }


            }
        });
        repeatBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    repeat = true;
                    dailyRL.setVisibility(View.VISIBLE);
                    frequencyRL.setVisibility(View.VISIBLE);
                    frequency.setText("Day");
                    startTV.setText("Start Repeat");
                    endTV.setText("End Repeat");
                    startDate.setText("");
                    endDate.setText("");
                } else {
                    repeat = false;
                    weekRL.setVisibility(View.GONE);
                    frequencyRL.setVisibility(View.GONE);
                    dailyRL.setVisibility(View.GONE);
                    startTV.setText("Start");
                    endTV.setText("End");
                    startDate.setText("");
                    endDate.setText("");
                }
            }
        });

        startDateAndTime();
        endDateAndTime();
        frequencyRL.setOnClickListener(v -> {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.frequency_dialog, null);
            TextView dailyTV = mView.findViewById(R.id.daily_tv);
            TextView weeklyTV = mView.findViewById(R.id.weekly_tv);

            mBuilder.setView(mView);
            AlertDialog frequencyDialog = mBuilder.create();
            frequencyDialog.show();

            dailyTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    frequency.setText("Day");
                    weekRL.setVisibility(View.GONE);
                    dailyRL.setVisibility(View.VISIBLE);
                    frequencyDialog.cancel();
                    frequency_text = "daily";

                }
            });
            weeklyTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    frequency.setText("Weekdays");
                    weekRL.setVisibility(View.VISIBLE);
                    dailyRL.setVisibility(View.GONE);
                    frequencyDialog.cancel();
                    frequency_text = "weekly";
                }
            });

        });


        mondayTime = new ArrayList<>();
        tuesdayTime = new ArrayList<>();
        wednesdayTime = new ArrayList<>();
        thursdayTime = new ArrayList<>();
        fridayTime = new ArrayList<>();
        saturdayTime = new ArrayList<>();
        sundayTime = new ArrayList<>();
        dailyTime = new ArrayList<>();

        mondayAdd();
        tuesdayAdd();
        wednesdayAdd();
        thursdayAdd();
        fridayAdd();
        saturdayAdd();
        sundayAdd();
        dailyAdd();

            mDayStartOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                    stringDate = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    setStartTime = String.format(Locale.getDefault(), "%02d%02d", hourOfDay, minute);

                    int endHourOfDay = hourOfDay;
                    int endMinute = minute;
                    if(checkForClick) {
                        checkForClick = false;
                        checkForAdd = true;
                    endTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDayEndOnTimeSetListener, endHourOfDay, endMinute, true);
                    endTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    endTimeDialog.setTitle("End Time");
                    endTimeDialog.show();
                    }

                }
            };
                mDayEndOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int endHourOfDay, int endMinute) {
                        endTime = String.format(Locale.getDefault(), "%02d:%02d", endHourOfDay, endMinute);
                        setEndTime = String.format(Locale.getDefault(), "%02d%02d", endHourOfDay, endMinute);

                        if (!checktimings(stringDate, endTime)) {
                            Toast.makeText(getContext(), "You can only schedule events for the future", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ChoosenDate choosenDate = new ChoosenDate();

                        if(checkForAdd){
                            checkForAdd =false;
                            if (monday) {
                                choosenDate.setPeriod("monday");
                                choosenDate.setStart(setStartTime);
                                choosenDate.setEnd(setEndTime);
                                mondayTime.add(choosenDate);
                                mondayRV.getAdapter().notifyDataSetChanged();
                            } else if (tuesday) {
                                choosenDate.setPeriod("tuesday");
                                choosenDate.setStart(setStartTime);
                                choosenDate.setEnd(setEndTime);
                                tuesdayTime.add(choosenDate);
                                tuesdayRV.getAdapter().notifyDataSetChanged();
                            } else if (wednesday) {
                                choosenDate.setPeriod("wednesday");
                                choosenDate.setStart(setStartTime);
                                choosenDate.setEnd(setEndTime);
                                wednesdayTime.add(choosenDate);
                                wednesdayRV.getAdapter().notifyDataSetChanged();
                            } else if (thursday) {
                                choosenDate.setPeriod("thursday");
                                choosenDate.setStart(setStartTime);
                                choosenDate.setEnd(setEndTime);
                                thursdayTime.add(choosenDate);
                                thursdayRV.getAdapter().notifyDataSetChanged();
                            } else if (friday) {
                                choosenDate.setPeriod("friday");
                                choosenDate.setStart(setStartTime);
                                choosenDate.setEnd(setEndTime);
                                fridayTime.add(choosenDate);
                                fridayRV.getAdapter().notifyDataSetChanged();
                            } else if (saturday) {
                                choosenDate.setPeriod("saturday");
                                choosenDate.setStart(setStartTime);
                                choosenDate.setEnd(setEndTime);
                                saturdayTime.add(choosenDate);
                                saturdayRV.getAdapter().notifyDataSetChanged();
                            } else if (sunday) {
                                choosenDate.setPeriod("sunday");
                                choosenDate.setStart(setStartTime);
                                choosenDate.setEnd(setEndTime);
                                sundayTime.add(choosenDate);
                                sundayRV.getAdapter().notifyDataSetChanged();
                            } else if (day) {
                                choosenDate.setPeriod("everyday");
                                choosenDate.setStart(setStartTime);
                                choosenDate.setEnd(setEndTime);
                                dailyTime.add(choosenDate);
                                dayRV.getAdapter().notifyDataSetChanged();
                            }
                        }
                    }

                };






      /*dateTv = (TextView) view.findViewById(R.id.create_event_date_tv);
       timeTv = (TextView) view.findViewById(R.id.create_event_time_tv);
        calendar = Calendar.getInstance();

        dateTv.setOnClickListener(v ->
            new DatePickerDialog(getContext(), (datePicker, year1, month1, day1) -> {
                Calendar eventTime = Calendar.getInstance();
                eventTime.set(year1, month1, day1);
                if (Calendar.getInstance().after(eventTime)) {
                    Toast.makeText(getContext(), "You can only schedule events for the future", Toast.LENGTH_SHORT).show();
                    return;
                }

                dateTv.setText(String.format(Locale.getDefault(), "%d.%d.%d", day1, month1 + 1, year1));
                stringDate = eventTime;
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

        event = (RealEvent) getArguments().getSerializable("event");
        if(event !=null){
            loadVies();
        }*/
    }


    private void loadVies() {
        /*Calendar eventTime = Calendar.getInstance();
        eventTime.setTime(event.stringDate());

        dateTv.setText(String.format(Locale.getDefault(), "%d.%d.%d", eventTime.get(Calendar.DAY_OF_MONTH),
                eventTime.get(Calendar.MONTH) + 1, eventTime.get(Calendar.YEAR)));

        DateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeTv.setText(format.format(event.timeFrom())+"");*/
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }

    private void startDateAndTime() {
        startRL.setOnClickListener(v -> {
                checkForClick = true;
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                startDateDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mStartDateSetListener, year, month, day);
                startDateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                startDateDialog.setTitle("Start Date");
                startDateDialog.show();

        });
        mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                eventStartDate = Calendar.getInstance();
                eventStartDate.set(year, month, day);
                month = month + 1;
                start_date = String.format(Locale.getDefault(), "%02d.%02d.%d", day, month, year);
                if (Calendar.getInstance().after(eventStartDate)) {
                    Toast.makeText(getContext(), "You can only schedule events for the future", Toast.LENGTH_SHORT).show();
                    return;
                }
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                if(checkForClick && !repeat) {
                    checkForClick = false;
                startTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mStartOnTimeSetListener, hourOfDay, minute, true);
                startTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                startTimeDialog.setTitle("Start Time");
                startTimeDialog.show();
                }
                else {
                    startDate.setText(start_date);
                }

            }
        };
            mStartOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    eventStartDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    eventStartDate.set(Calendar.MINUTE, minute);

                    startTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    stringDate = start_date + "   " + String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    eventStartTime = Calendar.getInstance();
                    eventStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    eventStartTime.set(Calendar.MINUTE, minute);
                    startDate.setText(stringDate);
                }
            };

    }

    private void endDateAndTime() {

        endRL.setOnClickListener(v -> {
            if (!repeat) {
                if(eventStartTime != null) {
                    int hourOfDay = eventStartTime.get(Calendar.HOUR_OF_DAY);
                    int minute = eventStartTime.get(Calendar.MINUTE);
                    endTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mEndOnTimeSetListener, hourOfDay, minute, true);
                    endTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    endTimeDialog.setTitle("End Time");
                    endTimeDialog.show();
                }
                else {
                    Toast.makeText(getContext(), "Please set a start date first", Toast.LENGTH_SHORT).show();
                }
            } else {
                if(eventStartDate != null) {
                    checkForClick = true;
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    endDateDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mEndDateSetListener, year, month, day);
                    endDateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    endDateDialog.setTitle("End Date");
                    endDateDialog.show();
                }
                else {
                    Toast.makeText(getContext(), "Please set a start date first", Toast.LENGTH_SHORT).show();
                }

            }

        });

        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                eventEndDate = Calendar.getInstance();
                eventEndDate.set(year, month, day);
                month = month + 1;
                end_date = String.format(Locale.getDefault(), "%02d.%02d.%d", day, month, year);
                if (eventStartDate.after(eventEndDate)) {
                    Toast.makeText(getContext(), "You have to set a correct end date", Toast.LENGTH_SHORT).show();
                    return;
                }
                endDate.setText(end_date);
                /*
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                if(checkForClick) {
                    checkForClick = false;
                    endTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mEndOnTimeSetListener, hourOfDay, minute, true);
                    endTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    endTimeDialog.setTitle("End Time");
                    endTimeDialog.show();

                }*/
            }
        };


        mEndOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                eventEndDate = Calendar.getInstance();
                eventEndDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                eventEndDate.set(Calendar.MINUTE, minute);
                endTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

                if (!repeat) {
                    if(!checktimings(startTime,endTime)){
                        Toast.makeText(getContext(), "You can only schedule events for the future", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    stringDate = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                } else {
                    if(start_date.equalsIgnoreCase(end_date)){
                        if(!checktimings(startTime,endTime)){
                            Toast.makeText(getContext(), "You can only schedule events for the future", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        stringDate = end_date + "   " + String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    }
                    else {
                        stringDate = end_date + "   " + String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    }

                }
                endDate.setText(stringDate);
            }
        };

    }

    private void mondayAdd() {

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        helper = new LinearSnapHelper();
        mondayAdapter = new DateAdapter(mondayTime);
        helper.attachToRecyclerView(mondayRV);
        mondayRV.setOnFlingListener(null);
        mondayRV.setAdapter(mondayAdapter);
        mondayRV.setLayoutManager(layoutManager);

        mondayRL.setOnClickListener(v -> {

            checkForClick = true;
            monday = true;
            tuesday = false;
            wednesday = false;
            thursday = false;
            friday = false;
            saturday = false;
            sunday = false;
            day = false;

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            eventStartTime = Calendar.getInstance();
            eventStartTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
            eventStartTime.set(Calendar.MINUTE,minute);

            startTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDayStartOnTimeSetListener, hourOfDay, minute, true);
            startTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            startTimeDialog.setTitle("Start Time");
            startTimeDialog.show();

        });

    }

    private void tuesdayAdd() {

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        helper = new LinearSnapHelper();
        tuesdayAdapter = new DateAdapter(tuesdayTime);
        helper.attachToRecyclerView(tuesdayRV);
        tuesdayRV.setOnFlingListener(null);
        tuesdayRV.setAdapter(tuesdayAdapter);
        tuesdayRV.setLayoutManager(layoutManager);

        tuesdayRL.setOnClickListener(v -> {

            checkForClick = true;
            monday = false;
            tuesday = true;
            wednesday = false;
            thursday = false;
            friday = false;
            saturday = false;
            sunday = false;
            day = false;

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            startTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDayStartOnTimeSetListener, hourOfDay, minute, true);
            startTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            startTimeDialog.setTitle("Start Time");
            startTimeDialog.show();


        });
    }

    private void wednesdayAdd() {

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        helper = new LinearSnapHelper();
        wednesdayAdapter = new DateAdapter(wednesdayTime);
        helper.attachToRecyclerView(wednesdayRV);
        wednesdayRV.setOnFlingListener(null);
        wednesdayRV.setAdapter(wednesdayAdapter);
        wednesdayRV.setLayoutManager(layoutManager);

        wednesdayRL.setOnClickListener(v -> {

            checkForClick = true;
            monday = false;
            tuesday = false;
            wednesday = true;
            thursday = false;
            friday = false;
            saturday = false;
            sunday = false;
            day = false;

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            startTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDayStartOnTimeSetListener, hourOfDay, minute, true);
            startTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            startTimeDialog.setTitle("Start Time");
            startTimeDialog.show();
        });

    }

    private void thursdayAdd() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        helper = new LinearSnapHelper();
        thursdayAdapter = new DateAdapter(thursdayTime);
        helper.attachToRecyclerView(thursdayRV);
        thursdayRV.setOnFlingListener(null);
        thursdayRV.setAdapter(thursdayAdapter);
        thursdayRV.setLayoutManager(layoutManager);

        thursdayRL.setOnClickListener(v -> {

            checkForClick = true;
            monday = false;
            tuesday = false;
            wednesday = false;
            thursday = true;
            friday = false;
            saturday = false;
            sunday = false;
            day = false;

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            startTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDayStartOnTimeSetListener, hourOfDay, minute, true);
            startTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            startTimeDialog.setTitle("Start Time");
            startTimeDialog.show();
        });
    }

    private void fridayAdd() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        helper = new LinearSnapHelper();
        fridayAdapter = new DateAdapter(fridayTime);
        helper.attachToRecyclerView(fridayRV);
        fridayRV.setOnFlingListener(null);
        fridayRV.setAdapter(fridayAdapter);
        fridayRV.setLayoutManager(layoutManager);

        fridayRL.setOnClickListener(v -> {

            checkForClick = true;
            monday = false;
            tuesday = false;
            wednesday = false;
            thursday = false;
            friday = true;
            saturday = false;
            sunday = false;
            day = false;

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            startTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDayStartOnTimeSetListener, hourOfDay, minute, true);
            startTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            startTimeDialog.setTitle("Start Time");
            startTimeDialog.show();
        });
    }

    private void saturdayAdd() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        helper = new LinearSnapHelper();
        saturdayAdapter = new DateAdapter(saturdayTime);
        helper.attachToRecyclerView(saturdayRV);
        saturdayRV.setOnFlingListener(null);
        saturdayRV.setAdapter(saturdayAdapter);
        saturdayRV.setLayoutManager(layoutManager);

        saturdayRL.setOnClickListener(v -> {

            checkForClick = true;
            monday = false;
            tuesday = false;
            wednesday = false;
            thursday = false;
            friday = false;
            saturday = true;
            sunday = false;
            day = false;

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            startTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDayStartOnTimeSetListener, hourOfDay, minute, true);
            startTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            startTimeDialog.setTitle("Start Time");
            startTimeDialog.show();
        });
    }

    private void sundayAdd() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        helper = new LinearSnapHelper();
        sundayAdapter = new DateAdapter(sundayTime);
        helper.attachToRecyclerView(sundayRV);
        sundayRV.setOnFlingListener(null);
        sundayRV.setAdapter(sundayAdapter);
        sundayRV.setLayoutManager(layoutManager);

        sundayRL.setOnClickListener(v -> {

            checkForClick = true;
            monday = false;
            tuesday = false;
            wednesday = false;
            thursday = false;
            friday = false;
            saturday = false;
            sunday = true;
            day = false;

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            startTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDayStartOnTimeSetListener, hourOfDay, minute, true);
            startTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            startTimeDialog.setTitle("Start Time");
            startTimeDialog.show();
        });
    }

    private void dailyAdd() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        helper = new LinearSnapHelper();
        dateAdapter = new DateAdapter(dailyTime);
        helper.attachToRecyclerView(sundayRV);
        dayRV.setOnFlingListener(null);
        dayRV.setAdapter(dateAdapter);
        dayRV.setLayoutManager(layoutManager);

        dayRL.setOnClickListener(v -> {

            checkForClick = true;
            monday = false;
            tuesday = false;
            wednesday = false;
            thursday = false;
            friday = false;
            saturday = false;
            sunday = false;
            day = true;

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);


            startTimeDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, mDayStartOnTimeSetListener, hourOfDay, minute, true);
            startTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            startTimeDialog.setTitle("Start Time");
            startTimeDialog.show();
        });

    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                        dateAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.full_indicator));
                        p2.setColor(getResources().getColor(R.color.colorWhite));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                       /* icon = BitmapFactory.decodeResource(getResources(), R.drawable.close_red_btn);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p2); */
                        p2.setStyle(Paint.Style.FILL);
                        p2.setTextSize(36);
                        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        float x =(float) itemView.getRight() - 4 * width;
                        float y = (float) itemView.getTop() + width *1.8f;
                        c.drawText("Delete",x,y,p2);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallbackMonday = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    mondayAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.full_indicator));
                        p2.setColor(getResources().getColor(R.color.colorWhite));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                       /* icon = BitmapFactory.decodeResource(getResources(), R.drawable.close_red_btn);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p2); */
                        p2.setStyle(Paint.Style.FILL);
                        p2.setTextSize(36);
                        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        float x =(float) itemView.getRight() - 4 * width;
                        float y = (float) itemView.getTop() + width *1.8f;
                        c.drawText("Delete",x,y,p2);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallbackTuesday = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    tuesdayAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.full_indicator));
                        p2.setColor(getResources().getColor(R.color.colorWhite));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                       /* icon = BitmapFactory.decodeResource(getResources(), R.drawable.close_red_btn);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p2); */
                        p2.setStyle(Paint.Style.FILL);
                        p2.setTextSize(36);
                        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        float x =(float) itemView.getRight() - 4 * width;
                        float y = (float) itemView.getTop() + width *1.8f;
                        c.drawText("Delete",x,y,p2);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallbackWednesday = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    wednesdayAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.full_indicator));
                        p2.setColor(getResources().getColor(R.color.colorWhite));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                       /* icon = BitmapFactory.decodeResource(getResources(), R.drawable.close_red_btn);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p2); */
                        p2.setStyle(Paint.Style.FILL);
                        p2.setTextSize(36);
                        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        float x =(float) itemView.getRight() - 4 * width;
                        float y = (float) itemView.getTop() + width *1.8f;
                        c.drawText("Delete",x,y,p2);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallbackThursday = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    thursdayAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.full_indicator));
                        p2.setColor(getResources().getColor(R.color.colorWhite));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                       /* icon = BitmapFactory.decodeResource(getResources(), R.drawable.close_red_btn);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p2); */
                        p2.setStyle(Paint.Style.FILL);
                        p2.setTextSize(36);
                        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        float x =(float) itemView.getRight() - 4 * width;
                        float y = (float) itemView.getTop() + width *1.8f;
                        c.drawText("Delete",x,y,p2);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallbackFriday = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    fridayAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.full_indicator));
                        p2.setColor(getResources().getColor(R.color.colorWhite));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                       /* icon = BitmapFactory.decodeResource(getResources(), R.drawable.close_red_btn);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p2); */
                        p2.setStyle(Paint.Style.FILL);
                        p2.setTextSize(36);
                        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        float x =(float) itemView.getRight() - 4 * width;
                        float y = (float) itemView.getTop() + width *1.8f;
                        c.drawText("Delete",x,y,p2);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallbackSaturday= new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    saturdayAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.full_indicator));
                        p2.setColor(getResources().getColor(R.color.colorWhite));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                       /* icon = BitmapFactory.decodeResource(getResources(), R.drawable.close_red_btn);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p2); */
                        p2.setStyle(Paint.Style.FILL);
                        p2.setTextSize(36);
                        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        float x =(float) itemView.getRight() - 4 * width;
                        float y = (float) itemView.getTop() + width *1.8f;
                        c.drawText("Delete",x,y,p2);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallbackSunday = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    sundayAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(getResources().getColor(R.color.full_indicator));
                        p2.setColor(getResources().getColor(R.color.colorWhite));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                       /* icon = BitmapFactory.decodeResource(getResources(), R.drawable.close_red_btn);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p2); */
                        p2.setStyle(Paint.Style.FILL);
                        p2.setTextSize(36);
                        //p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        float x =(float) itemView.getRight() - 4 * width;
                        float y = (float) itemView.getTop() + width *1.8f;
                        c.drawText("Delete",x,y,p2);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(dayRV);

        ItemTouchHelper itemTouchHelperMonday = new ItemTouchHelper(simpleItemTouchCallbackMonday);
        itemTouchHelperMonday.attachToRecyclerView(mondayRV);

        ItemTouchHelper itemTouchHelperTuesday = new ItemTouchHelper(simpleItemTouchCallbackTuesday);
        itemTouchHelperTuesday.attachToRecyclerView(tuesdayRV);

        ItemTouchHelper itemTouchHelperWednesday = new ItemTouchHelper(simpleItemTouchCallbackWednesday);
        itemTouchHelperWednesday.attachToRecyclerView(wednesdayRV);

        ItemTouchHelper itemTouchHelperThursday = new ItemTouchHelper(simpleItemTouchCallbackThursday);
        itemTouchHelperThursday.attachToRecyclerView(thursdayRV);

        ItemTouchHelper itemTouchHelperFriday = new ItemTouchHelper(simpleItemTouchCallbackFriday);
        itemTouchHelperFriday.attachToRecyclerView(fridayRV);


        ItemTouchHelper itemTouchHelperSaturday = new ItemTouchHelper(simpleItemTouchCallbackSaturday);
        itemTouchHelperSaturday.attachToRecyclerView(saturdayRV);

        ItemTouchHelper itemTouchHelperSunday = new ItemTouchHelper(simpleItemTouchCallbackSunday);
        itemTouchHelperSunday.attachToRecyclerView(sundayRV);


    }

    private void removeView() {
        if (getView().getParent() != null) {
            ((ViewGroup) getView().getParent()).removeView(getView());
        }
    }


    private void initializeViews(View initView){

        repeatBtn = initView.findViewById(R.id.create_event_switch_btn);
        startDate = initView.findViewById(R.id.create_event_start_date_lbl);
        endDate = initView.findViewById(R.id.create_event_end_date_lbl);
        frequency = initView.findViewById(R.id.create_event_choose_repeat);
        weekRL = initView.findViewById(R.id.create_event_week_rl);
        frequencyRL = initView.findViewById(R.id. create_event_frequency_rl);
        startRL = initView.findViewById(R.id.create_event_start_rl);
        endRL = initView.findViewById(R.id. create_event_end_rl);
        startTV = initView.findViewById(R.id.create_event_start_lbl);
        endTV = initView.findViewById(R.id.create_event_end_lbl);
        mondayRL = initView.findViewById(R.id.create_event_monday_rl);
        mondayRV = initView.findViewById(R.id.create_event_monday_time_rv);
        tuesdayRL = initView.findViewById(R.id.create_event_tuesday_rl);
        tuesdayRV= initView.findViewById(R.id.create_event_tuesday_time_rv);
        wednesdayRL = initView.findViewById(R.id.create_event_wednesday_rl);
        wednesdayRV = initView.findViewById(R.id.create_event_wednesday_time_rv);
        thursdayRL = initView.findViewById(R.id.create_event_thursday_rl);
        thursdayRV = initView.findViewById(R.id.create_event_thursday_time_rv);
        fridayRL = initView.findViewById(R.id.create_event_friday_rl);
        fridayRV = initView.findViewById(R.id.create_event_friday_time_rv);
        saturdayRL = initView.findViewById(R.id.create_event_saturday_rl);
        saturdayRV = initView.findViewById(R.id.create_event_saturday_time_rv);
        sundayRL = initView.findViewById(R.id.create_event_sunday_rl);
        sundayRV = initView.findViewById(R.id.create_event_sunday_time_rv);
        dayRL = initView.findViewById(R.id.create_event_day_rl);
        dayRV = initView.findViewById(R.id.create_event_day_time_rv);
        dailyRL = initView.findViewById(R.id.create_event_daily_rl);



    }

    /*
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.create_event_monday_time_layout);
            TextView[] t=new TextView[10];
            t[number] = new TextView(view.getContext());
            t[number].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            t[number].setText("programmatically created TextView" + number +"");
            t[number].setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            t[number].setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayout.addView(t[number]);
            number++;


            Toast.makeText(view.getContext(),number +"",Toast.LENGTH_SHORT).show();
     */



    private boolean checktimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }
}
