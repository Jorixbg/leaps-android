package com.example.xcomputers.leaps.homefeed.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.networking.feed.event.Event;
import com.example.networking.feed.event.RealEvent;
import com.example.xcomputers.leaps.LeapsApplication;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.utils.GlideInstance;
import com.example.xcomputers.leaps.utils.SectionedDataHolder;

import java.io.ByteArrayOutputStream;
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
    private Uri uri;

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

        if(!PreferenceManager.getDefaultSharedPreferences(mainHolder.itemView.getContext()).contains("Authorization")
                || PreferenceManager.getDefaultSharedPreferences(mainHolder.itemView.getContext()).getString("Authorization", null) == null){
            mainHolder.shareBtn.setVisibility(View.GONE);
            mainHolder.followBtn.setVisibility(View.GONE);
        }
        else {
            mainHolder.shareBtn.setVisibility(View.VISIBLE);
            mainHolder.followBtn.setVisibility(View.VISIBLE);
        }

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

        int distance = Math.round(event.distance());

        mainHolder.itemDistance.setText(String.valueOf(distance) + " km");

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
                String imgUrl = data.getItem(section, relativePos).imageUrl();
               //  Share to facebook only

            /* ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
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
                               sendShareIntent(uri,view);
                               resource.recycle();
                           }
                       });*/

               Intent intent =  new Intent(Intent.ACTION_SEND);
               intent.putExtra(Intent.EXTRA_TEXT, "Check out this event at Leaps! \n" +
                       data.getItem(section, relativePos).title()+"" + "\nDownload Leaps app now and get first training FREE.");
               intent.setType("text/plain");
               view.getContext().startActivity(Intent.createChooser(intent, "Share via..."));

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


    private void sendShareIntent(Uri uri,View view){
        Intent intent =  new Intent(Intent.ACTION_SEND);

       intent.putExtra(Intent.EXTRA_TEXT, "Check out this event at Leaps! \n" +
               event.title()+"" + "\nDownload Leaps app now and get first training FREE.");
        intent.setType("text/plain");
        view.getContext(). startActivity(Intent.createChooser(intent, "Share via..."));
       /* Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this event at Leaps! \n" +
                        event.title()+"");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        */


        /*PackageManager pm = view.getContext().getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_SEND, null);
        mainIntent.setType("text/plain");
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0); // returns all applications which can listen to the SEND Intent
        for (ResolveInfo info : resolveInfos) {
            ApplicationInfo applicationInfo = info.activityInfo.applicationInfo;

            //get package name, icon and label from applicationInfo object and display it in your custom layout

            //App icon = applicationInfo.loadIcon(pm);
            String name  = applicationInfo.loadLabel(pm).toString();
            String package_name = applicationInfo.packageName;
            Log.e("Tag name " , name +"");
            Log.e("Tag package_name " , package_name + "");
        }

        view.getContext().startActivity(mainIntent);*/

       /* Uri imageUri = uri;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //Target whatsapp:
        shareIntent.setPackage("com.whatsapp");
        //Add text and then Image URI
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello from Leaps");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        view.getContext().startActivity(shareIntent);*/




      //  view.getContext().startActivity(Intent.createChooser(intent,"compatible apps:"));
    }





    private void sendTextIntent(View view){
        Intent intent =  new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this event at Leaps! \n" +
                event.title()+"");
        intent.setType("text/plain");
        view.getContext().startActivity(Intent.createChooser(intent,"compatible apps:"));
    }




    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

        return Uri.parse(path);
    }

    public interface OnEventClickListener {
        void onEventClicked(Event event);
    }

    public interface OnHeaderClickListener {
        void onHeaderClicked(int index);
    }
}