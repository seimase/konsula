package com.konsula.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.MultiSearchModel;

/**
 * Created by SamuelSonny on 29-Jan-16.
 */
public class MultiSearchResultAdapter extends ArrayAdapter<MultiSearchModel.Result> {

    public MultiSearchResultAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String currentLanguage = getContext().getResources().getConfiguration().locale.getLanguage();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_filter, null);
            holder = new ViewHolder();
            holder.container = (LinearLayout) convertView.findViewById(R.id.spesialisasi_item_search_filter_container);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.name_item_search_filter_text_view);
            holder.kategoriFilterTextView = (TextView) convertView.findViewById(R.id.kategori_item_search_filter_text_view);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.nameTextView.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? getItem(position).english_name : getItem(position).bahasa_name);
        holder.kategoriFilterTextView.setText(getItem(position).category);
        return convertView;
    }

    private class ViewHolder {
        TextView nameTextView, kategoriFilterTextView;
        LinearLayout container;
    }
}
