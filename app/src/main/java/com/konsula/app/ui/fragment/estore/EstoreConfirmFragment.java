package com.konsula.app.ui.fragment.estore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konsula.app.R;

/**
 * Created by SamuelSonny on 04-May-16.
 */
public class EstoreConfirmFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estore_confirm, null);
        return view;
    }
}
