package com.example.xcomputers.leaps.test;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Ivan on 11/24/2017.
 */
@Layout(layoutId = R.layout.chat_view_layout)
public class ChatView  extends BaseView<EmptyPresenter> {


    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int currentPage = 1;

    private TextView userName;
    private RecyclerView chatRV;
    private EditText messageET;
    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private TextView sendBtn;
    private ImageView backBtn;
    private ChatMessages message;

    private List<ChatMessages> messages = new ArrayList<>();

    private DatabaseReference userRef;
    private DatabaseReference messageRef;
    private DatabaseReference notificationRef;
    private DatabaseReference newUser;
    private DatabaseReference inChatRef;

    private String userFullName;
    private String userImageUrl;
    private String userId;
    private String currentUserId;
    private long user_id;
    private long currentId;
    private String current_user_ref ;
    private String chat_user_ref ;
    private boolean inChatBoolean;


    private List<InboxUser> inboxUserList;
    private List<ChatMessages> chatMessagesList;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        userName = (TextView) view.findViewById(R.id.chat_user_name);
        chatRV = (RecyclerView) view.findViewById(R.id.chat_messages_list);
        messageET = (EditText) view.findViewById(R.id.chat_text);
        sendBtn = (TextView) view.findViewById(R.id.chat_send_btn);
        backBtn = (ImageView) view.findViewById(R.id.chat_back_btn);

        //fix
        userImageUrl = getArguments().getString("userImage");
        userId = getArguments().getString("userId");
        userFullName = getArguments().getString("userFullName");

        currentUserId = String.valueOf(User.getInstance().getUserId());
        currentId = User.getInstance().getUserId();
        user_id = Long.valueOf(userId);

        userName.setText(userFullName);

        notificationRef = FirebaseDatabase.getInstance().getReference().child("notifications");

        if(currentId > user_id){

            userRef = FirebaseDatabase.getInstance().getReference().child("rooms").child("room-"+userId+"-"+currentUserId+"-");
            messageRef = FirebaseDatabase.getInstance().getReference().child("messages").child("room-"+userId+"-"+currentUserId+"-");

        }else {
            userRef = FirebaseDatabase.getInstance().getReference().child("rooms").child("room-"+currentUserId+"-"+userId+"-");
            messageRef = FirebaseDatabase.getInstance().getReference().child("messages").child("room-"+currentUserId+"-"+userId+"-");

        }





        /*Log.e("String image ", userImageUrl);
        Log.e("String name ", userFullName);
        Log.e("String id ", userId);*/

        setUser();

        if(currentId > user_id){

            inChatRef = userRef.child("user2").child("inChat");
            inChatRef.setValue("true");
            inChatBoolean = true;
        }
        else {

            inChatRef = userRef.child("user1").child("inChat");
            inChatRef.setValue("true");
            inChatBoolean = true;
        }

        loadMessages();
        LoadingInboxMessages();

        chatAdapter = new ChatAdapter(messages,userImageUrl);

        layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SnapHelper helper = new LinearSnapHelper();
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setStackFromEnd(true);
        helper.attachToRecyclerView(chatRV);
        chatRV.setAdapter(chatAdapter);
        chatRV.setLayoutManager(layoutManager);


