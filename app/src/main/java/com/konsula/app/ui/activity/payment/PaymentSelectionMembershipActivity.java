package com.konsula.app.ui.activity.payment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DetailMembershipTransactionModel;
import com.konsula.app.service.model.PaymentMethodModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.transaction.MytransactionHistoryActivity;
import com.konsula.app.ui.adapter.PaymentMethodAdapter;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.ui.view.EstorePaymentStepView;
import com.konsula.app.util.RefreshTokenCallback;

import org.json.JSONObject;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 25/04/2016.
 */
public class PaymentSelectionMembershipActivity extends AppCompatActivity {
    private AlertDialog dialog;
    public static String TAG_PAYMENT_UUID = "payment_uuid";
    public static String TAG_FROM_PAYMENT = "payment";
    public static String TAG_SUBCATEGORY = "subcategory";
    private String payment_uuid;
    private String currentLanguage;
    private TextView textViewCharge;
    private TextView textViewCountDown;
    private long totalCountDown;
    private RecyclerView paymentlist;
    private int payment_id = 0;
    private FrameLayout btnnext;
    private LinearLayout layoutloading;
    private Button refresh;
    private LinearLayout l_view, llCountDown;
    private TextView btnSeeDetail;
    private  EstorePaymentStepView paymentStepView;
    private String subcategory;
    private ImageView imgArrow;
    private DetailMembershipTransactionModel.Results data;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subcategory = getIntent().getExtras().getString(TAG_SUBCATEGORY, "");
        if (subcategory.equalsIgnoreCase("estore")) {
            setTheme(R.style.MaterialTheme_Estore);
        } else if (subcategory.equalsIgnoreCase("teledoctor")) {
            setTheme(R.style.MaterialTheme_teledokter);
        } else {
            setTheme(R.style.MaterialTheme_Main);
        }
        setContentView(R.layout.activity_payment_selection);
        init();
    }

    private void init() {
        imgArrow = (ImageView)findViewById(R.id.imgArrow);
        paymentStepView = (EstorePaymentStepView) findViewById(R.id.payment_step_view);
        paymentStepView.setCurrentStep(2);
        paymentStepView.setVisibility(subcategory.equalsIgnoreCase("estore")?View.VISIBLE:View.GONE);
        btnSeeDetail = (TextView) findViewById(R.id.btn_see_detail);
        btnnext = (FrameLayout) findViewById(R.id.btn_next);
        paymentlist = (RecyclerView) findViewById(R.id.bank_list_recycler_view);
        paymentlist.setHasFixedSize(true);
        paymentlist.setClickable(true);
        paymentlist.setLayoutManager(new LinearLayoutManager(this));
        textViewCharge = (TextView) findViewById(R.id.text_membership_charge);
        textViewCountDown = (TextView) findViewById(R.id.text_count_down);
        payment_uuid = getIntent().getStringExtra(TAG_PAYMENT_UUID);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (LinearLayout) findViewById(R.id.l_view);
        llCountDown = (LinearLayout) findViewById(R.id.llCountDown);
        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();
            }
        });
        btnSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeDetail();
            }
        });
        if (subcategory.equalsIgnoreCase("teledoctor")){
            llCountDown.setVisibility(View.GONE);
            imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_green));
        }else if (subcategory.equalsIgnoreCase("estore")) {
            imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_chat));
        }else{
            imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_pembiayaan));
        }

        getData();

    }

    private void getData() {
        getDetailTransaction();
        getPaymentList();
    }

    public void Transactionmembership(View view) {

    }

    public void back(View view) {
        doDialogexit();
    }

    public void closeDetail(View view) {
        dialog.dismiss();
    }

	public int getThemeColor () {
        TypedValue value = new TypedValue ();
        PaymentSelectionMembershipActivity.this.getTheme ().resolveAttribute (R.attr.colorPrimary, value, true);
        return value.data;
    }

    private void seeDetail() {
        final int color = getThemeColor();
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, color);
        data = ((AppController) getApplication()).getSessionManager().getDetailMembershipTransaction();
        View invoiceDetail = LayoutInflater.from(this).inflate(R.layout.view_transaction_invoice_detail, null, true);
        TextView btnSeeDetailClose = (TextView) invoiceDetail.findViewById(R.id.button_seedetail_close);
        LinearLayout layoutitemdetail = (LinearLayout) invoiceDetail.findViewById(R.id.layout_item_transaction_detail);
        if (data.data.items != null) {
            for (int i = 0; i < data.data.items.size(); i++) {
                View viewItem = LayoutInflater.from(this).inflate(R.layout.item_transaction_invoice_detail, null, false);
                TextView textViewitemname = (TextView) viewItem.findViewById(R.id.detail_item_name);
                TextView textViewitemprice = (TextView) viewItem.findViewById(R.id.detail_item_price);
                textViewitemname.setText(data.data.items.get(i).order_name+" "+data.data.items.get(i).order_name_detail);
                textViewitemprice.setText(((AppController) getApplication()).getDefaultPriceFormat(data.data.currency, (double) data.data.items.get(i).price));
                layoutitemdetail.addView(viewItem);
            }
        }
        TextView textViewconfenence_fee = (TextView) invoiceDetail.findViewById(R.id.detail_item_confenence_fee);
        TextView textViewpromo_code = (TextView) invoiceDetail.findViewById(R.id.detail_item_promo_code);
        TextView textViewtotal_payment = (TextView) invoiceDetail.findViewById(R.id.detail_item_total_payment);
        LinearLayout linearLayoutpromocode = (LinearLayout) invoiceDetail.findViewById(R.id.view_promo);
        if (data.data.promotion_fee == 0) linearLayoutpromocode.setVisibility(View.GONE);
        textViewconfenence_fee.setText(((AppController) getApplication()).getDefaultPriceFormat(data.data.currency, (double) data.data.convenience_fee));
        textViewpromo_code.setText(((AppController) getApplication()).getDefaultPriceFormat(data.data.currency + "-", (double) data.data.promotion_fee));
        textViewtotal_payment.setText(((AppController) getApplication()).getDefaultPriceFormat(data.data.currency, (double) data.data.total_payment));
        btnSeeDetailClose.setBackgroundColor(getResources().getColor(R.color.green_xxl));
        btnSeeDetailClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(invoiceDetail);
        builder.setTitle(R.string.text_paymentsummary);
        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // TODO Auto-generated method stub
                Dialog d = ((Dialog) dialog);

                int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                View divider = d.findViewById(dividerId);
                if (divider != null) {
                    divider.setBackgroundColor(color);
                }
            }
        });
        dialog.show();
    }

    public void _continue(View view) {
        if (payment_id == 0) {
            ((AppController) getApplication()).doDialog(PaymentSelectionMembershipActivity.this, getResources().getString(R.string.text_warning_payment_method));
        } else if (payment_id == 1) {
            JSONObject props = new JSONObject();
            try{
                props.put("SOURCE", "Bank Transfer");
                ((AppController) getApplication()).setMixpanel("Payment Type");
                if (data.data.subcategory.equals("estore")){
                    for (int i = 0; i < data.data.items.size(); i++) {
                        Bundle params = new Bundle();
                        params.putString("content_ids", String.valueOf(data.data.items.get(i).detail.item_id));
                        params.putString("content_type", "product");
                        params.putString("value", data.data.items.get(i).order_name_detail);
                        params.putString("currency", data.data.items.get(i).currency);
                        AppController.getInstance().setFacebookEvent("AddPaymentInfo",params);
                    }
                }
            }catch (Exception e){

            }


            Intent intent = new Intent(this, PaymentBankTransferSelectionActivity.class);
            intent.putExtra(PaymentBankTransferSelectionActivity.TAG_COUNT_DOWN, totalCountDown);
            startActivity(intent);
        } else if(payment_id == 2) {
            JSONObject props = new JSONObject();
            try{
                props.put("SOURCE", "Credit Card");
                ((AppController) getApplication()).setMixpanel("Payment Type");
            }catch (Exception e){

            }
            Intent intent = new Intent(this, PaymentCreditCardMembershipActivity.class);
            intent.putExtra(PaymentCreditCardMembershipActivity.TAG_COUNT_DOWN, totalCountDown);
            startActivity(intent);
        }
    }

    private void getDetailTransaction() {
        NetworkManager.getNetworkService(getApplication())
                .getDetailTransaction(((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, payment_uuid, new Callback<DetailMembershipTransactionModel>() {

                    @Override
                    public void success(DetailMembershipTransactionModel detailMembershipTransactionModel, Response response) {
                        ((AppController) getApplication()).getSessionManager().setDetailMembershipTransaction(detailMembershipTransactionModel.results);
                        textViewCharge.setText(((AppController) getApplication()).getDefaultPriceFormat(detailMembershipTransactionModel.results.data.currency + " ", (double) detailMembershipTransactionModel.results.data.total_payment));
                        if (detailMembershipTransactionModel.results.data.expiry_countdown != 0) {


                            new CountDownTimer(detailMembershipTransactionModel.results.data.expiry_countdown, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    textViewCountDown.setText(((AppController) getApplication()).formatTime(millisUntilFinished, ":", ":", "", true));
                                    totalCountDown = millisUntilFinished;
                                }

                                public void onFinish() {
                                    doDialogexpired();
                                }

                            }.start();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(PaymentSelectionMembershipActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getPaymentList() {
//        btnnext.setClickable(false);
        NetworkManager.getNetworkService(getApplication())
                .getPaymentMethod(((AppController) getApplication())
                        .getSessionManager().getToken(), new Callback<PaymentMethodModel>() {
                    @Override
                    public void success(PaymentMethodModel paymentMethodModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).
                                isTokenValid(paymentMethodModel.messages, response, PaymentSelectionMembershipActivity.this, new RefreshTokenCallback() {
                                    @Override
                                    public void onRefreshTokenComplete() {
                                        getPaymentList();
                                    }


                                });
                        if (isTokenValid) {
                            btnnext.setClickable(true);
                            layoutloading.setVisibility(View.GONE);
                            l_view.setVisibility(View.VISIBLE);
                            btnnext.setVisibility(View.VISIBLE);
                            paymentlist.setAdapter(new PaymentMethodAdapter(getApplicationContext(), paymentMethodModel.results, R.layout.item_bank_membership, new PaymentMethodAdapter.OnBankClicked() {
                                @Override
                                public void OnBankClicked(int bankid) {
                                    payment_id = bankid;
                                }

                            }));
                            paymentlist.setLayoutManager(new LinearLayoutManager(PaymentSelectionMembershipActivity.this));
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        layoutloading.setVisibility(View.GONE);
                        refresh.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void doDialogexit() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent intent = new Intent(PaymentSelectionMembershipActivity.this, MytransactionHistoryActivity.class);
                    intent.putExtra(TAG_FROM_PAYMENT,true);
                    startActivity(intent);
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PaymentSelectionMembershipActivity.this);
        builder.setMessage(getResources().getString(R.string.text_membership_exit)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener).show();
    }

    private void doDialogexpired(){
        textViewCountDown.setText("00:00:00");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent intent = new Intent(PaymentSelectionMembershipActivity.this, MytransactionHistoryActivity.class);
                    intent.putExtra(TAG_FROM_PAYMENT,true);
                    startActivity(intent);
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PaymentSelectionMembershipActivity.this);
        builder.setMessage(getResources().getString(R.string.text_dialog_expired)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener).setCancelable(false).show();

    }

    @Override
    public void onBackPressed() {
        doDialogexit();
    }
}