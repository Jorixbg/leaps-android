package com.example.xcomputers.leaps.homefeed.activities;

import android.app.Activity;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.ItemCoord;
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.afollestad.sectionedrecyclerview.SectionedViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.networking.feed.event.Event;
import com.example.networking.feed.event.RealEvent;
import com.example.xcomputers.leaps.LeapsApplication;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.utils.GlideInstance;
import com.example.xcomputers.leaps.utils.SectionedDataHolder;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by xComputers on 13/06/2017.
 */

public class HomeFeedActivitiesAdapter extends SectionedRecyclerViewAdapter<SectionedViewHolder> {

    private SectionedDataHolder data;
    private OnEventClickListener listener;
    private OnHeaderClickListener headerClickListener;
    private Event event;
    private HomeFeedActivitiesPresenter presenter;
    private MainVH mainHolder;

    public HomeFeedActivitiesAdapter(SectionedDataHolder data,HomeFeedActivitiesPresenter presenter, OnEventClickListener listener, OnHeaderClickListener headerClickListener) {
        this.listener = listener;
        this.headerClickListener = headerClickListener;
        this.data = data;
        this.presenter = presenter;
    }



    @Override
    public int getSectionCount() {

        return data.getSectionCount();
    }

    @Override
    public int getItemCount(int sectionIndex) {

        return data.getItemsCount(sectionIndex);
    }

    @Override
    public void onBindHeaderViewHolder(SectionedViewHolder holder, int section, boolean expanded) {
        HeaderVH headerHolder = (HeaderVH) holder;
        // Setup header view
        headerHolder.headerName.setText(data.getHeaderForSection(section));
        if(section == 0){
            ((HeaderVH) holder).itemView.setBackgroundColor(ContextCompat.getColor(((HeaderVH) holder).itemView.getContext(), R.color.dirty_white));
        }
    }

    @Override
    public void onBindViewHolder(SectionedViewHolder holder, int section, int relativePosition, int absolutePosition) {
        mainHolder = (MainVH) holder;
        event = data.getItem(section, relativePosition);

        if(mainHolder.isClicked()){
            mainHolder.followBtn.setImageResource(R.drawable.follow_event);
        }
        else {
            mainHolder.followBtn.setImageResource(R.drawable.unfollow_event);
        }

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

        Date date = event.timeFrom();
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);

        mainHolder.itemDate.setText(
                eventTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                        + " "
                        + eventTime.get(Calendar.DAY_OF_MONTH)
                        + " "
                        + eventTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                        + ",  "
                        + dateFormatted
                        + ",  "
                        + eventTime.get(Calendar.YEAR)
                        + " ");

        //TODO mainHolder.itemDistance;
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
    public void onBindFooterViewHolder(SectionedViewHolder holder, int section) {
        // Setup footer view, if footers are enabled (see the next section)
    }

    @Override
    public SectionedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderVH(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_feed_recycler_header, parent, false));
            default:
                return new MainVH(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_feed_activities_recycler_item, parent, false));
        }
    }

    class HeaderVH extends SectionedViewHolder implements View.OnClickListener {
        private TextView headerName;

        HeaderVH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            headerName = (TextView) itemView.findViewById(R.id.home_feed_header_name);
        }

        @Override
        public void onClick(View view) {
            ItemCoord position = getRelativePosition();
            int section = position.section();
            headerClickListener.onHeaderClicked(section);
        }
    }

    class MainVH extends SectionedViewHolder implements View.OnClickListener {

        private ImageView itemPic;
        private TextView itemName;
        private TextView itemTitle;
        private TextView itemTag1;
        private TextView itemTag2;
        private TextView itemDate;
        private ImageView itemRecurringIcon;
        private TextView itemDistance;
        private TextView itemPrice;
        private ImageView eventPic;
        private ImageView followBtn;
        private ImageView shareBtn;
        private boolean isClicked;



        MainVH(View itemView) {
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
            itemPrice = (TextView) itemView.findViewById(R.id.feed_recycler_price);
            followBtn = (ImageView) itemView.findViewById(R.id.feed_recycler_follow_button);
            shareBtn = (ImageView) itemView.findViewById(R.id.feed_recycler_share_button);
            followBtn.setOnClickListener(this);
            shareBtn.setOnClickListener(this);



        }
        public boolean isClicked() {
            return isClicked;
        }

        public void setClicked(boolean clicked) {
            isClicked = clicked;
        }

        @Override
        public void onClick(View view) {
            ItemCoord position = getRelativePosition();
            int section = position.section();
            int relativePos = position.relativePos();
            //Toast.makeText(view.getContext(), "Item " + relativePos + " in section" + section + " clicked!", Toast.LENGTH_SHORT).show();
            // Fix Method to work like onEventClicked method
            if (view.getId() == followBtn.getId()){
                final long followingEventId = data.getItem(section, relativePos).eventId();
                presenter.followingEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization", ""), followingEventId);
                presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization",""));
                checkForFollowing();

            }
            if(view.getId() == shareBtn.getId()){
                String imgUrl = event.imageUrl();

                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentTitle(event.title())
                        .setContentDescription(event.description())
                        .setContentUrl(Uri.parse("http://ec2-35-157-240-40.eu-central-1.compute.amazonaws.com:8888/"+imgUrl))
                        .build();


                ShareDialog.show((Activity) view.getContext(), shareLinkContent);


            }
            if (view.getId() != followBtn.getId() && view.getId() != shareBtn.getId()){
                listener.onEventClicked(data.getItem(section, relativePos));
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



    public interface OnEventClickListener {
        void onEventClicked(Event event);
    }

    public interface OnHeaderClickListener {
        void onHeaderClicked(int index);
    }
}