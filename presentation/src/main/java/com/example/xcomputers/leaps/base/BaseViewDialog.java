package com.example.xcomputers.leaps.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Ivan on 10/16/2017.
 */

public abstract class BaseViewDialog <P extends BasePresenter> extends DialogFragment implements IBaseView {

    protected P presenter;
    private CompositeSubscription subscriptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        if(presenter == null){
            throw new IllegalStateException(String.format("The presenter for class %s is null", this.getClass().getCanonicalName()));
        }
        subscriptions = new CompositeSubscription();
    }

    @Override
    public void onResume() {

        super.onResume();
        if (subscriptions != null) {
            subscriptions.addAll(getSubscriptions());
        }
    }

    @Override
    public void onPause() {

        super.onPause();
        subscriptions.clear();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        subscriptions = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getClass().getAnnotation(Layout.class) == null || getClass().getAnnotation(Layout.class).layoutId() == 0) {
            throw new IllegalArgumentException(String.format("There is no layoutId annotation on %s", getClass().getName()));
        }
        return inflater.inflate(getClass().getAnnotation(Layout.class).layoutId(), container, false);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (!(getActivity() instanceof IActivity)) {
            throw new IllegalStateException("Your activity should implement the IActivity interface!");
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public boolean onBack() {

        return false;
    }

    public void showLoading() {

        ((IActivity) getActivity()).showLoading();
    }

    public void hideLoading() {

        ((IActivity) getActivity()).hideLoading();
    }

    protected  <View extends IBaseView> void openFragment(Class<View> clazz, Bundle args){
        ((IActivity)getActivity()).openFragment(clazz, args);
    }

    protected  <View extends IBaseView> void openFragment(Class<View> clazz, Bundle args, boolean addToBackStack){
        ((IActivity)getActivity()).openFragment(clazz, args, addToBackStack);
    }


    protected final void back() {

        getActivity().onBackPressed();
    }

    protected Subscription[] getSubscriptions() {

        List<Subscription> subs = new ArrayList<>();
        subs.add(presenter.errorObservable().subscribe(this::handleErrorDefault));
        addSubscriptions(subs);
        return subs.toArray(new Subscription[subs.size()]);
    }

    private void handleErrorDefault(String errorMessage){

       // Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    protected abstract P createPresenter();
    protected abstract void addSubscriptions(List<Subscription> subscriptions);

}