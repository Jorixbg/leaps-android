package com.example.xcomputers.leaps.registration.registrationscreens;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.registration.IRegistrationContainer;
import com.example.xcomputers.leaps.registration.IRegistrationInsideView;
import com.example.xcomputers.leaps.utils.CustomEditText;

import java.util.List;
import java.util.regex.Pattern;

import rx.Subscription;

import static com.example.xcomputers.leaps.registration.RegistrationContainerView.PASSWORD_KEY;

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
                passET.setError("Please provide a non empty password");
                passET.requestFocus();
                return;
            }
            if (!Pattern.matches("^(?=.*[a-zA-Z0-9])(?=.*[!@#$%&*()_+<>?{}~]).{6,}$", passET.getText().toString())) {
                passET.setError("Please provide a valid password");
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