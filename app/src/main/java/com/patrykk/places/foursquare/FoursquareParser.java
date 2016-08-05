package com.patrykk.places.foursquare;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.patrykk.places.R;
import com.patrykk.places.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class FoursquareParser {
    private Context mContext;

    public FoursquareParser(Context context) {
        mContext = context;
    }

    /**
     * Parsing explore endpoint response from Foursquare API to {@link FoursquareVenueModel} objects
     * and return them as a {@link ArrayList}. Request is made by
     * {@link com.android.volley.toolbox.JsonRequest} asynchronously
     *
     * @param object is {@link JSONObject} response from {@link com.android.volley.toolbox.JsonRequest}
     * @return {@link ArrayList} of type {@link FoursquareVenueModel} with found venues (Foursquare API
     * places)
     */
    public ArrayList<FoursquareVenueModel> ParseJSONObjectFoursquareExploreResponse(JSONObject object) {
        ArrayList<FoursquareVenueModel> mFoursquareVenueModels = new ArrayList<>();

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
            if (response.has("warning")) {
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

                    //Initialize new FoursquareVenueModel object for every venue
                    FoursquareVenueModel mFoursquareVenueModel = new FoursquareVenueModel();

                    // Get current venue
                    JSONObject item = items.getJSONObject(j);
                    JSONObject venue = item.getJSONObject("venue");

                    // Get venue id
                    mFoursquareVenueModel.setId(venue.getString("id"));

                    // get venue name
                    mFoursquareVenueModel.setName(venue.getString("name"));

                    // Get location details
                    JSONObject location = venue.getJSONObject("location");
                    mFoursquareVenueModel.setLatitude(location.getDouble("lat"));
                    mFoursquareVenueModel.setLongitude(location.getDouble("lng"));
                    mFoursquareVenueModel.setCountry(location.getString("country"));

                    // Address can be not specified
                    if (location.has("address"))
                        mFoursquareVenueModel.setAddress(location.getString("address"));
                    else
                        mFoursquareVenueModel.setAddress(mContext.getString(R.string.not_specified));

                    // City can be not specified
                    if (location.has("city"))
                        mFoursquareVenueModel.setCity(location.getString("city"));
                    else
                        mFoursquareVenueModel.setCity(mContext.getString(R.string.not_specified));

                    Log.d(Constants.LOG_TAG, mFoursquareVenueModel.getAddress());

                    // Get first category from categories list
                    JSONArray categories = venue.getJSONArray("categories");
                    JSONObject category = categories.getJSONObject(0);

                    mFoursquareVenueModel.setCategoryId(category.getString("id"));
                    JSONObject icon = category.getJSONObject("icon");
                    mFoursquareVenueModel.setCategoryUrl(icon.getString("prefix"), icon.getString("suffix"));

                    // Add item to list
                    mFoursquareVenueModels.add(mFoursquareVenueModel);
                }
            }

            return mFoursquareVenueModels;
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());
            return null;
        }
    }

//    public ArrayList<FoursquareCategoryModel> ParseJSONObjectFoursquareCategoriesResponse(JSONObject object) {
//        ArrayList<FoursquareCategoryModel> foursquareCategoryModels = new ArrayList<>();
//        try {
//            JSONObject meta = object.getJSONObject("meta");
//            int responseCode = (int) meta.get("code");
//            if (responseCode != HttpURLConnection.HTTP_OK) {
//                Log.e(Constants.LOG_TAG, meta.get("errorType") + ": " + meta.get("errorDetail"));
//                return null;
//            }
//
//            // Get response object from json
//            JSONObject response = object.getJSONObject("response");
//            JSONArray categories = response.getJSONArray("categories");
//
//            recurrence(categories, foursquareCategoryModels);
//
//            return foursquareCategoryModels;
//
//        } catch (JSONException e) {
//            Log.e(Constants.LOG_TAG, e.getMessage());
//            Toast.makeText(mContext, "Json parsing error", Toast.LENGTH_SHORT).show();
//            return null;
//        }
//    }
//
//    private void recurrence(JSONArray array, ArrayList<FoursquareCategoryModel> list){
//        for(int i =0; i<array.length(); i++){
//            FoursquareCategoryModel foursquareCategoryModel = new FoursquareCategoryModel();
//
//            try {
//                JSONObject category = array.getJSONObject(i);
//
//                foursquareCategoryModel.setId(category.getString("id"));
//                foursquareCategoryModel.setName(category.getString("name"));
//
//                JSONObject icon = category.getJSONObject("icon");
//                foursquareCategoryModel.setIconPrefix(icon.getString("prefix"));
//                foursquareCategoryModel.setIconSuffix(icon.getString("suffix"));
//
//                list.add(foursquareCategoryModel);
//
//                if(array.getJSONObject(i).has("categories")){
//                    recurrence(category.getJSONArray("categories"), list);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
