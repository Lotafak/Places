package com.patrykk.places.constants;

/**
 * Class for keeping constant values that are shared among whole application
 */
public class Constants {
    /**
     * Basic log tag
     */
    public static final String LOG_TAG = "LOG_TAG";

    /**
     * Key for intents extra login type
     */
    public static final String LOGIN_TYPE = "login_type";

    /**
     * Value of {@value LOGIN_TYPE} for facebook account login
     */
    public static final String LOGIN_TYPE_FACEBOOK = "Facebook";

    /**
     * Value of {@value LOGIN_TYPE} for Google account login
     */
    public static final String LOGIN_TYPE_GOOGLE = "Google";

    /**
     * Key for user name
     */
    public static final String USER_NAME = "name";

    /**
     * Key for intents extra boolean value indicates weather show location dialog fragment or not
     */
    public static final String SHOW_LOCATION_DIALOG = "loc_dialog";

    /**
     * Intents location type choose dialog tag
     */
    public static final String LOCATION_DIALOG_TAG = "location_dialog_tag";

    /**
     * Intents category choose dialog tag
     */
    public static final String CATEGORY_DIALOG_TAG = "category_dialog_tag";

    /**
     * Intents continue as dialog tag
     */
    public static final String CONTINUE_AS_TAG = "continue_as_tag";

    /**
     * Users chosen localization source - last known device location
     */
    public static final String LOCATION_DEVICE = "device";

    /**
     * Users chosen localization source - gps location
     */
    public static final String LOCATION_GPS = "gps";

    /**
     * Users chosen localization source - facebook "city" location
     */
    public static final String LOCATION_FACEBOOK = "facebook";

    /**
     * Code to mark intent as google account sin out
     */
    public static final int GOOGLE_SIGN_IN_CODE = 10;

    /**
     * Code to mark intent as request for location permission
     */
    public static final int REQUEST_PERMISSION_LOCATION_CODE = 20;

    /**
     * Tag for passing category String with bundle to FoursquareRequest
     */
    public static final String FOURSQUARE_REQUEST_CATEGORY = "foursquare_request_category";

    /**
     * Tag for passing latitude and longitude String with bundle to FoursquareRequest
     * Latlng has to follow template: XX.XX,YY.YY
     */
    public static final String FOURSQUARE_REQUEST_LATLNG = "foursquare_request_latlong";

    /**
     * Teg for making foursquare request
     */
    public static final String FOURSQUARE_REQUEST = "foursquare_request";
}
