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
 * Created by Konsula on 22/06/2016.
 */
public class VoucherContactAdapter extends RecyclerView.Adapter<VoucherContactAdapter.ViewHolder> {

    private List<String> items;
    private int itemLayout;

    public VoucherContactAdapter(List<String> items, int itemLayout) {
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
        String item = items.get(position);
        holder.dcontact_textView.setText(item);
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dcontact_textView;

        public ViewHolder(View itemView) {
            super(itemView);
            dcontact_textView = (TextView) itemView.findViewById(R.id.tv_estore_contact);
        }
    }
}
