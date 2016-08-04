package com.patrykk.places.foursquare;

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
import com.patrykk.places.R;
import com.patrykk.places.constants.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

public class FoursquareRequest extends android.support.v4.app.Fragment {

    private Context mContext;

    private RequestQueue mRequestQueue;

    private String mUrl = "https://api.foursquare.com/v2/venues/explore?";

    public OnRequestProcessedListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // If activity implements interface assign it to listener
            this.mListener = (OnRequestProcessedListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRequestProcessedListener interface");
        }

        mContext = context;
    }

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
    }

    @Override
    public void onStart() {
        super.onStart();

        makeVenuesExploreRequest();
    }

    /**
     * Configures URL for making an exmplore request to Foursquare API and makes asynchronous
     * {@link com.android.volley.toolbox.JsonRequest} request.
     *
     * @return {@link ArrayList} of type {@link FoursquareModel} with found venues (locations)
     */
    public String makeVenuesExploreRequest() {
        // Add credentials to url
        mUrl = addFoursquareApiCredentials(mContext.getResources().getString(R.string.client_id),
                mContext.getResources().getString(R.string.client_secret));

        // Add passed parameters to url
        Bundle bundle = getArguments();
        mUrl = addParameters(bundle);

        Log.d(Constants.LOG_TAG, mUrl);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                mUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Create response parses object
                        FoursquareParser foursquareParser = new FoursquareParser(mContext);


                        // Parse response and assign result to array
                        ArrayList<FoursquareModel> foursquareModels = foursquareParser.ParseJSONObjectFoursquareExploreResponse(response);

                        // Call handler method
                        if(foursquareModels != null)
                            FoursquareRequest.this.mListener.onResponseReady(foursquareModels);
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

    /**
     * Adds to request url required app credentials for non-logged Foursquare users.
     * Comes first, before any others parameters
     */
    private String addFoursquareApiCredentials(String id, String secret) {
        String credentials = "";
        credentials += "client_id=" + id;
        credentials += "&client_secret=" + secret;
        return mUrl.concat(credentials);
    }

    /**
     * Adds request parameters to url string
     *
     * @param parameters bundle containing latitude and longitude in string as XX.XX,YY.YY with
     *                   foursquare_request_latlng and category name with foursquare_request_category
     *                   tag.
     * @return url with added parameters
     */
    private String addParameters(Bundle parameters) {
        String newUrl = "";

        // Adding default parameters to url
        newUrl += "&limit=30&sortByDistance=1&v=20160802";

        // Obtaining parameters from passed bundle
        String latLng = parameters.getString(Constants.FOURSQUARE_REQUEST_LATLNG, "");
        String category = parameters.getString(Constants.FOURSQUARE_REQUEST_CATEGORY, "");

        // Checking if parameters are empty string, if not, adding parameters to url
        if (!latLng.equals(""))
            newUrl += "&ll=" + latLng;
        if (!category.equals(""))
            newUrl += "&section=" + category;

        return mUrl.concat(newUrl);
    }

    public interface OnRequestProcessedListener{
        void onResponseReady(ArrayList<FoursquareModel> list);
    }
}