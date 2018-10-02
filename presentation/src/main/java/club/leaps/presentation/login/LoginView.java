package club.leaps.presentation.login;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.forgotpassword.ForgotPasswordView;
import club.leaps.presentation.utils.CustomEditText;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.welcome.ILoginContainer;
import club.leaps.presentation.welcome.InsideView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.List;

import club.leaps.presentation.forgotpassword.ForgotPasswordView;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.welcome.ILoginContainer;
import club.leaps.presentation.welcome.InsideView;
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
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        forgotPassBtn.setOnClickListener(v -> openFragment(ForgotPasswordView.class, null));
        signInBtn.setOnClickListener(v -> {
            if(userNameEt.getText().toString().isEmpty()){
                userNameEt.setError(getString(R.string.non_empty_user_request));
                userNameEt.requestFocus();
                return;
            }
            if (passwordEt.getText().toString().isEmpty()) {
                passwordEt.setError(getString(R.string.non_empty_last_name_request));
                passwordEt.requestFocus();
                return;
            }
            showLoading();
            presenter.login(userNameEt.getText().toString(), passwordEt.getText().toString(),firebaseToken);
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
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(getString(R.string.auth_label), auth).apply();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("UserId", String.valueOf(User.getInstance().getUserId())).apply();
        }
        hideLoading();

        String deviceToken = EntityHolder.getInstance().getEntity().firebaseToken();
        String currentUserId = String.valueOf(User.getInstance().getUserId());

        HashMap<String, String> loginUser = new HashMap<>();
        loginUser.put("device_token",deviceToken);
        loginUser.put("id",currentUserId);
        loginUser.put("os","android");
        loginUser.put("name",User.getInstance().getFirstName()+" " + User.getInstance().getLastName());
        loginUser.put("image", User.getInstance().getImageUrl());

       DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        userRef.setValue(loginUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //  Toast.makeText(getApplicationContext(),"Successful.",Toast.LENGTH_SHORT).show();
                }
                else {
                    // Toast.makeText(getApplicationContext(),"ERROR.",Toast.LENGTH_SHORT).show();
                }
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });


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
