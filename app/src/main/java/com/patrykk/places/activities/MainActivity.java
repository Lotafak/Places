package com.patrykk.places.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.patrykk.places.foursquare.FoursquareModel;
import com.patrykk.places.foursquare.FoursquareRequest;
import com.patrykk.places.foursquare.LocalizationConverter;
import com.patrykk.places.R;
import com.patrykk.places.adapters.DrawerLayoutAdapter;
import com.patrykk.places.constants.Constants;
import com.patrykk.places.dialogs.ChooseCategoryDialog;
import com.patrykk.places.dialogs.ChooseLocationDialog;

import java.util.ArrayList;
import java.util.Locale;

/**
 * MainActivity is the Main Window of the app, here we have Google Maps, Foursquare search and result,
 * toolbar for logging out, for choosing location source and choosing foursquare venues category.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ChooseLocationDialog.OnLocationChosenListener,
        GoogleApiClient.OnConnectionFailedListener,
        ChooseCategoryDialog.OnCategoryChosenListener,
        FoursquareRequest.OnRequestProcessedListener {

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String mLoginType;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Address mAddress;
    private String mCategory;
    private boolean mFirstView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDrawerList();

        mLoginType = getIntent().getStringExtra(Constants.LOGIN_TYPE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);

        // Shows dialog for choosing location if intent says to do it
        if (getIntent().getBooleanExtra(Constants.SHOW_LOCATION_DIALOG, false)) {
            showLocationDialog();
            mFirstView = true;
        }
    }

    /**
     * Pops out DialogFragment for user to choose location source
     */
    private void showLocationDialog() {
        ChooseLocationDialog dialog = new ChooseLocationDialog();
        dialog.show(getSupportFragmentManager(), Constants.LOCATION_DIALOG_TAG);
    }

    /**
     * Initialize Drawer Layout List, adding items, handling drawer open/closed states
     */
    private void initializeDrawerList() {
        mDrawerList = (ListView) findViewById(R.id.places_list);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        try {
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } catch (NullPointerException e) {
            Toast.makeText(MainActivity.this, "Oops, something went wrong!", Toast.LENGTH_SHORT).show();
            finishAffinity();
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.drawer_open_title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Depending on login type (Google, Facebook) sets proper button title
        menu.getItem(2).setTitle(getString(R.string.log_out_button_title) + mLoginType);
        return true;
    }

    /**
     * Handles main buttons click events (Option menu buttons and Drawer Toggle)
     *
     * @param item clicked item to handle
     * @return true if event handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case R.id.change_localization_button:
                showLocationDialog();
                return true;

            case R.id.logout_button:
                logOut();
                return true;

            case R.id.change_category_button:
                showCategoryDialog();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCategoryDialog() {
        ChooseCategoryDialog dialog = new ChooseCategoryDialog();
        dialog.show(getSupportFragmentManager(), Constants.CATEGORY_DIALOG_TAG);
    }

    /**
     * Logs out user from either Facebook or Google account
     */
    private void logOut() {
        switch (mLoginType) {
            case Constants.LOGIN_TYPE_GOOGLE:
                createAndConnectToGoogleApiClient(new GoogleLogOutCallbackHandler());
                break;

            case Constants.LOGIN_TYPE_FACEBOOK:
                handleFacebookLogOut();
                break;

            default:
                Log.e(Constants.LOG_TAG, "Login type not known");
                break;
        }
    }

    /**
     * Logs out user from Facebook account and navigates user to LoginActivity
     */
    private void handleFacebookLogOut() {
        LoginManager.getInstance().logOut();

        Intent goToLoginActivityIntent = new Intent();
        goToLoginActivityIntent.setClass(this, LoginActivity.class);
        startActivity(goToLoginActivityIntent);
    }

    @Override
    public void onCategoryChosen(String category) {
        mCategory = category;

        sendFoursquareRequest(mCategory);
    }

    private void sendFoursquareRequest(String category) {
        Toast.makeText(MainActivity.this, category, Toast.LENGTH_SHORT).show();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.FOURSQUARE_REQUEST_CATEGORY, category);
        bundle.putString(Constants.FOURSQUARE_REQUEST_LATLNG, LocalizationConverter.AddressToQueryString(mAddress));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FoursquareRequest fr = new FoursquareRequest();
        fr.setArguments(bundle);
        ft.add(fr, Constants.FOURSQUARE_REQUEST);
        ft.commit();
    }

    /**
     * Set {@link DrawerLayout} list to {@link FoursquareRequest} parsed response
     *
     * @param list list of venues (places) obtained from Foursquare API
     */
    @Override
    public void onResponseReady(ArrayList<FoursquareModel> list) {
        DrawerLayoutAdapter adapter = new DrawerLayoutAdapter(this, list);
        mDrawerList.setAdapter(adapter);
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Handles {@link GoogleApiClient} callback to logs out user from Google services and
     * sends user to LoginActivity. Sets by
     * {@link #createAndConnectToGoogleApiClient(GoogleApiClient.ConnectionCallbacks)}
     */
    public class GoogleLogOutCallbackHandler implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Toast.makeText(MainActivity.this, "Logout Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Can't log out user, try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Handles {@link ChooseLocationDialog} item click
     *
     * @param locationType indicates which location source user wants to use
     */
    @Override
    public void onLocationChosen(String locationType) {
        Log.d(Constants.LOG_TAG, locationType);

        switch (locationType) {
            case Constants.LOCATION_FACEBOOK:
                handleFacebookLocation();
                break;
            case Constants.LOCATION_DEVICE:
                createAndConnectToGoogleApiClient(new LastKnownLocationCallbackHandler());
                break;
            case Constants.LOCATION_GPS:
                handleGpsLocation();
                break;
            default:
                Log.e(Constants.LOG_TAG, "Incorrect location string");
                break;
        }
    }

    /**
     * Creates GoogleApiClient object with connection to both Google Sign in and Location services
     * Google documentation says that connecting to services again in each activity separately is
     * lightweight operation and it's recommended to handle it like this
     *
     * {@param callback callback handler}
     */
    private void createAndConnectToGoogleApiClient(GoogleApiClient.ConnectionCallbacks callback) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            callback.onConnected(null);
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addConnectionCallbacks(callback)   // sets callback from parameter
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Handles {@link GoogleApiClient} Callbacks to obtain last known device localization.
     * Set by {@link #createAndConnectToGoogleApiClient(GoogleApiClient.ConnectionCallbacks)}
     */
    private class LastKnownLocationCallbackHandler implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Location mLastLocation = LocationServices
                    .FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                setLocation(LocalizationConverter.ToAddress(mLastLocation, MainActivity.this));
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    }

    /**
     * Creates locationManager object, checks permissions, request permissions if not granted
     * and checks if location is enabled
     */
    private void handleGpsLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_PERMISSION_LOCATION_CODE);
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            processLocation();
        } else {    // If not aks if user wants to go to settings
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_localization_title)
                    .setMessage(R.string.no_localization_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on
     * {@param permissions}
     *
     * @param requestCode  code passed to {@link #requestPermissions(String[], int)}
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_PERMISSION_LOCATION_CODE) {

            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Access granted", Toast.LENGTH_SHORT).show();

                processLocation();
            }
        }
    }

    /**
     * Invoked after checking permissions and provider availability.
     * Gets location from GPS and passes Address object to {@link #setLocation(Address)}
     */
    private void processLocation() {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mAddress = LocalizationConverter.ToAddress(location, this);
        if (mAddress != null)
            setLocation(mAddress);
        else {
            Address noGeocoderAddress = new Address(Locale.getDefault());
            noGeocoderAddress.setLatitude(location.getLatitude());
            noGeocoderAddress.setLongitude(location.getLongitude());
            setLocation(noGeocoderAddress);
        }
    }

    /**
     * Handles facebook location as a users location
     * Invoked by {@link #onLocationChosen(String)}
     * Invokes async task for users location with GraphRequest object and sets location on map when
     * finished and response was obtained successfully
     */
    private void handleFacebookLocation() {
        Bundle bundle = new Bundle();
        bundle.putString("fields", "location"); // Add query for users location

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),    // Getting logged users access token
                "me",   // Logged users data url
                bundle,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        // If GraphRequest fails log on console and inform user
                        if (response.getError() != null) {
                            Log.e(Constants.LOG_TAG, response.getError().getErrorMessage());

                            Toast.makeText(MainActivity.this, "Failed to obtain localization", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Converts response to LatLng object
                        mAddress = LocalizationConverter.ToAddress(response, getApplicationContext());
                        assert mAddress != null;
                        Log.d(Constants.LOG_TAG, mAddress.getLatitude() + ", " + mAddress.getLongitude());
                        setLocation(mAddress);
                    }
                }
        ).executeAsync();
    }

    /**
     * Add marker in users chosen location on the map, sets title and zoom to it
     *
     * @param address object containing information about localization
     */
    private void setLocation(Address address) {
        // Clear map from markers, polyline etc.
        mMap.clear();

        // Get latitude and longitude from address object
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        mMap.addMarker(new MarkerOptions().position(latLng).title(formatAddress(address)));

        CameraPosition cp = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));  // Zoom level: min 2.0 - whole world, max 21.0

        if (mFirstView)
        {
            showCategoryDialog();
            mFirstView = false;
        } else
            sendFoursquareRequest(mCategory);
    }

    /**
     * Format address object for setting google maps marker title
     * Method checks which object information are available (not null) and return title based on it
     *
     * @param address object containing information about localization
     * @return formatted string or empty {@code String} if not fulfilling any condition
     */
    private String formatAddress(Address address) {
        if (address.getCountryName() != null) {
            if (address.getThoroughfare() != null) {
                if (address.getSubThoroughfare() != null)
                    return address.getThoroughfare() + " " + address.getSubThoroughfare() + ", " + address.getCountryName();
                else
                    return address.getThoroughfare() + ", " + address.getCountryName();
            } else if (address.getLocality() != null)
                return address.getLocality() + ", " + address.getCountryName();

            return address.getCountryName();
        }

        return "";
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}