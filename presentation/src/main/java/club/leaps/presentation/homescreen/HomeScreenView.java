package club.leaps.presentation.homescreen;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import club.leaps.presentation.homecalendar.HomeCalendarContainer;
import club.leaps.networking.profile.FirebaseToken;
import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.IBaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.follow.FollowingEventContainer;
import club.leaps.presentation.homecalendar.HomeCalendarContainer;
import club.leaps.presentation.homefeed.HomeFeedContainer;
import club.leaps.presentation.payments.PaymentActivity;
import club.leaps.presentation.payments.PaymentsEngine;
import club.leaps.presentation.profile.ProfileTabViewContainer;
import club.leaps.presentation.profile.trainerProfile.EditProfilePresenter;
import club.leaps.presentation.test.ChatMessages;
import club.leaps.presentation.test.InboxContainer;
import club.leaps.presentation.test.InboxUser;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.welcome.WelcomeActivity;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

import static android.app.Activity.RESULT_OK;
import static club.leaps.presentation.homefeed.HomeFeedContainer.FEED_SEARCH_ORIGIN_KEY;
import static club.leaps.presentation.homefeed.SearchView.SEARCH_RESULT_KEY;

/**
 * Created by xComputers on 01/06/2017.
 */
@Layout(layoutId = R.layout.homescreen_view)
public class HomeScreenView extends BaseView<EditProfilePresenter> {

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
    private int count;


    private List<InboxUser> inboxUserList;
    private List<ChatMessages> chatMessagesList;
    private List<String> chatRooms;

    public static String arguments;
    private Bundle userid = new Bundle();


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
        chatRooms = new ArrayList<>();


        if(!getArguments().containsKey("SearchView") && getArguments()!=null && !getArguments().isEmpty()) {
            userid = getArguments();
        }

        String firebase_token = FirebaseInstanceId.getInstance().getToken();

        if(EntityHolder.getInstance().getEntity()!=null) {
            if (!firebase_token.equalsIgnoreCase(EntityHolder.getInstance().getEntity().firebaseToken())) {
                FirebaseToken firebaseToken = new FirebaseToken(firebase_token);
                presenter.updateFirebaseToken(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""),
                        firebaseToken);
            }
        }

        tabLayout = (TabLayout) view.findViewById(R.id.homescreen_tabs);
        createTabs();
        tabLayout.addOnTabSelectedListener(getTabSelectedListener());
        tabIndex = 0;
        arguments = getArguments().getString("SearchView");
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
        if(PreferenceManager.getDefaultSharedPreferences(getContext()).contains("Authorization")
                && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", null) != null){
            LoadingInboxMessages();
        }
        if(userid!=null && !userid.isEmpty()){
            tabIndex = INBOX_POSITION;
        }
        tabLayout.getTabAt(tabIndex).select();

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
                text = getString(R.string.homescreen_tab_following);
                break;
            case CALENDAR_POSITION:
                text = getString(R.string.homescreen_tab_pay);
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
        if(userid!=null && !userid.isEmpty()){
            bundle = userid;
        }
        if (selected) {
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
                if(tab.getPosition() == CALENDAR_POSITION){
                    getActivity().startActivityForResult(new Intent(getContext(), PaymentActivity.class), CALENDAR_REQUEST, null);
                    return;
                }
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


//            @Override
//            public void onActivityResult(int requestCode, int resultCode, Intent data) {
//                if (requestCode == 0) {
//                    if (resultCode == Activity.RESULT_OK) {
//                        DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
//                        // use the result to update your UI and send the payment method nonce to your server
//                    } else if (resultCode == Activity.RESULT_CANCELED) {
//                        // the user canceled
//                    } else {
//                        // handle errors here, an exception may be available in
//                        Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
//                    }
//                }
//            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {updateTabSelection(tab, false);}

            @SuppressLint("RestrictedApi")
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition() == CALENDAR_POSITION){
                    getActivity().startActivityForResult(new Intent(getContext(), PaymentActivity.class), CALENDAR_REQUEST, null);
                    return;
                }
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

    public void onPayPress() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJmNjAyYTI5NWI1MjFlZTQ5ODg5MGNmYWRmNTdhZjQ4YWU4ZjlmZTJlOTBkYzhlZDM3YTNjZWViZDAyNWM5YmJifGNyZWF0ZWRfYXQ9MjAxOS0wMS0xNlQxMDozNDoyNi43NDU1ODkzNjUrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJncmFwaFFMIjp7InVybCI6Imh0dHBzOi8vcGF5bWVudHMuc2FuZGJveC5icmFpbnRyZWUtYXBpLmNvbS9ncmFwaHFsIiwiZGF0ZSI6IjIwMTgtMDUtMDgifSwiY2hhbGxlbmdlcyI6W10sImVudmlyb25tZW50Ijoic2FuZGJveCIsImNsaWVudEFwaVVybCI6Imh0dHBzOi8vYXBpLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb206NDQzL21lcmNoYW50cy8zNDhwazljZ2YzYmd5dzJiL2NsaWVudF9hcGkiLCJhc3NldHNVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbSIsImF1dGhVcmwiOiJodHRwczovL2F1dGgudmVubW8uc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbSIsImFuYWx5dGljcyI6eyJ1cmwiOiJodHRwczovL29yaWdpbi1hbmFseXRpY3Mtc2FuZC5zYW5kYm94LmJyYWludHJlZS1hcGkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0");
        startActivityForResult(dropInRequest.getIntent(getContext()), 0);
    }

    @Override
    protected EditProfilePresenter createPresenter() {
        return new EditProfilePresenter();
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
        onPayPress();
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
                    count=0;
                    chatRooms.add(dataSnapshot.getKey());
                    DatabaseReference user1 = dataSnapshot.getRef();

                    user1.addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            if(dataSnapshot.getKey().contains("user")) {
                                InboxUser inboxUser = dataSnapshot.getValue(InboxUser.class);
                                if(inboxUser!=null ) {
                                    if (!inboxUser.getId().equalsIgnoreCase(User.getInstance().getUserId() + "")) {
                                        inboxUserList.add(inboxUser);
                                    }
                                }
                            }
                            else if (dataSnapshot.getKey().contains("last_message")) {
                                ChatMessages lastMessage = dataSnapshot.getValue(ChatMessages.class);
                                lastMessage.setRoom(chatRooms.get(count));
                                count++;
                                chatMessagesList.add(lastMessage);
                            }

                            for (int i = 0; i <inboxUserList.size()-1 ; i++) {
                                if(inboxUserList.get(i).getId().equalsIgnoreCase(inboxUserList.get(i+1).getId())){
                                    inboxUserList.remove(i);
                                }
                            }
                            for (int i = 0; i <chatMessagesList.size()-1 ; i++) {
                                if(chatMessagesList.get(i).getRoom().equalsIgnoreCase(chatMessagesList.get(i+1).getRoom())){
                                    chatMessagesList.remove(i);
                                }
                            }
                            Gson gson = new Gson();
                            String json = gson.toJson(inboxUserList);
                            String json2 = gson.toJson(chatMessagesList);
                            if(getActivity()!=null) {
                               PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("InboxUsers", json).apply();
                               PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("ChatMessages", json2).apply();
                            }
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

    @Override
    public void onResume() {
        super.onResume();
        LoadingInboxMessages();
    }
}
