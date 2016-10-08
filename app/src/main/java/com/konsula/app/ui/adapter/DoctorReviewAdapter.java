package com.konsula.app.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.DoctorReviewModel;

import java.util.List;

/**
 * Created by hiantohendry on 2/3/16.
 */
public class DoctorReviewAdapter extends RecyclerView.Adapter<DoctorReviewAdapter.ViewHolder> {

    private List<DoctorReviewModel.ReviewList> items;
    private int itemLayout;


    public DoctorReviewAdapter(List<DoctorReviewModel.ReviewList> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_general, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String textmengunjungi = holder.context.getResources().getString(R.string.text_mengunjungi);
        DoctorReviewModel.ReviewList item = items.get(position);
        holder.dateTextView.setText(items.get(position).date_label);
       // holder.nameTextView.setText(items.get(position).fullname);
        holder.nameTextView.setText(items.get(position).fullname + " "+ textmengunjungi + " "+ items.get(position).practice_name);
        holder.messageTextView.setText(items.get(position).feedback);
        holder.ratingBar.setRating(Float.parseFloat(items.get(position).total_point_overall));
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView nameTextView;
        public TextView messageTextView;
        public RatingBar ratingBar;
        public Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            dateTextView = (TextView) itemView.findViewById(R.id.item_review_date_text_view);
            nameTextView = (TextView) itemView.findViewById(R.id.item_review_name_text_view);
            messageTextView = (TextView) itemView.findViewById(R.id.item_review_message_text_view);
            ratingBar = (RatingBar) itemView.findViewById(R.id.item_result_detail_profile_total_rating);
        }
    }
}