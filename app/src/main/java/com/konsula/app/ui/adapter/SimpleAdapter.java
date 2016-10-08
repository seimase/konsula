package com.konsula.app.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.SimpleModel;

/**
 * Created by SamuelSonny on 16-Feb-16.
 */
public class SimpleAdapter extends ArrayAdapter<SimpleModel>{

    public SimpleAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
        ((TextView)convertView.findViewById(android.R.id.text1)).setText(getItem(position).value);
        ((TextView)convertView.findViewById(android.R.id.text1)).setTextColor(Color.parseColor("#000000"));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
