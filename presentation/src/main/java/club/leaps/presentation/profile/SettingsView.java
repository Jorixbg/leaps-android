package club.leaps.presentation.profile;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import club.leaps.presentation.R;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.utils.EntityHolder;


import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 23/07/2017.
 */
@Layout(layoutId = R.layout.settings_view)
public class SettingsView extends BaseView<SettingsPresenter> {

    private TextView seekProgressView;
    private SeekBar seekBar;
    private Button doneBtn;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doneBtn = (Button) view.findViewById(R.id.settings_done_btn);
        seekProgressView = (TextView) view.findViewById(R.id.search_distance_tv);
        seekBar = (SeekBar) view.findViewById(R.id.search_seek_bar);
        seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.light_blue), PorterDuff.Mode.SRC_IN);
        seekBar.setProgress(EntityHolder.getInstance().getEntity().maxDistance());
        seekProgressView.setText(seekBar.getProgress() +" "+view.getContext().getString(R.string.lbl_km));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    progress = 1;
                }
                seekProgressView.setText(String.valueOf(progress) + " "+ getString(R.string.lbl_km));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Empty
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Empty
            }
        });

        doneBtn.setOnClickListener(v -> {
            showLoading();
            presenter.updateUser(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""), seekBar.getProgress());
        });
    }

    @Override
    protected SettingsPresenter createPresenter() {
        return new SettingsPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getUpdateUserObservable()
                .subscribe(aVoid -> {
                    hideLoading();
                    Toast.makeText(getContext(), getString(R.string.max_distance_notification), Toast.LENGTH_SHORT).show();
                    back();
                }));
        subscriptions.add(presenter.getErrorObservable().subscribe(aVoid -> {
            hideLoading();
        }));
    }
}
