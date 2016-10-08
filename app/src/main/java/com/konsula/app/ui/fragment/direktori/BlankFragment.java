package com.konsula.app.ui.fragment.direktori;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konsula.app.R;

/**
 * Created by konsula on 12/2/2015.
 */
public class BlankFragment extends Fragment {

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_blank, container, false);
        return view;
    }
}
