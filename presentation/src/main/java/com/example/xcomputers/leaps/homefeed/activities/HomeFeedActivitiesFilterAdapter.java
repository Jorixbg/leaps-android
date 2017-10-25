package com.example.xcomputers.leaps.homefeed.activities;

import android.app.Activity;
import android.net.Uri;
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
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by xComputers on 09/07/2017.
 */

public class HomeFeedActivitiesFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private List<Event> data;
    private HomeFeedActivitiesAdapter.OnEventClickListener listener;
    private boolean isLoading;
    private Event event;
    private HomeFeedActivitiesPresenter presenter;

    public HomeFeedActivitiesFilterAdapter(List<Event> data, HomeFeedActivitiesPresenter presenter, HomeFeedActivitiesAdapter.OnEventClickListener listener) {
        this.listener = listener;
        this.data = data;
        this.presenter = presenter;
    }

    public void setData(List<Event> holder){
        this.data = holder;
        notifyDataSetChanged();
    }

    public void setLoadingState(boolean isLoading){
        this.isLoading = isLoading;
        if(isLoading){
            this.data.add(new RealEvent());
        }else{
            this.data.remove(this.data.size() - 1);
        }

    }

    public boolean getLoadState(){
        return isLoading;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ITEM:
            default:
                return new MainVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_feed_activities_recycler_item, parent, false));
            case VIEW_TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_recycler_item, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case VIEW_TYPE_ITEM:
                event = data.get(position);
                MainVH mainHolder = (MainVH) holder;
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
                mainHolder.itemDate.setText(
                        eventTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                                + " "
                                + eventTime.get(Calendar.DAY_OF_MONTH)
                                + eventTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));

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


                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position == data.size() - 1 && isLoading) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder{

        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MainVH extends RecyclerView.ViewHolder implements View.OnClickListener {


        private ImageView itemPic;
        private TextView itemName;
        private TextView itemTitle;
        private TextView itemTag1;
        private TextView itemTag2;
        private TextView itemDate;
        private ImageView itemRecurringIcon;
        private TextView itemDistance;
        private ImageView shareBtn;
        private ImageView followBtn;
        private boolean isClicked;
        private TextView itemPrice;
        private ImageView eventPic;

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
            shareBtn = (ImageView) itemView.findViewById(R.id.feed_recycler_share_button);
            itemPrice = (TextView) itemView.findViewById(R.id.feed_recycler_price);
            followBtn = (ImageView) itemView.findViewById(R.id.feed_recycler_follow_button);
            shareBtn = (ImageView) itemView.findViewById(R.id.feed_recycler_share_button);
            followBtn.setOnClickListener(this);
            shareBtn.setOnClickListener(this);
            //followBtn.setOnClickListener(view -> Toast.makeText(view.getContext(),getAdapterPosition()+" ",Toast.LENGTH_SHORT).show());


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
                final long followingEventId = data.get(position).eventId();
                presenter.followingEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization", ""), followingEventId);
                presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization",""));
                checkForFollowing();

            } if(view.getId() == shareBtn.getId()){
                String imgUrl = event.imageUrl();

                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentTitle(event.title())
                        .setContentDescription(event.description())
                        .setContentUrl(Uri.parse("http://ec2-35-157-240-40.eu-central-1.compute.amazonaws.com:8888/"+imgUrl))
                        .build();


                ShareDialog.show((Activity) view.getContext(), shareLinkContent);


            }
            if(view.getId() != followBtn.getId() && view.getId() != shareBtn.getId()){
                listener.onEventClicked(data.get(getAdapterPosition()));

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