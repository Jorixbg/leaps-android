package com.example.xcomputers.leaps.splash;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.Layout;

/**
 * Created by xComputers on 03/06/2017.
 */
@Layout(layoutId = R.layout.walkthrough_view)
public class WalkthroughThirdScreen extends AbstractWalkthroughScreen {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nextBtn.setOnClickListener( v -> ((IWalkthroughContainer)getParentFragment()).setPage(3));
        headerTv.setText(R.string.lbl_tutorial_header_3);
        contentTV.setText(R.string.lbl_tutorial_content_3);
        backgroundLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg3));
    }
}
