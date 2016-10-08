package com.konsula.app.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;

/**
 * Created by SamuelSonny on 16-Apr-16.
 */
public class InvoiceFooterView extends LinearLayout{
    private TextView txtGrandTotal;
    private TextView txtConvenienceFee;
    private TextView txtPromoCode;
    private TextView textViewPromocodeInfo;
    private LinearLayout llPromoCode;

    public InvoiceFooterView(Context context) {
        super(context);
        init();
    }

    public InvoiceFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InvoiceFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InvoiceFooterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_invoice_footer, this);
        txtGrandTotal = (TextView) view.findViewById(R.id.txt_grand_total);
        txtConvenienceFee = (TextView) view.findViewById(R.id.txt_convenience_fee);
        txtPromoCode = (TextView) view.findViewById(R.id.view_totalpromo);
        llPromoCode = (LinearLayout) view.findViewById(R.id.view_promocode);
        textViewPromocodeInfo = (TextView) findViewById(R.id.etore_promocode_info);
    }

    public void setGrandTotal(double grandTotal){
        txtGrandTotal.setText(AppController.getInstance().getDefaultPriceFormat("IDR ", grandTotal));
    }

    public void setConvenienceFee(double convenienceFee){
        txtConvenienceFee.setText("- " + AppController.getInstance().getDefaultPriceFormat("IDR ", Math.abs(convenienceFee)));
    }
    public void setPromoCodeInfo(String promo_type,String promocode,Double nominal) {
        if (promo_type.equalsIgnoreCase("amount")) {
            textViewPromocodeInfo.setText(getResources().getString(R.string.payment_promocode_info).replace("{promo_code}", promocode.toUpperCase()).replace("{price}", AppController.getInstance().getDefaultPriceFormat("IDR", nominal)));
        } else if (promo_type.equalsIgnoreCase("percentage")) {
            textViewPromocodeInfo.setText(getResources().getString(R.string.payment_promocode_info).replace("{promo_code}", promocode.toUpperCase()).replace("{price}", Math.round(nominal) + " %"));
         }
    }

    public void setPromoCode(double promocode) {
        txtPromoCode.setText("- " + AppController.getInstance().getDefaultPriceFormat("IDR ", Math.abs(promocode)));
    }

    public void showPromoCode(boolean show) {
        if (show)
            llPromoCode.setVisibility(View.VISIBLE);
        else
            llPromoCode.setVisibility(View.INVISIBLE);
    }

}
