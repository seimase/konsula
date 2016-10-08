package com.konsula.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.InitSearchModel;

/**
 * Created by SamuelSonny on 29-Jan-16.
 */
public class InitSearchDefaultLocationAdapter extends ArrayAdapter<InitSearchModel.LocationDropdown> {

    public InitSearchDefaultLocationAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_filter, null);
            holder = new ViewHolder();
            holder.imagelocation = (ImageView) convertView.findViewById(R.id.kategori_item_search_filter_image);
            holder.container = (LinearLayout) convertView.findViewById(R.id.spesialisasi_item_search_filter_container);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.name_item_search_filter_text_view);
            holder.kategoriFilterTextView = (TextView) convertView.findViewById(R.id.kategori_item_search_filter_text_view);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.nameTextView.setText(getItem(position).location_name);
        holder.kategoriFilterTextView.setText(getItem(position).location_type);
        if (getItem(position).location_name.equals(convertView.getResources().getString(R.string.currentLoc))){
            holder.kategoriFilterTextView.setVisibility(View.GONE);
            holder.imagelocation.setImageResource(R.drawable.ic_location);
        }else{
            holder.imagelocation.setVisibility(View.GONE);
            holder.kategoriFilterTextView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView imagelocation;
        TextView nameTextView, kategoriFilterTextView;
        LinearLayout container;
    }
}
