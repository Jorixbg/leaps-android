package club.leaps.presentation.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import club.leaps.presentation.LeapsApplication;
import club.leaps.presentation.R;

import java.io.File;

/**
 * Created by xComputers on 06/08/2017.
 */

public class ImageViewActivity extends AppCompatActivity {

    public static final String IMAGE_URL = "ImageViewActivity.IMAGE_URL";

    private ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_activity);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        imageView = (ImageView) findViewById(R.id.image);
        String url = getIntent().getStringExtra(IMAGE_URL);
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        Glide.with(this).load(LeapsApplication.BASE_URL + File.separator + url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.event_placeholder).into(imageView);
    }
}
