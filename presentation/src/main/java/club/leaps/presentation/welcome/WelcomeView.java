package club.leaps.presentation.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.login.LoginView;
import club.leaps.presentation.registration.RegistrationContainerView;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import club.leaps.presentation.registration.RegistrationContainerView;
import club.leaps.presentation.utils.EntityHolder;
import rx.Subscription;

import static club.leaps.presentation.registration.RegistrationContainerView.EMAIL_ADDRESS_KEY;
import static club.leaps.presentation.registration.RegistrationContainerView.FB_KEY;
import static club.leaps.presentation.registration.RegistrationContainerView.FIRST_NAME_KEY;
import static club.leaps.presentation.registration.RegistrationContainerView.GOOGLE_KEY;
import static club.leaps.presentation.registration.RegistrationContainerView.LAST_NAME_KEY;

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

    private GoogleSignInClient mGoogleSignInClient;


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

    private void handleGoogleSignInResult(GoogleSignInAccount result) {


        Bundle args = null;

            GoogleSignInAccount acct = result;
            if(acct != null){
                args = new Bundle();
                String email = acct.getEmail();
                if (email != null && !email.isEmpty()) {
                    args.putString(RegistrationContainerView.EMAIL_ADDRESS_KEY, email);
                }
                String firstName = acct.getGivenName();
                if (firstName != null && !firstName.isEmpty()) {
                    args.putString(RegistrationContainerView.FIRST_NAME_KEY, firstName);
                }
                String lastName = acct.getFamilyName();
                if (lastName != null && !lastName.isEmpty()) {
                    args.putString(RegistrationContainerView.LAST_NAME_KEY, lastName);
                }
                String googleId = acct.getId();
                if (googleId != null && !googleId.isEmpty()) {
                    args.putString(RegistrationContainerView.GOOGLE_KEY, googleId);
                }
                this.googleBundle = args;
                showLoading();
                presenter.loginGoogle(acct.getId());
                mGoogleSignInClient.signOut();
            }

    }
    private void googleSignIn() {
        /*Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);*/
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setupGoogleLogin(){

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        googleSignInBtn.setOnClickListener(v -> googleSignIn());
    }


    private void setupFacebookLogin() {

        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile",FB_EMAIL_KEY));
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
                            bundle.putString(RegistrationContainerView.EMAIL_ADDRESS_KEY, object.getString(FB_EMAIL_KEY));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        bundle.putString(RegistrationContainerView.FB_KEY, object.getString("id"));
                        bundle.putString(RegistrationContainerView.FIRST_NAME_KEY, object.getString("first_name"));
                        bundle.putString(RegistrationContainerView.LAST_NAME_KEY, object.getString("last_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LoginManager.getInstance().logOut();
                    facebookBundle = bundle;
                    presenter.loginFacebook(bundle.getString(RegistrationContainerView.FB_KEY));

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

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }


       /* if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
            return;
        }*/
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


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            Toast.makeText(getContext(), "Successs", Toast.LENGTH_SHORT).show();

            Log.e("Success", account+"");
            Log.e("Success", account.getDisplayName()+"");
            Log.e("Success", account.getId()+"");
            Log.e("Success", account.getEmail()+"");
            handleGoogleSignInResult(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            Log.e("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}
