package com.konsula.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.BankListModel;
import com.konsula.app.service.model.MembershipSubModel;
import com.konsula.app.ui.activity.membership.SummaryMembershipActivity;

import java.util.List;

/**
 * Created by Konsula on 26/04/2016.
 */
public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.ViewHolder> {
    public int mSelectedItem = -1;
    private List<BankListModel.Result> items;
    private int itemLayout;
    private Context mContext;

    public interface OnBankClicked {
        public void OnBankClicked(int bankid);
    }

    private OnBankClicked listener;

    public BankListAdapter(Context context, List<BankListModel.Result> items, int itemLayout, OnBankClicked listener) {
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
//        holder.radioButtonbank.setChecked(position == mSelectedItem);
        final BankListModel.Result item = items.get(position);
//        holder.textViewbankname.setText(item.bank_name);
//        holder.textViewbankname.setVisibility(View.INVISIBLE);
//        holder.item_row.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.OnBankClicked(item.bank_id);
//            }
//        });
//        holder.radioButtonbank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked)listener.OnBankClicked(item.bank_id);
//            }
//
//
//        });
        holder.radioButtonbank.setChecked(false);
        holder.radioButtonbank.setChecked(position == mSelectedItem);
        AppController.getInstance().displayImagePicasso(mContext,item.image, holder.imageViewBank);
        holder.itemView.setTag(item);
        holder.item = items.get(position);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewbankname;
        public RadioButton radioButtonbank;
        public LinearLayout item_row;
        public ImageView imageViewBank;
        BankListModel.Result item;
        public ViewHolder(View itemView) {
            super(itemView);
            item_row = (LinearLayout) itemView.findViewById(R.id.item_row);
            textViewbankname = (TextView) itemView.findViewById(R.id.item_bank_name);
            radioButtonbank = (RadioButton) itemView.findViewById(R.id.radio_bank);
            imageViewBank = (ImageView) itemView.findViewById(R.id.image_bank);
            /*View.OnTouchListener clickListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mSelectedItem = getAdapterPosition();
                    notifyItemRangeChanged(0, items.size());
                    return false;
                }
            };*/

            item_row.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    radioButtonbank.setChecked(true);
                    notifyDataSetChanged();
                    //mSelectedItem = getAdapterPosition();
                    listener.OnBankClicked(item.bank_id);
                }
            });

            radioButtonbank.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    radioButtonbank.setChecked(true);
                    notifyDataSetChanged();
                    //mSelectedItem = getAdapterPosition();

                    listener.OnBankClicked(item.bank_id);
                }
            });

            /*item_row.setOnTouchListener(clickListener);
            radioButtonbank.setOnTouchListener(clickListener);*/
        }
    }
}