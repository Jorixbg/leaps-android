package com.example.xcomputers.leaps.event;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.xcomputers.leaps.LeapsApplication;
import com.example.xcomputers.leaps.R;

import java.io.File;
import java.util.List;

/**
 * Created by xComputers on 02/07/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<String> data;

    public EventAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_images_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(LeapsApplication.BASE_URL + File.separator + data.get(position))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.event_placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        public EventViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.event_recycler_image);
           /* itemView.setOnClickListener(v ->{
                Intent intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.IMAGE_URL, data.get(getAdapterPosition()));
                itemView.getContext().startActivity(intent);
            });*/

        }

    }
}
