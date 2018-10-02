package club.leaps.presentation.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;
import club.leaps.presentation.base.IBaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.profile.trainerProfile.TrainerProfilePreview;
import club.leaps.presentation.profile.userProfile.UserProfilePreview;

import java.util.List;

import club.leaps.presentation.profile.trainerProfile.TrainerProfilePreview;
import club.leaps.presentation.profile.userProfile.UserProfilePreview;
import rx.Subscription;

/**
 * Created by xComputers on 18/07/2017.
 */
@Layout(layoutId = R.layout.profile_container_view)
public class ProfileTabViewContainer extends BaseView<EmptyPresenter> implements IProfileTabContainer {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Class clazz = User.getInstance().isTrainer() ? TrainerProfilePreview.class : UserProfilePreview.class;
        openProfileFrag(clazz, new Bundle());
    }

    private IBaseView currentFragment;


    public <View extends IBaseView> void openProfileFrag(Class<View> clazz, Bundle arguments) {

        openProfileFrag(clazz, arguments, true);
    }

    @Override
    public <View extends IBaseView> void openProfileFrag(Class<View> clazz, Bundle arguments, boolean addToBackStack) {

        String name = clazz.getCanonicalName();
        if (name == null) {
            return;
        }
        if (currentFragment != null && currentFragment.getClass().getCanonicalName().equals(name)) {
            return;
        }
        if (getChildFragmentManager().findFragmentByTag(name) != null) {
            currentFragment = popBackStack(getChildFragmentManager(), name, arguments);
        } else {
            Fragment fragmentToOpen = Fragment.instantiate(getContext(), name, arguments);
            if (addToBackStack) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.profile_container, fragmentToOpen, name)
                        .addToBackStack(name).commitAllowingStateLoss();
            } else {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.profile_container, fragmentToOpen, name)
                        .disallowAddToBackStack().commitAllowingStateLoss();
            }
            getChildFragmentManager().executePendingTransactions();
            currentFragment = (IBaseView) fragmentToOpen;
        }
    }

    @Override
    public boolean onBack() {

        if (currentFragment.onBack()) {
            return true;
        }

        FragmentManager fm = getChildFragmentManager();

        if (fm.getBackStackEntryCount() > 1) {
            int topEntryIndex = fm.getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(topEntryIndex - 1);
            for (int i = topEntryIndex; i >= 0; i--) {
                if (fm.getBackStackEntryAt(i) != null && backStackEntry.getName() != null) {
                    currentFragment = popBackStack(fm, backStackEntry.getName(), null);
                    return true;
                }
            }
        }
        return false;
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
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(currentFragment != null){
            ((Fragment)currentFragment).onActivityResult(requestCode, resultCode, data);
        }
    }
}
