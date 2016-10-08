package com.konsula.app.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.DoctorModel;

import java.util.List;

/**
 * Created by Konsula on 20/06/2016.
 */
public class DoctorScheduleAdapter extends RecyclerView.Adapter<DoctorScheduleAdapter.ViewHolder> {

    private List<DoctorModel.SummaryScheduleArr> items;
    private int itemLayout;

    public DoctorScheduleAdapter(List<DoctorModel.SummaryScheduleArr> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DoctorModel.SummaryScheduleArr item = items.get(position);
        holder.day_textView.setText(items.get(position).day);
        holder.time_textView.setText(items.get(position).hour + " WIB");
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView day_textView;
        public TextView time_textView;

        public ViewHolder(View itemView) {
            super(itemView);
            day_textView = (TextView) itemView.findViewById(R.id.doctor_location_schedule_day_textview);
            time_textView = (TextView) itemView.findViewById(R.id.doctor_location_schedule_time_textview);
        }
    }
}
