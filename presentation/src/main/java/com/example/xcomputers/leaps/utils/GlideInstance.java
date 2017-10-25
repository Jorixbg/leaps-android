package com.example.xcomputers.leaps.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.xcomputers.leaps.LeapsApplication;

import java.io.File;

/**
 * Created by xComputers on 15/06/2017.
 */

public class GlideInstance {

    private static Uri uriImg;
    private static Context contextImg;


    public static void loadImageCircle(Context context, String urlTarget, ImageView view, int placeHolder) {

        contextImg = context;
        if (TextUtils.isEmpty(urlTarget)) {
            urlTarget = " ";
        }else{
            urlTarget = LeapsApplication.BASE_URL + File.separator + urlTarget;
        }

        Bitmap picture = BitmapFactory.decodeResource(context.getResources(), placeHolder);
        RoundedBitmapDrawable circularBitmapDrawable =
                RoundedBitmapDrawableFactory.create(context.getResources(), picture);
        circularBitmapDrawable.setCircular(true);




        Glide.with(context).load(urlTarget).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(circularBitmapDrawable)
                .centerCrop().into(new BitmapImageViewTarget(view) {
            @Override
            protected void setResource(Bitmap resource) {

                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                view.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public static void loadImageCircle(Context context, Uri uri, ImageView view, int placeHolder) {
        contextImg = context;
        Bitmap picture = BitmapFactory.decodeResource(context.getResources(), placeHolder);
        RoundedBitmapDrawable circularBitmapDrawable =
                RoundedBitmapDrawableFactory.create(context.getResources(), picture);
        circularBitmapDrawable.setCircular(true);

        Glide.with(context)
                .load(uri).asBitmap()
                .placeholder(circularBitmapDrawable)
                .centerCrop()
                .into(new BitmapImageViewTarget(view) {
            @Override
            protected void setResource(Bitmap resource) {

                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                view.setImageDrawable(circularBitmapDrawable);
            }
        });
    }




}
