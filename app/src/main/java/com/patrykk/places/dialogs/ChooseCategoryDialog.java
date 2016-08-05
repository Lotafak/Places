package com.patrykk.places.dialogs;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.patrykk.places.R;

/**
 * Dialog for choosing category to filter venues.
 * Accessible for user with first enter the application and by clicking option menu "CHOOSE LOCATION"
 * button
 */
public class ChooseCategoryDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    // List with category keys
    String[] mItemsKeys;

    // List with category values
    String[] mItemsValues;

    ListView mListView;

    private OnCategoryChosenListener mListener;

    /**
     * Default non-parametric constructor required by DialogFragment base class
     */
    public ChooseCategoryDialog() {
    }

    /**
     * Gets data from resources
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemsKeys = getContext().getResources().getStringArray(R.array.category_keys);
        mItemsValues = getContext().getResources().getStringArray(R.array.category_values);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_dialog, container, false);

        mListView = (ListView) view.findViewById(R.id.list);

        // Set dialogs title
        getDialog().setTitle(R.string.choose_category_dialog_title);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set adapter with categories values
        mListView.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1,
                mItemsValues));
        mListView.setOnItemClickListener(this);
    }

    /**
     * Close the dialog and fires {@link OnCategoryChosenListener} onCategoryChosen method with
     * category key as parameter.
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        dismiss();
        this.mListener.onCategoryChosen(mItemsKeys[i]);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // If activity implements interface assign it to listener
            this.mListener = (OnCategoryChosenListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCategoryChosenListener interface");
        }
    }

    /**
     * Interface for communicating between calling activity and {@link ChooseCategoryDialog}.
     * Sends in parameter chosen category key
     */
    public interface OnCategoryChosenListener {
        void onCategoryChosen(String category);
    }
}
