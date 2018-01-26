package com.example.xcomputers.leaps.test;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
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
    private String currentUserId;
    private DatabaseReference userInfoRef;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUserId = String.valueOf(User.getInstance().getUserId());
        Type type = new TypeToken<List<InboxUser>>(){}.getType();
        Type type2 = new TypeToken<List<ChatMessages>>(){}.getType();
        Gson gson = new Gson();
        String users = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("InboxUsers", "");
        String messages = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("ChatMessages", "");
        List<InboxUser> usersList = gson.fromJson(users, type);
        chatMessagesList = gson.fromJson(messages, type2);

       loadMessages();


       layoutManager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
       inboxRecyclerView = (RecyclerView) view.findViewById(R.id.inbox_recycler_view);

        inboxAdapter = new InboxAdapter(usersList,chatMessagesList, user -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("userId", user.getId());
                bundle.putSerializable("userImage", user.getImage());
                bundle.putSerializable("userFullName", user.getName());
                openFragment(ChatView.class, bundle, true);
        });

        inboxRecyclerView.setAdapter(inboxAdapter);
        inboxRecyclerView.setLayoutManager(layoutManager);

        userInfoRef.keepSynced(true);

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

                    DatabaseReference user1 = dataSnapshot.getRef();

                    user1.addChildEventListener(new ChildEventListener() {

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

                            if (dataSnapshot.getKey().contains("last_message")) {
                                ChatMessages lastMessage = dataSnapshot.getValue(ChatMessages.class);
                                for (int i = 0; i < chatMessagesList.size(); i++) {
                                    if(lastMessage.getFrom().equals(chatMessagesList.get(i).getFrom())){
                                        inboxAdapter.changeMessageList(lastMessage);
                                        inboxAdapter.notifyDataSetChanged();
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

        hideLoading();

    }
}
