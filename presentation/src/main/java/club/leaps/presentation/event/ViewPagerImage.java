package club.leaps.presentation.event;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import club.leaps.presentation.LeapsApplication;
import club.leaps.presentation.R;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;

import java.io.File;
import java.util.List;

import rx.Subscription;


public class ViewPagerImage extends BaseView<EmptyPresenter> {

    private ImageView imageView;
    private String url;

    public ViewPagerImage(String url) {
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.event_images_recycler_item, container, false);
        imageView = (ImageView) root.findViewById(R.id.event_recycler_image);
        Glide.with(imageView.getContext())
                .load(LeapsApplication.BASE_URL + File.separator + url)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.event_placeholder)
                .into(imageView);
        return root;

    }



    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }

}
