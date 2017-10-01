package com.example.xcomputers.leaps.profile.tutorial;

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
public class TutorialSecondScreen extends AbstractTutorial{

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nextBtn.setOnClickListener( v -> ((TutorialActivity)getActivity()).setPage(2));
        headerTv.setText(R.string.lbl_tutorial_header_2);
        contentTV.setText(R.string.lbl_tutorial_content_2);
        backgroundLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg2));
    }
}
