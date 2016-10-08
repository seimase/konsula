package com.konsula.app.ui.activity.payment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DetailMembershipTransactionModel;
import com.konsula.app.service.model.PaymentCreditCardModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.direktori.PrivacyPolicyActivity;
import com.konsula.app.ui.activity.direktori.TermAndConditionActivity;
import com.konsula.app.ui.activity.transaction.MytransactionHistoryActivity;
import com.konsula.app.ui.adapter.spinnerAdapter;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.ui.view.CustomWebView;
import com.konsula.app.ui.view.EstorePaymentStepView;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

import id.co.veritrans.android.api.VTInterface.ITokenCallback;
import id.co.veritrans.android.api.VTModel.VTCardDetails;
import id.co.veritrans.android.api.VTModel.VTToken;
import id.co.veritrans.android.api.VTUtil.VTConfig;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import id.co.veritrans.android.api.VTDirect;

public class PaymentCreditCardMembershipActivity extends AppCompatActivity {

    public static String TAG_FROM_PAYMENT = "payment";
    public static final String TAG_COUNT_DOWN = "count_down";
    private AlertDialog dialog;
    private String currentLanguage;
    private TextView textViewCharge;
    private TextView textViewCountDown;
    private String payment_uuid, card_number, card_holdername, exp_month, exp_year, curr, signature;
    private long totalCountDown;
    private FrameLayout btnnext;
    private LinearLayout layoutloading;
    private LinearLayout l_view, llCountDown;
    private Button refresh;
    private TextView btnSeeDetail;
    private DetailMembershipTransactionModel.Results data;
    private TextView btnsyaratdanketentuan, btnkebijakanprivasi, textViewTos1, textViewtos2, textViewtos3;
    private CheckBox cb1;
    private EditText etName, etCard, etCvv;
    VTDirect vtDirect;
    private AppController appController;
    private Spinner spmonth, spyear;
    private List<String> monthlist, yearlist;
    private boolean secure = false;
    private  EstorePaymentStepView paymentStepView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = ((AppController) getApplication()).getSessionManager().getDetailMembershipTransaction();
        if (data.data.subcategory.equalsIgnoreCase("estore")) {
            setTheme(R.style.MaterialTheme_Estore);
        } else if (data.data.subcategory.equalsIgnoreCase("teledoctor")) {
            setTheme(R.style.MaterialTheme_teledokter);
        } else {
            setTheme(R.style.MaterialTheme_Main);
        }

