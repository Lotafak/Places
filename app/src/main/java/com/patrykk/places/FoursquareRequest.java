package com.patrykk.places;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class FoursquareRequest extends android.support.v4.app.Fragment {

    private Context mContext;
    private RequestQueue mRequestQueue;
    private FoursquareModel mFoursquareModel;
    private String mUrl = "https://api.foursquare.com/v2/venues/explore?";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate the cache
        Cache cache = new DiskBasedCache(mContext.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

        addFoursquareApiCredentials();
    }

    private void addFoursquareApiCredentials() {
        String credentials = "";
        credentials += "client_id=" + mContext.getResources().getString(R.string.client_id);
        credentials += "&client_secret=" + mContext.getResources().getString(R.string.client_secret);
        mUrl = mUrl.concat(credentials);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();

        makeRequest();
    }

    // TODO: Handle request (parse data etc.)
    public String makeRequest() {
        Bundle bundle = getArguments();
        mUrl = addParameters(bundle);
        Log.d(Constants.LOG_TAG, mUrl);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                mUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            int responseCode = (int)meta.get("code");
                            if(responseCode == 200) {
                                Log.d(Constants.LOG_TAG, "200");
                                FoursquareParser fp = new FoursquareParser();
                            }else {
                                Log.e(Constants.LOG_TAG, meta.get("errorType") + ": " + meta.get("errorDetail"));
                            }
                        } catch (JSONException e) {
                            Log.e(Constants.LOG_TAG, e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.LOG_TAG, error.toString() + ": " + error.networkResponse.statusCode);
            }
        });

        mRequestQueue.add(jsonRequest);
        return null;
    }

    private String addParameters(Bundle parameters) {
        String newUrl = mUrl;
        newUrl += "&limit=30&sortByDistance=1&v=20160802";
        String latLng = parameters.getString(Constants.FOURSQUARE_REQUEST_LATLNG, "");
        String category = parameters.getString(Constants.FOURSQUARE_REQUEST_CATEGORY, "");
        if(!latLng.equals(""))
            newUrl += "&ll=" + latLng;
        if(!category.equals(""))
            newUrl += "&category=" + category;

        return newUrl;
    }
}