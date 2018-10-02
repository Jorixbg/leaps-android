package club.leaps.presentation.profile;

import android.os.Bundle;

import club.leaps.presentation.base.IBaseView;

/**
 * Created by xComputers on 23/07/2017.
 */

public interface IProfileTabContainer {

    <View extends IBaseView> void openProfileFrag(Class<View> clazz, Bundle arguments, boolean addToBackStack);
}
