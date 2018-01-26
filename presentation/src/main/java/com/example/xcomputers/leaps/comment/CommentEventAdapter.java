package com.example.xcomputers.leaps.comment;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.networking.feed.trainer.Entity;
import com.example.networking.test.EventRatingResponse;
import com.example.xcomputers.leaps.LeapsApplication;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.GlideInstance;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ivan on 9/21/2017.
 */

public class CommentEventAdapter extends RecyclerView.Adapter<CommentEventAdapter.ViewHolder> {


    private static List<EventRatingResponse> comments;
    private Context context;
    private Entity user ;
    private EventRatingPresenter presenter;
    private int pageNumber = 1;
    private long eventId;

    public CommentEventAdapter(Context context,EventRatingPresenter presenter,long eventId, List<EventRatingResponse> comments){
        this.comments = comments;
        this.context = context;
        this.presenter = presenter;
        this.eventId = eventId;
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
        EventRatingResponse comment = comments.get(position);
        holder.userName.setText(comment.getOwnerName());
        holder.userComment.setText(comment.getComment());

        holder.ratingBar.setRating(comment.getRating());
        GlideInstance.loadImageCircle(context, comment.getOwnerImageUrl(), holder.userImage, R.drawable.profile_placeholder);
        Glide.with(holder.itemView.getContext())
                .load(LeapsApplication.BASE_URL + File.separator + comment.getCommentImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.event_placeholder)
                .into(holder.commentImage);


       if((++position)%20 == 0 && comments.size()%20 == 0){
            pageNumber++;
            presenter.getComment(PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext()).getString("Authorization", ""),
                    eventId, pageNumber, 20);
       }

        /*LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_ATOP);
        */
        Calendar commentTime = Calendar.getInstance();
        commentTime.setTime(comment.getDate());

        Date date = comment.getDate();
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateFormatted = formatter.format(date);

        holder.date.setText(
                        + commentTime.get(Calendar.DAY_OF_MONTH)
                        + " "
                        + commentTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                        + " "
                        + commentTime.get(Calendar.YEAR)
                        + ",  "
                        + dateFormatted);




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


    private String returnDate(long mil){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mil);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.format(Locale.getDefault(), "%d.%d.%d", mDay, mMonth + 1, mYear);
        return date;
    }


    public static void setComments(List<EventRatingResponse> comments) {
        CommentEventAdapter.comments.addAll(comments);

    }


}
