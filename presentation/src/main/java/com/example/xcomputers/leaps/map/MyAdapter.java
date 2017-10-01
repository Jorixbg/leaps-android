package com.example.xcomputers.leaps.map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.networking.feed.event.RealEvent;
import com.example.xcomputers.leaps.R;

import java.util.List;

/**
 * Created by Ivan on 9/8/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.NewViewHolder> {

    private List<RealEvent> eventite;
    private Context context;
    private int selectedPos = 0;
    private int lastPosition = -1;

    public MyAdapter(Context context, List<RealEvent> eventite) {
        this.eventite = eventite;
        this.context = context;
    }

    @Override
    public NewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View row = li.inflate(R.layout.event_map_recyclerview_item, parent,false);
        NewViewHolder vh = new NewViewHolder(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(NewViewHolder holder, final int position) {
        RealEvent event = eventite.get(position);
        eventite.get(position).setPosition(position);
        holder.name.setText(event.title());
        holder.trainer.setText(event.address());
       // holder.imageEvent.setImageResource(event.getImageId());
        holder.itemView.setSelected(selectedPos == position);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mainActivity = (MainActivity) context;
                //mainActivity.gotoLocation(eventite.get(position).getLatitude(),eventite.get(position).getLongitude(),15);

            }
        });

        if(position >lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
        else if(position < lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.bottom_from_up);
            holder.itemView.startAnimation(animation);
        }


    }

    @Override
    public int getItemCount() {
        return eventite.size();
    }


    public class NewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageEvent;
        TextView name;
        TextView trainer;
        LinearLayout linearLayout;


        public NewViewHolder(View row) {
            super(row);
            imageEvent = (ImageView) row.findViewById(R.id.image_iv);
            name = (TextView) row.findViewById(R.id.name_tv);
            trainer = (TextView) row.findViewById(R.id.age_tv);
            linearLayout = (LinearLayout) row.findViewById(R.id.linearLayout);


        }

    }


}
