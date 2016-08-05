package com.patrykk.places.foursquare;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.patrykk.places.R;
import com.patrykk.places.constants.Constants;
import com.patrykk.places.volley.ImgController;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FoursquareRequest extends android.support.v4.app.Fragment {

    private Context mContext;

    private RequestQueue mRequestQueue;

    // Foursquare venues explorer API endpoint
    private final String mExploreUrl = "https://api.foursquare.com/v2/venues/explore?";

    // Foursquare venues explorer API endpoint
//    private final String mCategoriesUrl = "https://api.foursquare.com/v2/venues/categories?";

    private final String mVersion = "&v=20160802";

    public OnRequestProcessedListener mListener;

    FoursquareParser mFoursquareParser;

    /**
     * Checking for communication interface in calling class
     *
     * @param context calling class
     */
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

//        // Instantiate the cache
//        Cache cache = new DiskBasedCache(mContext.getCacheDir(), 1024 * 1024); // 1MB cap
//
//        // Set up the network to use HttpURLConnection as the HTTP client.
//        Network network = new BasicNetwork(new HurlStack());

//        // Instantiate the RequestQueue with the cache and network.
//        mRequestQueue = new RequestQueue(cache, network);
//
//        // Start the queue
//        mRequestQueue.start();
//
        // Get Request Queue from volley singleton
        mRequestQueue = ImgController.getInstance().getRequestQueue();

        // Initialize parser
        mFoursquareParser = new FoursquareParser(mContext);
    }

    /**
     * Calling proper method based on Tag
     */
    @Override
    public void onStart() {
        super.onStart();

        switch (getTag()) {
            case Constants.FOURSQUARE_VENUE_REQUEST:
                makeVenuesExploreRequest();
                break;
//            case Constants.FOURSQUARE_CATEGORIES_REQUEST:
//                makeCategoriesRequest();
//                break;
            default:
                break;
        }
    }

    /**
     * Makes asynchronous {@link com.android.volley.toolbox.JsonRequest} request for
     * {@link FoursquareVenueModel} items list
     */
    public void makeVenuesExploreRequest() {
        // Add credentials to url
        String exploreUrl = addFoursquareApiCredentials(mContext.getResources().getString(R.string.client_id),
                mContext.getResources().getString(R.string.client_secret), mExploreUrl);

        // Add passed parameters to url
        Bundle bundle = getArguments();
        exploreUrl = addParameters(bundle, exploreUrl);

        Log.d(Constants.LOG_TAG, exploreUrl);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                exploreUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse response and assign result to array
                        ArrayList<FoursquareVenueModel> foursquareVenueModels = mFoursquareParser.ParseJSONObjectFoursquareExploreResponse(response);

                        // Call handler method
                        if (foursquareVenueModels != null)
                            FoursquareRequest.this.mListener.onVenuesResponseReady(foursquareVenueModels);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.LOG_TAG, error.toString() + ": " + error.networkResponse.statusCode);
            }
        });

        mRequestQueue.add(jsonRequest);
    }

//    public void makeCategoriesRequest() {
//        String categoriesUrl = addFoursquareApiCredentials(mContext.getString(R.string.client_id),
//                mContext.getString(R.string.client_secret),
//                mCategoriesUrl);
//        categoriesUrl += mVersion;
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                categoriesUrl,
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            FileOutputStream fos = mContext.openFileOutput(Constants.CATEGORIES_CACHE_FILE_NAME, Context.MODE_PRIVATE);
//                            fos.write(response.toString().getBytes());
//                            fos.close();
//                        } catch (IOException e) {
//                            Log.e(Constants.LOG_TAG, e.getMessage());
//                        }
//                        ArrayList<FoursquareCategoryModel> foursquareCategoryModels = mFoursquareParser.ParseJSONObjectFoursquareCategoriesResponse(response);
//
//                        if (foursquareCategoryModels != null) {
//                            FoursquareRequest.this.mListener.onCategoriesResponseReady(foursquareCategoryModels);
////                            makeCategoryIconRequest(foursquareCategoryModels);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(Constants.LOG_TAG, error.toString() + ": " + error.networkResponse.statusCode);
//            }
//        });
//
//        mRequestQueue.add(jsonObjectRequest);
//    }

//    public void makeCategoryIconRequest(final ArrayList<FoursquareCategoryModel> list) {
//        final HashMap<String, Bitmap> hashMap = new HashMap<>();
//        for (int i = 0; i<list.size(); i++) {
//            String imageUrl = list.get(i).getUrl();
//
//            final int finalI = i;
//            ImageRequest ir = new ImageRequest(imageUrl,
//                    new Response.Listener<Bitmap>() {
//                        @Override
//                        public void onResponse(Bitmap response) {
//                            list.get(finalI).setIcon(response);
//                            hashMap.put(list.get(finalI).getId(), response);
//                            if(finalI == list.size() - 1){
//                                FoursquareRequest.this.mListener.onCategoriesResponseReady(list, hashMap);
//                            }
//                        }
//                    }, 0, 0, null, null, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e(Constants.IMAGE_REQUEST_LOG_TAG, error.toString() + ": " + error.networkResponse.statusCode);
//                }
//            });
//
//            mRequestQueue.add(ir);
//        }
//    }

    /**
     * Adds to request url required app credentials for non-logged Foursquare users.
     * Comes first, before any others parameters
     */
    private String addFoursquareApiCredentials(String id, String secret, String url) {
        String credentials = "";
        credentials += "client_id=" + id;
        credentials += "&client_secret=" + secret;
        return url.concat(credentials);
    }

    /**
     * Adds request parameters to url string
     *
     * @param parameters bundle containing latitude and longitude in string as XX.XX,YY.YY with
     *                   foursquare_request_latlng and category name with foursquare_request_category
     *                   tag.
     * @return url with added parameters
     */
    private String addParameters(Bundle parameters, String url) {
        String parametersQuery = "";

        // Adding default parameters to url
        parametersQuery += "&limit=30&sortByDistance=1";
        parametersQuery += mVersion;

        // Obtaining parameters from passed bundle
        String latLng = parameters.getString(Constants.FOURSQUARE_REQUEST_LATLNG, "");
        String category = parameters.getString(Constants.FOURSQUARE_REQUEST_CATEGORY, "");

        // Checking if parameters are empty string, if not, adding parameters to url
        if (!latLng.equals(""))
            parametersQuery += "&ll=" + latLng;
        if (!category.equals(""))
            parametersQuery += "&section=" + category;

        return url.concat(parametersQuery);
    }

    /**
     * Communication interface with calling class. Calling class needs to implement it.
     */
    public interface OnRequestProcessedListener {
        /**
         * Called by {@link #makeVenuesExploreRequest()} when {@link FoursquareVenueModel} list is ready.
         *
         * @param list {@link FoursquareVenueModel} list
         */
        void onVenuesResponseReady(ArrayList<FoursquareVenueModel> list);

//        void onCategoriesResponseReady(ArrayList<FoursquareCategoryModel> list);
    }
}