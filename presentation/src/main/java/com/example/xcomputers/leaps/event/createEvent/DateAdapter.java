package com.example.xcomputers.leaps.event.createEvent;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.networking.test.ChoosenDate;
import com.example.xcomputers.leaps.R;

import java.util.List;

/**
 * Created by IvanGachmov on 12/8/2017.
 */

class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<ChoosenDate> dateList;

    public DateAdapter(List<ChoosenDate> dateList) {
        this.dateList = dateList;
    }


    @Override
    public DateAdapter.DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DateAdapter.DateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_time_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DateAdapter.DateViewHolder holder, int position) {
        String start = dateList.get(position).getStart();
        String end = dateList.get(position).getEnd();

        StringBuilder startTime = new StringBuilder(start);
        StringBuilder endTime = new StringBuilder(end);
        if(start.length()==3){
            startTime.insert(0,"0");
            startTime.insert(2,":");
        }
        else {
            startTime.insert(2,":");
        }
        if(end.length()==3){

            endTime.insert(0,"0");
            endTime.insert(2,":");
        }
        else {

            endTime.insert(2,":");
        }

        String name = startTime + " - " + endTime;
        holder.time.setText(name);
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public void removeItem(int position) {
        dateList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dateList.size());
    }

    class DateViewHolder extends RecyclerView.ViewHolder{

        private TextView time;
        public DateViewHolder(View itemView) {
            super(itemView);
            time =  itemView.findViewById(R.id.time_item_lbl);
           /* itemView.setOnClickListener(v ->{
                Intent intent = new Intent(itemView.getContext(), ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.IMAGE_URL, data.get(getAdapterPosition()));
                itemView.getContext().startActivity(intent);
            });*/

        }

    }
}