package com.patrykk.places;

import android.Manifest;
import android.content.Context;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * MainActivity is the Main Window of the app, here we have Google Maps, Foursquare search and result,
 * toolbar for logging out and for choosing location source.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OnMapReadyCallback,
        ChooseLocationDialog.OnLocationChosenListener,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String mLoginType;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Location location;

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
        mDrawerList = (ListView) findViewById(R.id.navigationList);

        addDrawerItems();

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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


    /**
     * Adding a few sample items for Drawer Layout list
     */
    private void addDrawerItems() {
        String[] sampleArray = {"Item1", "Item2", "Item3", "Item4", "Item5"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                sampleArray);
        mDrawerList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Depending on login type (Google, Facebook) sets proper button title
        menu.getItem(1).setTitle("Log out from " + mLoginType);
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Logs out user from either Facebook or Google account
     */
    private void logOut() {
        switch (mLoginType) {
            case Constants.LOGIN_TYPE_GOOGLE:
                handleGoogleLogOut();
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

    /**
     * Logs out user from Google account
     * Creates GoogleApiClient object with connection to Google Sign in services
     * Google documentation says that connecting to services again in each activity is lightweight
     * operation and it's recommended to handle it like this
     */
    private void handleGoogleLogOut() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addConnectionCallbacks(this)   // sets
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * GoogleApiClient listener fires when client is connected and ready to use
     * Logs out user from Google services and sends user to LoginActivity
     *
     * @param bundle extra information about connection
     */
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
                // TODO
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
     * Creates locationManager object, checks permissions, request permissions if not granted
     * and checks if location is enabled
     */
    private void handleGpsLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSION_REQUEST_LOCATION_CODE);
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            processLocation();
        } else {    // If not go to settings
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
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

        if (requestCode == Constants.PERMISSION_REQUEST_LOCATION_CODE) {

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
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Address address = LocalizationConverter.ToAddress(location, this);
        if (address != null)
            setLocation(address);
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
                        Address address = LocalizationConverter.ToAddress(response, getApplicationContext());
                        assert address != null;
                        Log.d(Constants.LOG_TAG, address.getLatitude() + ", " + address.getLongitude());
                        setLocation(address);
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
        mMap.clear();

        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        mMap.addMarker(new MarkerOptions().position(latLng).title(formatAddress(address)));
        CameraPosition cp = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));  // Zoom level: min 2.0 - whole world, max 21.0
    }

    /**
     * Format address object for setting google maps marker title
     * Method checks which object information are available (not null) and return title based on it
     *
     * @param address object containing information about localization
     * @return formatted string
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
        return "Undefined";
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