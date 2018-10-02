package club.leaps.presentation.base;

import android.os.Bundle;

/**
 * Created by xComputers on 22/05/2017.
 */

public interface IActivity {

    <View extends IBaseView> void openFragment(Class<View> viewClass, Bundle arguments);
    <View extends IBaseView> void openFragment(Class<View> viewClass, Bundle arguments, boolean addToBackStack);
   // <View extends IBaseView> void openLoginFragment(Class<View> viewClass, Bundle arguments, boolean addToBackStack);
    void showLoading();

    void hideLoading();
}
