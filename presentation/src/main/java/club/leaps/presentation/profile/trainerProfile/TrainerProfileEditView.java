package club.leaps.presentation.profile.trainerProfile;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import club.leaps.networking.feed.trainer.Entity;
import club.leaps.networking.feed.trainer.Image;
import club.leaps.networking.login.UserResponse;
import club.leaps.presentation.LeapsApplication;
import club.leaps.presentation.MainActivity;
import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.crop.CropActivity;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.FilePathDescriptor;
import club.leaps.presentation.utils.GlideInstance;
import club.leaps.presentation.utils.TagView;
import com.google.android.flexbox.FlexboxLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;

import static android.app.Activity.RESULT_OK;

/**
 * Created by xComputers on 23/07/2017.
 */
@Layout(layoutId = R.layout.trainer_edit_profile)
public class TrainerProfileEditView extends BaseView<EditProfilePresenter> {

    private static final int REQUEST_STORAGE = 3456;
    private static final int FIRST_IMAGE = 1;
    private static final int SECOND_IMAGE = 2;
    private static final int THIRD_IMAGE = 3;
    private static final int FOURTH_IMAGE = 4;
    private static final int FIFTH_IMAGE = 5;

    private static final int FIRST_IMAGE_REQUEST = 20;
    private static final int SECOND_IMAGE_REQUEST = 30;
    private static final int THIRD_IMAGE_REQUEST = 40;
    private static final int FOURTH_IMAGE_REQUEST = 50;
    private static final int FIFTH_IMAGE_REQUEST = 60;
    private static final int MAIN_IMAGE_REQUEST = 10;

    private TextView firstPicHolder;
    private ImageView firstPicDelete;
    private TextView secondPicHolder;
    private ImageView secondPicDelete;
    private TextView thirdPicHolder;
    private ImageView thirdPicDelete;
    private TextView fourthPicHolder;
    private ImageView fourthPicDelete;
    private TextView fifthPicHolder;
    private ImageView fifthPicDelete;
    private ImageView mainPicImageView;
    private ImageView firstImageImageView;
    private ImageView secondImageImageView;
    private ImageView thirdImageImageView;
    private ImageView fourthImageImageView;
    private ImageView fifthImageImageView;
    private EditText nameEt;
    private EditText userNameEt;
    private Spinner genderSpinner;
    private TextView birthDayTv;
    private EditText locationEt;
    private EditText aboutMeET;
    private EditText yearsEt;
    private EditText priceEt;
    private Button doneBtn;
    private long birthDayStamp;
    private List<Image> imagesToDelete;
    private Map<Integer, Uri> images;
    private Uri mainImageUri;
    private int imageRequest = -1;
    private int imageCounter = FIRST_IMAGE;
    private int deleteImageCounter = 0;
    private String auth;
    private int requestUriId;
    private FlexboxLayout tagsContainer;
    private List<String> tagList;
    private  Entity entity;
    private Context editViewContext;
    private boolean MainImageUpdate;

