package club.leaps.presentation.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import club.leaps.presentation.R;
import club.leaps.presentation.base.IActivity;
import club.leaps.presentation.base.IBaseView;


/**
 * Created by xComputers on 15/07/2017.
 */

public class WelcomeActivity extends AppCompatActivity implements IActivity {

    private FrameLayout container;
    private IBaseView currentFragment;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        container = (FrameLayout) findViewById(R.id.welcome_activity_frame);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        openFragment(WelcomeContainer.class, new Bundle());
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
        if (getSupportFragmentManager().findFragmentByTag(name) != null) {
            currentFragment = popBackStack(getSupportFragmentManager(), name, arguments);
        } else {
            Fragment fragmentToOpen = Fragment.instantiate(this, name, arguments);
            if (addToBackStack) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.welcome_activity_frame, fragmentToOpen, name)
                        .addToBackStack(name).commitAllowingStateLoss();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.welcome_activity_frame, fragmentToOpen, name)
                        .disallowAddToBackStack().commitAllowingStateLoss();
            }

            getSupportFragmentManager().executePendingTransactions();
            currentFragment = (IBaseView) fragmentToOpen;
        }
    }

    @Override
    public void onBackPressed() {

        if(currentFragment.onBack()){
            return;
        }

        FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 1) {
            int topEntryIndex = fm.getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(topEntryIndex - 1);
            for (int i = topEntryIndex; i >= 0; i--) {
                if (fm.getBackStackEntryAt(i) != null && backStackEntry.getName() != null) {
                    currentFragment = popBackStack(fm, backStackEntry.getName(), null);
                    break;
                }
            }
        } else {
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
}
