package club.leaps.presentation.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.FrameLayout;

import club.leaps.presentation.R;
import club.leaps.presentation.base.IActivity;
import club.leaps.presentation.base.IBaseView;

import club.leaps.presentation.base.IActivity;
import club.leaps.presentation.base.IBaseView;

/**
 * Created by Ivan on 9/7/2017.
 */
public class GoogleMapContainer extends AppCompatActivity implements IActivity {

    private IBaseView currentFragment;
    private FrameLayout container;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        container = (FrameLayout) findViewById(R.id.container);

            openFragment(GoogleMapView.class,new Bundle());
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
                        .replace(R.id.container, fragmentToOpen, name)
                        .addToBackStack(name).commitAllowingStateLoss();
            } else {
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

        if (currentFragment.onBack()) {
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
    }

    @Override
    public void hideLoading() {

    }

}
