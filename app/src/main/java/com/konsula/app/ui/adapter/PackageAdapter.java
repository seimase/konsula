package com.konsula.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreCategoryModel;
import com.konsula.app.service.model.EstoreCategoryTagModel;
import com.konsula.app.service.model.EstorePackageModel;
import com.konsula.app.service.model.PaymentMethodModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konsula on 10/05/2016.
 */
public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {
    private ArrayList<EstorePackageModel> items;

    public PackageAdapter(ArrayList<EstorePackageModel> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_package_filter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final EstorePackageModel item = items.get(position);
        holder.tvPackage.setText(item.tag_name);
        holder.cbPackage.setChecked(item.selected);
        holder.cbPackage.setTag(item);
        holder.cbPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                EstorePackageModel pack = (EstorePackageModel) cb.getTag();

                pack.selected = cb.isChecked();
                item.selected = cb.isChecked();

                cb.setTag(pack);
                notifyItemChanged(position);
            }
        });
        holder.cbPackage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.selected = isChecked;
            }
        });

        holder.item_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout) v;
                CheckBox cb = (CheckBox) ll.getChildAt(0);
                EstorePackageModel pack = (EstorePackageModel) cb.getTag();
                Boolean check;
                if (cb.isChecked()) {
                    check = !cb.isChecked();
                } else {
                    check = !cb.isChecked();
                }
                pack.selected = check;
                item.selected = check;
                cb.setTag(pack);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public ArrayList<EstorePackageModel> getList() { return items; }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPackage;
        public CheckBox cbPackage;
        public LinearLayout item_row;

        public ViewHolder(final View itemView) {
            super(itemView);
            item_row = (LinearLayout) itemView.findViewById(R.id.item_row);
            tvPackage = (TextView) itemView.findViewById(R.id.tvPackage);
            cbPackage = (CheckBox) itemView.findViewById(R.id.cbPackage);
        }
    }
}


