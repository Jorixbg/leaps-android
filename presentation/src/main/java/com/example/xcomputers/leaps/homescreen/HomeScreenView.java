package com.example.xcomputers.leaps.homescreen;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.IBaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.follow.FollowingEventContainer;
import com.example.xcomputers.leaps.homecalendar.HomeCalendarContainer;
import com.example.xcomputers.leaps.homefeed.HomeFeedContainer;
import com.example.xcomputers.leaps.profile.ProfileTabViewContainer;
import com.example.xcomputers.leaps.test.ChatMessages;
import com.example.xcomputers.leaps.test.InboxContainer;
import com.example.xcomputers.leaps.test.InboxUser;
import com.example.xcomputers.leaps.welcome.WelcomeActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

import static android.app.Activity.RESULT_OK;
import static com.example.xcomputers.leaps.homefeed.HomeFeedContainer.FEED_SEARCH_ORIGIN_KEY;
import static com.example.xcomputers.leaps.homefeed.SearchView.SEARCH_RESULT_KEY;

/**
 * Created by xComputers on 01/06/2017.
 */
@Layout(layoutId = R.layout.homescreen_view)
public class HomeScreenView extends BaseView<EmptyPresenter> {

    public static final String OPEN_CALENDAR_KEY = "HomeScreenView.OPEN_CALENDAR_KEY";
    public static final String OPEN_FEED_KEY = "HomeScreenView.OPEN_FEED_KEY";
    public static final String OPEN_PROFILE_KEY = "HomeScreenView.OPEN_PROFILE_KEY";
    public static final String OPEN_EVENT_FOLLOWING_KEY="HomeScreenView.OPEN_EVENT_FOLLOWING_KEY";
    public static final String OPEN_INBOX_KEY="HomeScreenView.OPEN_INBOX_KEY";

    private static final int EVENT_FOLLOWING_REQUEST = 1;
    private static final int CALENDAR_REQUEST = 2;
    private static final int INBOX_REQUEST = 3;
    private static final int PROFILE_REQUEST = 4;


    private static final int FEED_POSITION = 0;
    private static final int EVENT_FOLLOWING_POSITION = 1;
    private static final int CALENDAR_POSITION = 2;
    private static final int INBOX_POSITION = 3;
    private static final int PROFILE_POSITION = 4;

    private static TabLayout tabLayout;
    private IBaseView currentFragment;
    private int tabIndex;


    private List<InboxUser> inboxUserList;
    private List<ChatMessages> chatMessagesList;

    public static String arguments;



    public static TabLayout getTabLayout() {
        return tabLayout;
    }

    public static String getString(){
        return arguments;
    }

    public static void setArguments(String arguments) {
        HomeScreenView.arguments = arguments;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) view.findViewById(R.id.homescreen_tabs);
        createTabs();
        tabLayout.addOnTabSelectedListener(getTabSelectedListener());
        tabIndex = 0;
        arguments = getArguments().getString("Hello");
        if(getArguments().containsKey(OPEN_FEED_KEY)){
            tabIndex = FEED_POSITION;
            getArguments().remove(OPEN_FEED_KEY);
        }else if(getArguments().containsKey(OPEN_EVENT_FOLLOWING_KEY)){
            tabIndex = EVENT_FOLLOWING_POSITION;
            getArguments().remove(OPEN_EVENT_FOLLOWING_KEY);
        }
        else if(getArguments().containsKey(OPEN_CALENDAR_KEY)){
            tabIndex = CALENDAR_POSITION;
            getArguments().remove(OPEN_CALENDAR_KEY);
        }
        else if(getArguments().containsKey(OPEN_INBOX_KEY)){
            tabIndex = INBOX_POSITION;
            getArguments().remove(OPEN_INBOX_KEY);
        }else if(getArguments().containsKey(OPEN_PROFILE_KEY)){
            tabIndex = PROFILE_POSITION;
            getArguments().remove(OPEN_PROFILE_KEY);
        }
        tabLayout.getTabAt(tabIndex).select();


