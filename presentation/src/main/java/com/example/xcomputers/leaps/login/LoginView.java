package com.example.xcomputers.leaps.login;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.forgotpassword.ForgotPasswordView;
import com.example.xcomputers.leaps.utils.CustomEditText;
import com.example.xcomputers.leaps.welcome.ILoginContainer;
import com.example.xcomputers.leaps.welcome.InsideView;

import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 03/06/2017.
 */
@Layout(layoutId = R.layout.login_view)
public class LoginView extends BaseView<LoginPresenter> implements InsideView {

    private CustomEditText userNameEt;
    private CustomEditText passwordEt;
    private Button forgotPassBtn;
    private Button signInBtn;
    private ILoginContainer container;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userNameEt = (CustomEditText) view.findViewById(R.id.login_username_et);
        passwordEt = (CustomEditText) view.findViewById(R.id.login_password_et);
        forgotPassBtn = (Button) view.findViewById(R.id.forgot_password_button);
        signInBtn = (Button) view.findViewById(R.id.sign_in_button);
        forgotPassBtn.setOnClickListener(v -> openFragment(ForgotPasswordView.class, null));
        signInBtn.setOnClickListener(v -> {
            showLoading();
            presenter.login(userNameEt.getText().toString(), passwordEt.getText().toString());
        });
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getErrorValidationSubject().subscribe(this::onValidationError));
        subscriptions.add(presenter.getLoginObservable().subscribe(this::onLoginSuccess));
        subscriptions.add(presenter.getLoginErrorObservable().subscribe(this::onError));
    }

    private void onLoginSuccess(String auth){
        if(auth != null){
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("Authorization", auth).apply();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("UserId", String.valueOf(User.getInstance().getUserId())).apply();
        }
        hideLoading();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public boolean onBack() {
        return false;
    }



    private void onError(Throwable t){
        hideLoading();
        Toast.makeText(getContext(), getString(R.string.lbl_general_error), Toast.LENGTH_SHORT).show();
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    private void onValidationError(String message){
        hideLoading();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setContainer(ILoginContainer container) {
        this.container = container;
    }
}
