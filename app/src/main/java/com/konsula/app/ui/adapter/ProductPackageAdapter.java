package com.konsula.app.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreProductItemModel;
import com.konsula.app.service.model.EstoreProductModel;
import com.konsula.app.service.model.ProductPackageModel;
import com.konsula.app.ui.activity.estore.EstoreTransactionActivity;

/**
 * Created by SamuelSonny on 16-Apr-16.
 */
public class ProductPackageAdapter extends ArrayAdapter<EstoreProductItemModel.ProductsItem>{
    public ProductPackageAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product_package, null);
            holder = new ViewHolder();
            holder.txtProductPackageName = (TextView) convertView.findViewById(R.id.txt_product_package_name);
            holder.txtProductPackageDescription = (TextView) convertView.findViewById(R.id.txt_product_package_description);
            holder.txtProductPackagePrice = (TextView) convertView.findViewById(R.id.txt_product_package_price);
            holder.btnProductPackageMin = (TextView) convertView.findViewById(R.id.btn_product_package_min);
            holder.txtProductPackageAmount = (TextView) convertView.findViewById(R.id.txt_product_package_amount);
            holder.btnProductPackagePlus = (TextView) convertView.findViewById(R.id.btn_product_package_plus);
            holder.txtProductPackageInfo = (TextView) convertView.findViewById(R.id.txt_product_package_info);
            holder.llProductPackageInfo = (LinearLayout) convertView.findViewById(R.id.ll_product_package_info);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        final EstoreProductItemModel.ProductsItem item = getItem(position);
        holder.txtProductPackageName.setText(item.item_name);
        try {
            holder.txtProductPackageDescription.setText(Html.fromHtml(item.description));
        }catch (Exception e){
            holder.txtProductPackageDescription.setText(item.description);
        }
        setEstimatedCost(holder, item);
        holder.txtProductPackagePrice.setText(AppController.getInstance().getDefaultPriceFormat("IDR ", (double) item.sell_price));
        holder.btnProductPackageMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.amount > 0){
                    item.amount = item.amount - 1;
                }
                setEstimatedCost(holder, item);
            }
        });
        holder.btnProductPackagePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.amount < item.available_quota) {
                    item.amount = item.amount + 1;
                }
                setEstimatedCost(holder, item);
            }
        });
        if (item.available_quota < 1) {
            holder.txtProductPackageInfo.setVisibility(View.VISIBLE);
            holder.llProductPackageInfo.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setEstimatedCost(ViewHolder holder, EstoreProductItemModel.ProductsItem item){
        holder.txtProductPackageAmount.setText(item.amount + "");
        ((EstoreTransactionActivity)getContext()).renderEstimatedCost();
    }

    private class ViewHolder{
        TextView txtProductPackageName;
        TextView txtProductPackageDescription;
        TextView txtProductPackagePrice;
        TextView btnProductPackageMin;
        TextView txtProductPackageAmount;
        TextView btnProductPackagePlus;
        TextView txtProductPackageInfo;
        LinearLayout llProductPackageInfo;
    }
}
