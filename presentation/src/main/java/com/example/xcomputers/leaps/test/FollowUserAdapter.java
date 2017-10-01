package com.example.xcomputers.leaps.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.networking.test.FollowedUser;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.utils.GlideInstance;

import java.util.List;

/**
 * Created by Ivan on 9/29/2017.
 */

public class FollowUserAdapter  extends RecyclerView.Adapter<FollowUserAdapter.ViewHolder> {

    private List<FollowedUser> followers;
    private Context context;

    public FollowUserAdapter(Context context, List<FollowedUser> followers) {
        this.followers = followers;
        this.context = context;


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View row = li.inflate(R.layout.follow_user_item, parent, false);
        ViewHolder vh = new ViewHolder(row);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Todo onBindView
        FollowedUser follow = followers.get(position);
        holder.userName.setText(follow.getUsername());
        GlideInstance.loadImageCircle(context, follow.getProfileImage(), holder.userImage, R.drawable.profile_placeholder);

    }


    @Override
    public int getItemCount() {
        return followers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.follow_user_imageView);
            userName = (TextView) itemView.findViewById(R.id.follow_user_textView);

        }
    }
}
