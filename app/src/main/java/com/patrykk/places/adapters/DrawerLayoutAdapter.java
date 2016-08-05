package com.patrykk.places.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.patrykk.places.R;
import com.patrykk.places.foursquare.FoursquareVenueModel;
import com.patrykk.places.volley.ImgController;

import java.util.ArrayList;

/**
 * Adapter for drawer layout {@link android.widget.ListView}. Accepts {@link FoursquareVenueModel}
 * list of items as source data.
 */
public class DrawerLayoutAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private ArrayList<FoursquareVenueModel> mFoursquareVenueModels;
    private Context mContext;

    /**
     * Constructor for {@link DrawerLayoutAdapter}
     *
     * @param venueList List of venues (places) of class {@link FoursquareVenueModel} to be shown
     *                  in list
     */
    public DrawerLayoutAdapter(Context context, ArrayList<FoursquareVenueModel> venueList) {
        mContext = context;
        mFoursquareVenueModels = venueList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mFoursquareVenueModels.size();
    }

    @Override
    public Object getItem(int i) {
        return mFoursquareVenueModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Creating ViewHolder for list item
        ViewHolder holder;

        // If view is created for the first time, inflate it and fill holder with view references
        if (view == null) {
            view = mInflater.inflate(R.layout.foursquare_result_list_item, viewGroup, false);

            holder = new ViewHolder();
            holder.iconImageView = (NetworkImageView) view.findViewById(R.id.foursquare_icon);
            holder.nameTextView = (TextView) view.findViewById(R.id.foursquare_name);
            holder.addressTextView = (TextView) view.findViewById(R.id.foursquare_address);

            view.setTag(holder);
        } else {    // When view is created already, assign it to holder
            holder = (ViewHolder) view.getTag();
        }

        NetworkImageView iconImageView = holder.iconImageView;
        TextView nameTextView = holder.nameTextView;
        TextView addressTextView = holder.addressTextView;

        FoursquareVenueModel model = (FoursquareVenueModel) getItem(i);

        // Get default icon for not yet loaded icons
        iconImageView.setDefaultImageResId(R.drawable.default_icon);
        // Get the icon from url using Volley
        iconImageView.setImageUrl(model.getCategoryUrl(), ImgController.getInstance().getImageLoader());

        nameTextView.setText(model.getName());
        addressTextView.setText(model.getFullAddress());

        return view;
    }

    // ViewHolder class for keeping references to view
    private static class ViewHolder {
        public NetworkImageView iconImageView;
        public TextView nameTextView;
        public TextView addressTextView;
    }
}
