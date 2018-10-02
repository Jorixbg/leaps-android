package club.leaps.presentation.trainer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesAdapter;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesPresenter;
import club.leaps.networking.feed.event.Event;
import club.leaps.networking.feed.event.RealEvent;
import club.leaps.presentation.LeapsApplication;
import club.leaps.presentation.R;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesAdapter;
import club.leaps.presentation.homefeed.activities.HomeFeedActivitiesPresenter;
import club.leaps.presentation.utils.GlideInstance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by xComputers on 02/07/2017.
 */

public class UserEventsAdapter extends RecyclerView.Adapter<UserEventsAdapter.TrainerEventViewHolder> {


    private List<Event> events;
    private HomeFeedActivitiesAdapter.OnEventClickListener listener;
    private HomeFeedActivitiesPresenter presenter;
    private Uri uri;
    private List<RealEvent> followingEvents;

    public UserEventsAdapter(List<Event> events,List<RealEvent> followingEvents,HomeFeedActivitiesPresenter presenter ,HomeFeedActivitiesAdapter.OnEventClickListener listener) {

        this.events = events;
        this.followingEvents = followingEvents;
        this.listener = listener;
        this.presenter = presenter;
    }

    @Override
    public TrainerEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View row = li.inflate(R.layout.home_feed_activities_recycler_item, parent, false);
        UserEventsAdapter.TrainerEventViewHolder vh = new UserEventsAdapter.TrainerEventViewHolder(row);
        return vh;
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


        Date date = event.timeFrom();
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);

        holder.itemDate.setText(
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



        int distance = Math.round(event.distance());
        holder.itemDistance.setText(String.valueOf(distance) + " km");

        if (event.priceFrom() > 0) {
            holder.itemPrice.setBackground(ContextCompat.getDrawable(holder.itemPrice.getContext(), R.drawable.round_white_button_shape));
            holder.itemPrice.setAllCaps(false);
            holder.itemPrice.setText(String.format(Locale.getDefault(), "%s%s", holder.itemPrice.getContext().getString(R.string.from), event.priceFrom()));
        } else {
            holder.itemPrice.setBackground(ContextCompat.getDrawable(holder.itemPrice.getContext(), R.drawable.register_button_shape));
            holder.itemPrice.setAllCaps(true);
            holder.itemPrice.setText(R.string.lbl_free);
        }

        boolean clicked = false;


        if(followingEvents !=null) {
            for (int i = 0; i < followingEvents.size(); i++) {
                if (event.eventId() == followingEvents.get(i).eventId()) {
                    holder.followBtn.setImageResource(R.drawable.follow_event);
                    holder.setClicked(true);
                    clicked = true;
                    break;
                }
            }
            if (!clicked) {
                holder.followBtn.setImageResource(R.drawable.unfollow_event);
                holder.setClicked(false);
            }
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
        private ImageView followBtn;
        private ImageView shareBtn;
        private boolean isClicked;
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
            followBtn = (ImageView) itemView.findViewById(R.id.feed_recycler_follow_button);
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
            int position = getAdapterPosition();
            //Toast.makeText(view.getContext(), "Item " + relativePos + " in section" + section + " clicked!", Toast.LENGTH_SHORT).show();
            // Fix Method to work like onEventClicked method
            if (view.getId() == followBtn.getId()){
                final long followingEventId = events.get(position).eventId();
                presenter.followingEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization", ""), followingEventId);
                presenter.getFollowFutureEvent(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("Authorization",""));
                checkForFollowing();

            }
            if(view.getId() == shareBtn.getId()){
              /*   String imgUrl = events.get(position).imageUrl();
                //  Share to facebook only

            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentTitle(event.title())
                        .setContentDescription(event.description())
                        .setContentUrl(Uri.parse("http://ec2-35-157-240-40.eu-central-1.compute.amazonaws.com:8888/"+imgUrl))
                        .build();
               ShareDialog shareDialog = new ShareDialog((Activity) view.getContext());
               shareDialog.show(shareLinkContent, ShareDialog.Mode.AUTOMATIC);


                Glide.with(view.getContext())
                        .load(LeapsApplication.BASE_URL + File.separator + imgUrl)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(100,100) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                uri = getImageUri(view.getContext(),resource);
                                sendShareIntent(uri,view,position);
                                resource.recycle();
                            }
                        });*/

                Intent intent =  new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, view.getContext().getString(R.string.lbl_check_event)+"\n" +
                        events.get(position).title()+"" + "\n"+view.getContext().getString(R.string.lbl_download_app));
                intent.setType("text/plain");
                view.getContext().startActivity(Intent.createChooser(intent, view.getContext().getString(R.string.share_title)));

            }
            if (view.getId() != followBtn.getId() && view.getId() != shareBtn.getId()){
                listener.onEventClicked(events.get(getAdapterPosition()));
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

        public Uri getImageUri(Context inContext, Bitmap inImage) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

            return Uri.parse(path);
        }

        private void sendShareIntent(Uri uri,View view,int pos) {
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.putExtra(Intent.EXTRA_TEXT, view.getContext().getString(R.string.lbl_check_event)+"\n" +
                    events.get(pos).title() + "" + "\n"+view.getContext().getString(R.string.lbl_download_app));
            intent.setType("text/plain");
            view.getContext().startActivity(Intent.createChooser(intent, view.getContext().getString(R.string.share_title)));
        }

    }
}

