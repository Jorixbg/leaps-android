package club.leaps.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import club.leaps.presentation.R;
import club.leaps.presentation.base.IActivity;
import club.leaps.presentation.base.IBaseView;
import club.leaps.presentation.homefeed.SearchView;
import club.leaps.presentation.homescreen.HomeScreenView;
import club.leaps.presentation.splash.SplashView;
import com.testfairy.TestFairy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity implements IActivity {

    private IBaseView currentFragment;
    private ProgressBar progressBar;
    private FrameLayout container;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isInFront;

    public static boolean isInApp() {
        return inApp;
    }

    private static boolean inApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TestFairy.begin(this, "7f4fa6d331f494d93cfb3b9e36749aced4263367");


        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "club.leaps.presentation",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", "Error");

        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", "Error2");

        }*/

        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        container = (FrameLayout) findViewById(R.id.container);
        final LayoutInflater factory = getLayoutInflater();
        final View textEntryView = factory.inflate(R.layout.walkthrough_container, null);
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        inApp = true;
        if(null != bundle && bundle.getString("userid") != null){
            openFragment(SplashView.class, bundle, false);
        }
        else {
            openFragment(SplashView.class, new Bundle(), false);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (currentFragment != null && data !=null) {
            ((Fragment) currentFragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public <View extends IBaseView> void openFragment(Class<View> clazz, Bundle arguments) {
        openFragment(clazz, arguments, true);
    }

    @Override
    public <View extends IBaseView> void openFragment(Class<View> clazz, Bundle arguments, boolean addToBackStack) {

        String name = clazz.getCanonicalName();
        if (name == null) {
            return;
        }
        if (currentFragment != null && currentFragment.getClass().getCanonicalName().equals(name)) {
            return;
        }
        if(currentFragment != null && currentFragment.getClass().getName().equals(SearchView.class.getName())){


        }
        if (getSupportFragmentManager().findFragmentByTag(name) != null) {
            currentFragment = popBackStack(getSupportFragmentManager(), name, arguments);
        } else {
            Fragment fragmentToOpen = Fragment.instantiate(this, name, arguments);

            if (addToBackStack) {
                if(fragmentToOpen.getClass().getName().equals(SearchView.class.getName())){
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom,
                            R.anim.slide_in_top,R.anim.slide_out_bottom)
                            .replace(R.id.container, fragmentToOpen, name)
                            .addToBackStack(name).commitAllowingStateLoss();
                }
                else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragmentToOpen, name)
                            .addToBackStack(name).commitAllowingStateLoss();
                }

            }

            else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragmentToOpen, name)
                        .disallowAddToBackStack().commitAllowingStateLoss();
            }
            getSupportFragmentManager().executePendingTransactions();
            currentFragment = (IBaseView) fragmentToOpen;

        }
    }

    @Override
    public void onBackPressed() {


        FragmentManager fm = getSupportFragmentManager();
        IBaseView frag = null;
        if(fm.getBackStackEntryCount() > 1) {
            FragmentManager.BackStackEntry backStackEntry2 = fm.getBackStackEntryAt(1);
            frag = popBackStack(fm, backStackEntry2.getName(), null);
        }

       if((currentFragment.getClass().getSimpleName().equals("TrainerProfileEditView") || currentFragment.getClass().getSimpleName().equals("UserEditProfileView"))
               || frag !=null && frag.getClass().getSimpleName().equals("FollowUserView") ){
           Bundle bundle = new Bundle();
           bundle.putSerializable("HomeScreenView.OPEN_PROFILE_KEY",null);
           openFragment(HomeScreenView.class,bundle,false);
       }

        if(currentFragment.getClass().getSimpleName().equals("ChatView")
                || frag !=null && frag.getClass().getSimpleName().equals("ChatView") ){
            Bundle bundle = new Bundle();
            bundle.putSerializable("HomeScreenView.OPEN_INBOX_KEY",null);
            openFragment(HomeScreenView.class,bundle,false);
        }


        if (currentFragment.onBack()) {
            return;
        }


        if (fm.getBackStackEntryCount() > 1) {
            int topEntryIndex = fm.getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(topEntryIndex - 1);
            for (int i = topEntryIndex; i >= 0; i--) {
                if (fm.getBackStackEntryAt(i) != null && backStackEntry.getName() != null) {
                    currentFragment = popBackStack(fm, backStackEntry.getName(), null);
                    break;
                }
            }
        }
        if(isInFront){

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                supportFinishAfterTransition();
                return;
            }

            this.doubleBackToExitPressedOnce = true;

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
        else {
            supportFinishAfterTransition();

        }
    }

    protected IBaseView popBackStack(FragmentManager fragmentManager, String fragmentViewName, Bundle args) {

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentViewName);
        if (fragment != null && fragment.getArguments() != null && args != null) {
            fragment.getArguments().putAll(args);
        }
        fragmentManager.popBackStack(fragmentViewName, 0);
        fragmentManager.executePendingTransactions();

        return (IBaseView) fragment;
    }

    @Override
    public void showLoading() {

        progressBar.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {

        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    public void hideKeyboard() {

        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (currentFragment != null) {
            ((Fragment) currentFragment).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }

}