        if(PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) != null){
                LoadingInboxMessages();
        }




    }

    private void createTabs() {
        for (int i = 0; i < 5; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setIcon(getIconForTab(i, i == 0));
            tab.setText(getTextForTab(i));
            tabLayout.addTab(tab);
            TabLayout.Tab created = tabLayout.getTabAt(i);
            if(created != null){
                created.setCustomView(R.layout.homescreen_tab);
            }
        }
    }

    private int getIconForTab(int position, boolean selected) {
        int drawable = -1;

        switch (position) {
            case FEED_POSITION:
                drawable = selected ? R.drawable.nav_home : R.drawable.nav_home_o;
                break;
            case EVENT_FOLLOWING_POSITION:
                drawable = selected ? R.drawable.tab_follow_event : R.drawable.tab_follow_event_empty;
                break;
            case CALENDAR_POSITION:
                drawable = selected ? R.drawable.nav_cal_s : R.drawable.nav_cal;
                break;
            case INBOX_POSITION:
                drawable = selected ? R.drawable.chat_open : R.drawable.chat_closed;
                break;
            case PROFILE_POSITION:
                drawable = selected ? R.drawable.nav_profile_s : R.drawable.nav_profile_o;
                break;
        }

        return drawable;
    }

    private String getTextForTab(int position) {

        String text = null;
        switch (position) {

            case FEED_POSITION:
                text = getString(R.string.homescreen_tab_home);
                break;
            case EVENT_FOLLOWING_POSITION:
                text = "FOLLOWING";
                break;
            case CALENDAR_POSITION:
                text = getString(R.string.homescreen_tab_cal);
                break;
            case INBOX_POSITION:
                text = getString(R.string.homescreen_tab_chat);
                break;
            case PROFILE_POSITION:
                text = getString(R.string.homescreen_tab_profile);
                break;
        }
        return text;
    }

    private void updateTabSelection(TabLayout.Tab tab, boolean selected){

        View customView = tab.getCustomView();
        if (customView == null) {
            return;
        }

        ImageView tabIcon = (ImageView) customView.findViewById(android.R.id.icon);
        tabIcon.setImageResource(getIconForTab(tab.getPosition(), selected));
        Bundle bundle = new Bundle();
        if(tab.getPosition() == FEED_POSITION) {
            if (getArguments().containsKey(SEARCH_RESULT_KEY)) {
                bundle.putSerializable(SEARCH_RESULT_KEY, getArguments().getSerializable(SEARCH_RESULT_KEY));
                bundle.putString(FEED_SEARCH_ORIGIN_KEY, getArguments().getString(FEED_SEARCH_ORIGIN_KEY));
                getArguments().remove(SEARCH_RESULT_KEY);
                getArguments().remove(FEED_SEARCH_ORIGIN_KEY);
                getArguments().remove(OPEN_FEED_KEY);
            }
        } else if(tab.getPosition() == EVENT_FOLLOWING_POSITION){
            if(getArguments().containsKey(OPEN_EVENT_FOLLOWING_KEY)){
                getArguments().remove(OPEN_EVENT_FOLLOWING_KEY);
            }

        }
        else if (tab.getPosition() == CALENDAR_POSITION){
            if(getArguments().containsKey(OPEN_CALENDAR_KEY)){
                getArguments().remove(OPEN_CALENDAR_KEY);
            }
        } else if (tab.getPosition() == INBOX_POSITION){
            if(getArguments().containsKey(OPEN_INBOX_KEY)){
                getArguments().remove(OPEN_INBOX_KEY);
            }
        }else if(tab.getPosition() == PROFILE_POSITION){
            if(getArguments().containsKey(OPEN_PROFILE_KEY)){
                getArguments().remove(OPEN_PROFILE_KEY);
            }
        }
        if(selected) {
            openTabView(tab.getPosition(), bundle);
        }
    }

    private void openTabView(int position, Bundle arguments){

        String name = getClassForPosition(position).getCanonicalName();
        if (name == null) {
            return;
        }
        if (currentFragment != null && currentFragment.getClass().getCanonicalName().equals(name)){
            ((Fragment)currentFragment).getArguments().putAll(arguments);
            return;
        }
      /*  if (getChildFragmentManager().findFragmentByTag(name) != null) {
            currentFragment = popBackStack(name, arguments);
        } else {
            Fragment fragmentToOpen = Fragment.instantiate(getContext(), name, arguments);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.homescreen_container, fragmentToOpen, name)
                    .addToBackStack(name).commitAllowingStateLoss();
            currentFragment = (IBaseView) fragmentToOpen;
        }*/


  /*  @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    public void reloadFragment(){
        String name = getClassForPosition(tabLayout.getSelectedTabPosition()).getCanonicalName();
        if (name == null) {
            return;
        }
        Fragment fragmentToOpen = getChildFragmentManager().findFragmentByTag(name);
        getChildFragmentManager().beginTransaction()
                .remove(fragmentToOpen).commitAllowingStateLoss();*/


        Fragment fragmentToOpen = Fragment.instantiate(getContext(), name, arguments);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.homescreen_container, fragmentToOpen, name)
                .addToBackStack(name).commitAllowingStateLoss();
        currentFragment = (IBaseView) fragmentToOpen;
    }

   /* private IBaseView popBackStack(String fragmentViewName, Bundle args) {

        Fragment fragment = getChildFragmentManager().findFragmentByTag(fragmentViewName);
        if (fragment != null && fragment.getArguments() != null && args != null) {
            fragment.getArguments().putAll(args);
        }
        getChildFragmentManager().popBackStack(fragmentViewName, 0);

        return (IBaseView) fragment;
    }*/

    private Class getClassForPosition(int position){
        Class frag = null;
        switch (position){
            case FEED_POSITION:
                frag = HomeFeedContainer.class;
                break;
            case EVENT_FOLLOWING_POSITION:
                frag = FollowingEventContainer.class;
                break;
            case CALENDAR_POSITION:
                frag = HomeCalendarContainer.class;
                break;
            case INBOX_POSITION:
                frag = InboxContainer.class;
                break;
            case PROFILE_POSITION:
                frag = ProfileTabViewContainer.class;
                break;
        }
        return frag;
    }

    private TabLayout.OnTabSelectedListener getTabSelectedListener(){
        return new TabLayout.OnTabSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == CALENDAR_POSITION && (!PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null)) {
                    getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class), CALENDAR_REQUEST, null);
                }else if(tab.getPosition() == EVENT_FOLLOWING_POSITION && (!PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null)){
                    getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class), EVENT_FOLLOWING_REQUEST, null);
                }else if(tab.getPosition() == INBOX_POSITION && (!PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null)){
                    getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class), INBOX_REQUEST, null);
                }else if(tab.getPosition() == PROFILE_POSITION && (!PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null)) {
                    getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class), PROFILE_REQUEST, null);
                }
                else {
                    updateTabSelection(tab, true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {updateTabSelection(tab, false);}

            @SuppressLint("RestrictedApi")
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition() == CALENDAR_POSITION && (!PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null)) {
                    startActivityForResult(new Intent(getContext(), WelcomeActivity.class), CALENDAR_REQUEST, null);
                }else if(tab.getPosition() == EVENT_FOLLOWING_POSITION && (!PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null)){
                    getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class), EVENT_FOLLOWING_REQUEST, null);
                }else if(tab.getPosition() == INBOX_POSITION && (!PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null)){
                    getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class), INBOX_REQUEST, null);
                }else if(tab.getPosition() == PROFILE_POSITION && (!PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) == null)){
                    getActivity().startActivityForResult(new Intent(getContext(), WelcomeActivity.class), PROFILE_REQUEST, null);
                }else {
                    updateTabSelection(tab, true);
                }
            }
        };
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case EVENT_FOLLOWING_REQUEST:
                    tabLayout.getTabAt(EVENT_FOLLOWING_POSITION).select();
                    break;
                case CALENDAR_REQUEST:
                    tabLayout.getTabAt(CALENDAR_POSITION).select();
                    break;
                case INBOX_REQUEST:
                    tabLayout.getTabAt(INBOX_POSITION).select();
                    break;
                case PROFILE_REQUEST:
                    tabLayout.getTabAt(PROFILE_POSITION).select();
                    break;
            }
        }
        else {
            tabLayout.getTabAt(FEED_POSITION).select();
        }
        ((Fragment)currentFragment).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onBack() {
        return currentFragment.onBack();
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
    
}
