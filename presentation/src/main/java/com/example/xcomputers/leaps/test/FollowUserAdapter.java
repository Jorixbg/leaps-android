package com.example.xcomputers.leaps.test;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networking.feed.event.AttendeeResponse;
import com.example.networking.following.FollowingService;
import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.utils.EntityHolder;
import com.example.xcomputers.leaps.utils.GlideInstance;
import com.example.xcomputers.leaps.utils.LoginResponseToUserTypeMapper;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Ivan on 9/29/2017.
 */

public class FollowUserAdapter  extends RecyclerView.Adapter<FollowUserAdapter.ViewHolder> {

    private List<AttendeeResponse> followers;
    private List<AttendeeResponse> friends;
    private Context context;
    private FollowingService followingService;
    private long userId;
    private OnFollowerClickedListener listener;

    public FollowUserAdapter(Context context, List<AttendeeResponse> followers,List<AttendeeResponse> friends,FollowingService followingService,OnFollowerClickedListener listener) {
        this.followers = followers;
        this.context = context;
        this.followingService = followingService;
        this.listener = listener;
        this.friends = friends;
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
        AttendeeResponse follow = followers.get(position);
        holder.userName.setText(follow.getUsername());
        GlideInstance.loadImageCircle(context, follow.getProfileImage(), holder.userImage, R.drawable.profile_placeholder);


       /* if(friends !=null) {
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
        }*/


            boolean clicked = false;
            for (int i = 0; i < User.getInstance().getFollowed().getFollowingUsers().size(); i++) {
                if (User.getInstance().getFollowed().getFollowingUsers().get(i).getUserId() == follow.getUserId()) {
                    holder.followBtn.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.following_button_clicked));
                    holder.followBtn.setText("FOLLOWED");
                    holder.followBtn.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
                    holder.setClicked(true);
                    clicked = true;
                    break;
                }
            }
            if(User.getInstance().getUserId() == follow.getUserId()){
            holder.followBtn.setVisibility(View.GONE);
        }
        if(!clicked){
            holder.followBtn.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.following_button_unclicked));
            holder.followBtn.setText("FOLLOW");
            holder.followBtn.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),  R.color.light_blue));
            holder.setClicked(false);
        }

        }




    @Override
    public int getItemCount() {
        return followers.size();
    }





    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImage;
        TextView userName;
        Button followBtn;
        private boolean isClicked;

        public ViewHolder(View itemView) {
            super(itemView);
            userImage = (ImageView) itemView.findViewById(R.id.follow_user_imageView);
            userName = (TextView) itemView.findViewById(R.id.follow_user_textView);
           followBtn = (Button) itemView.findViewById(R.id.follow_btn);
            followBtn.setOnClickListener(this);
            itemView.setOnClickListener(view -> {
                listener.OnFollowerClicked(followers.get(getAdapterPosition()));
            });

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == followBtn.getId()) {
                userId= followers.get(getAdapterPosition()).getUserId();
               follow();
               checkForFollowing();
            }

        }

        private void follow(){
            followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(itemView.getContext()).getString("Authorization", ""));
            followingService.FollowingUser(userId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userResponse -> {
                        getUser();
                    }, throwable -> {
                        followingService.removeHeader("Authorization");
                        unfollow();
                    });

        }

        private void unfollow(){
            followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(itemView.getContext()).getString("Authorization", ""));
            followingService.UnFollowingUser(userId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(userResponse -> {
                        getUser();
                    }, throwable -> {
                        followingService.removeHeader("Authorization");
                    });
        }

        private void getUser(){
            followingService.addHeader("Authorization", PreferenceManager.getDefaultSharedPreferences(itemView.getContext()).getString("Authorization", ""));
            followingService.getUser(User.getInstance().getUserId())
                    .subscribe(userResponse -> {
                        LoginResponseToUserTypeMapper.map(userResponse);
                        EntityHolder.getInstance().setEntity(userResponse);
                        followingService.removeHeader("Authorization");
                    },(throwable) -> {
                        followingService.removeHeader("Authorization");
                        Toast.makeText(itemView.getContext(),"Get user Something went wrong!",Toast.LENGTH_SHORT).show();
                    });


        }


       public boolean isClicked() {
            return isClicked;
        }

        public void setClicked(boolean clicked) {
            isClicked = clicked;
        }


        public void checkForFollowing(){
            if (!isClicked) {
                followBtn.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.following_button_clicked));
                followBtn.setText("FOLLOWED");
                followBtn.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
                isClicked=true;
            }
            else {
                followBtn.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.following_button_unclicked));
                followBtn.setText("FOLLOW");
                followBtn.setTextColor(ContextCompat.getColor(itemView.getContext(),  R.color.light_blue));
                isClicked=false;

            }
        }

    }

    public interface OnFollowerClickedListener {
        void OnFollowerClicked(AttendeeResponse AttendeeResponse);
    }

}
