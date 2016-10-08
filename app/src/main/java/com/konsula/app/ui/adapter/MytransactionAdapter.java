package com.konsula.app.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.BankListModel;
import com.konsula.app.service.model.HistoryTransactionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Konsula on 26/04/2016.
 */
public class MytransactionAdapter extends RecyclerView.Adapter<MytransactionAdapter.ViewHolder> {
    //
    private List<HistoryTransactionModel.Result> items;
    private int itemLayout;
    private Context mContext;
    int position;
    boolean bTimeStart = false;
    long lCounter;

    private List<ViewHolder> lstHolders;
    private Handler mHandler = new Handler();
    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (ViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining_New();
                }
            }
        }
    };


    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
                lCounter += 1000;
            }
        }, 1000, 1000);
    }

    public interface OnTransactionClicked {
        public void onTransactionClick(String next_action, String payment_uuid, String item_category);
    }

    public interface OnCancelTransactionClicked {
        public void onCancelTransactionClick(String item_category, String next_action, String payment_status_label, String payment_uuid);
    }

    private OnTransactionClicked listener;
    private OnCancelTransactionClicked cancellistener;


    public MytransactionAdapter(Context context, List<HistoryTransactionModel.Result> items, int itemLayout, OnTransactionClicked listener, OnCancelTransactionClicked cancellistener) {
        mContext = context;
        this.items = items;
        this.itemLayout = itemLayout;
        this.listener = listener;
        this.cancellistener = cancellistener;
        lstHolders = new ArrayList<>();
        lCounter = 1000;
        startUpdateTimer();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        ViewHolder holder = new ViewHolder(v);
/*
        final HistoryTransactionModel.Result item = items.get(position);
        holder.setData(items.get(position));
        String currentLanguage = mContext.getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        holder.TransactionName.setText(item.item_name);
        holder.TransactionDesc.setText(item.item_description);
        switch (item.payment_status_label) {
            case "unpaid":
                if (item.item_category.equals("teledoctor")) {
                    holder.viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_xxl));
                    holder.transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                } else {
                    holder.viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.red_deep));
                    holder.transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_close));
                }
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.pink));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.pink));
                break;
            case "verifying":
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.grey_dark));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.grey_dark));
                break;
            case "paid":
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.green_xxl));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.green_xxl));
                break;
            case "expired":
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.red_deep));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.red_deep));
                break;
            case "cancelled":
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.pink));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.pink));
                break;
        }
        holder.TransactionDate.setText(((AppController) mContext).dateJoin(item.created_timestamp, currentLanguage).toUpperCase());
*/

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        final HistoryTransactionModel.Result item = items.get(position);
        holder.setData(items.get(position));
        String currentLanguage = mContext.getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        holder.TransactionName.setText(item.item_name);
        holder.TransactionDesc.setText(item.item_description);
        switch (item.payment_status_label) {
            case "unpaid":
                if (item.item_category.equals("teledoctor")) {
                    holder.viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_xxl));
                    holder.transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                } else {
                    holder.viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.red_deep));
                    holder.transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_close));
                }
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.pink));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.pink));
                break;
            case "verifying":
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.grey_dark));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.grey_dark));
                break;
            case "paid":
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.green_xxl));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.green_xxl));
                break;
            case "expired":
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.red_deep));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.red_deep));
                break;
            case "cancelled":
                holder.indicator.setBackgroundColor(mContext.getResources().getColor(R.color.pink));
                holder.TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.pink));
                break;
        }
        holder.TransactionDate.setText(((AppController) mContext).dateJoin(item.created_timestamp, currentLanguage).toUpperCase());
        //holder.TransactionTime.setText("");

        holder.itemView.setTag(item);
        synchronized (lstHolders) {
            lstHolders.add(holder);
        }
        if (item.expiry_countdown != 0) {
          /*  synchronized (lstHolders) {
                lstHolders.add(holder);
            }*/
            //holder.updateTimeRemaining(item);
        }
        holder.itemtransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTransactionClick(item.next_action, item.payment_uuid, item.item_category);
            }
        });
        holder.TransactionStatus.setText(item.payment_status_label.toUpperCase());
        holder.TransactionAmount.setText(((AppController) mContext).getDefaultPriceFormat(item.currency, (double) item.total_payment).toUpperCase());
        holder.transactionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancellistener.onCancelTransactionClick(item.item_category, item.next_action, item.payment_status_label, item.payment_uuid);
            }
        });

        //position++;

    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView TransactionName;
        public TextView TransactionDesc;
        public TextView TransactionDate;
        public TextView TransactionAmount;
        public TextView TransactionStatus;
        public TextView TransactionTime;
        public LinearLayout indicator;
        public LinearLayout itemtransaction;
        public LinearLayout viewcancel;
        public ImageButton transactionCancel;

         public HistoryTransactionModel.Result hItem ;

        public void setData(HistoryTransactionModel.Result hItems) {
            hItem = hItems;
            updateTimeRemaining_New();
        }

        public void updateTimeRemaining_New() {
            /*new CountDownTimer(hItem.expiry_countdown, 1000) {

                public void onTick(long millisUntilFinished) {
                    TransactionTime.setText((AppController.getInstance().formatTime(millisUntilFinished, ":", ":", "", true)));
                    bTimeStart = false;
                }

                public void onFinish() {
                    TransactionTime.setText("");
                    viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_dark));
                    transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                    indicator.setBackgroundColor(mContext.getResources().getColor(R.color.red_deep));
                    TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.red_deep));
                    TransactionStatus.setText("expired".toUpperCase());
                    hItem.next_action = "expired";
                    hItem.payment_status_label = "expired";
                }

            }.start();*/

            long timeDiff = hItem.expiry_countdown - lCounter;

            if (timeDiff > 0) {
                int seconds = (int) (timeDiff / 1000) % 60;
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
                TransactionTime.setText(String.format("%02d",hours) + ":" + String.format("%02d",minutes) + ":" + String.format("%02d",seconds));
            } else {
                TransactionTime.setText("");
                if (hItem.expiry_countdown != 0){
                    viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_dark));
                    transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                    indicator.setBackgroundColor(mContext.getResources().getColor(R.color.red_deep));
                    TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.red_deep));
                    TransactionStatus.setText("expired".toUpperCase());
                    hItem.next_action = "expired";
                    hItem.payment_status_label = "expired";
                }else{
                    /*TransactionName.setText(hItem.item_name);
                    TransactionDesc.setText(hItem.item_description);

                    */

                    switch (hItem.payment_status_label) {
                        case "unpaid":
                            if (hItem.item_category.equals("teledoctor")) {
                                viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_xxl));
                                transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                            } else {
                                viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.red_deep));
                                transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_close));
                            }
                            indicator.setBackgroundColor(mContext.getResources().getColor(R.color.pink));
                            TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.pink));
                            break;
                        case "verifying":
                            indicator.setBackgroundColor(mContext.getResources().getColor(R.color.grey_dark));
                            TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.grey_dark));
                            transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                            viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_xxl));
                            break;
                        case "paid":
                            indicator.setBackgroundColor(mContext.getResources().getColor(R.color.green_xxl));
                            TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.green_xxl));
                            transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                            viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_xxl));
                            break;
                        case "expired":
                            indicator.setBackgroundColor(mContext.getResources().getColor(R.color.red_deep));
                            TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.red_deep));
                            transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                            viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_xxl));
                            break;
                        case "cancelled":
                            indicator.setBackgroundColor(mContext.getResources().getColor(R.color.pink));
                            TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.pink));
                            transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                            viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_xxl));
                            break;
                    }
                }



            }
        }

        public void updateTimeRemaining(final HistoryTransactionModel.Result item) {
            new CountDownTimer(item.expiry_countdown, 1000) {

                public void onTick(long millisUntilFinished) {
                    TransactionTime.setText((AppController.getInstance().formatTime(millisUntilFinished, ":", ":", "", true)));
                    bTimeStart = false;
                }

                public void onFinish() {
                    TransactionTime.setText("");
                    viewcancel.setBackgroundColor(mContext.getResources().getColor(R.color.grey_dark));
                    transactionCancel.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_eye));
                    indicator.setBackgroundColor(mContext.getResources().getColor(R.color.red_deep));
                    TransactionStatus.setTextColor(mContext.getResources().getColor(R.color.red_deep));
                    TransactionStatus.setText("expired".toUpperCase());
                    item.next_action = "expired";
                    item.payment_status_label = "expired";
                }

            }.start();
        }

        public ViewHolder(View itemView) {
            super(itemView);
            viewcancel = (LinearLayout) itemView.findViewById(R.id.view_cancel_transaction);
            indicator = (LinearLayout) itemView.findViewById(R.id.indicator_transaction);
            TransactionName = (TextView) itemView.findViewById(R.id.item_result_transaction_name);
            TransactionDesc = (TextView) itemView.findViewById(R.id.item_result_transaction_description);
            TransactionDate = (TextView) itemView.findViewById(R.id.item_result_transaction_date);
            TransactionAmount = (TextView) itemView.findViewById(R.id.item_result_transaction_total_amount);
            TransactionStatus = (TextView) itemView.findViewById(R.id.item_result_transaction_status);
            TransactionTime = (TextView) itemView.findViewById(R.id.item_result_total_time);
            itemtransaction = (LinearLayout) itemView.findViewById(R.id.item_myhistorytransaction);
            transactionCancel = (ImageButton) itemView.findViewById(R.id.item_cancle_transaction);
        }
    }


}

