package com.konsula.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.AvailablePaymentMethodModel;

/**
 * Created by SamuelSonny on 04-May-16.
 */
public class AvailablePaymentMethodAdapter extends ArrayAdapter<AvailablePaymentMethodModel.Result>{
    private int selectedIndex;
    public AvailablePaymentMethodAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_estore_payment_selection, null);
            holder = new ViewHolder();
            holder.radioPaymentSelection = (RadioButton) convertView.findViewById(R.id.radio_payment_selection);
            holder.imgPaymentSelection = (ImageView) convertView.findViewById(R.id.img_payment_selection);
            holder.txtPaymentSelection = (TextView) convertView.findViewById(R.id.txt_payment_selection);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        AvailablePaymentMethodModel.Result item = getItem(position);
        holder.radioPaymentSelection.setChecked(selectedIndex == position);
        holder.txtPaymentSelection.setText(item.method_name);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private class ViewHolder{
        RadioButton radioPaymentSelection;
        ImageView imgPaymentSelection;
        TextView txtPaymentSelection;
    }
}
