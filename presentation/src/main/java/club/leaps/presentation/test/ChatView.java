package club.leaps.presentation.test;

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
import android.widget.Toast;

import club.leaps.presentation.follow.GetUserPresenter;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.networking.login.UserResponse;
import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.follow.GetUserPresenter;
import club.leaps.presentation.utils.EntityHolder;
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
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Ivan on 11/24/2017.
 */
@Layout(layoutId = R.layout.chat_view_layout)
public class ChatView  extends BaseView<GetUserPresenter> {


    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int currentPage = 1;

    private static final String ROOM = "room-";
    private static final String ROOMS = "rooms";
    private static final String MESSAGES = "messages";

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
    private DatabaseReference newUser;
    private DatabaseReference inChatRef;
    private DatabaseReference userInChatRef;

    private String userFullName;
    private String userImageUrl;
    private String userId;
    private String currentUserId;
    private String seenMessageRoom;
    private long user_id;
    private long currentId;
    private String current_user_ref ;
    private String chat_user_ref ;
    private boolean inChat;
    private boolean chatExist;
    private String messagesShared;
    private List<InboxUser> inboxUserList;
    private List<ChatMessages> chatMessagesList;
    private List<String> chatRooms;
    private int count = 0;
    private UserResponse userToChat;
    private String chatRoom;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Initializing the variables and the views
         */
        chatRooms = new ArrayList<>();

        userName = (TextView) view.findViewById(R.id.chat_user_name);
        chatRV = (RecyclerView) view.findViewById(R.id.chat_messages_list);
        messageET = (EditText) view.findViewById(R.id.chat_text);
        sendBtn = (TextView) view.findViewById(R.id.chat_send_btn);
        backBtn = (ImageView) view.findViewById(R.id.chat_back_btn);

        /**
         * Getting the messages from the SharedPreferences and converting them into a list.
         */

        messagesShared = PreferenceManager.getDefaultSharedPreferences(view.getContext()).getString("ChatMessages", "");
        Type type2 = new TypeToken<List<ChatMessages>>(){}.getType();
        Gson gson = new Gson();
        chatMessagesList = gson.fromJson(messagesShared, type2);

        /**
         * Getting the variables from the bundle.
         */
        userImageUrl = getArguments().getString("userImage");
        userId = getArguments().getString("userId");
        userFullName = getArguments().getString("userFullName");

        currentUserId = String.valueOf(User.getInstance().getUserId());
        currentId = User.getInstance().getUserId();
        user_id = Long.valueOf(userId);

        userName.setText(userFullName);

        /**
         * Comparing the two users so that we can now to which Firebase DatabaseReference we need to set or get information.
         */

        if(currentId > user_id){
            userRef = FirebaseDatabase.getInstance().getReference().child(ROOMS).child(ROOM+userId+"-"+currentUserId+"-");
            messageRef = FirebaseDatabase.getInstance().getReference().child(MESSAGES).child(ROOM+userId+"-"+currentUserId+"-");
            seenMessageRoom = "-"+userId+"-"+currentUserId+"-";

        }else {
            userRef = FirebaseDatabase.getInstance().getReference().child(ROOMS).child(ROOM+currentUserId+"-"+userId+"-");
            messageRef = FirebaseDatabase.getInstance().getReference().child(MESSAGES).child(ROOM+currentUserId+"-"+userId+"-");
            seenMessageRoom = "-"+currentUserId+"-"+userId+"-";
        }

        /**
         * This method is used to loop through the Firebase Database to find if the room exists.
         */
        searchForChatRoom();

        loadMessages();

        chatAdapter = new ChatAdapter(messages,userImageUrl);

        layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SnapHelper helper = new LinearSnapHelper();
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setStackFromEnd(true);
        helper.attachToRecyclerView(chatRV);
        chatRV.setAdapter(chatAdapter);
        chatRV.setLayoutManager(layoutManager);

