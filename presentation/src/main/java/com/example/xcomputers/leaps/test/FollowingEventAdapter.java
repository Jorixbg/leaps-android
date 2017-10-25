package com.example.xcomputers.leaps.test;

import android.content.Context;
import android.preference.PreferenceManager;
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
import com.example.networking.feed.event.RealEvent;
import com.example.xcomputers.leaps.LeapsApplication;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.utils.GlideInstance;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ivan on 10/11/2017.
 */

public class FollowingEventAdapter extends RecyclerView.Adapter<FollowingEventAdapter.ViewHolder> {

    private List<RealEvent> followingEvents;
    private Context context;
    private Event event;
    private FollowingEventPresenter presenter;


    public FollowingEventAdapter( Context context,List<RealEvent> followingEvents, FollowingEventPresenter presenter) {
        this.followingEvents = followingEvents;
        this.context = context;
        this.presenter = presenter;
    }



    @Override
    public FollowingEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View row = li.inflate(R.layout.home_feed_activities_recycler_item, parent, false);
        FollowingEventAdapter.ViewHolder vh = new FollowingEventAdapter.ViewHolder(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(FollowingEventAdapter.ViewHolder holder, int position) {
        ViewHolder mainHolder = (ViewHolder) holder;
        event = followingEvents.get(position);


        GlideInstance.loadImageCircle(mainHolder.itemPic.getContext(), event.ownerPicUrl(), mainHolder.itemPic, R.drawable.profile_placeholder);
        Glide.with(mainHolder.eventPic.getContext()).load(LeapsApplication.BASE_URL + File.separator + event.imageUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.drawable.event_placeholder).into(mainHolder.eventPic);
        mainHolder.itemName.setText(event.ownerName());
        mainHolder.itemTitle.setText(event.title());
        if (event.tags() != null) {
            if (event.tags().length > 0) {
                mainHolder.itemTag1.setText(event.tags()[0]);
            }else{
                mainHolder.itemTag1.setVisibility(View.INVISIBLE);
                mainHolder.itemTag2.setVisibility(View.INVISIBLE);
            }
            if (event.tags().length > 1) {
                mainHolder.itemTag2.setText(event.tags()[1]);
            }else{
                mainHolder.itemTag2.setVisibility(View.INVISIBLE);
            }
        }
        mainHolder.followBtn.setImageResource(R.drawable.unfollow_event);

        Calendar eventTime = Calendar.getInstance();
        eventTime.setTime(event.timeFrom());
        mainHolder.itemDate.setText(
                eventTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                        + " "
                        + eventTime.get(Calendar.DAY_OF_MONTH)
                        + eventTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));

        //TODO mainHolder.itemDistance;
        //TODO mainHolder.shareBtn;
        if (event.priceFrom() > 0) {
            mainHolder.itemPrice.setBackground(ContextCompat.getDrawable(mainHolder.itemPrice.getContext(), R.drawable.round_white_button_shape));
            mainHolder.itemPrice.setAllCaps(false);
            mainHolder.itemPrice.setText(String.format(Locale.getDefault(), "%s%.2f", mainHolder.itemPrice.getContext().getString(R.string.from), event.priceFrom()));
        } else {
            mainHolder.itemPrice.setBackground(ContextCompat.getDrawable(mainHolder.itemPrice.getContext(), R.drawable.register_button_shape));
            mainHolder.itemPrice.setAllCaps(true);
            mainHolder.itemPrice.setText(R.string.lbl_free);
        }
        boolean clicked = false;

        List<RealEvent> eventList = presenter.getRealEventList();
        if(eventList !=null) {
            for (int i = 0; i < eventList.size(); i++) {
                if (event.eventId() == eventList.get(i).eventId()) {
                    mainHolder.followBtn.setImageResource(R.drawable.follow_event);
                    mainHolder.setClicked(true);
                    clicked = true;
                    break;
                }
            }
            if (!clicked) {
                mainHolder.followBtn.setImageResource(R.drawable.unfollow_event);
                mainHolder.setClicked(false);
            }
        }

    }

    @Override
    public int getItemCount() {
        return followingEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
        private ImageView followBtn;
        private boolean isClicked;


        public ViewHolder(View itemView) {
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
            followBtn = (ImageView) itemView.findViewById(R.id.feed_recycler_follow_button);
            followBtn.setOnClickListener(this);
        }

        public boolean isClicked() {
            return isClicked;
        }

        public void setClicked(boolean clicked) {
            isClicked = clicked;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId() == followBtn.getId()){
                final long followingEventId = followingEvents.get(position).eventId();
                presenter.followingEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization", ""), followingEventId);
                presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization",""));
                checkForFollowing();
            }

        }

        public void checkForFollowing(){
            if (!isClicked) {
                followBtn.setImageResource(R.drawable.follow_event);
                isClicked=true;

            }
            else {
                followBtn.setImageResource(R.drawable.unfollow_event);
                isClicked=false;
            }
        }



    }
}