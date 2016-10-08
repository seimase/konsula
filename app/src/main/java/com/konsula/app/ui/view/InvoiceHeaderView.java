package com.konsula.app.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;

/**
 * Created by Konsula on 01/06/2016.
 */
public class InvoiceHeaderView extends LinearLayout {
    private EditText txtPromoCode;
    private TextView btnPromoCode;
    private LinearLayout viewPromoCodeCancel;
    private LinearLayout viewPromoCode;

    public InvoiceHeaderView(Context context) {
        super(context);
        init();
    }

    public InvoiceHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InvoiceHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InvoiceHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_hrader_promocode, this);
        txtPromoCode = (EditText) view.findViewById(R.id.txt_promo_code);
        btnPromoCode = (TextView) view.findViewById(R.id.btn_promo_code);
        viewPromoCodeCancel = (LinearLayout) view.findViewById(R.id.layout_promocode_cancel);
        viewPromoCode = (LinearLayout) view.findViewById(R.id.layout_promocode);
    }

//    public void setGrandTotal(double grandTotal) {
//        txtGrandTotal.setText(AppController.getInstance().getDefaultPriceFormat("IDR ", grandTotal));
//    }
//
//    public void setConvenienceFee(double convenienceFee) {
//        txtConvenienceFee.setText("- " + AppController.getInstance().getDefaultPriceFormat("IDR ", Math.abs(convenienceFee)));
//    }
}

