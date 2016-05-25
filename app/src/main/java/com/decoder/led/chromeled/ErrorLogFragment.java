package com.decoder.led.chromeled;


import android.os.Bundle;
import android.content.Context;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.lang.Object;
/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorLogFragment extends Fragment {
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.error_log_layout, container, false);

        ((MainActivity) getActivity()).setActionBarTitle("Error Log");

        return myView;
    }
}
