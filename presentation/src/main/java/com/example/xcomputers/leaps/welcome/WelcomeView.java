package com.example.xcomputers.leaps.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.User;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.login.LoginView;
import com.example.xcomputers.leaps.registration.RegistrationContainerView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

import rx.Subscription;

import static com.example.xcomputers.leaps.registration.RegistrationContainerView.EMAIL_ADDRESS_KEY;
import static com.example.xcomputers.leaps.registration.RegistrationContainerView.FB_KEY;
import static com.example.xcomputers.leaps.registration.RegistrationContainerView.FIRST_NAME_KEY;
import static com.example.xcomputers.leaps.registration.RegistrationContainerView.GOOGLE_KEY;
import static com.example.xcomputers.leaps.registration.RegistrationContainerView.LAST_NAME_KEY;

/**
 * Created by xComputers on 25/05/2017.
 */
@Layout(layoutId = R.layout.welcome_view)
public class WelcomeView extends BaseView<WelcomePresenter>  implements GoogleApiClient.OnConnectionFailedListener, InsideView{

    private static final String FB_EMAIL_KEY = "email";
    private static final int RC_SIGN_IN = 1234;

    private LoginButton facebookLoginButton;
    Button googleSignInBtn;
    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;
    private ILoginContainer container;
    private Bundle googleBundle;
    private Bundle facebookBundle;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        facebookLoginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
        googleSignInBtn = (Button) view.findViewById(R.id.google_login_button);
        view.findViewById(R.id.welcome_login_button).setOnClickListener(v -> container.openLoginFragment(LoginView.class, new Bundle(), true));
        view.findViewById(R.id.register_btn).setOnClickListener(v -> container.openLoginFragment(RegistrationContainerView.class, new Bundle(), true));
        setupFacebookLogin();
        setupGoogleLogin();
    }

    public void setContainer(ILoginContainer container){
        this.container = container;
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {

        Bundle args = null;
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct != null){
                args = new Bundle();
                String email = acct.getEmail();
                if (email != null && !email.isEmpty()) {
                    args.putString(EMAIL_ADDRESS_KEY, email);
                }
                String firstName = acct.getGivenName();
                if (firstName != null && !firstName.isEmpty()) {
                    args.putString(FIRST_NAME_KEY, firstName);
                }
                String lastName = acct.getFamilyName();
                if (lastName != null && !lastName.isEmpty()) {
                    args.putString(LAST_NAME_KEY, lastName);
                }
                String googleId = acct.getId();
                if (googleId != null && !googleId.isEmpty()) {
                    args.putString(GOOGLE_KEY, googleId);
                }
                this.googleBundle = args;
                showLoading();
                presenter.loginGoogle(acct.getId());
                Auth.GoogleSignInApi.signOut(googleApiClient);
            }
        }
    }
    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setupGoogleLogin(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleSignInBtn.setOnClickListener(v -> googleSignIn());
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    private void setupFacebookLogin() {

        facebookLoginButton.setReadPermissions(Arrays.asList(FB_EMAIL_KEY, "public_profile"));
        facebookLoginButton.setFragment(this);
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setOnClickListener(view -> showLoading());
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken != null) {
                    return;
                }
                if(getActivity() != null){
                    hideLoading();
                }
                LoginManager.getInstance().logOut();
            }
        }.startTracking();
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {

                    Bundle bundle = new Bundle();
                    if (object.has(FB_EMAIL_KEY)) {
                        try {
                            bundle.putString(EMAIL_ADDRESS_KEY, object.getString(FB_EMAIL_KEY));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        bundle.putString(FB_KEY, object.getString("id"));
                        bundle.putString(FIRST_NAME_KEY, object.getString("first_name"));
                        bundle.putString(LAST_NAME_KEY, object.getString("last_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LoginManager.getInstance().logOut();
                    facebookBundle = bundle;
                    presenter.loginFacebook(bundle.getString(FB_KEY));

                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                hideLoading();
            }

            @Override
            public void onError(FacebookException exception) {
                hideLoading();
                Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected WelcomePresenter createPresenter() {
        return new WelcomePresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getAuthObservable().subscribe(auth -> {
            hideLoading();
            if(auth != null){
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("Authorization", auth).apply();
            }
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("UserId", String.valueOf(User.getInstance().getUserId())).apply();
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }));

        subscriptions.add(presenter.getSubjectNotFoundObservable().subscribe(aVoid -> {
            hideLoading();
            container.openLoginFragment(RegistrationContainerView.class, facebookBundle == null ? googleBundle : facebookBundle, true);
        }));
        subscriptions.add(presenter.getErrorObservable().subscribe(message -> {
            hideLoading();
            if(!message.isEmpty()){
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onBack() {
        getActivity().finish();
        return true;
    }
}
