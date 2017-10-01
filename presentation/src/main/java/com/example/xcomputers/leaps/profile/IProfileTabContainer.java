package com.example.xcomputers.leaps.profile;

import android.os.Bundle;

import com.example.xcomputers.leaps.base.IBaseView;

/**
 * Created by xComputers on 23/07/2017.
 */

public interface IProfileTabContainer {

    <View extends IBaseView> void openProfileFrag(Class<View> clazz, Bundle arguments, boolean addToBackStack);
}
