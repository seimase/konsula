package com.konsula.app.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreProductModel;
import com.konsula.app.service.model.InvoiceModel;

/**
 * Created by SamuelSonny on 16-Apr-16.
 */
public class InvoiceAdapter extends ArrayAdapter<EstoreProductModel.ProductsItem>{
    private int childrenHeight;
    public InvoiceAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_invoice, null);
            holder = new ViewHolder();
            holder.invoiceContainer = (LinearLayout) convertView.findViewById(R.id.invoice_container);
            holder.txtProductPackageName = (TextView) convertView.findViewById(R.id.txt_product_package_name);
            holder.txtProductPackageDescription = (TextView) convertView.findViewById(R.id.txt_product_package_description);
            holder.txtProductPackagePrice = (TextView) convertView.findViewById(R.id.txt_product_package_price);
            holder.txtProductPackagePriceDetail = (TextView) convertView.findViewById(R.id.txt_product_package_price_detail);
            holder.txtProductPackageSubtotal = (TextView) convertView.findViewById(R.id.txt_product_package_subtotal);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        EstoreProductModel.ProductsItem item = getItem(position);

        holder.txtProductPackageName.setText(item.item_name);
        try {
            holder.txtProductPackageDescription.setText(Html.fromHtml(item.description));
        }catch (Exception e){
            holder.txtProductPackageDescription.setText(item.description);
        }
        holder.txtProductPackagePrice.setText(AppController.getInstance().getDefaultPriceFormat("IDR ", (double)item.sell_price));
        String currentLanguage = getContext().getResources().getConfiguration().locale.getLanguage();
        if (item.amount > 1){
            holder.txtProductPackagePriceDetail.setText("(" + item.amount + " " + getContext().getString(R.string.estore_items) + " x " + AppController.getInstance().getDefaultPriceFormat("IDR ", (double)item.sell_price) + ")");
        }else{
            holder.txtProductPackagePriceDetail.setText("(" + item.amount + " " + getContext().getString(R.string.estore_item) + " x " + AppController.getInstance().getDefaultPriceFormat("IDR ", (double)item.sell_price) + ")");
        }

        holder.txtProductPackageSubtotal.setText(AppController.getInstance().getDefaultPriceFormat("IDR ", (double)item.sell_price * item.amount));

        if(position == getCount() - 1){
            holder.invoiceContainer.setBackgroundResource(0);
        }else{
            holder.invoiceContainer.setBackgroundResource(R.drawable.border_bottom_light_gray);
        }
        return convertView;
    }

    private class ViewHolder{
        LinearLayout invoiceContainer;
        TextView txtProductPackageName;
        TextView txtProductPackageDescription;
        TextView txtProductPackagePrice;
        TextView txtProductPackagePriceDetail;
        TextView txtProductPackageSubtotal;
    }
}
