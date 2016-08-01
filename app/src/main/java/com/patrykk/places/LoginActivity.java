package com.patrykk.places;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        ContinueAsDialog.OnContinueAsDialogClicked {

    private LoginButton mFbLoginButton;
    private SignInButton mGoogleSignInButton;
    private CallbackManager mCallbackManager;
    private GoogleSignInOptions mGoogleSignInOptions;
    private static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        mFbLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);
        mFbLoginButton.setReadPermissions(Arrays.asList("user_location", "public_profile"));
        mFbLoginButton.registerCallback(mCallbackManager, facebookCallback);

        mGoogleSignInButton = (SignInButton) findViewById(R.id.googleLoginButton);
        mGoogleSignInButton.setOnClickListener(this);

        // Configure Sign-in to request User ID, email and basic profile (provided by DEFAULT_SIGN_IN)
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();

        if (AccessToken.getCurrentAccessToken() != null) {
            Profile profile = Profile.getCurrentProfile();
            ContinueAsDialog continueAsDialog = ContinueAsDialog.newInstance(profile.getName());
            continueAsDialog.show(getSupportFragmentManager(), Constants.CONTINUE_AS_TAG);
        }
    }

    /**
     * Callback for facebook account login
     * When user is logged in successfully intent redirects to MainActivity
     */
    FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            goToMainAcitivityWithFacebook();
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

    private void goToMainAcitivityWithFacebook() {
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
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN_CODE);
    }

    /**
     * Result handling for both loging with Google acc and Facebook acc
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Prevents user to go back to MainActivity after logging out
     * Back button now quits the app
     */
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void continueAsLogged(boolean yesNo) {
        if(yesNo){
            goToMainAcitivityWithFacebook();
        }else{
            LoginManager.getInstance().logOut();
            Toast.makeText(LoginActivity.this, "You can log in now !", Toast.LENGTH_SHORT).show();
        }
    }
}