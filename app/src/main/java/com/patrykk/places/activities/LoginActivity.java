package com.patrykk.places.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.patrykk.places.R;
import com.patrykk.places.constants.Constants;
import com.patrykk.places.dialogs.ContinueAsDialog;

import java.util.Arrays;

/**
 * Login page activity is the first page user will see. Here the user can log in with either
 * Facebook or Google account
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        ContinueAsDialog.OnContinueAsDialogClicked {

    private SignInButton mGoogleSignInButton;
    private CallbackManager mCallbackManager;
    private static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeFacebookApi();

        initializeGoogleApi();

        /** If user is logged in already with facebook account give user opportunity to use
            this account within the app */
        if (AccessToken.getCurrentAccessToken() != null) {
            Profile profile = Profile.getCurrentProfile();
            ContinueAsDialog continueAsDialog = ContinueAsDialog.newInstance(profile.getName());
            continueAsDialog.show(getSupportFragmentManager(), Constants.CONTINUE_AS_TAG);
        }
    }

    /**
     * Initialize Google sign in button and creates google client
     */
    private void initializeGoogleApi() {
        mGoogleSignInButton = (SignInButton) findViewById(R.id.googleLoginButton);
        mGoogleSignInButton.setOnClickListener(this);

        // Configure Sign-in to request User ID, email and basic profile (provided by DEFAULT_SIGN_IN)
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();
    }

    /**
     * Initialize Facebook login button, register callbacks for button click events,
     * facebook api is set to request location and public profile from user
     */
    private void initializeFacebookApi() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        LoginButton mFbLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);
        mFbLoginButton.setReadPermissions(Arrays.asList("user_location", "public_profile"));
        mFbLoginButton.registerCallback(mCallbackManager, facebookCallback);
    }

    /**
     * Callback for facebook account login
     * When user is logged in successfully intent redirects to MainActivity
     */
    FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            goToMainActivityWithFacebook();
        }

        @Override
        public void onCancel() {
            Log.e(Constants.LOG_TAG, "Login attempt cancelled");
            Toast.makeText(LoginActivity.this, "Logging cancelled!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException error) {
            Log.e(Constants.LOG_TAG, "Facebook logging Error");
            Toast.makeText(LoginActivity.this, "Oops! Something gone wrong", Toast.LENGTH_SHORT).show();
        }
    };

    private void goToMainActivityWithFacebook() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MainActivity.class);
        intent.putExtra(Constants.LOGIN_TYPE, Constants.LOGIN_TYPE_FACEBOOK);
        intent.putExtra(Constants.SHOW_LOCATION_DIALOG, true);
        startActivity(intent);
    }

    /**
     * Handles GoogleApiClient onConnectionFailed event
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(LoginActivity.this, "Oops! Something gone wrong", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles Google SignIn button event
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.googleLoginButton:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN_CODE);
                break;
        }
    }

    /**
     * Result handling for both logging with Google acc and Facebook acc
     * depends on request code sent with intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.GOOGLE_SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * If logging in is successful user is sent to main window
     *
     * @param result Google account sign in result
     */
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(Constants.LOG_TAG, "handleSignInResult: " + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acc = result.getSignInAccount();
            assert acc != null;
            mGoogleSignInButton.setEnabled(false);

            Intent goToMainIntent = new Intent();
            goToMainIntent.putExtra(Constants.LOGIN_TYPE, Constants.LOGIN_TYPE_GOOGLE);
            goToMainIntent.putExtra(Constants.USER_NAME, acc.getDisplayName());
            goToMainIntent.putExtra(Constants.SHOW_LOCATION_DIALOG, true);
            goToMainIntent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(goToMainIntent);
        }
    }

    /**
     * Prevents user to go back to MainActivity after logging out
     * Back button now quits the app
     */
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    /**
     * Implementation of Interface method from ContinueAsDialog class
     * Handles information sends from dialog buttons click
     *
     * @param yesNo user wants to keep logged in (true) or wants to change account (false)
     */
    @Override
    public void continueAsLogged(boolean yesNo) {
        if(yesNo){
            goToMainActivityWithFacebook();
        }else{
            LoginManager.getInstance().logOut();
            Toast.makeText(LoginActivity.this, "You can log in now !", Toast.LENGTH_SHORT).show();
        }
    }
}