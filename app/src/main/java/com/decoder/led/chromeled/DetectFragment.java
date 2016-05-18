package com.decoder.led.chromeled;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jason on 5/14/2016.
 */
public class DetectFragment extends Fragment {
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.detect_layout, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Detect");
        return myView;
    }


}
