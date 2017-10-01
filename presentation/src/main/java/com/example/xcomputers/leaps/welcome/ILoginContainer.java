package com.example.xcomputers.leaps.welcome;

import android.os.Bundle;

import com.example.xcomputers.leaps.base.IBaseView;

/**
 * Created by xComputers on 04/07/2017.
 */

public interface ILoginContainer {

    boolean onLoginBack();
    <View extends IBaseView> void openLoginFragment(Class<View> clazz, Bundle arguments, boolean addToBackStack);
}
