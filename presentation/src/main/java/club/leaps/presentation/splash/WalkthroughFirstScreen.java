package club.leaps.presentation.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import club.leaps.presentation.R;
import club.leaps.presentation.base.Layout;

/**
 * Created by xComputers on 03/06/2017.
 */
@Layout(layoutId = R.layout.walkthrough_view)
public class WalkthroughFirstScreen extends AbstractWalkthroughScreen {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nextBtn.setOnClickListener( v -> ((IWalkthroughContainer)getParentFragment()).setPage(1));
        headerTv.setText(R.string.lbl_tutorial_header_1);
        contentTV.setText(R.string.lbl_tutorial_content_1);
        backgroundLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg1));
    }
}
