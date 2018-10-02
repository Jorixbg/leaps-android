package club.leaps.presentation.welcome;

import android.os.Bundle;

import club.leaps.presentation.base.IBaseView;

/**
 * Created by xComputers on 04/07/2017.
 */

public interface ILoginContainer {

    boolean onLoginBack();
    <View extends IBaseView> void openLoginFragment(Class<View> clazz, Bundle arguments, boolean addToBackStack);
}
