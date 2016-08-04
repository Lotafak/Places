package com.patrykk.places;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class FoursquareParser {
    private ArrayList<FoursquareModel> mFoursquareModels;
    private Context mContext;

    public FoursquareParser(Context context) {
        mContext = context;
        mFoursquareModels = new ArrayList<>();
    }

    /**
     * Parsing explore endpoint response from Foursquare API to {@link FoursquareModel} objects
     * and return them as a {@link ArrayList}. Request is made by
     * {@link com.android.volley.toolbox.JsonRequest} asynchronously
     *
     * @param object is {@link JSONObject} response from {@link com.android.volley.toolbox.JsonRequest}
     * @return {@link ArrayList} of type {@link FoursquareModel} with found venues (Foursquare API
     * places)
     */
    public ArrayList<FoursquareModel> ParseJSONObjectFoursquareExploreResponse(JSONObject object) {
        try {
            JSONObject meta = object.getJSONObject("meta");
            int responseCode = (int) meta.get("code");
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(Constants.LOG_TAG, meta.get("errorType") + ": " + meta.get("errorDetail"));
                return null;
            }

            // Get response object from json
            JSONObject response = object.getJSONObject("response");

            // Get warning message if available and display it to user
            if(response.has("warning")){
                JSONObject warning = response.getJSONObject("warning");
                Toast.makeText(mContext, warning.getString("text"), Toast.LENGTH_LONG).show();
            }

            // Get Foursquare groups and iterate over it
            JSONArray groups = response.getJSONArray("groups");
            for (int i = 0; i < groups.length(); i++) {
                // Get current item
                JSONObject group = groups.getJSONObject(i);
                JSONArray items = group.getJSONArray("items");

                for (int j = 0; j < items.length(); j++) {

                    //Initialize new FoursquareModel object for every venue
                    FoursquareModel mFoursquareModel = new FoursquareModel();

                    // Get current venue
                    JSONObject item = items.getJSONObject(j);
                    JSONObject venue = item.getJSONObject("venue");

                    // Get venue id
                    mFoursquareModel.setId(venue.getString("id"));

                    // get venue name
                    mFoursquareModel.setName(venue.getString("name"));

                    // Get location details
                    JSONObject location = venue.getJSONObject("location");
                    mFoursquareModel.setLatitude(location.getDouble("lat"));
                    mFoursquareModel.setLongitude(location.getDouble("lng"));
                    mFoursquareModel.setCountry(location.getString("country"));

                    // Address can be not specified
                    if (location.has("address"))
                        mFoursquareModel.setAddress(location.getString("address"));
                    else
                        mFoursquareModel.setAddress(mContext.getString(R.string.not_specified));

                    // City can be not specified
                    if (location.has("city"))
                        mFoursquareModel.setCity(location.getString("city"));
                    else
                        mFoursquareModel.setCity(mContext.getString(R.string.not_specified));

                    Log.d(Constants.LOG_TAG, mFoursquareModel.getAddress());

                    // Get first category from categories list
                    JSONArray categories = venue.getJSONArray("categories");
                    JSONObject category = categories.getJSONObject(0);

                    /**
                     * Get only id of category, because we download all the categories (icons,
                     * names etc.) only one time in {@link MainActivity}
                     */
                    mFoursquareModel.setCategory_id(category.getString("id"));

                    // Add item to list
                    mFoursquareModels.add(mFoursquareModel);
                }
            }

            return mFoursquareModels;
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            Toast.makeText(mContext, "Json parsing error", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}
