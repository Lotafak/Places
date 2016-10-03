package com.patrykk.places.constants;

/**
 * Class for keeping constant values that are shared among whole application
 */
public class Constants {
    /**
     * Basic log tag
     */
    public static final String LOG_TAG = "log_tag";

    /**
     * Tag for volley image request
     */
    public static final String IMAGE_REQUEST_LOG_TAG = "image_requst_log_tag";

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
     * Tag for making foursquare venue request
     */
    public static final String FOURSQUARE_VENUE_REQUEST = "foursquare_venue_request";

    /**
     * Tag for making foursquare categories request
     */
    public static final String FOURSQUARE_CATEGORIES_REQUEST = "foursquare_categories_request";

    /**
     * Log out button position in option menu
     */
    public static final int LOG_OUT_BUTTON_POSITION = 3;

    /**
     * Facebook location item position on location list in dialog
     */
    public static final int FACEBOOK_LOCATION_LIST_POSITION = 2;

    public static final String MARKER_VENUE_NAME = "marker_venue_name";

    public static final String VENUE_URL = "https://foursquare.com/v/";

    public static final String WEB_VIEW_DIALOG_TAG = "WEB_VIEW_DIALOG";

    public static final String VENUES_LIMIT = "venues_limit";
}
