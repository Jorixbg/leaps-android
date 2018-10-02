package club.leaps.presentation.homefeed.trainers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import club.leaps.networking.feed.event.Event;
import club.leaps.networking.feed.trainer.Entity;
import club.leaps.networking.login.UserResponse;
import club.leaps.presentation.R;
import club.leaps.presentation.utils.GlideInstance;

import java.util.Date;
import java.util.List;

/**
 * Created by xComputers on 11/07/2017.
 */

public class FeedTrainersFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private List<Entity> data;
    private boolean isLoading;
    private FeedTrainersAdapter.OnTrainerClickedListener listener;

    public FeedTrainersFilterAdapter(List<Entity> data, FeedTrainersAdapter.OnTrainerClickedListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setData(List<Entity> data){
        this.data = data;
        notifyDataSetChanged();
    }

    public void setLoadingState(boolean isLoading){
        this.isLoading = isLoading;
        if(isLoading){
            data.add(new UserResponse());
        }else{
            data.remove(data.size() - 1);
        }
        notifyDataSetChanged();
    }

    public boolean getLoadingState(){
        return isLoading;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == data.size() - 1 && isLoading){
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            return new TrainersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_trainers_recycler_item, parent, false));
        }else if(viewType == VIEW_TYPE_LOADING){
            return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_recycler_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TrainersViewHolder){
            ((TrainersViewHolder)holder).bind(data.get(position));

        }

    }

    @Override
    public int getItemCount() {
        return data.size();
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
                listener.onTrainerClicked(data.get(getAdapterPosition()));
            });
        }

        void bind(Entity trainer) {

            names.setText(trainer.firstName() + " " + trainer.lastName());
            GlideInstance.loadImageCircle(pic.getContext(), trainer.profileImageUrl(), pic, R.drawable.profile_placeholder);
            String tagsString = "";
            if(trainer.specialities() == null){
                return;
            }
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

    class LoadingViewHolder extends RecyclerView.ViewHolder{

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
