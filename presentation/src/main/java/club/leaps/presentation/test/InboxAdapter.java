package club.leaps.presentation.test;

import android.graphics.Typeface;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import club.leaps.presentation.utils.GlideInstance;

/**
 * Created by IvanGachmov on 11/30/2017.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxHolder>{

    private List<InboxUser> userList;
    private List<ChatMessages> lastMessagesList;
    private static List<InboxMessage> inboxMessageList;
    private OnInboxClickListener listener;
    private Bundle bundle;
    private long userId;
    private long currentUserId;

    public InboxAdapter(List<InboxUser> userList,List<ChatMessages> lastMessagesList,Bundle bundle,OnInboxClickListener listener) {
        this.bundle = bundle;
        this.userList = userList;
        this.lastMessagesList = lastMessagesList;
        this.listener = listener;
    }

    @Override
    public InboxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View row = li.inflate(R.layout.inbox_user, parent, false);
        InboxAdapter.InboxHolder vh = new InboxAdapter.InboxHolder(row);
        inboxMessageList = new ArrayList<>();
        LoadingUsers();

        return vh;
    }

    private void LoadingUsers() {

        if(userList.size() >= 1) {
            for (int i = 0; i <userList.size()-1 ; i++) {
                if(userList.get(i).getId().equalsIgnoreCase(userList.get(i+1).getId())){
                    userList.remove(i);
                }
            }
            for (int i = 0; i < userList.size(); i++) {
                InboxMessage inboxMessage = new InboxMessage(userList.get(i), lastMessagesList.get(i));
                inboxMessageList.add(inboxMessage);
            }

            for (int j = 0; j < inboxMessageList.size(); j++) {
                boolean hasASwap = false;
                for (int k = 0; k < inboxMessageList.size() - 1; k++) {
                    if (inboxMessageList.get(k).getChatMessages().getTime() < inboxMessageList.get(k + 1).getChatMessages().getTime()) {
                        InboxMessage temp = inboxMessageList.get(k);
                        inboxMessageList.set(k, inboxMessageList.get(k + 1));
                        inboxMessageList.set(k + 1, temp);
                        hasASwap = true;
                    }
                }
                if (!hasASwap) {
                    break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(InboxHolder holder, int position) {


       // InboxUser user = userList.get(position);
       // ChatMessages last_message = lastMessagesList.get(position);
        InboxUser user = inboxMessageList.get(position).getInboxUser();
        ChatMessages last_message = inboxMessageList.get(position).getChatMessages();
        holder.userName.setText(user.getName());
        GlideInstance.loadImageCircle(holder.userPic.getContext(), user.getImage(), holder.userPic, R.drawable.profile_placeholder);
        currentUserId = User.getInstance().getUserId();
        long userIdFromList = Long.parseLong(user.getId());

        if(user.getId().equalsIgnoreCase(last_message.getFrom())){
            if(currentUserId > userIdFromList) {
                if (last_message.isSeenUser2()) {
                    holder.lastMessage.setText(last_message.getMessage());
                } else {
                    holder.lastMessage.setText(last_message.getMessage());
                    holder.lastMessage.setTypeface(holder.lastMessage.getTypeface(), Typeface.BOLD);
                    holder.lastMessage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
                }
            }
            else {
                if (last_message.isSeenUser1()) {
                    holder.lastMessage.setText(last_message.getMessage());
                } else {
                    holder.lastMessage.setText(last_message.getMessage());
                    holder.lastMessage.setTypeface(holder.lastMessage.getTypeface(), Typeface.BOLD);
                    holder.lastMessage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
                }
            }
        }
        else {
            holder.lastMessage.setText("You: " + last_message.getMessage());
        }


        Calendar currentTime = Calendar.getInstance();
        Calendar messageTime = Calendar.getInstance();
        currentTime.setTimeInMillis(System.currentTimeMillis());
        messageTime.setTimeInMillis(last_message.getTime());

        boolean sameDay = currentTime.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                currentTime.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR);
        boolean thisWeek = currentTime.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                currentTime.get(Calendar.WEEK_OF_YEAR) == messageTime.get(Calendar.WEEK_OF_YEAR);

        Date date = new Date(last_message.getTime());
        if(sameDay){
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            String dateFormatted = formatter.format(date);
            holder.time.setText(dateFormatted);
        }
        else if(thisWeek){
            String dateFormatted =   messageTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            holder.time.setText(dateFormatted);
        }
        else {

            String dateFormatted = messageTime.get(Calendar.DAY_OF_MONTH)
                    + " "
                    + messageTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            holder.time.setText(dateFormatted);
        }

        openChat(user);



    }
    private void openChat(InboxUser user) {
        if (bundle != null && !bundle.isEmpty()) {
            currentUserId = User.getInstance().getUserId();
            userId = Long.parseLong(bundle.getString("userid"));
            boolean isSeen = Boolean.parseBoolean(bundle.getString("seenuser"));
            if(currentUserId>userId){
                if(!isSeen) {
                    if (bundle != null && !bundle.isEmpty()) {
                        if (user.getId().equalsIgnoreCase(bundle.getString("userid"))) {
                            listener.onUserClicked(user);
                            bundle.putString("seenuser","true");
                        }
                    }
                }
            }
            else {
                if(!isSeen) {
                    if (bundle != null && !bundle.isEmpty()) {
                        if (user.getId().equalsIgnoreCase(bundle.getString("userid"))) {
                            listener.onUserClicked(user);
                            bundle.putString("seenuser","true");
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class InboxHolder extends RecyclerView.ViewHolder{

        private ImageView userPic;
        private TextView userName;
        private TextView lastMessage;
        private TextView time;


        public InboxHolder(View itemView) {
            super(itemView);


            userPic = (ImageView) itemView.findViewById(R.id.inbox_user_pic);
            userName  = (TextView) itemView.findViewById(R.id.inbox_user_name);
            lastMessage = itemView.findViewById(R.id.inbox_text);
            time = itemView.findViewById(R.id.inbox_user_time);
            itemView.setOnClickListener(view -> {
                listener.onUserClicked(inboxMessageList.get(getAdapterPosition()).getInboxUser());
            });

        }
    }

    public interface OnInboxClickListener {
        void onUserClicked(InboxUser inboxUser);
    }

    public static void changeMessageList(ChatMessages chatMessage) {
        for (int i = 0; i < inboxMessageList.size() ; i++) {
            if(chatMessage.getRoom().equals(inboxMessageList.get(i).getChatMessages().getRoom())){
                inboxMessageList.get(i).setChatMessages(chatMessage);
            }
        }
    }
}
