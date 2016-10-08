package com.konsula.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.LocationSearchModel;

/**
 * Created by Fadli on 29-Jan-16.
 */
public class LocationSearchResultAdapter extends ArrayAdapter<LocationSearchModel.Result> {

    public LocationSearchResultAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_filter, null);
            holder = new ViewHolder();
            holder.container = (LinearLayout) convertView.findViewById(R.id.spesialisasi_item_search_filter_container);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.name_item_search_filter_text_view);
            holder.countryFilter = (TextView) convertView.findViewById(R.id.kategori_item_search_filter_text_view);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.nameTextView.setText(getItem(position).location_name);
        holder.countryFilter.setText(getItem(position).location_type);
        return convertView;
    }

    private class ViewHolder {
        TextView nameTextView, countryFilter;
        LinearLayout container;
    }
}