        sendBtn.setOnClickListener(v->{
            //todo need to be fix the node.js function
            HashMap<String,String> notificationData = new HashMap<>();
            notificationData.put("from",currentUserId);
            notificationData.put("type","message");
            String userIdChat = getArguments().getString("userId");

            FirebaseDatabase.getInstance().getReference().child("notifications").child(userIdChat).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    }

    private void LoadingInboxMessages() {

        String  currentUserId = String.valueOf(User.getInstance().getUserId());
        inboxUserList = new ArrayList<>();
        chatMessagesList = new ArrayList<>();
        DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference().child(ROOMS);


        userInfoRef.orderByChild(String.valueOf(User.getInstance().getUserId())).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.getKey().contains("-"+currentUserId+"-")){
                    chatRooms.add(dataSnapshot.getKey());
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
                                lastMessage.setRoom(chatRooms.get(count));
                                count++;
                                chatMessagesList.add(lastMessage);
                            }

                            Gson gson = new Gson();
                            String json = gson.toJson(inboxUserList);
                            String json2 = gson.toJson(chatMessagesList);
                            if(getActivity()!=null) {
                                if (json != null && !json.isEmpty()) {
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove("InboxUsers").apply();
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("InboxUsers", json).apply();
                                }
                                if (json2 != null && !json2.isEmpty()) {
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove("ChatMessages").apply();
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("ChatMessages", json2).apply();
                                }
                            }

                         /*
                            Log.e("Message snapshot ", dataSnapshot.toString());
                            Log.e("Message child value ", dataSnapshot.getValue().toString());
                            Log.e("Message child key ", dataSnapshot.getKey().toString());
                            Log.e("Message child snapshot ", dataSnapshot.toString());*/
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                           if(dataSnapshot.getKey().contains("user")) {
                                InboxUser inboxUser = dataSnapshot.getValue(InboxUser.class);
                                if(!inboxUser.getId().equalsIgnoreCase(User.getInstance().getUserId()+"")){
                                    inboxUserList.add(inboxUser);
                                }

                            }
                            else if (dataSnapshot.getKey().contains("last_message")) {
                                ChatMessages lastMessage = dataSnapshot.getValue(ChatMessages.class);
                                //todo See why when user is chating the inbox is not updating
                               if(currentId > user_id){
                                   chatRoom=ROOM+currentId+"-"+user_id+"-";
                               }
                               else {
                                   chatRoom=ROOM+user_id+"-"+currentId+"-";
                               }
                               if(chatRoom!=null && !chatRoom.isEmpty()){
                                   lastMessage.setRoom(chatRoom);
                               }
                               for (int i = 0; i <chatMessagesList.size() ; i++) {
                                   if(chatMessagesList.get(i).getRoom().equalsIgnoreCase(lastMessage.getRoom())){
                                       chatMessagesList.remove(i);
                                       chatMessagesList.add(i,lastMessage);
                                   }
                               }
                               for (int i = 0; i <inboxUserList.size()-1 ; i++) {
                                   if(inboxUserList.get(i).getId().equalsIgnoreCase(inboxUserList.get(i+1).getId())){
                                       inboxUserList.remove(i);
                                   }

                               }
                            }

                            Gson gson = new Gson();
                            String json = gson.toJson(inboxUserList);
                            String json2 = gson.toJson(chatMessagesList);
                            if(getActivity()!=null) {
                                if (json != null && !json.isEmpty()) {
                                    Log.e("Size inbox3",inboxUserList.size()+"");
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove("InboxUsers").apply();
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("InboxUsers", json).apply();
                                }
                                if (json2 != null && !json2.isEmpty()) {
                                    Log.e("Size inbox4",chatMessagesList.size()+"");
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove("ChatMessages").apply();
                                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("ChatMessages", json2).apply();
                                }
                            }
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
    protected GetUserPresenter createPresenter() {
        return new GetUserPresenter() ;
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getUserSubject().subscribe(this::SuccessFollowing));
    }

