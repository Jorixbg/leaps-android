package club.leaps.presentation.test;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.utils.GlideInstance;

import java.util.List;

import club.leaps.presentation.utils.GlideInstance;

//import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ivan on 11/24/2017.
 */

public class ChatAdapter  extends RecyclerView.Adapter{

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<ChatMessages> messagesList;
    private String friendUrl;
   // private FirebaseAuth firebaseAuth;

    public ChatAdapter(List<ChatMessages> messagesList1,String friendUrl){
        this.messagesList = messagesList1;
      //  this.firebaseAuth = firebaseAuth;
        this.friendUrl = friendUrl;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;


        //return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ChatMessages message = messagesList.get(position);



        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }





      /*  if(!userImage.equalsIgnoreCase("default")) {
            Picasso.with(this).load(userImage).placeholder(R.drawable.profile_placeholder).into(profileImage);


            String current_user_id = String.valueOf(User.getInstance().getUserId());
        String userImageUrl = User.getInstance().getImageUrl();


        String from_user = message.getFrom();

        if(from_user.equals(User.getInstance().getUserId()+"")){

            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);
            GlideInstance.loadImageCircle(holder.profileImage.getContext(), userImageUrl, holder.profileImage, R.drawable.profile_placeholder);
        }
        else {

            holder.messageText.setBackgroundResource(R.color.primaryBlue);
            holder.messageText.setTextColor(Color.WHITE);
            holder.profileImage.setVisibility(View.GONE);

        }

        holder.messageText.setText(message.getMessage());


public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    private List<ChatMessages> messagesList;
   // private FirebaseAuth firebaseAuth;

    public ChatAdapter(List<ChatMessages> messagesList1){
        this.messagesList = messagesList1;
      //  this.firebaseAuth = firebaseAuth;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {

     //   String current_user_id = firebaseAuth.getCurrentUser().getUid();

        ChatMessages c = messagesList.get(position);

        String from_user = c.getFrom();



        if(from_user.equals("current_user_id")){

            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);
        }
        else {


            holder.messageText.setBackgroundResource(R.color.primaryBlue);
            holder.messageText.setTextColor(Color.WHITE);

        }

        holder.messageText.setText(c.getMessage());

      /*  if(!userImage.equalsIgnoreCase("default")) {
            Picasso.with(this).load(userImage).placeholder(R.drawable.profile_placeholder).into(profileImage);

        }*/

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessages message = (ChatMessages) messagesList.get(position);



        String current_user_id = String.valueOf(User.getInstance().getUserId());

        String from_user = message.getFrom();

        if(from_user.equals(User.getInstance().getUserId()+"")){

            return VIEW_TYPE_MESSAGE_SENT;
        }
        else {

            return VIEW_TYPE_MESSAGE_RECEIVED;

        }


    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public ImageView profileImage;

        public ChatViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text);
            profileImage = (ImageView) itemView.findViewById(R.id.single_message_image);
        }


    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        }

        void bind(ChatMessages message) {
            messageText.setText(message.getMessage());

            messageText.setBackgroundResource(R.drawable.message_send_bacground);
            messageText.setTextColor(Color.WHITE);

        // Format the stored timestamp into a readable String using method.
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(ChatMessages message) {
            messageText.setText(message.getMessage());

            String userImageUrl = User.getInstance().getImageUrl();


            messageText.setBackgroundResource(R.drawable.message_received_background);
            messageText.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.receivedMsgFont));


            GlideInstance.loadImageCircle(profileImage.getContext(), friendUrl, profileImage, R.drawable.profile_placeholder);
        }
    }

}
