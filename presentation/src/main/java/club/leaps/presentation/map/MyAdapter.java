package club.leaps.presentation.map;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import club.leaps.presentation.event.EventActivity;
import club.leaps.networking.feed.event.RealEvent;
import club.leaps.presentation.LeapsApplication;
import club.leaps.presentation.R;
import club.leaps.presentation.event.EventActivity;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static club.leaps.presentation.event.EventActivity.EVENT_KEY;

/**
 * Created by Ivan on 9/8/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.NewViewHolder> {

    private List<RealEvent> eventite;
    private Context context;
    private  RealEvent event;
    private int firstVisible = 0;

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
        event = eventite.get(position);
        eventite.get(position).setPosition(position);
        Glide.with(holder.itemPic.getContext())
                .load(LeapsApplication.BASE_URL + File.separator + event.imageUrl())
                .placeholder(R.drawable.event_placeholder)
                .into(holder.itemPic);

        holder.itemTitle.setText(event.title());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.timeFrom());
        holder.itemDetails.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                + ", "
                + calendar.get(Calendar.HOUR)
                + ":"
                + String.format(Locale.getDefault(), "%02d",calendar.get(Calendar.MINUTE)));


        if (position == firstVisible) {
            holder.indicator.setVisibility(View.VISIBLE);
            holder.indicator.setBackgroundColor(holder.itemView.getResources().getColor(R.color.full_indicator));
        }
        else {
            holder.indicator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventite.size();
    }


    public class NewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView itemPic;
        private TextView itemTitle;
        private TextView itemDetails;
        private TextView indicator;


        NewViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemPic = (ImageView) itemView.findViewById(R.id.cal_recycler_item_pic);
            itemTitle = (TextView) itemView.findViewById(R.id.cal_recycler_item_title);
            itemDetails = (TextView) itemView.findViewById(R.id.cal_recycler_item_details);
            indicator = itemView.findViewById(R.id.map_recycler_indicator);

        }

        @Override
        public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), EventActivity.class);
                intent.putExtra(EventActivity.EVENT_KEY, eventite.get(getAdapterPosition()));
                itemView.getContext().startActivity(intent);

        }
    }

    public void changeItem(int position){
        firstVisible = position;
        notifyItemChanged(firstVisible);
        notifyDataSetChanged();
    }


}