    private DatePickerDialog birthdayDialog;
    private DatePickerDialog.OnDateSetListener mBirthdaySetListener;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        images = new HashMap<>();
        imagesToDelete = new ArrayList<>();
        tagList = new ArrayList<>();
        editViewContext = getContext();
        auth = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", "");
        initViews(view);
        initListeners();
        setInititialData();
        setupFlexBox();
    }



    private void setupFlexBox() {

        List<String> tags = entity.specialities();
        for (String tag : tags) {
            TagView tagView = createTag(tag);
            tagView.toggle(R.drawable.event_tag_shape, R.drawable.round_blue_tag_shape);
            tagList.add(tagView.getText().toString());
            setupTagListener(tagView);
            tagsContainer.addView(tagView);
        }
        TagView tagView = createTag("+");
        tagView.setOnClickListener(v -> onTagAdd());
        tagsContainer.addView(tagView);
    }

    private void setupTagListener(TagView tagView) {
        tagView.setOnClickListener(v -> {
            tagView.toggle(R.drawable.event_tag_shape, R.drawable.round_blue_tag_shape);
            if (tagView.isSelected()) {
                tagList.add(tagView.getText().toString());
            } else {
                tagList.remove(tagView.getText().toString());
            }
        });
    }

    private TagView createTag(String text) {
        TagView tagView = new TagView(getContext());
        tagView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_tag_shape));
        tagView.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
        tagView.setText(text);
        return tagView;
    }

    private void onTagAdd() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(R.string.lbl_tag_prompt_name);
        final EditText tag = new EditText(getContext());

        tag.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(tag);
        dialogBuilder.setView(ll);

        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(R.string.ok, (dialog, id) -> {
            String tagText = tag.getText().toString();
            if (!TextUtils.isEmpty(tagText)) {
                TagView tagView = createTag(tagText);
                setupTagListener(tagView);
                tagsContainer.addView(tagView, tagsContainer.getChildCount() - 1);
            } else {
                Toast.makeText(getContext(), R.string.error_empty_tag, Toast.LENGTH_SHORT).show();
            }
            ((MainActivity) getActivity()).hideKeyboard();
        });
        dialogBuilder.setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.dismiss()));
        AlertDialog alert = dialogBuilder.create();
        alert.show();

    }


    private void setInititialData() {
        loadImages();
        entity = EntityHolder.getInstance().getEntity();

        /*List<String> tags = entity.specialities();
        for (String tag : tags) {
            TagView tagView = new TagView(getContext());
            tagView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_tag_shape));
            tagView.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
            tagView.setText(tag);
            tagsContainer.addView(tagView);
        }*/



        nameEt.setText(entity.firstName() + " " + entity.lastName());
        userNameEt.setText(entity.username());
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.gender_male));
        list.add(getString(R.string.gender_female));
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
        yearsEt.setText(String.valueOf(entity.yearsOfTraining()));
        priceEt.setText((String.valueOf(entity.sesionPrice())));
    }

    private void loadImages() {
        GlideInstance.loadImageCircle(getContext(), EntityHolder.getInstance().getEntity().profileImageUrl(), mainPicImageView, R.drawable.profile_placeholder);
        List<Image> images = EntityHolder.getInstance().getEntity().images();
        if (images.size() > 0) {
            Image first = images.get(0);
            setupGlideCallBack(first.getImageUrl(), firstImageImageView, firstPicDelete, firstPicHolder);
            if(images.size() >= 2) {
                Image second = images.get(1);
                if (second != null) {
                    setupGlideCallBack(second.getImageUrl(), secondImageImageView, secondPicDelete, secondPicHolder);
                }
            }
            if(images.size() >= 3) {
                Image third = images.get(2);
                if (third != null) {
                    setupGlideCallBack(third.getImageUrl(), thirdImageImageView, thirdPicDelete, thirdPicHolder);
                }
            }
            if(images.size() >= 4) {
                Image fourth = images.get(3);
                if (fourth != null) {
                    setupGlideCallBack(fourth.getImageUrl(), fourthImageImageView, fourthPicDelete, fourthPicHolder);
                }
            }
            if(images.size() >= 5) {
                Image fifth = images.get(4);
                if (fifth != null) {
                    setupGlideCallBack(fifth.getImageUrl(), fifthImageImageView, fifthPicDelete, fifthPicHolder);
                }
            }
        }
    }


    private void setupGlideCallBack(String url, ImageView imageView, ImageView deleteImage, TextView placeHolder) {
        Glide.with(getContext()).load(LeapsApplication.BASE_URL + File.separator + url).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setBackground(resource.getCurrent());
                deleteImage.setVisibility(View.VISIBLE);
                placeHolder.setText("");
                placeHolder.setBackground(null);
                return false;
            }
        }).into(imageView);
    }

    private void checkPermissionAndRequestImage(int imageRequest) {
        if (checkStoragePermission()) {
            requestImage(imageRequest);
        } else {
            this.imageRequest = imageRequest;
        }
    }

    private void requestImage(int requestIndex) {
        Intent intent = new Intent(getContext(),CropActivity.class);
        getActivity().startActivityForResult(intent,requestIndex);
    }

    private void onImageClicked(TextView holder, ImageView imageHolder, ImageView deleteImage, Uri uri) {

        holder.setText("");
        holder.setBackground(null);
        imageHolder.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(uri).into(imageHolder);
        deleteImage.setVisibility(View.VISIBLE);
    }

    private void onDeleteClicked(TextView holder, ImageView deleteImage, ImageView imageHolder, int index) {
        imageHolder.setBackground(null);
        imageHolder.setVisibility(View.INVISIBLE);
        deleteImage.setVisibility(View.GONE);
        holder.setBackgroundResource(R.drawable.round_blue_edit_text);
        holder.setText("+");
        if(imagesToDelete.size()>0){
            presenter.deleteImage(auth, imagesToDelete.get(deleteImageCounter).getImageId());
        }
        if (images.get(index) != null) {
            images.remove(index);
        }
    }

    private void onMainImageUpdate(Uri uri) {
        MainImageUpdate = true;
        mainImageUri = uri;
        GlideInstance.loadImageCircle(getContext(), uri, mainPicImageView, R.drawable.profile_placeholder);
    }

    private void initViews(View view) {
        nameEt = (EditText) view.findViewById(R.id.name_et);
        userNameEt = (EditText) view.findViewById(R.id.username_et);
        genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        birthDayTv = (TextView) view.findViewById(R.id.birthday_tv);
        locationEt = (EditText) view.findViewById(R.id.location_tv);
        aboutMeET = (EditText) view.findViewById(R.id.about_me_et);
        yearsEt = (EditText) view.findViewById(R.id.years_et);
        priceEt = (EditText) view.findViewById(R.id.price_et);
        firstImageImageView = (ImageView) view.findViewById(R.id.first_image_imageView);
        secondImageImageView = (ImageView) view.findViewById(R.id.second_image_imageView);
        thirdImageImageView = (ImageView) view.findViewById(R.id.third_image_imageView);
        fourthImageImageView = (ImageView) view.findViewById(R.id.fourth_image_imageView);
        fifthImageImageView = (ImageView) view.findViewById(R.id.fifth_image_imageView);
        mainPicImageView = (ImageView) view.findViewById(R.id.create_event_main_pic_imageView);
        firstPicHolder = (TextView) view.findViewById(R.id.create_event_first_pic_placeholder);
        firstPicDelete = (ImageView) view.findViewById(R.id.create_event_first_pic_delete);
        secondPicHolder = (TextView) view.findViewById(R.id.create_event_second_pic_placeholder);
        secondPicDelete = (ImageView) view.findViewById(R.id.create_event_second_pic_delete);
        thirdPicHolder = (TextView) view.findViewById(R.id.create_event_third_pic_placeholder);
        thirdPicDelete = (ImageView) view.findViewById(R.id.create_event_third_pic_delete);
        fourthPicHolder = (TextView) view.findViewById(R.id.create_event_fourth_pic_placeholder);
        fourthPicDelete = (ImageView) view.findViewById(R.id.create_event_fourth_pic_delete);
        fifthPicHolder = (TextView) view.findViewById(R.id.create_event_fifth_pic_placeholder);
        fifthPicDelete = (ImageView) view.findViewById(R.id.create_event_fifth_pic_delete);
        tagsContainer = (FlexboxLayout) view.findViewById(R.id.trainer_tags_container);
        doneBtn = (Button) view.findViewById(R.id.done_btn);
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
        yearsEt = null;
        priceEt = null;
        firstImageImageView = null;
        secondImageImageView = null;
        thirdImageImageView = null;
        fourthImageImageView = null;
        fifthImageImageView = null;
        mainPicImageView = null;
        firstPicHolder = null;
        firstPicDelete = null;
        secondPicHolder = null;
        secondPicDelete = null;
        thirdPicHolder = null;
        thirdPicDelete = null;
        fourthPicHolder = null;
        fourthPicDelete = null;
        fifthPicHolder = null;
        fifthPicDelete = null;
        doneBtn = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initListeners() {
        firstPicDelete.setOnClickListener(v -> {
            if (EntityHolder.getInstance().getEntity().images().size() > 0) {
                imagesToDelete.add(EntityHolder.getInstance().getEntity().images().get(0));
            }
            onDeleteClicked(firstPicHolder, firstPicDelete, firstImageImageView, FIRST_IMAGE);
        });
        secondPicDelete.setOnClickListener(v -> {
            if (EntityHolder.getInstance().getEntity().images().size() >= 2) {
                imagesToDelete.add(EntityHolder.getInstance().getEntity().images().get(1));
            }
            onDeleteClicked(secondPicHolder, secondPicDelete, secondImageImageView, SECOND_IMAGE);
        });
        thirdPicDelete.setOnClickListener(v -> {
            if (EntityHolder.getInstance().getEntity().images().size() >= 3) {
                imagesToDelete.add(EntityHolder.getInstance().getEntity().images().get(2));
            }
            onDeleteClicked(thirdPicHolder, thirdPicDelete, thirdImageImageView, THIRD_IMAGE);
        });
        fourthPicDelete.setOnClickListener(v -> {
            if (EntityHolder.getInstance().getEntity().images().size() >= 4) {
                imagesToDelete.add(EntityHolder.getInstance().getEntity().images().get(3));
            }
            onDeleteClicked(fourthPicHolder, fourthPicDelete, fourthImageImageView, FOURTH_IMAGE);
        });
        fifthPicDelete.setOnClickListener(v -> {
            if (EntityHolder.getInstance().getEntity().images().size() >= 5) {
                imagesToDelete.add(EntityHolder.getInstance().getEntity().images().get(4));
            }
            onDeleteClicked(fifthPicHolder, fifthPicDelete, fifthImageImageView, FIFTH_IMAGE);
        });
        mainPicImageView.setOnClickListener(v -> checkPermissionAndRequestImage(MAIN_IMAGE_REQUEST));
        firstPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(FIRST_IMAGE_REQUEST));
        secondPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(SECOND_IMAGE_REQUEST));
        thirdPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(THIRD_IMAGE_REQUEST));
        fourthPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(FOURTH_IMAGE_REQUEST));
        fifthPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(FIFTH_IMAGE_REQUEST));
        doneBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(nameEt.getText().toString())
                    || TextUtils.isEmpty(userNameEt.getText().toString())
                    || TextUtils.isEmpty(birthDayTv.getText().toString())
                    || TextUtils.isEmpty(locationEt.getText().toString())
                    || TextUtils.isEmpty(aboutMeET.getText().toString())
                    || TextUtils.isEmpty(yearsEt.getText().toString())
                    || TextUtils.isEmpty(priceEt.getText().toString())) {
                Toast.makeText(getContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }
            Entity entity = EntityHolder.getInstance().getEntity();
            showLoading();

            int years;
            int price;
            try{
                years = Integer.valueOf(yearsEt.getText().toString());
                price = Integer.valueOf(priceEt.getText().toString());
            }catch (Exception e){
                Toast.makeText(getContext(), R.string.error_price_years ,Toast.LENGTH_SHORT).show();
                return;
            }
            presenter.updateUser(auth,entity.userId(),
                    entity.email(),
                    userNameEt.getText().toString(),
                    genderSpinner.getSelectedItemPosition() == 0 ? "m" : "f",
                    locationEt.getText().toString(),
                    User.getInstance().getMaxDistance(),
                    nameEt.getText().toString().split(" ")[0],
                    nameEt.getText().toString().split(" ")[1],
                    birthDayStamp,
                    entity.description(),
                    aboutMeET.getText().toString(),
                    years,
                    entity.phoneNumber(),
                    price,
                    tagList);
            uploadImage();
            if(MainImageUpdate){
                presenter.uploadMainImage(auth, User.getInstance().getUserId(), getImageFromUri(mainImageUri));
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null && requestUriId == 0) {

            Uri uriImg=data.getParcelableExtra("result");

            TextView holder = null;
            ImageView deleteImage = null;
            ImageView imageView = null;
            int req = -1;
            switch (requestCode) {
                case FIRST_IMAGE_REQUEST:
                    holder = firstPicHolder;
                    deleteImage = firstPicDelete;
                    imageView = firstImageImageView;
                    req = FIRST_IMAGE;
                    break;
                case SECOND_IMAGE_REQUEST:
                    imageView = secondImageImageView;
                    holder = secondPicHolder;
                    deleteImage = secondPicDelete;
                    req = SECOND_IMAGE;
                    break;
                case THIRD_IMAGE_REQUEST:
                    imageView = thirdImageImageView;
                    holder = thirdPicHolder;
                    deleteImage = thirdPicDelete;
                    req = THIRD_IMAGE;
                    break;
                case FOURTH_IMAGE_REQUEST:
                    imageView = fourthImageImageView;
                    holder = fourthPicHolder;
                    deleteImage = fourthPicDelete;
                    req = FOURTH_IMAGE;
                    break;
                case FIFTH_IMAGE_REQUEST:
                    imageView = fifthImageImageView;
                    holder = fifthPicHolder;
                    deleteImage = fifthPicDelete;
                    req = FIFTH_IMAGE;
                    break;
                case MAIN_IMAGE_REQUEST:
                    onMainImageUpdate(uriImg);
                    return;
            }
                images.put(req,uriImg);
                onImageClicked(holder, imageView, deleteImage, uriImg);

            }



        }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    requestImage(imageRequest);
                    imageRequest = -1;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadImage() {
       /* if (images.get(imageCounter) != null) {
            presenter.uploadImage(auth, User.getInstance().getUserId(), getImageFromUri(images.get(imageCounter)));
        } */
        Integer keyToRemove = new Integer(-1);
        boolean finish = false ;
        for(Map.Entry<Integer, Uri> entry : images.entrySet()){
            keyToRemove = entry.getKey();
            finish = true;
            presenter.uploadImage(auth, User.getInstance().getUserId(), getImageFromUri(images.get(keyToRemove)));
        }

        if(finish){
            Toast.makeText(getContext(), R.string.lbl_profile_edit_success, Toast.LENGTH_SHORT).show();
            hideLoading();
            back();
            return;
        }

        if(keyToRemove == -1) {
            Toast.makeText(getContext(), R.string.lbl_profile_edit_success, Toast.LENGTH_SHORT).show();
            hideLoading();
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private MultipartBody.Part getImageFromUri(Uri uri){
        File photo = new File(FilePathDescriptor.getPath(editViewContext, uri));
        RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), photo);
        MultipartBody.Part pic = MultipartBody.Part.createFormData("image", photo.getName(), mainImageBody);
        return pic;
    }

    @Override
    protected EditProfilePresenter createPresenter() {
        return new EditProfilePresenter();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

        subscriptions.add(presenter.getUpdateUserObservable().subscribe(this::UpdateUserSuccess));

        subscriptions.add(presenter.getDeletePicObservable().subscribe(aVoid -> {
            deleteImageCounter++;
            if (imagesToDelete.size() -1 >= deleteImageCounter ) {
                presenter.deleteImage(auth, imagesToDelete.get(deleteImageCounter).getImageId());
            } else {
                if(mainImageUri != null) {
                    presenter.uploadMainImage(auth, User.getInstance().getUserId(), getImageFromUri(mainImageUri));
                }else{
                   // uploadImage();
                }
            }
        }));

        subscriptions.add(presenter.getUploadImageObservable().subscribe(image -> {
            Entity entity = EntityHolder.getInstance().getEntity();
            if(entity.images().size() -1 >= imageCounter){
                entity.images().remove(imageCounter);
            }
            entity.images().add(imageCounter, image);
            imageCounter++;
            uploadImage();
        }));

        subscriptions.add(presenter.getUploadMainPicObservable().subscribe(image -> {
            uploadImage();
            back();
        }));


        subscriptions.add(presenter.getGeneralErrorObservable().subscribe(message ->{
            hideLoading();
            Log.e("Error!!",message.getMessage()+"");
        }));
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void UpdateUserSuccess(Integer response){
        Log.e("response",response+"");
        if (imagesToDelete.size() > 0) {
            presenter.deleteImage(auth, imagesToDelete.get(deleteImageCounter).getImageId());
        }
        if(!MainImageUpdate){
            back();
        }
    }
}
