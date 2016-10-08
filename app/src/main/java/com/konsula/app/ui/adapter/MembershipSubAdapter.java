package com.konsula.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.MembershipSubModel;
import com.konsula.app.ui.activity.membership.SummaryMembershipActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Konsula on 05/04/2016.
 */
public class MembershipSubAdapter extends RecyclerView.Adapter<MembershipSubAdapter.ViewHolder> {

    private List<MembershipSubModel.Subplan> items;
    private MembershipSubModel.Results results;
    private int itemLayout;
    private Context mContext;

    public MembershipSubAdapter(Context context, List<MembershipSubModel.Subplan> items, MembershipSubModel.Results results1, int itemLayout) {
        mContext = context;
        results = results1;
        this.items = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MembershipSubModel.Subplan item = items.get(position);
        String description = "";
        String month = mContext.getResources().getString(R.string.text_hari);
        String month2 = mContext.getResources().getString(R.string.text_hari2);
        String current = mContext.getResources().getConfiguration().locale.getLanguage();
        if (current.equals("en") && items.get(position).month > 1) {
            month = month + "s";
        }
//        holder.textViewmonth.setText(Integer.toString(items.get(position).month) + " " + month);
        if (items.get(position).active_day < 2){
            holder.textViewmonth.setText(Integer.toString(items.get(position).active_day) + " " + month);
        }else{
            holder.textViewmonth.setText(Integer.toString(items.get(position).active_day) + " " + month2);
        }
        
        /*if (items.get(position).charge == items.get(position).gimmick) {
            holder.tvcharge.setVisibility(View.INVISIBLE);
            holder.tvpermonth.setVisibility(View.INVISIBLE);
        } else if (items.get(position).charge < items.get(position).gimmick) {
            holder.tvcharge.setBackground(mContext.getResources().getDrawable(R.drawable.cross_red_line));
            holder.tvgimmick.setTextColor(mContext.getResources().getColor(R.color.red_deep));
        }else if (items.get(position).charge > items.get(position).gimmick){
            holder.tvgimmick.setVisibility(View.GONE);
        }*/

        if (items.get(position).charge == items.get(position).gimmick) {
            holder.tvcharge.setVisibility(View.INVISIBLE);
            holder.tvpermonth.setVisibility(View.INVISIBLE);
        }
        holder.tvcharge.setBackground(mContext.getResources().getDrawable(R.drawable.cross_red_line));
        holder.tvgimmick.setTextColor(mContext.getResources().getColor(R.color.red_deep));

        holder.tvgimmick.setText(AppController.getInstance().getDefaultPriceFormat(items.get(position).currency, Double.parseDouble(Integer.toString(items.get(position).charge))));
        holder.tvcharge.setText(AppController.getInstance().getDefaultPriceFormat(items.get(position).currency, Double.parseDouble(Integer.toString(items.get(position).gimmick))));

        holder.tvpermonth.setVisibility(View.GONE);
        //holder.tvgimmick.setText(AppController.getInstance().getDefaultPriceFormat(items.get(position).currency, Double.parseDouble(Integer.toString(items.get(position).charge))));
        int harga = items.get(position).charge / items.get(position).active_day;
        //int harga = items.get(position).charge;
        //holder.tvpermonth.setText(AppController.getInstance().getDefaultPriceFormat(items.get(position).currency, Double.parseDouble(Integer.toString(harga))) + "/" + mContext.getResources().getString(R.string.text_hari));
        for (int i = 0; i < items.get(position).description.size(); i++) {
            if (i == 0) {
                description = "-";
            }
            description = description + items.get(position).description.get(i) + "\n -";
        }
        holder.btnitemmembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject props = new JSONObject();
                try {
                    props.put("SOURCE", "plus");
                    ((AppController) mContext.getApplicationContext()).setMixpanel("Membership Type", props);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(mContext, SummaryMembershipActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(SummaryMembershipActivity.plan_id, results.plan_id);
                intent.putExtra(SummaryMembershipActivity.subplan_id, item.subplan_id);
                mContext.startActivity(intent);

            }
        });
        holder.tvdescription.setText(description);
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewmonth;
        public TextView tvcharge;
        public TextView tvgimmick;
        public TextView tvdescription;
        public TextView tvpermonth;
        public LinearLayout btnitemmembership;

        public ViewHolder(View itemView) {
            super(itemView);
            btnitemmembership = (LinearLayout) itemView.findViewById(R.id.btnitemmembership);
            textViewmonth = (TextView) itemView.findViewById(R.id.tvlama);
            tvcharge = (TextView) itemView.findViewById(R.id.tvcharge);
            tvgimmick = (TextView) itemView.findViewById(R.id.tvgimmick);
            tvdescription = (TextView) itemView.findViewById(R.id.tvdescription);
            tvpermonth = (TextView) itemView.findViewById(R.id.tvchargepermonth);
        }
    }
}