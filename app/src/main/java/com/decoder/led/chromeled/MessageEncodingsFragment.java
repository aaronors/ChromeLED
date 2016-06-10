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
public class MessageEncodingsFragment extends Fragment {
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.message_encodigs, container, false);

        ((MainActivity) getActivity()).setActionBarTitle("Message Encodings");

        return myView;
    }
}
