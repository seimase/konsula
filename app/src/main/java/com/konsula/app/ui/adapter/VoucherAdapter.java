package com.konsula.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.CommonModel;
import com.konsula.app.service.model.MyVoucherModel;
import com.konsula.app.service.model.PaymentMethodModel;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.ui.activity.direktori.KlinikProfileActivity;
import com.konsula.app.ui.activity.estore.EstoreProductActivity;

import java.util.List;

/**
 * Created by Konsula on 23/05/2016.
 */
public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {

    private int itemLayout;
    private Context mContext;
    List<MyVoucherModel.Valid> mResources;


    public VoucherAdapter(Context context, List<MyVoucherModel.Valid> resource, int itemLayout) {
        mContext = context;
        this.itemLayout = itemLayout;
        mResources = resource;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MyVoucherModel.Valid item = mResources.get(position);
        AppController.getInstance().displayImage(mContext,item.photo, holder.imageView_photo);
        holder.textView_order_name.setText(item.order_name);
        holder.textView_practice_name.setText(item.practice_name);
        holder.textView_voucher_code.setText(item.voucher_code);
        holder.textView_valid_until.setText(AppController.getInstance().dateBirth(item.valid_until));
        holder.textView_price.setText(AppController.getInstance().getDefaultPriceFormat(item.currency,Double.parseDouble(item.price)));
        holder.textView_practice_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, KlinikProfileActivity.class);
                intent.putExtra("praticeUri", item.practice_identity_uri);
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a =new Intent(mContext,EstoreProductActivity.class);
                a.putExtra(EstoreProductActivity.IDENTITY_URI,item.product_identity_uri);
                mContext.startActivity(a);
            }
        });

        if (item.practice_contact ==null){
            holder.layout_contact.setVisibility(View.GONE);
        }else {
            holder.view_contact.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            holder.view_contact.setHasFixedSize(true);
            holder.view_contact.setAdapter(new VoucherContactAdapter(item.practice_contact, R.layout.item_estore_contact_detail));
        }
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return mResources.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_order_name;
        public TextView textView_practice_name;
        public TextView textView_price;
        public TextView textView_valid_until;
        public TextView textView_voucher_code;
        public ImageView imageView_photo;
        public LinearLayout layout_contact;
        public RecyclerView view_contact;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_order_name = (TextView) itemView.findViewById(R.id.item_result_order_name);
            textView_practice_name = (TextView) itemView.findViewById(R.id.item_result_practice_name);
            textView_price = (TextView) itemView.findViewById(R.id.item_result_price);
            textView_valid_until = (TextView) itemView.findViewById(R.id.item_result_valid_until);
            textView_voucher_code = (TextView) itemView.findViewById(R.id.item_result_voucher_code);
            imageView_photo = (ImageView) itemView.findViewById(R.id.item_result_photo);
            layout_contact =(LinearLayout) itemView.findViewById(R.id.item_voucher_content_contact);
            view_contact =(RecyclerView) itemView.findViewById(R.id.item_voucher_contact_detail_layout);
        }
    }
}