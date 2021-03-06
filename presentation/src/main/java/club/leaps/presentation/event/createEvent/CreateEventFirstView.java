package club.leaps.presentation.event.createEvent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import club.leaps.presentation.utils.TagView;
import club.leaps.networking.feed.event.EventImage;
import club.leaps.networking.feed.event.RealEvent;
import club.leaps.presentation.LeapsApplication;
import club.leaps.presentation.R;
import club.leaps.presentation.TagsHolder;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.crop.CropActivity;
import club.leaps.presentation.utils.TagView;
import com.google.android.flexbox.FlexboxLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private Map<Integer, Uri> imagesToDelete;
    private List<String> tagList;
    private int imageRequest = -1;
    private RealEvent event;
    private Uri uriImg;
    private int counter;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        images = new HashMap<>();
        imagesToDelete = new HashMap<>();
        tagList = new ArrayList<>();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        event = (RealEvent) getArguments().getSerializable("event");

        initViews(view);
        if(event !=null){
            loadVies();
        }
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
                titleEt.setError(getString(R.string.empty_event_title));
                titleEt.requestFocus();
            } else if (TextUtils.isEmpty(descriptionEt.getText().toString())) {
                descriptionEt.setError(getString(R.string.empty_event_description));
                descriptionEt.requestFocus();
            } else if (images.get(MAIN_IMAGE) == null && getActivity()==null) {
                Toast.makeText(getContext(), R.string.error_event_picture, Toast.LENGTH_SHORT).show();
            } else {
                ((ICreateEventActivity) getActivity()).collectData(images, imagesToDelete,titleEt.getText().toString(), descriptionEt.getText().toString(), tagList);
            }

        });
        firstPicDelete.setOnClickListener(v ->onDeleteClicked(firstPicHolder, firstPicDelete, firstImageImageView, FIRST_IMAGE));
        secondPicDelete.setOnClickListener(v ->onDeleteClicked(secondPicHolder, secondPicDelete, secondImageImageView, SECOND_IMAGE));
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
        imageHolder.setVisibility(View.GONE);
        deleteImage.setVisibility(View.GONE);
        holder.setBackgroundResource(R.drawable.round_blue_edit_text);
        holder.setText("+");
        images.remove(index);
    }

    private void onMainImageUpdate(int requestIndex, Uri uri) {
        images.put(requestIndex, uri);
        imagesToDelete.put(counter,uri);
        counter++;
        mainPic.setBackground(null);
        mainPic.setText("");
        Glide.with(getContext()).load(uri).into(mainPicImageView);
    }

    private void initViews(View view) {
        nextBtn = (ImageView) view.findViewById(R.id.create_event_next_btn);
        firstImageImageView = (ImageView) view.findViewById(R.id.first_image_imageView);
        secondImageImageView = (ImageView) view.findViewById(R.id.second_image_imageView);
        thirdImageImageView = (ImageView) view.findViewById(R.id.third_image_imageView);
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

    private void loadVies() {

        descriptionEt.setText(event.description());
        titleEt.setText(event.title());
        String[] tags = event.tags();
        for (String tag : tags) {
            TagView tagView = new TagView(getContext());
            tagView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_tag_shape));
            tagView.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
            tagView.setText(tag);
            flexboxLayout.addView(tagView);
        }

        loadImages();
    }


    private void loadImages() {
        List<EventImage> images = event.images();
        if (images.size() > 0) {
            EventImage first = images.get(0);
            setupGlideCallBack(first.getImageUrl(), mainPicImageView, null, mainPic);
            if(images.size() >= 2) {
                EventImage second = images.get(1);
                if (second != null) {
                    setupGlideCallBack(first.getImageUrl(), firstImageImageView, firstPicDelete, firstPicHolder);
                }
            }
            if(images.size() >= 3) {
                EventImage second = images.get(2);
                if (second != null) {
                    setupGlideCallBack(second.getImageUrl(), secondImageImageView, secondPicDelete, secondPicHolder);
                }
            }
            if(images.size() >= 4) {
                EventImage third = images.get(3);
                if (third != null) {
                    setupGlideCallBack(third.getImageUrl(), thirdImageImageView, thirdPicDelete, thirdPicHolder);
                }
            }
            if(images.size() >= 5) {
                EventImage fourth = images.get(4);
                if (fourth != null) {
                    setupGlideCallBack(fourth.getImageUrl(), fourthImageImageView, fourthPicDelete, fourthPicHolder);
                }
            }
            if(images.size() >= 6) {
                EventImage fifth = images.get(5);
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
                if(imageView != mainPicImageView) {
                    deleteImage.setVisibility(View.VISIBLE);
                }
                placeHolder.setText("");
                placeHolder.setBackground(null);
                return false;
            }
        }).into(imageView);
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

        if (resultCode == RESULT_OK && data != null) {
            uriImg=data.getParcelableExtra("result");
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
                    onMainImageUpdate(requestCode, uriImg);
                    return;
            }
            images.put(requestCode,uriImg);
            imagesToDelete.put(counter,uriImg);
            counter++;
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



}
