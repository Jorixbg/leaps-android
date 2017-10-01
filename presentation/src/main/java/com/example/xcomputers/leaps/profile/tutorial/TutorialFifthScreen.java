package com.example.xcomputers.leaps.profile.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.Layout;

/**
 * Created by xComputers on 13/07/2017.
 */
@Layout(layoutId = R.layout.walkthrough_view)
public class TutorialFifthScreen extends AbstractTutorial {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nextBtn.setText(R.string.lbl_got_it);
        nextBtn.setOnClickListener( v -> getActivity().finish());
        headerTv.setText(R.string.lbl_tutorial_header_5);
        contentTV.setText(R.string.lbl_tutorial_content_5);
        backgroundLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg3));
    }
}
