package club.leaps.presentation.test;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;
import club.leaps.presentation.base.Layout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by IvanGachmov on 11/30/2017.
 */

@Layout(layoutId = R.layout.inbox_container_view)
public class InboxContainer extends BaseView<EmptyPresenter> implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView inboxRecyclerView;
    private InboxAdapter inboxAdapter;
    private LinearLayoutManager layoutManager;
    private List<InboxUser> users = new ArrayList<>();
    private List<ChatMessages> chatMessagesList;
    private List<InboxUser> usersList;
    private List<ChatMessages> cleardChatMessagesList;
    private List<InboxUser> cleardUsersList;
    private String currentUserId;
    private DatabaseReference userInfoRef;

    private Type type;
    private Type type2;
    private Gson gson;
    private String usersString;
    private String messages;
    private boolean deleted;
    private boolean defaultList = true;
    private boolean searching;
    private Bundle userId;
    private String chatRoom;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);
       cleardChatMessagesList = new ArrayList<>();
       cleardUsersList = new ArrayList<>();
       currentUserId = String.valueOf(User.getInstance().getUserId());
       type = new TypeToken<List<InboxUser>>(){}.getType();
       type2 = new TypeToken<List<ChatMessages>>(){}.getType();
       gson = new Gson();

       uploadListsFromSharedPref();

       loadMessages();
       userId = getArguments();

       layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
       inboxRecyclerView = (RecyclerView) view.findViewById(R.id.inbox_recycler_view);

       inboxAdapter = new InboxAdapter(cleardUsersList,cleardChatMessagesList,userId, user -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("userId", user.getId());
                bundle.putSerializable("userImage", user.getImage());
                bundle.putSerializable("userFullName", user.getName());
                openFragment(ChatView.class, bundle, true);
       });


       inboxRecyclerView.setAdapter(inboxAdapter);
       inboxRecyclerView.setLayoutManager(layoutManager);
       userInfoRef.keepSynced(true);

        /* @Override
    public void onStart() {
        super.onStart();
        if(userId!=null && !userId.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("userId", userId.getString("userid"));
            bundle.putSerializable("userImage", userId.getString("userimage"));
            bundle.putSerializable("userFullName", userId.getString("user_name"));
            openFragment(ChatView.class, bundle, true);
        }
    }
        }*/
    }

    @Override
    public void onRefresh() {

    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }

    private void loadMessages() {
        showLoading();
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

        //Query messageQuery = userInfoRef.limitToLast(currentPage*TOTAL_ITEMS_TO_LOAD);

        Query queryRef = userInfoRef.(User.getInstance().getUserId()+"");

        //DatabaseReference messageRef = rootRef.child("messages");



        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                User user = dataSnapshot.getValue(User.class);

                Log.e("Message query ", dataSnapshot.getValue().toString());

                users.add(user);
                inboxAdapter.notifyDataSetChanged();
                //inboxRecyclerView.scrollToPosition(messages.size()-1);

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


        userInfoRef = FirebaseDatabase.getInstance().getReference().child("rooms");
        userInfoRef.orderByChild(currentUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.getKey().contains("-"+currentUserId+"-")){
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
                chatRoom = dataSnapshot.getKey();
                dataSnapshot.getRef().addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
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
                        deleted = false;
                        if (dataSnapshot.getKey().contains("last_message")) {
                            for (int i = 0; i <usersList.size()-1 ; i++) {
                                if(usersList.get(i).getId().equalsIgnoreCase(usersList.get(i+1).getId())){
                                    usersList.remove(i);
                                    chatMessagesList.remove(i);
                                    deleted=true;
                                    defaultList=false;
                                }
                            }

                            if(!deleted) {
                                for (int i = 0; i < usersList.size() / 2; i++) {
                                    for (int j = usersList.size() / 2; j < usersList.size(); j++) {
                                        if (usersList.get(i).getId().equalsIgnoreCase(usersList.get(j).getId())) {
                                            usersList.remove(i);
                                            chatMessagesList.remove(i);
                                            defaultList = false;
                                            searching = true;
                                        }
                                        if(!searching){
                                            break;
                                        }
                                    }
                                    if(!searching){
                                        break;
                                    }
                                }
                            }


                            ChatMessages lastMessage = dataSnapshot.getValue(ChatMessages.class);
                            long userIdInChat = Long.parseLong(lastMessage.getSender());
                            long currentUserId = User.getInstance().getUserId();
                            if(userIdInChat!=currentUserId) {
                                if (userIdInChat < currentUserId) {
                                    chatRoom = "room-" + String.valueOf(userIdInChat) + "-" + String.valueOf(currentUserId) + "-";
                                } else {
                                    chatRoom = "room-" + String.valueOf(currentUserId) + "-" + String.valueOf(userIdInChat) + "-";
                                }
                            }

                            if(chatRoom!=null && !chatRoom.isEmpty()){
                                lastMessage.setRoom(chatRoom);
                                for (int i = 0; i < chatMessagesList.size(); i++) {
                                    if(lastMessage.getRoom().equalsIgnoreCase(chatMessagesList.get(i).getRoom())) {
                                        inboxAdapter.changeMessageList(lastMessage);
                                        chatMessagesList.remove(i);
                                        chatMessagesList.add(i,lastMessage);
                                        inboxRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            }

                            String json = gson.toJson(usersList);
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
        hideLoading();

    }


    @Override
    public void onResume() {
        super.onResume();
        defaultList=true;
        deleted = false;
          /* inboxAdapter = new InboxAdapter(cleardUsersList,cleardChatMessagesList,userId, user -> {
               Bundle bundle = new Bundle();
               bundle.putSerializable("userId", user.getId());
               bundle.putSerializable("userImage", user.getImage());
               bundle.putSerializable("userFullName", user.getName());
               openFragment(ChatView.class, bundle, true);
           });
           inboxRecyclerView.setAdapter(inboxAdapter);
           inboxRecyclerView.setLayoutManager(layoutManager);*/
    }

    private void uploadListsFromSharedPref(){
        usersString = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("InboxUsers", "");
        messages = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("ChatMessages", "");
        usersList = gson.fromJson(usersString, type);
        chatMessagesList = gson.fromJson(messages, type2);
        if(usersList!=null){
            for (int i = 0; i <usersList.size()-1 ; i++) {
                if(usersList.get(i).getId().equalsIgnoreCase(usersList.get(i+1).getId())){
                    cleardUsersList.add(usersList.get(i));
                    cleardChatMessagesList.add(chatMessagesList.get(i));
                    deleted=true;
                    defaultList=false;
                }
                if(!deleted){
                    break;
                }
            }

            if(!deleted) {
                for (int i = 0; i < usersList.size() / 2; i++) {
                    for (int j = usersList.size() / 2; j < usersList.size(); j++) {
                        if (usersList.get(i).getId().equalsIgnoreCase(usersList.get(j).getId())) {
                            cleardUsersList.add(usersList.get(i));
                            cleardChatMessagesList.add(chatMessagesList.get(i));
                            defaultList = false;
                            searching = true;
                        }
                        if(!searching){
                            break;
                        }
                    }
                    if(!searching){
                        break;
                    }
                }
            }

            if(defaultList){
                cleardUsersList = usersList;
                cleardChatMessagesList = chatMessagesList;
            }
        }

    }
}
