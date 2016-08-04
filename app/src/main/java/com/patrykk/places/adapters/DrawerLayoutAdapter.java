package com.patrykk.places.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.patrykk.places.foursquare.FoursquareModel;
import com.patrykk.places.R;

import java.util.ArrayList;


public class DrawerLayoutAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private ArrayList<FoursquareModel> mFoursquareModels;
    private Context mContext;

    public DrawerLayoutAdapter(Context context, ArrayList<FoursquareModel> list) {
        mContext = context;
        mFoursquareModels = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mFoursquareModels.size();
    }

    @Override
    public Object getItem(int i) {
        return mFoursquareModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.foursquare_result_list_item, viewGroup, false);

            holder = new ViewHolder();
            holder.iconImageView = (ImageView) view.findViewById(R.id.foursquare_icon);
            holder.nameTextView = (TextView) view.findViewById(R.id.foursquare_name);
            holder.addressTextView = (TextView) view.findViewById(R.id.foursquare_address);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ImageView iconImageView = holder.iconImageView;
        TextView nameTextView = holder.nameTextView;
        TextView addressTextView = holder.addressTextView;

        FoursquareModel model = (FoursquareModel) getItem(i);

        iconImageView.setImageResource(R.drawable.category_icon);
        nameTextView.setText(model.getName());
        addressTextView.setText(model.getFullAddress());

        return view;
    }

    private static class ViewHolder {
        public ImageView iconImageView;
        public TextView nameTextView;
        public TextView addressTextView;
    }
}
