package com.example.xcomputers.leaps.test;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networking.feed.trainer.Image;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.FilePathDescriptor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ivan on 9/18/2017.
 */
@Layout(layoutId = R.layout.rate_event_page)
public class EventRatingView extends BaseView<EmptyPresenter> {

    private static final int REQUEST_STORAGE = 3456;
    private static final int FIRST_IMAGE = 1;

    private static final int FIRST_IMAGE_REQUEST = 20;


    private RatingBar ratingBar;
    private EditText  comment;
    private Button pictureBtn;
    private ImageView addComment;
    private ImageView commentDeleteImage;
    private ImageView commentImage;
    private List<Image> imagesToDelete;
    private Map<Integer, Uri> images;
    private int imageRequest = -1;
    private float rating = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        images = new HashMap<>();
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), requestIndex);
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
        addComment = (ImageView) view.findViewById(R.id.add_comment_btn);
        commentImage = (ImageView) view.findViewById(R.id.comment_imageView) ;
        commentDeleteImage = (ImageView) view.findViewById(R.id.comment_pictre_delete);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ratingBar = null;
        comment = null;
        pictureBtn = null;
        addComment = null;
        commentImage = null;
    }

    private void initListeners() {

        commentDeleteImage.setOnClickListener(v -> {
            if (EntityHolder.getInstance().getEntity().images().size() > 0) {
                imagesToDelete.add(EntityHolder.getInstance().getEntity().images().get(0));
            }
            onDeleteClicked(pictureBtn, commentDeleteImage, commentImage, FIRST_IMAGE);
        });


        pictureBtn.setOnClickListener(v -> checkPermissionAndRequestImage(FIRST_IMAGE_REQUEST));

        addComment.setOnClickListener(v -> {
          if (TextUtils.isEmpty(comment.getText().toString())){
                Toast.makeText(getContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
                return;
            }
          if(ratingBar.getRating()<=0){
              Toast.makeText(getContext(), "Please give your rating", Toast.LENGTH_SHORT).show();
              return;
          }
          else {
              rating = ratingBar.getRating();
              Toast.makeText(getContext(),
                      String.valueOf(rating), Toast.LENGTH_LONG).show();
          }
            openFragment(EventCommentPage.class, new Bundle());

        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            TextView holder = null;
            ImageView deleteImage = null;
            ImageView imageView = null;
            int req = -1;
                    holder = pictureBtn;
                    deleteImage = commentDeleteImage;
                    imageView = commentImage;
                    req = FIRST_IMAGE;

            images.put(req, data.getData());
            onImageClicked(holder, imageView,deleteImage, data.getData());
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

   private void uploadImage() {
       /* if (images.get(imageCounter) != null) {
            presenter.uploadImage(auth, User.getInstance().getUserId(), getImageFromUri(images.get(imageCounter)));
        }
        Integer keyToRemove = new Integer(-1);
        for(Map.Entry<Integer, Uri> entry : images.entrySet()){
            keyToRemove = entry.getKey();
            presenter.uploadImage(auth, User.getInstance().getUserId(), getImageFromUri(images.get(keyToRemove)));
        }

        if(keyToRemove == -1) {
            Toast.makeText(getContext(), R.string.lbl_profile_edit_success, Toast.LENGTH_SHORT).show();
            hideLoading();
            back();
            return;
        }*/
    }

    private MultipartBody.Part getImageFromUri(Uri uri){
        File photo = new File(FilePathDescriptor.getPath(getContext(), uri));
        RequestBody mainImageBody = RequestBody.create(MediaType.parse("image/*"), photo);
        MultipartBody.Part pic = MultipartBody.Part.createFormData("image", photo.getName(), mainImageBody);
        return pic;
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }
}
