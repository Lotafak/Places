package com.patrykk.places.parsers;

import com.patrykk.places.constants.Constants;

/**
 * Class for parsing to proper venue url address
 */

public class FoursuareVenueUrlPareser {
    public static String makeUrl(String title, String id){
        return Constants.VENUE_URL + title.replace(" ", "-") + "/" + id;
    }
}
