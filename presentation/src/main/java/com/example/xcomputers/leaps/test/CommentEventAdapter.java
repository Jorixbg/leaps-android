package com.example.xcomputers.leaps.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.networking.feed.trainer.Entity;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.GlideInstance;

import java.util.List;

/**
 * Created by Ivan on 9/21/2017.
 */

public class CommentEventAdapter extends RecyclerView.Adapter<CommentEventAdapter.ViewHolder> {

    private List<EventComment> comments;
    private Context context;
    private Entity user ;

    public CommentEventAdapter(Context context, List<EventComment> comments){
        this.comments = comments;
        this.context = context;

         user = EntityHolder.getInstance().getEntity();

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View row = li.inflate(R.layout.comment_view_item, parent,false);
        ViewHolder vh = new ViewHolder(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Todo onBindView
        EventComment comment = comments.get(position);
        holder.userName.setText(comment.getUserName());
        holder.userComment.setText(comment.getUserComment());
        holder.date.setText(comment.getDate());
        holder.ratingBar.setRating(comment.getRating());
        GlideInstance.loadImageCircle(context, user.profileImageUrl(), holder.userImage, R.drawable.profile_placeholder);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        ImageView commentImage;
        TextView userName;
        TextView userComment;
        TextView date;
        RatingBar ratingBar;


        public ViewHolder(View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.comment_user_image);
            commentImage = (ImageView) itemView.findViewById(R.id.comment_image);
            userName = (TextView) itemView.findViewById(R.id.comment_user_name);
            userComment = (TextView) itemView.findViewById(R.id.user_comment);
            date = (TextView) itemView.findViewById(R.id.comment_date);
            ratingBar = (RatingBar) itemView.findViewById(R.id.comment_ratingbar);


        }
    }
}
