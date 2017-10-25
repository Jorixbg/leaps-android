package com.example.xcomputers.leaps.test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networking.feed.event.Event;
import com.example.networking.feed.trainer.Image;
import com.example.networking.test.RateId;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.utils.FilePathDescriptor;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

/**
 * Created by Ivan on 9/18/2017.
 */
@Layout(layoutId = R.layout.rate_event_page)
public class EventRatingView extends BaseView<EventRatingPresenter> {

    private static final int REQUEST_STORAGE = 3456;
    private static final int FIRST_IMAGE = 1;
    private static final int FIRST_IMAGE_REQUEST = 2;

    private RatingBar ratingBar;
    private EditText  comment;
    private Button pictureBtn;
    private ImageView addCommentBtn;
    private ImageView commentDeleteImage;
    private ImageView commentImage;
    private List<Image> imagesToDelete;
    private Map<Integer, Uri> images;
    private int imageRequest = -1;
    private int rating = 0;
    private Event event;
    private Uri uri;
    private  int req = -1;
    private int rateId = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        images = new HashMap<>();
        event = (Event) getArguments().getSerializable("eventObj");
        imagesToDelete = new ArrayList<>();
        initViews(view);
        initListeners();

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
        imageHolder.setVisibility(View.GONE);
        deleteImage.setVisibility(View.GONE);
        holder.setBackgroundResource(R.drawable.round_blue_edit_text);
        holder.setText("+");
        if (images.get(index) != null) {
            images.remove(index);
        }
    }

    private void initViews(View view) {

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBarEvent);
        comment = (EditText) view.findViewById(R.id.txtComment);
        pictureBtn = (Button) view.findViewById(R.id.comment_picture);
        addCommentBtn = (ImageView) view.findViewById(R.id.add_comment_btn);
        commentImage = (ImageView) view.findViewById(R.id.comment_imageView) ;
        commentDeleteImage = (ImageView) view.findViewById(R.id.comment_pictre_delete);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ratingBar = null;
        comment = null;
        pictureBtn = null;
        addCommentBtn = null;
        commentImage = null;
    }

    private void initListeners() {

        commentDeleteImage.setOnClickListener(v -> {
                        onDeleteClicked(pictureBtn, commentDeleteImage, commentImage, FIRST_IMAGE);
        });

        pictureBtn.setOnClickListener(v -> {
            checkPermissionAndRequestImage(FIRST_IMAGE_REQUEST);
        });

        addCommentBtn.setOnClickListener(v -> {
          if (TextUtils.isEmpty(comment.getText().toString())){
                Toast.makeText(getContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
                return;
            }
          if(ratingBar.getRating()<=0){
              Toast.makeText(getContext(), "Please give your rating", Toast.LENGTH_SHORT).show();
              return;
          }
          else {
              rating = (int) ratingBar.getRating();
              Toast.makeText(getContext(),
                      String.valueOf(rating), Toast.LENGTH_LONG).show();
          }
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = df.format(c.getTime());

            Date d = null;
            try {
                d = df.parse(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long milliseconds = d.getTime();

            presenter.rateEvent(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""),
                    event.eventId(),rating,comment.getText().toString(),milliseconds);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode ==2&& data != null) {
            if(resultCode == Activity.RESULT_OK){
                uri=data.getParcelableExtra("result");
                Log.e("Uri",uri+"");
                TextView holder = null;
                ImageView deleteImage = null;
                ImageView imageView = null;
                    holder = pictureBtn;
                    deleteImage = commentDeleteImage;
                    imageView = commentImage;
                    req = FIRST_IMAGE;
                images.put(req, uri);
                onImageClicked(holder, imageView,deleteImage,uri);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
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


    private MultipartBody.Part getImageFromUri(Uri uri){
        File photo = new File(FilePathDescriptor.getPath(getContext(), uri));
        RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), photo);
        MultipartBody.Part pic = MultipartBody.Part.createFormData("image", photo.getName(), mainImageBody);
        return pic;
    }

    @Override
    protected EventRatingPresenter createPresenter() {
        return new EventRatingPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getRateIdObservable().subscribe(this::getRateId));
        subscriptions.add(presenter.getImageObservable().subscribe(this::getImage));
    }

    private void getRateId(RateId integer){
        rateId = integer.getRateId();
        if(rateId!=0){
            presenter.uploadMainImage(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""),rateId,getImageFromUri(uri));
        }
    }

    private void getImage(Image image){
        Image imageU = image;

        Bundle bundle = new Bundle();
        bundle.putSerializable("eventObj",event);
        openFragment(EventCommentPage.class, bundle, true);
    }

}