    private void SuccessFollowing(UserResponse userResponse){
        userToChat = userResponse;

        if(currentId > user_id){
            current_user_ref = "user2";
            chat_user_ref = "user1";
        }
        else {
            current_user_ref = "user1";
            chat_user_ref = "user2";
        }

        String messageToSend = messageET.getText().toString();



        String deviceToken = EntityHolder.getInstance().getEntity().firebaseToken();

        HashMap<String, String> currentUserMap = new HashMap<>();
        currentUserMap.put("device_token",deviceToken);
        currentUserMap.put("id",currentUserId);
        currentUserMap.put("name",User.getInstance().getFirstName()+" " + User.getInstance().getLastName());
        currentUserMap.put("image", User.getInstance().getImageUrl());
        currentUserMap.put("inChat", "true");

        HashMap<String, String> chatUserMap = new HashMap<>();
        chatUserMap.put("device_token",userToChat.firebaseToken());
        chatUserMap.put("id",userId);
        chatUserMap.put("name",userFullName);
        chatUserMap.put("image",userImageUrl);
        chatUserMap.put("inChat", "false");

        Map messageMap = new HashMap();
        messageMap.put("message",messageToSend);

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
        messageUserMap.put("last_message",messageMap);
        messageUserMap.put(current_user_ref,currentUserMap);
        messageUserMap.put(chat_user_ref,chatUserMap);

        if(currentId > user_id){
            userRef = FirebaseDatabase.getInstance().getReference().child(ROOMS).child(ROOM+userId+"-"+currentUserId+"-");
            messageRef = FirebaseDatabase.getInstance().getReference().child(MESSAGES).child(ROOM+userId+"-"+currentUserId+"-");

        }else {
            userRef = FirebaseDatabase.getInstance().getReference().child(ROOMS).child(ROOM+currentUserId+"-"+userId+"-");
            messageRef = FirebaseDatabase.getInstance().getReference().child(MESSAGES).child(ROOM+currentUserId+"-"+userId+"-");
        }


        DatabaseReference userMessagePush = messageRef.push();

        String push_id = userMessagePush.getKey();

        Map messageMessageMap = new HashMap();
        messageMessageMap.put(push_id,messageMap);

        userRef.setValue(messageUserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    messageET.setText("");
                    // Toast.makeText(getApplicationContext(),"Successful.",Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(getApplicationContext(),"ERROR.",Toast.LENGTH_SHORT).show();
                }
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        messageRef.setValue(messageMessageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    messageET.setText("");
                    // Toast.makeText(getApplicationContext(),"Successful.",Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(getApplicationContext(),"ERROR.",Toast.LENGTH_SHORT).show();
                }
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
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

            if(!chatExist){
                createChat();
            }
            else {
                Map messageMap = new HashMap();
                messageMap.put("message",message);
                if(currentId > user_id) {
                    messageMap.put("seenUser2",true);
                    if(!inChat){
                        messageMap.put("seenUser1",false);
                    }
                    else {
                        messageMap.put("seenUser1",true);
                    }
                }
                else {
                    messageMap.put("seenUser1",true);
                    if(!inChat){
                        messageMap.put("seenUser2",false);
                    }
                    else {
                        messageMap.put("seenUser2",true);
                    }
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

            LoadingInboxMessages();

        }

    }

    private void loadMessages() {

        DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference().child(MESSAGES);

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


        DatabaseReference seenRef = FirebaseDatabase.getInstance().getReference().child(ROOMS);


        seenRef.orderByChild(String.valueOf(User.getInstance().getUserId())).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.getKey().contains(seenMessageRoom)){

                    DatabaseReference seenMessageRef = dataSnapshot.getRef();

                    seenMessageRef.addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            if(dataSnapshot.getKey().contains("last_message")) {
                                if(currentId > user_id) {
                                    seenMessageRef.child(dataSnapshot.getKey()).child("seenUser2").setValue(true);
                                }
                                else {
                                    seenMessageRef.child(dataSnapshot.getKey()).child("seenUser1").setValue(true);
                                }
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
                            Log.e("Data1",dataSnapshot.getKey()+"");
                            Log.e("Data",dataSnapshot.getValue()+"");
                            ChatMessages message = dataSnapshot.getValue(ChatMessages.class);
                            messages.add(message);
                            chatAdapter.notifyDataSetChanged();
                            if(chatRV.getAdapter().getItemCount()!=0) {
                                int position = chatRV.getAdapter().getItemCount() - 1;
                                chatRV.smoothScrollToPosition(position);
                            }

                         /* if(!message.getSender().equalsIgnoreCase(User.getInstance().getUserId()+"")){
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

    private void searchForChatRoom(){
        /**
         * A DatabaseReference is created for the specific child in the Firebase Database.
         */
        DatabaseReference userInfoRef = FirebaseDatabase.getInstance().getReference().child(ROOMS);
        /**
         * userInfoRef is ordered by the current user so that only rooms that referenced to
         * the current user to be listen for changes.Then to the DatabaseReference is called an
         * EventListener to listen for changes in the database.
         */
        userInfoRef.orderByChild(currentUserId).addChildEventListener(new ChildEventListener() {
            /**
             * OnChildAdded method is called when there are some new information added to database.
             */
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.getKey().contains("-"+currentUserId+"-")){
                    /**
                     * When the dataSnapshot.getKey() contains the current user a new DatabaseReference is made for
                     * the specific child to be listen to.                     *
                     */
                    DatabaseReference user1 = dataSnapshot.getRef();
                    user1.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.getKey().contains("user")) {
                                /**
                                 * If the dataSnapshot.getKey() from the user1 reference contains the current
                                 * user the boolean chatExist is set to true. This boolean is used so that to know if the chat
                                 * is needed to be create.
                                 */
                                InboxUser inboxUser = dataSnapshot.getValue(InboxUser.class);
                                if(inboxUser.getId().equalsIgnoreCase(userId)){
                                    chatExist=true;
                                }
                                if(chatExist) {
                                    /**
                                     * if it exists the current user and the chat user are compared so that we
                                     * know which user is "user1" and "user2".
                                     */
                                    if(currentId > user_id){
                                        /**
                                         * inChatRef for the current user is created so that
                                         * his value in the chat to be set to true in the database.
                                         */
                                        inChatRef = userRef.child("user2").child("inChat");
                                        inChatRef.setValue("true");
                                        /**
                                         * getIfUserIsInChat is used to get the inChat status for the second user
                                         */
                                        getIfUserIsInChat("user1");
                                    }
                                    else {
                                        inChatRef = userRef.child("user1").child("inChat");
                                        inChatRef.setValue("true");
                                        getIfUserIsInChat("user2");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot.getKey().contains("last_message")) {
                                ChatMessages lastMessage = dataSnapshot.getValue(ChatMessages.class);
                                if(currentId!=Long.parseLong(lastMessage.getSender())) {
                                    if (currentId > Long.parseLong(lastMessage.getSender())) {
                                        chatRoom = ROOM + lastMessage.getSender() + "-" + currentId + "-";
                                    } else {
                                        chatRoom = ROOM + currentId + "-" + lastMessage.getSender() + "-";
                                    }
                                    lastMessage.setRoom(chatRoom);
                                    for (int i = 0; i <chatMessagesList.size() ; i++) {
                                        if(lastMessage.getRoom().equalsIgnoreCase(chatMessagesList.get(i).getRoom())){
                                            chatMessagesList.remove(i);
                                            chatMessagesList.add(i,lastMessage);
                                        }

                                    }
                                }
                                Gson gson = new Gson();
                                String json2 = gson.toJson(chatMessagesList);
                                if(getActivity()!=null) {
                                    if (json2 != null && !json2.isEmpty()) {
                                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove("ChatMessages").apply();
                                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("ChatMessages", json2).apply();
                                    }
                                }
                            }
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

    private void createChat(){



        if(currentId > user_id){
            current_user_ref = "user2";
            chat_user_ref = "user1";
        }
        else {
            current_user_ref = "user1";
            chat_user_ref = "user2";
        }

        presenter.getUser(user_id,PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));


    }

    private void getIfUserIsInChat(String user){
        String userFromRoom = user;

        userInChatRef = userRef.child(userFromRoom).child("inChat");

        userInChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inChat=Boolean.parseBoolean(dataSnapshot.getValue()+"");
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
            inChat = false;
        }else {
            inChatRef = userRef.child("user1").child("inChat");
            inChatRef.setValue("false");
            inChat = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(currentId > user_id){
            inChatRef = userRef.child("user2").child("inChat");
            inChatRef.setValue("false");
            inChat = false;
        }else {
            inChatRef = userRef.child("user1").child("inChat");
            inChatRef.setValue("false");
            inChat = false;
        }
    }
}