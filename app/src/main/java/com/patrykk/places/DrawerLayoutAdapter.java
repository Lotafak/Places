package com.patrykk.places;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;


public class DrawerLayoutAdapter extends BaseAdapter {
    private ArrayList<FoursquareModel> mFoursquareModels;
    private Context mContext;

    public DrawerLayoutAdapter(Context context, ArrayList<FoursquareModel> list) {
        mContext = context;
        mFoursquareModels = list;
    }

    @Override
    public int getCount() {
        return mFoursquareModels.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
