package com.konsula.app.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.konsula.app.R;

/**
 * Created by Konsula on 29/02/2016.
 */
public class SplashFragment extends Fragment {

    final static String LAYOUT_ID = "layoutid";
    private Button btnLogin;

    public static SplashFragment newInstance(int layoutId) {
        SplashFragment pane = new SplashFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(getArguments().getInt(LAYOUT_ID, -1), container, false);

        return rootView;
    }
}

