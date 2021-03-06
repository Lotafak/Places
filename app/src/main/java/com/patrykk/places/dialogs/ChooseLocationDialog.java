package com.patrykk.places.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.patrykk.places.activities.MainActivity;
import com.patrykk.places.R;
import com.patrykk.places.constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Location dialog fragment class
 * Accessible for user when logging in (and redirecting to {@link MainActivity} class)
 * or from apps options menu
 */
public class ChooseLocationDialog extends android.support.v4.app.DialogFragment implements AdapterView.OnItemClickListener {

    // List with location keys
    ArrayList<String> mItemsKeys;

    // List with location values
    ArrayList<String> mItemsValues;

    ListView mListView;

    /**
     * Default non-parametric constructor required by DialogFragment base class
     */
    public ChooseLocationDialog() {
    }

    /**
     * Filling items list with options depending on weather user is logged into FB or not
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemsKeys = new ArrayList<>(Arrays.asList(Constants.LOCATION_DEVICE, Constants.LOCATION_GPS, Constants.LOCATION_FACEBOOK));
        mItemsValues = new ArrayList<>(Arrays.asList("Last device location", "GPS Location", "Facebook location"));
    }

    /**
     * Creating view for locationList
     * Inflating view from layout file
     * Setting dialog fragment title
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_dialog, container);
        mListView = (ListView) view.findViewById(R.id.list);
        getDialog().setTitle(getString(R.string.location_dialog_title));
        return view;
    }

    /**
     * Injecting data to ListView and setting click handler
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                mItemsValues));
        mListView.setOnItemClickListener(this);
    }

    /**
     * Destroy dialog fragment and sends users choice back to MainActivity
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        // If user logged in with Google account using facebook location is disabled
        if(AccessToken.getCurrentAccessToken() == null && i == Constants.FACEBOOK_LOCATION_LIST_POSITION){
            Toast.makeText(getActivity(), "In order to use Facebook location log in with Facebook account", Toast.LENGTH_LONG).show();
        }else{
            dismiss();
            this.mListener.onLocationChosen(mItemsKeys.get(i));
        }
    }

    /**
     * Interface implemented by MainActivity to recieve users input from dialog
     * onLocationChosen event fires when user clicks on location source from list
     */
    public interface OnLocationChosenListener {
        void onLocationChosen(String locationType);
    }

    private OnLocationChosenListener mListener;

    /**
     * Method checks if MainActivity implements callback interface for getting data from dialog
     *
     * @param context should always be MainActivity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // If activity implements interface assign it to listener
            this.mListener = (OnLocationChosenListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCompleteListener interface");
        }
    }
}