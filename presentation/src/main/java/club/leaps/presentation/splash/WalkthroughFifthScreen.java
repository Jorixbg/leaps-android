package club.leaps.presentation.splash;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import club.leaps.presentation.R;
import club.leaps.presentation.base.Layout;

import static club.leaps.presentation.splash.SplashView.HAS_SEEN_TUTORIAL;

/**
 * Created by xComputers on 13/07/2017.
 */
@Layout(layoutId = R.layout.walkthrough_view)
public class WalkthroughFifthScreen extends AbstractWalkthroughScreen {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(SplashView.HAS_SEEN_TUTORIAL, SplashView.HAS_SEEN_TUTORIAL).apply();
        nextBtn.setText(R.string.lbl_got_it);
        nextBtn.setOnClickListener( v -> {
            ((IWalkthroughContainer)getParentFragment()).openWelcomeScreen();
        });
        headerTv.setText(R.string.lbl_tutorial_header_5);
        contentTV.setText(R.string.lbl_tutorial_content_5);
        backgroundLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg3));
    }
}
