package com.patrykk.places.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.patrykk.places.R;

public class ContinueAsDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener {
    private static final String USERS_NAME = "name";

    private String usersName;

    private OnContinueAsDialogClicked mListener;

    public interface OnContinueAsDialogClicked {
        void continueAsLogged(boolean yesNo);
    }

    public ContinueAsDialog() {
        // Required empty public constructor
    }

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
        View view = inflater.inflate(R.layout.fragment_continue_as, container, false);

        Button mButtonYes = (Button) view.findViewById(R.id.button_yes);
        Button mButtonNo = (Button) view.findViewById(R.id.button_no);

        mButtonYes.setOnClickListener(this);
        mButtonNo.setOnClickListener(this);

        TextView titleView = (TextView) getDialog().findViewById(android.R.id.title);
        titleView.setSingleLine(false);
        String title = "Do you want to continue as " + usersName + " ?";
        titleView.setText(title);

        return view;
    }

    @Override
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.button_yes:
                this.mListener.continueAsLogged(true);
                break;
            case R.id.button_no:
                this.mListener.continueAsLogged(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.mListener = (OnContinueAsDialogClicked) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnContinueAsDialogClicked interface");
        }
    }
}
