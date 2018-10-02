package club.leaps.presentation.registration;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.IBaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.registration.registrationscreens.RegistrationBirthdayScreen;
import club.leaps.presentation.registration.registrationscreens.RegistrationEmailScreen;
import club.leaps.presentation.registration.registrationscreens.RegistrationNamesScreen;
import club.leaps.presentation.registration.registrationscreens.RegistrationPasswordScreen;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.welcome.ILoginContainer;
import club.leaps.presentation.welcome.InsideView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import club.leaps.presentation.registration.registrationscreens.RegistrationBirthdayScreen;
import club.leaps.presentation.registration.registrationscreens.RegistrationEmailScreen;
import club.leaps.presentation.registration.registrationscreens.RegistrationNamesScreen;
import club.leaps.presentation.registration.registrationscreens.RegistrationPasswordScreen;
import rx.Subscription;

/**
 * Created by xComputers on 04/06/2017.
 */
@Layout(layoutId = R.layout.registration_container_view)
public class RegistrationContainerView extends BaseView<RegistrationPresenter> implements IRegistrationContainer, InsideView {

    public static final String FIRST_NAME_KEY = "RegistrationContainerView|firstName";
    public static final String LAST_NAME_KEY = "RegistrationContainerView|lastName";
    public static final String EMAIL_ADDRESS_KEY = "RegistrationContainerView|email";
    public static final String BIRTHDAY_KEY = "RegistrationContainerView|birthDay";
    public static final String PASSWORD_KEY = "RegistrationContainerView|password";
    public static final String FB_KEY = "RegistrationContainerView|fbId";
    public static final String GOOGLE_KEY = "RegistrationContainerView|googleId";

    private static final int NAMES_VIEW = 0;
    private static final int EMAIL_VIEW = 1;
    private static final int PASSWORD_VIEW = 2;
    private static final int BIRTHDAY_VIEW = 3;

    private FragmentManager manager;
    private IRegistrationInsideView currentFragment;
    private List<Class<? extends BaseView>> insideViews;
    private int viewCounter = -1;
    private ILoginContainer container;

    private String firstName;
    private String lastName;
    private String email;
    private long birthDay;
    private String password;
    private String fbId;
    private String googleId;
    private String firebaseToken;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = getChildFragmentManager();
        manager.addOnBackStackChangedListener(() -> currentFragment = (IRegistrationInsideView) manager.findFragmentById(R.id.container));
        insideViews = new ArrayList<>();
        addInsideViews(insideViews);
        if(getArguments() != null){
            Log.e("Whaat",getArguments().getString(FB_KEY)+"");
            if(getArguments().containsKey(FB_KEY)){
                fbId = getArguments().getString(FB_KEY);
            }
            if(getArguments().containsKey(GOOGLE_KEY)){
                googleId = getArguments().getString(GOOGLE_KEY);
            }
        }
        openFrag(insideViews.get(NAMES_VIEW), getBundleForClass(NAMES_VIEW));
    }

    private void addInsideViews(List<Class<? extends BaseView>> list) {
        list.add(RegistrationNamesScreen.class);
        list.add(RegistrationEmailScreen.class);
        list.add(RegistrationPasswordScreen.class);
        list.add(RegistrationBirthdayScreen.class);
    }

    @Override
    public boolean onBack() {
        if (viewCounter == 0) {
            return false;
        } else {
            popBack();
            return true;
        }
    }

    private void popBack() {
        Fragment fragment = manager.findFragmentByTag(insideViews.get(viewCounter - 1).getCanonicalName());
        if(fragment.getArguments() != null){
            fragment.getArguments().putAll(getBundleForClass(viewCounter - 1));
        }
        ((IRegistrationInsideView)fragment).setContainer(this);
        manager.popBackStack();
        --viewCounter;
    }

    @Override
    protected RegistrationPresenter createPresenter() {
        return new RegistrationPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getRegistrationObservable().subscribe(this::onSuccess, this::onError));
        subscriptions.add(presenter.getErrorSubject().subscribe(s -> {
            hideLoading();
            if(!s.isEmpty()) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void onSuccess(String auth) {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(getString(R.string.auth_label), auth).apply();
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("UserId", String.valueOf(User.getInstance().getUserId())).apply();
        hideLoading();
        String deviceToken = EntityHolder.getInstance().getEntity().firebaseToken();
        String currentUserId = String.valueOf(User.getInstance().getUserId());

        HashMap<String, String> loginUser = new HashMap<>();
        Log.e("DeviceToken",deviceToken);
        loginUser.put("device_token",deviceToken);
        loginUser.put("id",currentUserId);
        loginUser.put("os","android");
        loginUser.put("name",User.getInstance().getFirstName()+" " + User.getInstance().getLastName());
        loginUser.put("image", User.getInstance().getImageUrl());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        userRef.setValue(loginUser).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private void onError(Throwable t) {
        hideLoading();
    }

    public <View extends IBaseView> void openFrag(Class<View> clazz, Bundle arguments) {

        String name = clazz.getCanonicalName();
        if (name == null) {
            return;
        }
        if (currentFragment != null && currentFragment.getClass().getCanonicalName().equals(name)) {
            return;
        }
        Fragment fragmentToOpen = Fragment.instantiate(getContext(), name, arguments);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.registration_container, fragmentToOpen, name)
                .addToBackStack(name).commit();
        getChildFragmentManager().executePendingTransactions();
        ((IRegistrationInsideView)fragmentToOpen).setContainer(this);
        currentFragment = (IRegistrationInsideView) fragmentToOpen;
        viewCounter++;
    }

    @Override
    public void gatherData(String... data) {
        switch (viewCounter) {
            case NAMES_VIEW:
                firstName = data[0];
                lastName = data[1];
                firebaseToken = data[2];
                openFrag(insideViews.get(EMAIL_VIEW), getBundleForClass(EMAIL_VIEW));
                break;
            case EMAIL_VIEW:
                email = data[0];
                openFrag(insideViews.get(PASSWORD_VIEW), getBundleForClass(PASSWORD_VIEW));
                break;
            case PASSWORD_VIEW:
                password = data[0];
                openFrag(insideViews.get(BIRTHDAY_VIEW), getBundleForClass(BIRTHDAY_VIEW));
                break;
            case BIRTHDAY_VIEW:
                birthDay = Long.valueOf(data[0]);
                presenter.register(email, password, firstName, lastName, birthDay, fbId, googleId,firebaseToken);
                break;
        }
    }

    private Bundle getBundleForClass(int viewPosition) {
        Bundle insideArgs = new Bundle();
        Bundle args = getArguments();
        switch (viewPosition) {

            case NAMES_VIEW:
                constructBundle(args, insideArgs, FIRST_NAME_KEY, this.firstName);
                constructBundle(args, insideArgs, LAST_NAME_KEY, this.lastName);
                break;
            case EMAIL_VIEW:
                constructBundle(args, insideArgs, EMAIL_ADDRESS_KEY, this.email);
                break;
            case PASSWORD_VIEW:
                constructBundle(args, insideArgs, PASSWORD_KEY, this.password);
                break;
            case BIRTHDAY_VIEW:
                return new Bundle();
        }
        return insideArgs;
    }

    private void constructBundle(Bundle args, Bundle insideArgs, String key, String globalValue){
        String value = "";
        if(args != null && args.containsKey(key)){
            value = args.getString(key);
        } else if(globalValue != null && !globalValue.isEmpty()){
            value = globalValue;
        }
        insideArgs.putString(key, value);
    }

    @Override
    public void setContainer(ILoginContainer container) {
        this.container = container;
    }
}
