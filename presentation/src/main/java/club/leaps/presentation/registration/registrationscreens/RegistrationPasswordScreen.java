package club.leaps.presentation.registration.registrationscreens;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import club.leaps.presentation.R;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.registration.IRegistrationContainer;
import club.leaps.presentation.registration.IRegistrationInsideView;
import club.leaps.presentation.utils.CustomEditText;

import java.util.List;
import java.util.regex.Pattern;

import rx.Subscription;

import static club.leaps.presentation.registration.RegistrationContainerView.PASSWORD_KEY;

/**
 * Created by xComputers on 04/06/2017.
 */
@Layout(layoutId = R.layout.registration_password_view)
public class RegistrationPasswordScreen extends BaseView<EmptyPresenter> implements IRegistrationInsideView {

    private Button cancelBtn;
    private Button nextBtn;
    private CustomEditText passET;
    private IRegistrationContainer container;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelBtn = (Button) view.findViewById(R.id.reg_pass_cancel_btn);
        nextBtn = (Button) view.findViewById(R.id.reg_pass_action_btn);
        passET = (CustomEditText) view.findViewById(R.id.reg_pass_et);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                if (getArguments().containsKey(PASSWORD_KEY)) {
                    passET.setText(getArguments().getString(PASSWORD_KEY));
                }
            }
        } else if (savedInstanceState.containsKey(PASSWORD_KEY)) {
            passET.setText(savedInstanceState.getString(PASSWORD_KEY));
        }
        nextBtn.setOnClickListener(v -> {
            if (passET.getText().toString().isEmpty()) {
                passET.setError(getString(R.string.empty_password_request));
                passET.requestFocus();
                return;
            }
            if (!Pattern.matches("^(?=.*[a-zA-Z0-9])(?=.*[!@#$%&*()_+<>?{}~.]).{6,}$", passET.getText().toString())) {
                passET.setError(getString(R.string.valid_password_request));
                passET.requestFocus();
                return;
            }

            container.gatherData(passET.getText().toString());
        });

        cancelBtn.setOnClickListener(v -> {
            getActivity().finish();
        });
    }

    @Override
    public boolean onBack() {
        return container.onBack();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!passET.getText().toString().isEmpty()) {
            outState.putString(PASSWORD_KEY, passET.getText().toString());
        }
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
    }

    @Override
    public void setContainer(IRegistrationContainer container) {
        this.container = container;
    }
}