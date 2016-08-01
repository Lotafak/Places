package com.patrykk.places;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// TODO:
public class ContinueAsDialog extends Fragment {
    private static final String USERS_NAME = "name";

    private String usersName;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
         boolean continueAsLogged();
    }

    public ContinueAsDialog() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ContinueAsDialog newInstance(String name) {
        ContinueAsDialog fragment = new ContinueAsDialog();
        Bundle args = new Bundle();
        args.putString(USERS_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usersName = getArguments().getString(USERS_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_continue_as_dialog, container, false);
    }

}
