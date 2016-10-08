package com.konsula.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.AppointmentListModel;

import java.text.SimpleDateFormat;

/**
 * Created by SamuelSonny on 13-Feb-16.
 */
public class AppointmentListAdapter extends ArrayAdapter<AppointmentListModel.Result>{

    public AppointmentListAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_appointment_list, null);
            holder = new ViewHolder();
            holder.timestampTextView = (TextView) convertView.findViewById(R.id.item_appointment_timestamp_text_view);
            holder.doctorNameTextView = (TextView) convertView.findViewById(R.id.item_appointment_doctor_name_text_view);
            holder.practiceNameTextView = (TextView) convertView.findViewById(R.id.item_appointment_practice_name_text_view);
            holder.reasonTextView = (TextView) convertView.findViewById(R.id.item_appointment_reason_text_view);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        AppointmentListModel.Result item = getItem(position);
        try {
            holder.timestampTextView.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(item.schedule_date)) + " " + item.hour_start.substring(0, 5) + " " + item.time_abbreviation);
        } catch (Exception e) {

        }
        holder.doctorNameTextView.setText(item.doctor_name);
        holder.practiceNameTextView.setText(item.practice_name);
        holder.reasonTextView.setText(item.reason);
        return convertView;
    }

    private class ViewHolder{
        TextView timestampTextView;
        TextView doctorNameTextView;
        TextView practiceNameTextView;
        TextView reasonTextView;
    }
}
