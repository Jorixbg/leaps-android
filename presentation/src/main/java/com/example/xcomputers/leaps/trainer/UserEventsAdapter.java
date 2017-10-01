package com.example.xcomputers.leaps.trainer;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.networking.feed.event.Event;
import com.example.xcomputers.leaps.LeapsApplication;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.homefeed.activities.HomeFeedActivitiesAdapter;
import com.example.xcomputers.leaps.utils.GlideInstance;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by xComputers on 02/07/2017.
 */

public class UserEventsAdapter extends RecyclerView.Adapter<UserEventsAdapter.TrainerEventViewHolder> {


    private List<Event> events;
    private HomeFeedActivitiesAdapter.OnEventClickListener listener;

    public UserEventsAdapter(List<Event> events, HomeFeedActivitiesAdapter.OnEventClickListener listener) {

        this.events = events;
        this.listener = listener;
    }

    @Override
    public TrainerEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrainerEventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_feed_activities_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(TrainerEventViewHolder holder, int position) {
        Event event = events.get(position);
        GlideInstance.loadImageCircle(holder.itemPic.getContext(), event.ownerPicUrl(), holder.itemPic, R.drawable.event_placeholder);
        Glide.with(holder.eventPic.getContext()).load(LeapsApplication.BASE_URL + File.separator + event.imageUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.event_placeholder).into(holder.eventPic);
        holder.itemName.setText(event.ownerName());
        holder.itemTitle.setText(event.title());
        if (event.tags() != null) {
            if (event.tags().length > 0) {
                holder.itemTag1.setText(event.tags()[0]);
            } else {
                holder.itemTag1.setVisibility(View.INVISIBLE);
                holder.itemTag2.setVisibility(View.INVISIBLE);
            }
            if (event.tags().length > 1) {
                holder.itemTag2.setText(event.tags()[1]);
            } else {
                holder.itemTag2.setVisibility(View.INVISIBLE);
            }
        }

        Calendar eventTime = Calendar.getInstance();
        eventTime.setTime(event.timeFrom());
        holder.itemDate.setText(
                eventTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                        + " "
                        + eventTime.get(Calendar.DAY_OF_MONTH)
                        + eventTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));

        //TODO mainHolder.itemDistance;
        //TODO mainHolder.shareBtn;
        if (event.priceFrom() > 0) {
            holder.itemPrice.setBackground(ContextCompat.getDrawable(holder.itemPrice.getContext(), R.drawable.round_white_button_shape));
            holder.itemPrice.setAllCaps(false);
            holder.itemPrice.setText(String.format(Locale.getDefault(), "%s%s", holder.itemPrice.getContext().getString(R.string.from), event.priceFrom()));
        } else {
            holder.itemPrice.setBackground(ContextCompat.getDrawable(holder.itemPrice.getContext(), R.drawable.register_button_shape));
            holder.itemPrice.setAllCaps(true);
            holder.itemPrice.setText(R.string.lbl_free);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class TrainerEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView itemPic;
        private TextView itemName;
        private TextView itemTitle;
        private TextView itemTag1;
        private TextView itemTag2;
        private TextView itemDate;
        private ImageView itemRecurringIcon;
        private TextView itemDistance;
        private ImageView shareBtn;
        private TextView itemPrice;
        private ImageView eventPic;

        public TrainerEventViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemPic = (ImageView) itemView.findViewById(R.id.feed_recycler_profile_pic);
            eventPic = (ImageView) itemView.findViewById(R.id.feed_recycler_background);
            itemName = (TextView) itemView.findViewById(R.id.feed_recycler_name);
            itemTitle = (TextView) itemView.findViewById(R.id.feed_recycler_title);
            itemTag1 = (TextView) itemView.findViewById(R.id.feed_recycler_tag1);
            itemTag2 = (TextView) itemView.findViewById(R.id.feed_recycler_tag2);
            itemDate = (TextView) itemView.findViewById(R.id.feed_recycler_date);
            itemRecurringIcon = (ImageView) itemView.findViewById(R.id.feed_recycler_recurring_icon);
            itemDistance = (TextView) itemView.findViewById(R.id.feed_recycler_distance_tv);
            shareBtn = (ImageView) itemView.findViewById(R.id.feed_recycler_share_button);
            itemPrice = (TextView) itemView.findViewById(R.id.feed_recycler_price);
        }

        @Override
        public void onClick(View view) {
            listener.onEventClicked(events.get(getAdapterPosition()));
        }
    }
}

