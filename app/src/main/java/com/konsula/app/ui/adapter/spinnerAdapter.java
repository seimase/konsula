package com.konsula.app.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Konsula on 16/02/2016.
 */
public class spinnerAdapter extends ArrayAdapter<String> {

    public spinnerAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        // TODO Auto-generated constructor stub

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ((TextView) v).setTextColor(Color.parseColor("#000000"));

        return v;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View v = super.getDropDownView(position, convertView, parent);
        ((TextView) v).setTextColor(Color.parseColor("#000000"));
        return v;
    }


    @Override
    public int getCount() {

        // TODO Auto-generated method stub
        int count = super.getCount();

        return count > 0 ? count : count;


    }


}