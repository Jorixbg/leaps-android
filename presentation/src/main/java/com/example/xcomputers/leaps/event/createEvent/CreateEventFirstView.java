package com.example.xcomputers.leaps.event.createEvent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.TagsHolder;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.utils.TagView;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

import static android.app.Activity.RESULT_OK;

/**
 * Created by xComputers on 15/07/2017.
 */
@Layout(layoutId = R.layout.create_event_first_view)
public class CreateEventFirstView extends BaseView<EmptyPresenter> {

    private static final int REQUEST_STORAGE = 123;
    private static final int FIRST_IMAGE = 2;
    private static final int SECOND_IMAGE = 3;
    private static final int THIRD_IMAGE = 4;
    private static final int FOURTH_IMAGE = 5;
    private static final int FIFTH_IMAGE = 6;
    private static final int MAIN_IMAGE = 1;

    private TextView mainPic;
    private FlexboxLayout flexboxLayout;
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
    private EditText descriptionEt;
    private EditText titleEt;
    private ImageView mainPicImageView;
    private ImageView firstImageImageView;
    private ImageView secondImageImageView;
    private ImageView thirdImageImageView;
    private ImageView fourthImageImageView;
    private ImageView fifthImageImageView;
    private ImageView nextBtn;

    private Map<Integer, Uri> images;
    private List<String> tagList;
    private int imageRequest = -1;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        images = new HashMap<>();
        tagList = new ArrayList<>();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews(view);
        view.findViewById(R.id.create_event_first_title).requestFocus();
        initListeners();
        setupFlexBox();
    }

    private void setupFlexBox() {

        List<String> tags = TagsHolder.getInstance().getTags();
        for (String tag : tags) {
            TagView tagView = createTag(tag);
            setupTagListener(tagView);
            flexboxLayout.addView(tagView);
        }
        TagView tagView = createTag("+");
        tagView.setOnClickListener(v -> onTagAdd());
        flexboxLayout.addView(tagView);
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
                flexboxLayout.addView(tagView, flexboxLayout.getChildCount() - 1);
            } else {
                Toast.makeText(getContext(), R.string.error_empty_tag, Toast.LENGTH_SHORT).show();
            }
            ((CreateEventActivity) getActivity()).hideKeyboard();
        });
        dialogBuilder.setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.dismiss()));
        AlertDialog alert = dialogBuilder.create();
        alert.show();

    }

    private void initListeners() {
        nextBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(titleEt.getText().toString())) {
                Toast.makeText(getContext(), R.string.error_event_title, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(descriptionEt.getText().toString())) {
                Toast.makeText(getContext(), R.string.error_event_description, Toast.LENGTH_SHORT).show();
            } else if (images.get(MAIN_IMAGE) == null) {
                Toast.makeText(getContext(), R.string.error_event_picture, Toast.LENGTH_SHORT).show();
            } else {
                ((ICreateEventActivity) getActivity()).collectData(images, titleEt.getText().toString(), descriptionEt.getText().toString(), tagList);
            }
        });
        firstPicDelete.setOnClickListener(v -> onDeleteClicked(firstPicHolder, firstPicDelete, firstImageImageView, FIRST_IMAGE));
        secondPicDelete.setOnClickListener(v -> onDeleteClicked(secondPicHolder, secondPicDelete, secondImageImageView, SECOND_IMAGE));
        thirdPicDelete.setOnClickListener(v -> onDeleteClicked(thirdPicHolder, thirdPicDelete, thirdImageImageView, THIRD_IMAGE));
        fourthPicDelete.setOnClickListener(v -> onDeleteClicked(fourthPicHolder, fourthPicDelete, fourthImageImageView, FOURTH_IMAGE));
        fifthPicDelete.setOnClickListener(v -> onDeleteClicked(fifthPicHolder, fifthPicDelete, fifthImageImageView, FIFTH_IMAGE));
        mainPic.setOnClickListener(v -> checkPermissionAndRequestImage(MAIN_IMAGE));
        firstPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(FIRST_IMAGE));
        secondPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(SECOND_IMAGE));
        thirdPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(THIRD_IMAGE));
        fourthPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(FOURTH_IMAGE));
        fifthPicHolder.setOnClickListener(v -> checkPermissionAndRequestImage(FIFTH_IMAGE));
    }

    private void checkPermissionAndRequestImage(int imageRequest) {
        if (checkStoragePermission()) {
            requestImage(imageRequest);
        } else {
            this.imageRequest = imageRequest;
        }
    }

    private void requestImage(int requestIndex) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), requestIndex);

        Toast.makeText(getContext(), "requestImage", Toast.LENGTH_SHORT).show();
    }

    private void onImageClicked(TextView holder, ImageView imageHolder, ImageView deleteImage, Uri uri) {

        holder.setText("");
        holder.setBackground(null);
        imageHolder.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(uri).into(imageHolder);
        deleteImage.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "onImageClicked", Toast.LENGTH_SHORT).show();
    }

    private void onDeleteClicked(TextView holder, ImageView deleteImage, ImageView imageHolder, int index) {
        imageHolder.setVisibility(View.GONE);
        deleteImage.setVisibility(View.GONE);
        holder.setBackgroundResource(R.drawable.round_blue_edit_text);
        holder.setText("+");
        images.remove(index);
    }

    private void onMainImageUpdate(int requestIndex, Uri uri) {
        images.put(requestIndex, uri);
        mainPic.setBackground(null);
        mainPic.setText("");
        Glide.with(getContext()).load(uri).into(mainPicImageView);
    }

    private void initViews(View view) {
        nextBtn = (ImageView) view.findViewById(R.id.create_event_next_btn);
        firstImageImageView = (ImageView) view.findViewById(R.id.first_image_imageView);
        secondImageImageView = (ImageView) view.findViewById(R.id.second_image_imageView);
        thirdImageImageView = (ImageView) view.findViewById(R.id._third_image_imageView);
        fourthImageImageView = (ImageView) view.findViewById(R.id.fourth_image_imageView);
        fifthImageImageView = (ImageView) view.findViewById(R.id.fifth_image_imageView);
        mainPic = (TextView) view.findViewById(R.id.create_event_main_pic_placeholder);
        mainPicImageView = (ImageView) view.findViewById(R.id.create_event_main_pic_imageView);
        flexboxLayout = (FlexboxLayout) view.findViewById(R.id.create_events_tags_flex);
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
        descriptionEt = (EditText) view.findViewById(R.id.create_event_first_description_et);
        titleEt = (EditText) view.findViewById(R.id.create_event_first_title_et);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nextBtn = null;
        firstImageImageView = null;
        secondImageImageView = null;
        thirdImageImageView = null;
        fourthImageImageView = null;
        fifthImageImageView = null;
        mainPic = null;
        flexboxLayout = null;
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
        descriptionEt = null;
        titleEt = null;
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getContext(), "onActivityResult", Toast.LENGTH_SHORT).show();

        if (resultCode == RESULT_OK && data != null) {
            TextView holder = null;
            ImageView deleteImage = null;
            ImageView imageView = null;
            switch (requestCode) {
                case FIRST_IMAGE:
                    holder = firstPicHolder;
                    deleteImage = firstPicDelete;
                    imageView = firstImageImageView;
                    break;
                case SECOND_IMAGE:
                    imageView = secondImageImageView;
                    holder = secondPicHolder;
                    deleteImage = secondPicDelete;
                    break;
                case THIRD_IMAGE:
                    imageView = thirdImageImageView;
                    holder = thirdPicHolder;
                    deleteImage = thirdPicDelete;
                    break;
                case FOURTH_IMAGE:
                    imageView = fourthImageImageView;
                    holder = fourthPicHolder;
                    deleteImage = fourthPicDelete;
                    break;
                case FIFTH_IMAGE:
                    imageView = fifthImageImageView;
                    holder = fifthPicHolder;
                    deleteImage = fifthPicDelete;
                    break;
                case MAIN_IMAGE:
                    onMainImageUpdate(requestCode, data.getData());
                    return;
            }
            images.put(requestCode, data.getData());
            onImageClicked(holder, imageView, deleteImage, data.getData());
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
}
