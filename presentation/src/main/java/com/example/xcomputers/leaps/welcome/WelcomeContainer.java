package com.example.xcomputers.leaps.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.IBaseView;
import com.example.xcomputers.leaps.base.Layout;

import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 04/07/2017.
 */
@Layout(layoutId = R.layout.welcome_container)
public class WelcomeContainer extends BaseView<EmptyPresenter> implements ILoginContainer {

    private FrameLayout container;
    private IBaseView loginFragment;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container = (FrameLayout) view.findViewById(R.id.welcome_container);
        openLoginFragment(WelcomeView.class, new Bundle(), true);
    }

    @Override
    public <View extends IBaseView> void openLoginFragment(Class<View> clazz, Bundle arguments, boolean addToBackStack) {

        String name = clazz.getCanonicalName();
        if (name == null) {
            return;
        }
        if (loginFragment != null && loginFragment.getClass().getCanonicalName().equals(name)) {
            return;
        }
        if (getChildFragmentManager().findFragmentByTag(name) != null) {
            loginFragment = popBackStack(getChildFragmentManager(), name, arguments);
        } else {
            Fragment fragmentToOpen = Fragment.instantiate(getContext(), name, arguments);
            if (addToBackStack) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.welcome_container, fragmentToOpen, name)
                        .addToBackStack(name).commit();
            } else {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.welcome_container, fragmentToOpen, name)
                        .disallowAddToBackStack().commit();
            }
            getChildFragmentManager().executePendingTransactions();
            loginFragment = (IBaseView) fragmentToOpen;
            ((InsideView)fragmentToOpen).setContainer(this);
        }

    }

    @Override
    public boolean onLoginBack() {

        if (loginFragment.onBack()) {
            return true;
        }

        FragmentManager fm = getChildFragmentManager();

        if (fm.getBackStackEntryCount() > 1) {
            int topEntryIndex = fm.getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(topEntryIndex - 1);
            for (int i = topEntryIndex; i >= 0; i--) {
                if (fm.getBackStackEntryAt(i) != null && backStackEntry.getName() != null) {
                    loginFragment = popBackStack(fm, backStackEntry.getName(), null);
                    break;
                }
            }
        } else {
            getActivity().supportFinishAfterTransition();
        }
        return true;
    }

    @Override
    public boolean onBack() {
        return onLoginBack();
    }

    protected IBaseView popBackStack(FragmentManager fragmentManager, String fragmentViewName, Bundle args) {

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentViewName);
        if (fragment != null && fragment.getArguments() != null && args != null) {
            fragment.getArguments().putAll(args);
        }
        fragmentManager.popBackStack(fragmentViewName, 0);
        fragmentManager.executePendingTransactions();
        ((InsideView)fragment).setContainer(this);
        return (IBaseView) fragment;
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }
}
