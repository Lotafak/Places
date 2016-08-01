package com.patrykk.places;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.facebook.GraphResponse;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Helper class converting Facebooks API {@link GraphResponse}
 * {@link GraphResponse} contains location only if user allow FB API to read it from his profile
 * {@link Geocoder} object query 5 places by name and then converter takes first one as the desired address
 */
public class GraphResponseToLocationConverter {

    /**
     * Converts {@link GraphResponse} to {@link LatLng}
     * @param graphResponse Facebook API GraphResponse data with location ID and name
     * @param context application context
     * @return {@link LatLng} object containing Latitude and Longitude
     */
    public static LatLng ToAddress(GraphResponse graphResponse, Context context){
        try {
            JSONObject object = graphResponse.getJSONObject();
            JSONObject location = object.getJSONObject("location");
            String locationName = location.getString("name");

            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses = geocoder.getFromLocationName(locationName, 5);
            if (addresses == null) {
                Toast.makeText(context, "Can't find location", Toast.LENGTH_SHORT).show();
            } else {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (JSONException | NullPointerException | IOException e) {
            Log.e(Constants.LOG_TAG, e.toString());
            return null;
        }
        return null;
    }
}
