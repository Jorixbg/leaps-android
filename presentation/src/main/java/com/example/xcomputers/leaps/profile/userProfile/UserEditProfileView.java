package com.example.xcomputers.leaps.profile.userProfile;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networking.feed.trainer.Entity;
import com.example.networking.login.UserResponse;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.profile.trainerProfile.EditProfilePresenter;
import com.example.xcomputers.leaps.crop.CropActivity;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.FilePathDescriptor;
import com.example.xcomputers.leaps.utils.GlideInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by xComputers on 24/07/2017.
 */
@Layout(layoutId = R.layout.user_edit_profile_view)
public class UserEditProfileView extends BaseView<EditProfilePresenter> {

    private static final int REQUEST_STORAGE = 9482;
    private static final int IMAGE_REQUEST = 9989;

    private EditText nameEt;
    private EditText userNameEt;
    private Spinner genderSpinner;
    private TextView birthDayTv;
    private EditText locationEt;
    private EditText aboutMeET;
    private Button doneBtn;
    private ImageView profilePicImageView;
    private long birthDayStamp;
    private Uri mainImageUri;
    private String auth;

    private DatePickerDialog birthdayDialog;
    private DatePickerDialog.OnDateSetListener mBirthdaySetListener;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        profilePicImageView.setOnClickListener(v -> checkPermissionAndRequestImage(IMAGE_REQUEST));
        doneBtn.setOnClickListener(v -> onDoneClicked());
        setInititialData();
    }

    private void setInititialData() {

        Entity entity = EntityHolder.getInstance().getEntity();
        GlideInstance.loadImageCircle(getContext(), entity.profileImageUrl(), profilePicImageView, R.drawable.profile_placeholder);
        nameEt.setText(entity.firstName() + " " + entity.lastName());
        userNameEt.setText(entity.username());
        List<String> list = new ArrayList<>();
        list.add("Male");
        list.add("Female");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.profile_spinner_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.profile_spinner_item);
        genderSpinner.setAdapter(arrayAdapter);
        if(entity.gender()!=null && !entity.gender().isEmpty()) {
            genderSpinner.setSelection(0, false);
        }
        genderSpinner.setSelection("m".equals(entity.gender()) ? 0 : 1, false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(entity.birthDay()));

        birthDayTv.setText(calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH)+1) + "." + calendar.get(Calendar.YEAR));
        birthDayStamp = entity.birthDay();

        birthDayTv.setOnClickListener(v -> {

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            birthdayDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mBirthdaySetListener, year, month, day);
            birthdayDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            birthdayDialog.show();
        });

        mBirthdaySetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar userAge = Calendar.getInstance();
                userAge.set(year, month++, day);
                Calendar minAdultAge = Calendar.getInstance();
                minAdultAge.add(Calendar.YEAR, -18);
                if (minAdultAge.before(userAge)) {
                    Toast.makeText(getContext(), R.string.error_not_adult, Toast.LENGTH_SHORT).show();
                    return;
                }
                //birthDay = (String.format("%s %s %s", String.valueOf(year1), String.valueOf(month1), String.valueOf(day1)));
                birthDayStamp = userAge.getTimeInMillis();
                birthDayTv.setText((String.format("%s.%s.%s",  String.valueOf(day),String.valueOf(month),String.valueOf(year))));


            }
        };
        locationEt.setText(entity.address());
        aboutMeET.setText(entity.longDescription());
    }

    private void onDoneClicked() {
        if (TextUtils.isEmpty(nameEt.getText().toString())
                || TextUtils.isEmpty(userNameEt.getText().toString())
                || TextUtils.isEmpty(birthDayTv.getText().toString())
                || TextUtils.isEmpty(locationEt.getText().toString())
                || TextUtils.isEmpty(aboutMeET.getText().toString())) {
            Toast.makeText(getContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        Entity entity = EntityHolder.getInstance().getEntity();
        showLoading();
        auth = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", "");
        presenter.updateUser(auth, entity.userId(),
                entity.email(),
                userNameEt.getText().toString(),
                genderSpinner.getSelectedItemPosition() == 0 ? "m" : "f",
                locationEt.getText().toString(),
                User.getInstance().getMaxDistance(), nameEt.getText().toString().split(" ")[0],
                nameEt.getText().toString().split(" ")[1],
                birthDayStamp,
                entity.description(),
                aboutMeET.getText().toString(),
                0, "", 0,null);
        onBack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST) {
                Uri uriImg=data.getParcelableExtra("result");
                mainImageUri = uriImg;
                GlideInstance.loadImageCircle(getContext(), mainImageUri, profilePicImageView, R.drawable.profile_placeholder);
            }
        }

    }



    private void checkPermissionAndRequestImage(int imageRequest) {
        if (checkStoragePermission()) {
            requestImage(imageRequest);
        }
    }

    public boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.lbl_storage_permission_title)
                        .setMessage(R.string.lbl_storage_permission_message)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_STORAGE);
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    private MultipartBody.Part getImageFromUri(Uri uri) {
        File photo = new File(FilePathDescriptor.getPath(getContext(), uri));
        RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), photo);
        MultipartBody.Part pic = MultipartBody.Part.createFormData("image", photo.getName(), mainImageBody);
        return pic;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    requestImage(IMAGE_REQUEST);
                }
            }
        }
    }

    private void requestImage(int requestIndex) {
        Intent intent = new Intent(getContext(),CropActivity.class);
        getActivity().startActivityForResult(intent,requestIndex);
    }

    private void initViews(View view) {
        nameEt = (EditText) view.findViewById(R.id.name_et);
        userNameEt = (EditText) view.findViewById(R.id.username_et);
        genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        birthDayTv = (TextView) view.findViewById(R.id.birthday_tv);
        locationEt = (EditText) view.findViewById(R.id.location_tv);
        aboutMeET = (EditText) view.findViewById(R.id.about_me_et);
        doneBtn = (Button) view.findViewById(R.id.done_btn);
        profilePicImageView = (ImageView) view.findViewById(R.id.profile_edit_picture);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nameEt = null;
        userNameEt = null;
        genderSpinner = null;
        birthDayTv = null;
        locationEt = null;
        aboutMeET = null;
        doneBtn = null;
        profilePicImageView = null;
    }

    @Override
    protected EditProfilePresenter createPresenter() {
        return new EditProfilePresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getUpdateUserObservable()
                .subscribe(aVoid -> {
                    if(mainImageUri == null){
                        hideLoading();
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        back();
                        return;
                    }
                    presenter.uploadMainImage(auth, User.getInstance().getUserId(), getImageFromUri(mainImageUri));
                }));

        subscriptions.add(presenter.getUploadMainPicObservable().subscribe(image -> {
            User.getInstance().setImageUrl(image.getImageUrl());
            ((UserResponse) EntityHolder.getInstance().getEntity()).setProfileImage(image.getImageUrl());
            hideLoading();
            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            back();
        }));

        subscriptions.add(presenter.getGeneralErrorObservable().subscribe(message ->{
            hideLoading();
            if (!message.isEmpty()){
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        }));
    }



}
