package com.decoder.led.chromeled;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorLogFragment extends Fragment {
    View myView;


    public ErrorLogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.error_log_layout, container, false);

        ((MainActivity) getActivity()).setActionBarTitle("Error Log");
        return myView;
    }

}
