package com.patrykk.places.foursquare;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.GraphResponse;
import com.patrykk.places.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Helper class converting Facebooks API {@link GraphResponse}
 * {@link GraphResponse} contains location only if user allow FB API to read it from his profile
 * {@link Geocoder} object query 1 place by name and then converter it as the desired address
 */
public class LocalizationConverter {

    /**
     * Converts {@link GraphResponse} to {@link Address}
     *
     * @param graphResponse Facebook API {@link GraphResponse} data with location ID and name
     * @param context       application context
     * @return {@link Address} object
     */
    public static Address ToAddress(GraphResponse graphResponse, Context context) {
        try {
            JSONObject object = graphResponse.getJSONObject();
            JSONObject location = object.getJSONObject("location");
            String locationName = location.getString("name");

            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses == null) {
                Toast.makeText(context, "Can't find location", Toast.LENGTH_SHORT).show();
            } else {
                return addresses.get(0);
            }
        } catch (JSONException | NullPointerException | IOException e) {
            Log.e(Constants.LOG_TAG, e.toString());
            return null;
        }
        return null;
    }

    public static Address ToAddress(Location location, Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            Toast.makeText(context, "Warning: No internet connection! Application will provide less information about localization", Toast.LENGTH_LONG).show();
            return null;
        }
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address a;

            if (addresses == null) {
                Toast.makeText(context, "Can't find location!", Toast.LENGTH_SHORT).show();
                return null;
            } else
                 a = addresses.get(0);
            return a;
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, e.toString());
            Toast.makeText(context, "Can't get location", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static String AddressToQueryString(Address address) {
        if (address == null) {
            Log.e(Constants.LOG_TAG, "address is null in AddressToQuesyString(Address address)");
            return "";
        }
        String string = "";
        string += address.getLatitude() + "," + address.getLongitude();
        return string;
    }
}
