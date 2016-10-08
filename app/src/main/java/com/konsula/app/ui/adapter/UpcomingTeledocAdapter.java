package com.konsula.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.UpcomingTeledocModel;
import com.konsula.app.ui.activity.teledokter.TeledocDetailActivity;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;

import java.util.List;

/**
 * Created by Konsula on 14/06/2016.
 */
public class UpcomingTeledocAdapter extends RecyclerView.Adapter<UpcomingTeledocAdapter.ViewHolder> {

    private List<UpcomingTeledocModel.Datum> items;
    private int itemLayout;
    private Context mContext;

    public interface OnTransactionClicked {
        public void onTransactionClick(String uuid);
    }

    private OnTransactionClicked listener;

    public UpcomingTeledocAdapter(Context context, List<UpcomingTeledocModel.Datum> items, int itemLayout, OnTransactionClicked listener) {
        mContext = context;
        this.items = items;
        this.itemLayout = itemLayout;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UpcomingTeledocModel.Datum item = items.get(position);
        holder.textViewStatus.setText(item.tele_status);
        holder.textViewname.setText(item.doctor_name);
        holder.textViewdate.setText(((AppController) AppController.getAppContext()).setDatefull(item.schedule));
        holder.textViewreason.setText(item.reason);
        holder.textViewStatus.setTextColor(item.tele_status.equals("Confirmed") ? mContext.getResources().getColor(R.color.green_xxl) : mContext.getResources().getColor(R.color.black));
        holder.btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTransactionClick(item.tele_uuid);
            }
        });
        holder.btnSeedetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppController) AppController.getAppContext()).getSessionManager().setupcomingteledocdata(item);
                Intent intent = new Intent(mContext, TeledocDetailActivity.class);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewStatus;
        public TextView textViewname;
        public TextView textViewdate;
        public TextView textViewreason;
        public ImageButton btncancel;
        public TextView btnSeedetail;

        public ViewHolder(View itemView) {
            super(itemView);
            btncancel = (ImageButton) itemView.findViewById(R.id.item_cancle_upcoming_);
            textViewStatus = (TextView) itemView.findViewById(R.id.item_result_upcoming_teledoc_status);
            textViewname = (TextView) itemView.findViewById(R.id.item_result_upcomingteledoc_detail_name);
            textViewdate = (TextView) itemView.findViewById(R.id.item_result_detail_upcomingteledoc_date);
            textViewreason = (TextView) itemView.findViewById(R.id.item_result_detail_upcoming_teledoc);
            btnSeedetail = (TextView) itemView.findViewById(R.id.item_seedetail);
        }
    }

}
