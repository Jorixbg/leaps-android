package com.example.xcomputers.leaps.forgotpassword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.utils.CustomEditText;

import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 03/06/2017.
 */
@Layout(layoutId = R.layout.forgot_password_view)
public class ForgotPasswordView extends BaseView<ForgotPasswordPresenter> {

    private CustomEditText emailEt;
    private Button sendEmailRecoveryBtn;
    private Button cancelBtn;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEt = (CustomEditText) view.findViewById(R.id.forgot_pass_email_et);
        cancelBtn = (Button) view.findViewById(R.id.forgot_pass_cancel_btn);
        cancelBtn.setVisibility(View.INVISIBLE);
        sendEmailRecoveryBtn = (Button) view.findViewById(R.id.forgot_pass_action_btn);
        sendEmailRecoveryBtn.setOnClickListener(v -> {
            showLoading();
            presenter.sendPasswordEmail(emailEt.getText().toString());
        });
    }

    @Override
    protected ForgotPasswordPresenter createPresenter() {
        return new ForgotPasswordPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getForgotPassObservable().subscribe(this::onSuccess, this::onError));
        subscriptions.add(presenter.getValidationObservable().subscribe(this::onValidationError));
        subscriptions.add(presenter.getGeneralErrorObservable().subscribe(aVoid -> onError(new Throwable())));
    }

    private void onValidationError(String message){
        hideLoading();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void onSuccess(Void aVoid){

        hideLoading();
        Toast.makeText(getContext(), "Email with the new password has been sent", Toast.LENGTH_SHORT).show();
        back();
    }

    private void onError(Throwable t){
        hideLoading();
        back();
    }
}
