package com.konsula.app.ui.fragment.direktori;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.konsula.app.R;

/**
 * Created by Konsula on 15/03/2016.
 */
public class AboutUsFargment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.activity_about_us, container, false);

        return view;
    }
}
