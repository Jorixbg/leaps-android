package club.leaps.presentation.homefeed.trainers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import club.leaps.networking.feed.event.Event;
import club.leaps.networking.feed.trainer.Entity;
import club.leaps.presentation.R;
import club.leaps.presentation.utils.GlideInstance;

import java.util.Date;
import java.util.List;

/**
 * Created by xComputers on 15/06/2017.
 */

public class FeedTrainersAdapter extends RecyclerView.Adapter<FeedTrainersAdapter.TrainersViewHolder> {

    List<Entity> trainers;
    private OnTrainerClickedListener listener;

    public FeedTrainersAdapter(List<Entity> trainers, OnTrainerClickedListener listener) {
        this.listener = listener;
        this.trainers = trainers;
    }


    @Override
    public TrainersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrainersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_trainers_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(TrainersViewHolder holder, int position) {
        holder.bind(trainers.get(position));
    }

    @Override
    public int getItemCount() {
        return trainers.size();
    }

    class TrainersViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;
        TextView names;
        TextView tags;
        TextView eventsOpen;
        TextView eventsFinished;

        public TrainersViewHolder(View itemView) {
            super(itemView);
            pic = (ImageView) itemView.findViewById(R.id.feed_trainers_pic);
            names = (TextView) itemView.findViewById(R.id.feed_trainers_names);
            tags = (TextView) itemView.findViewById(R.id.feed_trainers_tags);
            eventsOpen = (TextView) itemView.findViewById(R.id.feed_trainers_events_open);
            eventsFinished = (TextView) itemView.findViewById(R.id.feed_trainers_events_finished);
            itemView.setOnClickListener(view -> {
                listener.onTrainerClicked(trainers.get(getAdapterPosition()));
            });
        }

        void bind(Entity trainer) {

            names.setText(trainer.firstName() + " " + trainer.lastName());
            GlideInstance.loadImageCircle(pic.getContext(), trainer.profileImageUrl(), pic, R.drawable.profile_placeholder);
            String tagsString = "";
            for(int i = 0; i < trainer.specialities().size(); i++){
                if(i != 0 && i != 3){
                    tagsString = tagsString.concat(", ");
                }
                tagsString = tagsString.concat(trainer.specialities().get(i));
                if(i == 3){
                    break;
                }
            }
            tags.setText(tagsString);
            Date date = new Date(System.currentTimeMillis());
            int open = 0;
            int finished = 0;
            for(Event event : trainer.hosting()){
                if(event.date().before(date)){
                    finished++;
                }
            }
            open = trainer.hosting().size() - finished;
            eventsOpen.setText(String.valueOf(open));
            eventsFinished.setText(String.valueOf(finished));
        }
    }

    public interface OnTrainerClickedListener {
        void onTrainerClicked(Entity trainer);
    }
}
