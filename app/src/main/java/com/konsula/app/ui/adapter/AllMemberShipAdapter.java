package com.konsula.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AllPlanModel;
import com.konsula.app.service.model.DoctorReviewModel;
import com.konsula.app.ui.activity.direktori.DoctorProfileActivity;
import com.konsula.app.ui.activity.membership.MembershipSubActivity;
import com.konsula.app.ui.activity.membership.RenewActivity;

import java.util.List;

/**
 * Created by Konsula on 05/04/2016.
 */
public class AllMemberShipAdapter extends RecyclerView.Adapter<AllMemberShipAdapter.ViewHolder> {

    private List<AllPlanModel.Body> items;
    private int itemLayout;
    private Context mContext;


    public AllMemberShipAdapter(Context context, List<AllPlanModel.Body> items, int itemLayout) {
        mContext = context;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AllPlanModel.Body item = items.get(position);
        holder.text_facility.setText(item.label);
        if (item.plan.plus.equals("y")){
            holder.image_plus.setBackgroundResource(R.drawable.ic_check_membership);
        }else if (item.plan.plus.equals("-")){
            holder.image_plus.setBackgroundResource(R.drawable.ic_strip);
        }else {
            holder.image_plus.setVisibility(View.GONE);
            holder.text_plus.setText(item.plan.plus);
        }

        if (item.plan.basic.equals("y")){
            holder.image_basic.setBackgroundResource(R.drawable.ic_check_membership);
        }else if (item.plan.basic.equals("-")){
            holder.image_basic.setBackgroundResource(R.drawable.ic_strip);
        }else {
            holder.image_basic.setVisibility(View.GONE);
            holder.text_basic.setText(item.plan.basic);
        }
        holder.itemView.setTag(item);

    }

    @Override
    public int getItemCount() {
        return items.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_facility;
        public ImageView image_plus;
        public TextView text_plus;
        public TextView text_basic;
        public Context context;
        public ImageView image_basic;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            text_facility = (TextView) itemView.findViewById(R.id.text_facility);
            text_plus = (TextView) itemView.findViewById(R.id.text_plus);
            image_plus = (ImageView) itemView.findViewById(R.id.image_plus);
            image_basic = (ImageView) itemView.findViewById(R.id.image_basic);
            text_basic = (TextView) itemView.findViewById(R.id.text_basic);
        }
    }
}