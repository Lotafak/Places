package com.patrykk.places;

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
    public static final String LOGIN_TYPE = "LOGIN_TYPE";

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
     * Intents location dialog tag
     */
    public static final String LOCATION_DIALOG_TAG = "LOCATION_DIALOG_TAG";

    /**
     * Intents continue as dialog tag
     */
    public static final String CONTINUE_AS_TAG = "CONTINUE_AS_TAG";

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
}