        sendBtn.setOnClickListener(v->{

            HashMap<String,String> notificationData = new HashMap<>();
            notificationData.put("from",currentUserId);
            notificationData.put("type","message");

            notificationRef.child(user_id+"").push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    sendMessage();

                }
            });
        });

        backBtn.setOnClickListener(v->{
            getActivity().onBackPressed();
        });




        userRef.keepSynced(true);
        messageRef.keepSynced(true);
        inChatRef.keepSynced(true);


    }

    private void LoadingInboxMessages() {

        String  currentUserId = String.valueOf(User.getInstance().getUserId());
        inboxUserList = new ArrayList<>();
        chatMessagesList = new ArrayList<>();
        DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference().child("rooms");


        userInfoRef.orderByChild(String.valueOf(User.getInstance().getUserId())).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.getKey().contains("-"+currentUserId+"-")){

                    DatabaseReference user1 = dataSnapshot.getRef();

                    user1.addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            if(dataSnapshot.getKey().contains("user")) {
                                InboxUser inboxUser = dataSnapshot.getValue(InboxUser.class);
                                if(!inboxUser.getId().equalsIgnoreCase(User.getInstance().getUserId()+"")){
                                    inboxUserList.add(inboxUser);
                                }
                            }
                            else if (dataSnapshot.getKey().contains("last_message")) {
                                ChatMessages lastMessage = dataSnapshot.getValue(ChatMessages.class);
                                chatMessagesList.add(lastMessage);
                            }



                            Gson gson = new Gson();
                            String json = gson.toJson(inboxUserList);
                            String json2 = gson.toJson(chatMessagesList);

                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("InboxUsers", json).apply();
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("ChatMessages", json2).apply();
                         /*
                            Log.e("Message value ", dataSnapshot.getValue().toString());
                            Log.e("Message key ", dataSnapshot.getKey().toString());
                            Log.e("Message snapshot ", dataSnapshot.toString());
                            Log.e("Message child value ", dataSnapshot.getValue().toString());
                            Log.e("Message child key ", dataSnapshot.getKey().toString());
                            Log.e("Message child snapshot ", dataSnapshot.toString());*/
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                   /* Map<String, String> itemPrice = dataSnapshot.getValue(Map.class);

                    //users.add(user);
                    Map.Entry<String,String> entry = itemPrice.entrySet().iterator().next();
                    String key = entry.getKey();
                    Log.e("Message user ", itemPrice.toString());
                    Log.e("Message user ", key);;*/
                }

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });




    }
    private void sendMessage() {

        String message = messageET.getText().toString();

        if(!TextUtils.isEmpty(message)){

            //String current_user_ref = "messages/"+currentUserId+"/"+userId;
            String current_user_ref = "messages/" +currentUserId+userId;
            String chat_user_ref = "messages/"+userId+"/"+currentUserId;

            DatabaseReference userMessagePush = messageRef.push();

            String push_id = userMessagePush.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message",message);

            if(currentId > user_id) {
                messageMap.put("seenUser2",true);
                messageMap.put("seenUser1",false);
            }
            else {
                messageMap.put("seenUser2",false);
                messageMap.put("seenUser1",true);
            }
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("sender",currentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(push_id,messageMap);


            messageRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError !=null) {

                        Log.e("CHAT ERROR", databaseError.getMessage().toString());

                    }
                }
            });

            Map lastMessage = new HashMap();
            lastMessage.put("last_message",messageMap);

            userRef.updateChildren(lastMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                       // Toast.makeText(getApplicationContext(),"Successful saved last message.",Toast.LENGTH_SHORT).show();
                    }
                    else {
                       // Toast.makeText(getApplicationContext(),"ERROR.",Toast.LENGTH_SHORT).show();
                    }
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

                }
            });

            messageET.setText("");

        }

    }


    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter() ;
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }

    private void setUser() {

        if(currentId > user_id){
             current_user_ref = "user2";
             chat_user_ref = "user1";
        }
        else {
             current_user_ref = "user2";
             chat_user_ref = "user1";
        }

        String last_message = "last_message";

        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getKey().contains("last_message")){
                    message = dataSnapshot.getValue(ChatMessages.class);
                    if(currentId > user_id) {
                        message.setSeenUser2(true);
                    }
                    else {
                        message.setSeenUser1(true);
                    }
                    userRef.child(last_message).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String deviceToken = FirebaseInstanceId.getInstance().getToken();


        HashMap<String, String> currentUserMap = new HashMap<>();
        currentUserMap.put("device_token",deviceToken);
        currentUserMap.put("id",currentUserId);
        currentUserMap.put("name",User.getInstance().getFirstName()+" " + User.getInstance().getLastName());
        currentUserMap.put("image", User.getInstance().getImageUrl());
        currentUserMap.put("inChat", "false");

        HashMap<String, String> chatUserMap = new HashMap<>();
        chatUserMap.put("id",userId);
        chatUserMap.put("name",userFullName);
        chatUserMap.put("image",userImageUrl);
        chatUserMap.put("inChat", "false");

        Map messageUserMap = new HashMap();
        messageUserMap.put(current_user_ref,currentUserMap);
        messageUserMap.put(chat_user_ref,chatUserMap);



        userRef.setValue(messageUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  //  Toast.makeText(getApplicationContext(),"Successful.",Toast.LENGTH_SHORT).show();
                }
                else {
                   // Toast.makeText(getApplicationContext(),"ERROR.",Toast.LENGTH_SHORT).show();
                }
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        newUser = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        String device = FirebaseInstanceId.getInstance().getToken();


        HashMap<String, String> tokenMap= new HashMap<>();
        tokenMap.put("device_token",device);
        tokenMap.put("id",currentUserId);
        tokenMap.put("name",User.getInstance().getFirstName()+" " + User.getInstance().getLastName());


        newUser.setValue(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(getApplicationContext(),"Successful Token.",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadMessages() {

        DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference().child("messages");

        userInfoRef.keepSynced(true);

       /* make.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(currentUserId+userId)) {
                    messageRef[0] = rootRef.child("messages").child(currentUserId+userId);
                }
                else{
                    messageRef[0] = rootRef.child("messages").child(userId+currentUserId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query messageQuery = make.limitToLast(currentPage*TOTAL_ITEMS_TO_LOAD);

        Query queryRef = make.orderByChild("sender").equalTo(User.getInstance().getUserId()+"");

        DatabaseReference messageRef = rootRef.child("messages");



        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ChatMessages message = dataSnapshot.getValue(ChatMessages.class);

                Log.e("Message query ", dataSnapshot.getValue().toString());

                messages.add(message);
                chatAdapter.notifyDataSetChanged();
                chatRV.scrollToPosition(messages.size()-1);

                //swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        userInfoRef.orderByChild(currentUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if((dataSnapshot.getKey().contains("-"+currentUserId+"-"+userId+"-" ))||(dataSnapshot.getKey().contains("-"+userId+"-"+currentUserId+"-" ))){

                    DatabaseReference user1 = dataSnapshot.getRef();



                    user1.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(currentId > user_id) {
                                user1.child(dataSnapshot.getKey()).child("seenUser2").setValue(true);
                            }
                            else {

                                user1.child(dataSnapshot.getKey()).child("seenUser1").setValue(true);
                            }
                            ChatMessages message = dataSnapshot.getValue(ChatMessages.class);
                            messages.add(message);
                            chatAdapter.notifyDataSetChanged();
                            if(chatRV.getAdapter().getItemCount()!=0) {
                                int position = chatRV.getAdapter().getItemCount() - 1;
                                chatRV.smoothScrollToPosition(position);
                            }




                         /*  if(!message.getSender().equalsIgnoreCase(User.getInstance().getUserId()+"")){
                                messages.add(message);
                                chatAdapter.notifyDataSetChanged();
                            }


                            Log.e("Message child snapshot ", user1+"");
                            Log.e("Message child snapsho" ,  user1.child(dataSnapshot.getKey()).child("seen")+"");
                            Log.e("Message child snapshot ", dataSnapshot.getKey()+"");

                            Log.e("Message snapshot ", dataSnapshot.toString());

                            Log.e("Message value ", dataSnapshot.getValue().toString());
                            Log.e("Message key ", dataSnapshot.getKey().toString());

                            Log.e("Message child value ", dataSnapshot.getValue().toString());
                            Log.e("Message child key ", dataSnapshot.getKey().toString());
                            Log.e("Message child snapshot ", dataSnapshot.toString());*/
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                   /* Map<String, String> itemPrice = dataSnapshot.getValue(Map.class);

                    //users.add(user);
                    Map.Entry<String,String> entry = itemPrice.entrySet().iterator().next();
                    String key = entry.getKey();
                    Log.e("Message user ", itemPrice.toString());
                    Log.e("Message user ", key);;*/
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if(currentId > user_id){
            inChatRef = userRef.child("user2").child("inChat");
            inChatRef.setValue("false");
            inChatBoolean = false;
        }else {
            inChatRef = userRef.child("user1").child("inChat");
            inChatRef.setValue("false");
            inChatBoolean = false;
        }

    }
}
