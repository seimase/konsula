package com.konsula.app.ui.activity.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.DetailSummaryTransactionModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.view.EstorePaymentStepView;

import org.apache.commons.lang3.text.WordUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 25/04/2016.
 */
public class PaymentConfirmActivity extends AppCompatActivity {
    private TextView textViewInvoicenumber;
    private TextView textViewConfenenceFee;
    private TextView textViewTotalPrice;
    private TextView textViewStatus;
    private TextView textViewThanks;
    private TextView textViewuntuk;
    private TextView textViewketerangan;
    private TextView textViewket;
    private TextView textViewketPromocode;
    private TextView textViewtotalpromocode;
    private LinearLayout l_loading;
    private RelativeLayout view_loading;
    LinearLayout layoutitemdetail;
    private Button refresh;
    public static String payment_uuid = "payment_uuid";
    public static String TAG_FROM_PAYMENT = "from_payment";
    public static String TAG_SUBCATEGORY = "subcategory";
    private String uuid;
    private Boolean from_payment;
    private String subcategory;
    private EstorePaymentStepView paymentStepView;
    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subcategory = getIntent().getExtras().getString(TAG_SUBCATEGORY, "");
        if (subcategory.equalsIgnoreCase("estore")) {
            ((AppController) getApplication()).setMixpanel("Do e-Store Transaction Successfully");
            setTheme(R.style.MaterialTheme_Estore);
        } else if (subcategory.equalsIgnoreCase("teledoctor")) {
            ((AppController) getApplication()).setMixpanel("Do Transaction Successfully");
            ((AppController) getApplication()).setFacebookEvent("CompleteTeledoc");
            setTheme(R.style.MaterialTheme_teledokter);
        } else {
            ((AppController) getApplication()).setMixpanel("Upgrade Membership Successfully");
            setTheme(R.style.MaterialTheme_Main);
        }
        setContentView(R.layout.activity_payment_confirm);
        init();
        uuid = getIntent().getExtras().getString(payment_uuid, "");
        from_payment = getIntent().getExtras().getBoolean(TAG_FROM_PAYMENT);
        getData(uuid);

    }

    public void close(View view) {
        if (from_payment){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        }else {
            finish();
        }
    }

    private void init() {
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        paymentStepView = (EstorePaymentStepView) findViewById(R.id.payment_step_view);
        paymentStepView.setCurrentStep(3);
        paymentStepView.setVisibility(subcategory.equalsIgnoreCase("estore")?View.VISIBLE:View.GONE);
        layoutitemdetail = (LinearLayout) findViewById(R.id.layout_item_transaction_detail);
        textViewInvoicenumber = (TextView) findViewById(R.id.text_invoicenumber);
        textViewConfenenceFee = (TextView) findViewById(R.id.text_confenence_fee);
        textViewTotalPrice = (TextView) findViewById(R.id.text_total_price);
        textViewStatus =(TextView) findViewById(R.id.text_status);
        textViewThanks =(TextView) findViewById(R.id.text_thanks);
        textViewuntuk =(TextView) findViewById(R.id.text_untuk);
        textViewketerangan =(TextView) findViewById(R.id.text_keterangan);
        textViewket =(TextView) findViewById(R.id.ket_membership);
        l_loading =(LinearLayout) findViewById(R.id.l_loading);
        view_loading =(RelativeLayout) findViewById(R.id.view_loading);
        textViewketPromocode =(TextView) findViewById(R.id.membership_text_detail_promocode);
        textViewtotalpromocode =(TextView) findViewById(R.id.detail_item_promo_code);
        refresh =(Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setVisibility(View.GONE);
                l_loading.setVisibility(View.VISIBLE);
                getData(uuid);
            }
        });
    }

    private void getData(String uuid) {
        NetworkManager.getNetworkService(getApplication())
                .getPaymentTransaction(((AppController) getApplication())
                        .getSessionManager().getToken(),currentLanguage, uuid, new Callback<DetailSummaryTransactionModel>() {
                    @Override
                    public void success(DetailSummaryTransactionModel historyTransactionModel, Response response) {
                        if (historyTransactionModel.results != null){
                            view_loading.setVisibility(View.GONE);
                            l_loading.setVisibility(View.GONE);
                            textViewInvoicenumber.setText(String.valueOf(historyTransactionModel.results.invoice_number));
                            textViewConfenenceFee.setText(((AppController)getApplication()).getDefaultPriceFormat(historyTransactionModel.results.currency,(double)historyTransactionModel.results.convenience_fee));
                            textViewTotalPrice.setText(((AppController)getApplication()).getDefaultPriceFormat(historyTransactionModel.results.currency,(double)historyTransactionModel.results.total_payment));
                            textViewStatus.setText(historyTransactionModel.results.payment_status.toUpperCase());
                            if (historyTransactionModel.results.items != null) {
                                for (int i = 0; i < historyTransactionModel.results.items.size(); i++) {
                                    View viewItem = LayoutInflater.from(PaymentConfirmActivity.this).inflate(R.layout.item_transaction_invoice_detail, null, false);
                                    TextView textViewitemname = (TextView) viewItem.findViewById(R.id.detail_item_name);
                                    TextView textViewitemprice = (TextView) viewItem.findViewById(R.id.detail_item_price);
                                    textViewitemname.setText(historyTransactionModel.results.items.get(i).order_name+" "+historyTransactionModel.results.items.get(i).order_name_detail);
                                    textViewitemprice.setText(((AppController) getApplication()).getDefaultPriceFormat(historyTransactionModel.results.currency, (double) historyTransactionModel.results.items.get(i).price));
                                    layoutitemdetail.addView(viewItem);
                                }
                            }
                            if (historyTransactionModel.results.payment_status_label.equals("expired")){
                                textViewThanks.setText(getResources().getString(R.string.text_maaf));
                                textViewuntuk.setVisibility(View.GONE);
                                textViewketerangan.setText(WordUtils.capitalize(getResources().getString(R.string.text_expired_ket)));
                                textViewket.setVisibility(View.GONE);
                                textViewStatus.setTextColor(getResources().getColor(R.color.red_deep));
                                textViewStatus.setText("EXPIRED");
                            }
                            if (historyTransactionModel.results.payment_status_label.equals("unpaid")){
                                textViewket.setVisibility(View.GONE);
                                textViewStatus.setTextColor(getResources().getColor(R.color.red_deep));
                            }else if(historyTransactionModel.results.payment_status_label.equals("verifying")){
                                textViewStatus.setTextColor(getResources().getColor(R.color.grey_dark));
                            }else if(historyTransactionModel.results.payment_status_label.equals("paid")){
                                textViewket.setVisibility(View.GONE);
                                textViewStatus.setTextColor(getResources().getColor(R.color.green_xxl));
                            }else if(historyTransactionModel.results.payment_status_label.equals("cancelled")){
                                textViewket.setVisibility(View.GONE);
                                textViewStatus.setTextColor(getResources().getColor(R.color.red_deep));
                            }
                            if (historyTransactionModel.results.promotion_fee==0){
                                textViewketPromocode.setVisibility(View.GONE);
                                textViewtotalpromocode.setVisibility(View.GONE);
                            }else {
                                textViewketPromocode.setText(getResources().getString(R.string.estore_promotion_code) + " ( " + historyTransactionModel.results.promo_code + " )");
                                textViewtotalpromocode.setText(((AppController) getApplication()).getDefaultPriceFormat(historyTransactionModel.results.currency+" -", (double) historyTransactionModel.results.promotion_fee));
                            }
                        }else {
                            ((AppController) getApplication()).doDialog(PaymentConfirmActivity.this,historyTransactionModel.messages.response_text);
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        refresh.setVisibility(View.VISIBLE);
                        l_loading.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        close(null);
    }
}