        setContentView(R.layout.activity_payment_credit_card);
        init();
    }

    public int getThemeColor () {
        TypedValue value = new TypedValue ();
        PaymentCreditCardMembershipActivity.this.getTheme ().resolveAttribute (R.attr.colorPrimary, value, true);
        return value.data;
    }

    private void init() {
        appController = new AppController();
        btnnext = (FrameLayout) findViewById(R.id.btn_next);
        paymentStepView = (EstorePaymentStepView) findViewById(R.id.payment_step_view);
        paymentStepView.setCurrentStep(2);
        paymentStepView.setVisibility(data.data.subcategory.equalsIgnoreCase("estore")?View.VISIBLE:View.GONE);
        textViewCharge = (TextView) findViewById(R.id.text_membership_charge);
        textViewCharge.setText(((AppController) getApplication()).getDefaultPriceFormat(data.data.currency, (double) data.data.total_payment));
        totalCountDown = getIntent().getLongExtra(TAG_COUNT_DOWN, 0);
        if (totalCountDown != 0) {

            new CountDownTimer(totalCountDown, 1000) {

                public void onTick(long millisUntilFinished) {
                    textViewCountDown.setText(((AppController) getApplication()).formatTime(millisUntilFinished, ":", ":", "", true));
                    totalCountDown = millisUntilFinished;
                }

                public void onFinish() {
                    doDialogexpired();
                }

            }.start();
        }
        textViewCountDown = (TextView) findViewById(R.id.text_count_down);

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (LinearLayout) findViewById(R.id.l_view);
        llCountDown = (LinearLayout) findViewById(R.id.llCountDown);
        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnSeeDetail = (TextView) findViewById(R.id.btn_see_detail);
        btnSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeDetail();
            }
        });


        cb1 = (CheckBox) findViewById(R.id.cb1);
        btnsyaratdanketentuan = (TextView) findViewById(R.id.text_syaratketentuan);
        btnkebijakanprivasi = (TextView) findViewById(R.id.text_kebijakanprivasi);
        textViewTos1 = (TextView) findViewById(R.id.text_tos1);
        textViewtos2 = (TextView) findViewById(R.id.text_tos2);
        textViewtos3 = (TextView) findViewById(R.id.text_tos3);
        textViewtos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    cb1.setChecked(false);
                } else {
                    cb1.setChecked(true);
                }
            }
        });
        textViewTos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    cb1.setChecked(false);
                } else {
                    cb1.setChecked(true);
                }
            }
        });
        textViewtos3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    cb1.setChecked(false);
                } else {
                    cb1.setChecked(true);
                }
            }
        });
        btnsyaratdanketentuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentCreditCardMembershipActivity.this, TermAndConditionActivity.class);
                startActivity(intent);
            }
        });
        btnkebijakanprivasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentCreditCardMembershipActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        etName = (EditText) findViewById(R.id.etName);
        etCard = (EditText) findViewById(R.id.etCardNumber);
        etCvv = (EditText) findViewById(R.id.etCVV);
        spmonth = (Spinner) findViewById(R.id.sp_month);
        spyear = (Spinner) findViewById(R.id.sp_year);

        monthlist = new ArrayList<String>();
        monthlist.add("Expire Month");
        for (int i = 1; i < 13; i++) {
            monthlist.add(String.format("%02d", i));
        }
        spinnerAdapter monthAdapter = new spinnerAdapter(getApplicationContext(), R.layout.item_spinner_golongandarah);
        monthAdapter.addAll(monthlist);
        monthAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spmonth.setAdapter(monthAdapter);

        yearlist = new ArrayList<String>();
        yearlist.add("Expire Year");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 6; i++) {
            yearlist.add(String.valueOf(year + i));
        }
        spinnerAdapter yearAdapter = new spinnerAdapter(getApplicationContext(), R.layout.item_spinner_golongandarah);
        yearAdapter.addAll(yearlist);
        yearAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spyear.setAdapter(yearAdapter);

        layoutloading.setVisibility(View.GONE);
        l_view.setVisibility(View.VISIBLE);
        btnnext.setVisibility(View.VISIBLE);
        if (data.data.subcategory.equalsIgnoreCase("teledoctor"))
            llCountDown.setVisibility(View.GONE);
    }

    public void back(View view) {
        finish();
    }

    public void closeDetail(View view) {
        dialog.setCancelable(true);
        dialog.dismiss();
        btnnext.setClickable(true);
        secure = false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void doDialogexpired() {
        textViewCountDown.setText("00:00:00");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent intent = new Intent(PaymentCreditCardMembershipActivity.this, MytransactionHistoryActivity.class);
                    intent.putExtra(TAG_FROM_PAYMENT, true);
                    startActivity(intent);
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PaymentCreditCardMembershipActivity.this);
        builder.setMessage(getResources().getString(R.string.text_dialog_expired)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener).setCancelable(false).show();

    }

    private void seeDetail() {
        final int color = getThemeColor();
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, color);
        DetailMembershipTransactionModel.Results data = ((AppController) getApplication()).getSessionManager().getDetailMembershipTransaction();
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
        dopaymentcc();
    }

    private void dopaymentcc() {
        if (validate()) {
            if (cb1.isChecked()) {
                btnnext.setClickable(false);
                //Initialize Credit Card
                VTConfig.VT_IsProduction = AppConstant.VERITRANS_IS_PRODUCTION;
                VTConfig.CLIENT_KEY = AppConstant.getVeritransClientKey();
                vtDirect = new VTDirect();
                VTCardDetails cardDetails = new VTCardDetails();

                cardDetails.setCard_number(etCard.getText().toString().trim());
                cardDetails.setCard_cvv(etCvv.getText().toString().trim());
                cardDetails.setCard_exp_month(Integer.parseInt(spmonth.getSelectedItem().toString().trim()));
                cardDetails.setCard_exp_year(Integer.parseInt(spyear.getSelectedItem().toString().trim()));
                cardDetails.setSecure(true);
                cardDetails.setGross_amount(String.valueOf(data.data.total_payment));

                vtDirect.setCard_details(cardDetails);

                payment_uuid = data.data.payment_uuid;
                card_number = vtDirect.getCard_details().getCard_number();
                card_holdername = etName.getText().toString().trim();
                exp_month = String.format("%02d", vtDirect.getCard_details().getCard_exp_month());
                exp_year = String.valueOf(vtDirect.getCard_details().getCard_exp_year());
                curr = data.data.currency;
                signature = data.data.signature_key;

                final ProgressDialog loadingDialog = AppController.createProgressDialog(PaymentCreditCardMembershipActivity.this);
                loadingDialog.setMessage(getResources().getString(R.string.dialog_title_please_wait));
                loadingDialog.setCancelable(false);
                loadingDialog.show();
                vtDirect.getToken(new ITokenCallback() {
                    @Override
                    public void onSuccess(VTToken token) {
                        btnnext.setClickable(true);
                        loadingDialog.setCancelable(true);
                        loadingDialog.cancel();
                        if (token.getRedirect_url() != null) {
                            // 3ds on
                            Log.d("VtLog", token.getToken_id());

                            CustomWebView webView = new CustomWebView(PaymentCreditCardMembershipActivity.this);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                        case MotionEvent.ACTION_UP:
                                            if (!v.hasFocus()) {
                                                v.requestFocus();
                                            }
                                            break;
                                    }
                                    return false;
                                }
                            });
                            webView.setWebChromeClient(new WebChromeClient());
                            webView.setWebViewClient(new VtWebViewClient(token.getToken_id(), String.valueOf(data.data.total_payment)));
                            webView.loadUrl(token.getRedirect_url());

                            CustomAlertDialogBuilder alertBuilder = new CustomAlertDialogBuilder(PaymentCreditCardMembershipActivity.this, getResources().getColor(R.color.colorPrimaryDark));
                            alertBuilder.setView(webView);
                            alertBuilder.setTitle("3D Secure");
                            dialog = alertBuilder.create();
                            webView.requestFocus(View.FOCUS_DOWN);
                            dialog.setCancelable(false);
                            dialog.show();
                            secure = false;
                        } else {
                            //3ds off
                            Log.e("KONSULA", token.getToken_id());
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        btnnext.setClickable(true);
                        loadingDialog.setCancelable(true);
                        loadingDialog.cancel();
                        appController.doDialog(PaymentCreditCardMembershipActivity.this, getResources().getString(R.string.no_connection));
                    }
                });
            } else {
                appController.doDialog(PaymentCreditCardMembershipActivity.this, getResources().getString(R.string.text_peringatan_tos));
            }
        }
    }

    private class VtWebViewClient extends WebViewClient {

        String token;
        String price;

        public VtWebViewClient(String token, String price) {
            this.token = token;
            this.price = price;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            btnnext.setClickable(true);

            Log.d("VtLog", url);
            final ProgressDialog dialogupload = AppController.createProgressDialog(PaymentCreditCardMembershipActivity.this);
            dialogupload.setCancelable(false);
            dialogupload.show();
            if (url.startsWith(AppConstant.getPaymentApiUrl() + "/callback/")) {
                if (secure) {
                    // process 3ds on
                    String token_id = ((AppController) getApplication()).getSessionManager().getToken();
                    NetworkManager.getNetworkService(getApplication())
                            .doPaymentCC(token_id, payment_uuid, token, price, card_number,
                                    card_holdername, exp_month, exp_year, curr, signature, new Callback<PaymentCreditCardModel>() {
                                        @Override
                                        public void success(PaymentCreditCardModel paymentCreditCardModel, Response response) {
                                            dialog.setCancelable(true);
                                            dialog.dismiss();
                                            boolean isTokenValid = ((AppController) getApplication()).
                                                    isTokenValid(paymentCreditCardModel.messages, response, PaymentCreditCardMembershipActivity.this, new RefreshTokenCallback() {
                                                        @Override
                                                        public void onRefreshTokenComplete() {
                                                            dialogupload.setCancelable(true);
                                                            dialogupload.dismiss();
                                                            dopaymentcc();
                                                        }


                                                    });
                                            if (isTokenValid) {
                                                dialogupload.setCancelable(true);
                                                dialogupload.dismiss();
                                                btnnext.setClickable(true);
                                                if (paymentCreditCardModel.results.status) {
                                                    Intent intent = new Intent(PaymentCreditCardMembershipActivity.this, PaymentConfirmActivity.class);
                                                    intent.putExtra(PaymentConfirmActivity.payment_uuid, paymentCreditCardModel.results.payment_uuid);
                                                    intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, true);
                                                    startActivity(intent);
                                                } else {
                                                    ((AppController) getApplication()).doDialog(PaymentCreditCardMembershipActivity.this, paymentCreditCardModel.messages.response_text);
                                                }
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            dialog.setCancelable(true);
                                            dialog.dismiss();
                                            dialogupload.setCancelable(true);
                                            dialogupload.dismiss();
                                            Toast.makeText(PaymentCreditCardMembershipActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                    dialogupload.setCancelable(true);
                    dialogupload.dismiss();
                } else {
                    btnnext.setClickable(false);
                    dialog.setCancelable(true);
                    dialog.dismiss();
                    //process 3ds off

                    VTConfig.VT_IsProduction = AppConstant.VERITRANS_IS_PRODUCTION;
                    VTConfig.CLIENT_KEY = AppConstant.getVeritransClientKey();
                    vtDirect = new VTDirect();
                    VTCardDetails cardDetails = new VTCardDetails();

                    cardDetails.setCard_number(etCard.getText().toString().trim());
                    cardDetails.setCard_cvv(etCvv.getText().toString().trim());
                    cardDetails.setCard_exp_month(Integer.parseInt(spmonth.getSelectedItem().toString().trim()));
                    cardDetails.setCard_exp_year(Integer.parseInt(spyear.getSelectedItem().toString().trim()));
                    cardDetails.setSecure(false);
                    cardDetails.setGross_amount(String.valueOf(data.data.total_payment));
                    vtDirect.setCard_details(cardDetails);

                    payment_uuid = data.data.payment_uuid;
                    card_number = vtDirect.getCard_details().getCard_number();
                    card_holdername = etName.getText().toString().trim();
                    exp_month = String.format("%02d", vtDirect.getCard_details().getCard_exp_month());
                    exp_year = String.valueOf(vtDirect.getCard_details().getCard_exp_year());
                    curr = data.data.currency;
                    signature = data.data.signature_key;

                    final ProgressDialog loadingDialog = AppController.createProgressDialog(PaymentCreditCardMembershipActivity.this);
                    loadingDialog.setMessage(getResources().getString(R.string.dialog_title_please_wait));
                    loadingDialog.setCancelable(false);
                    loadingDialog.show();
                    vtDirect.getToken(new ITokenCallback() {
                        @Override
                        public void onSuccess(VTToken token) {
                            btnnext.setClickable(true);
                            loadingDialog.setCancelable(true);
                            loadingDialog.cancel();
                            if (token.getRedirect_url() == null) {
                                Log.d("VtLog", token.getToken_id());
                                NetworkManager.getNetworkService(getApplication())
                                        .doPaymentCC(((AppController) getApplication())
                                                        .getSessionManager().getToken(), payment_uuid, token.getToken_id(), price, card_number,
                                                card_holdername, exp_month, exp_year, curr, signature, new Callback<PaymentCreditCardModel>() {
                                                    @Override
                                                    public void success(PaymentCreditCardModel paymentCreditCardModel, Response response) {
                                                        dialog.setCancelable(true);
                                                        dialog.dismiss();
                                                        boolean isTokenValid = ((AppController) getApplication()).
                                                                isTokenValid(paymentCreditCardModel.messages, response, PaymentCreditCardMembershipActivity.this, new RefreshTokenCallback() {
                                                                    @Override
                                                                    public void onRefreshTokenComplete() {
                                                                        dialogupload.setCancelable(true);
                                                                        dialogupload.dismiss();
                                                                        dopaymentcc();
                                                                    }


                                                                });
                                                        if (isTokenValid) {
                                                            dialogupload.setCancelable(true);
                                                            dialogupload.dismiss();
                                                            btnnext.setClickable(true);
                                                            if (paymentCreditCardModel.results.status) {
                                                                Intent intent = new Intent(PaymentCreditCardMembershipActivity.this, PaymentConfirmActivity.class);
                                                                intent.putExtra(PaymentConfirmActivity.payment_uuid, paymentCreditCardModel.results.payment_uuid);
                                                                intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, true);
                                                                startActivity(intent);
                                                            } else {
                                                                ((AppController) getApplication()).doDialog(PaymentCreditCardMembershipActivity.this, paymentCreditCardModel.messages.response_text);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void failure(RetrofitError error) {
                                                        dialog.setCancelable(true);
                                                        dialog.dismiss();
                                                        dialogupload.setCancelable(true);
                                                        dialogupload.dismiss();
                                                        Toast.makeText(PaymentCreditCardMembershipActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            btnnext.setClickable(true);
                            loadingDialog.setCancelable(true);
                            loadingDialog.cancel();
                            dialogupload.setCancelable(true);
                            dialogupload.dismiss();
                            appController.doDialog(PaymentCreditCardMembershipActivity.this, e.getMessage());
                        }
                    });
                }

            } else if (url.startsWith(AppConstant.getPaymentApiUrl() + "/redirect/")) {
                /* Do nothing */
                if (dialogupload != null) {
                    dialogupload.setCancelable(true);
                    dialogupload.dismiss();
                }
            } else if (url.contains("3dsecure")) {
                secure = true;
                if (dialogupload != null) {
                    dialogupload.setCancelable(true);
                    dialogupload.dismiss();
                }
            } else {
                if (dialogupload != null) {
                    dialogupload.setCancelable(true);
                    dialogupload.dismiss();
                }
            }
        }
    }

    private boolean validate() {
        // check blank
        if (etName.getText().toString().isEmpty() || etCard.getText().toString().isEmpty() || etCvv.getText().toString().isEmpty()
                || spmonth.getSelectedItemPosition() == 0 || spyear.getSelectedItemPosition() == 0) {
            appController.doDialog(PaymentCreditCardMembershipActivity.this, getResources().getString(R.string.dialog_register_input_all));
            return false;
        }
        if (etCard.getText().toString().length() < 16 || (etCard.getText().toString().charAt(0) != '4' && etCard.getText().toString().charAt(0) != '5')) {
            appController.doDialog(PaymentCreditCardMembershipActivity.this, getResources().getString(R.string.text_card_number_input));
            return false;
        }
        if (data.data.total_payment < 10000) {
            appController.doDialog(PaymentCreditCardMembershipActivity.this, getResources().getString(R.string.text_creditcard_amount));
            return false;
        }
        return true;
    }

}
