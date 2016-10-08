package com.konsula.app.ui.activity.payment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.konsula.app.service.model.BankListModel;
import com.konsula.app.service.model.BillingTransactionModel;
import com.konsula.app.service.model.DetailMembershipTransactionModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.transaction.MytransactionHistoryActivity;
import com.konsula.app.ui.adapter.BankListAdapter;
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
public class PaymentBankTransferSelectionActivity extends AppCompatActivity {
    public static final String TAG_COUNT_DOWN = "count_down";
    private AlertDialog dialog;
    private RecyclerView bankList;
    private String currentLanguage;
    private TextView textViewCharge;
    private TextView textViewContDown;
    private DetailMembershipTransactionModel.Results data;
    private long totalCountDown;
    private int bank_id = 0;

    private FrameLayout btnnext;
    private LinearLayout layoutloading;
    private LinearLayout l_view, llCountDown;
    private Button refresh;
    private TextView btnSeeDetail;
    private EstorePaymentStepView paymentStepView;
    private ImageView imgArrow;

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
        setContentView(R.layout.activity_payment_bank_selection);
        init();

    }

    public int getThemeColor() {
        TypedValue value = new TypedValue();
        PaymentBankTransferSelectionActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    private void init() {
        imgArrow = (ImageView)findViewById(R.id.imgArrow);
        btnnext = (FrameLayout) findViewById(R.id.btn_next);
        textViewContDown = (TextView) findViewById(R.id.text_count_down);
        totalCountDown = getIntent().getLongExtra(TAG_COUNT_DOWN, 0);
        paymentStepView = (EstorePaymentStepView) findViewById(R.id.payment_step_view);
        paymentStepView.setCurrentStep(2);
        paymentStepView.setVisibility(data.data.subcategory.equalsIgnoreCase("estore") ? View.VISIBLE : View.GONE);
        bankList = (RecyclerView) findViewById(R.id.bank_list_recycler_view);
        bankList.setHasFixedSize(true);
        bankList.setClickable(true);
        bankList.setLayoutManager(new LinearLayoutManager(this));
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        textViewCharge = (TextView) findViewById(R.id.text_charge);
        textViewCharge.setText(((AppController) getApplication()).getDefaultPriceFormat(data.data.currency, (double) data.data.total_payment));
        if (totalCountDown != 0) {

            new CountDownTimer(totalCountDown, 1000) {

                public void onTick(long millisUntilFinished) {
                    textViewContDown.setText(((AppController) getApplication()).formatTime(millisUntilFinished, ":", ":", "", true));
                    totalCountDown = millisUntilFinished;
                }

                public void onFinish() {
                    doDialogexpired();
                }

            }.start();
        }

        if (data.data.subcategory.equalsIgnoreCase("teledoctor")){
            //llCountDown.setVisibility(View.GONE);
            imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_green));
        }else if (data.data.subcategory.equalsIgnoreCase("estore")) {
            imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_chat));
        }else{
            imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_pembiayaan));
        }

        getData();
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (LinearLayout) findViewById(R.id.l_view);
        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l_view.setVisibility(View.INVISIBLE);
                layoutloading.setVisibility(View.VISIBLE);
                getData();
            }
        });
        btnSeeDetail = (TextView) findViewById(R.id.btn_see_detail);
        btnSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeDetail();
            }
        });

        llCountDown = (LinearLayout) findViewById(R.id.llCountDown);
        if (data.data.subcategory.equalsIgnoreCase("teledoctor"))
            llCountDown.setVisibility(View.GONE);
    }

    private void getData() {
        getBankList();
    }

    public void Transactionmembership(View view) {

    }

    public void back(View view) {
        finish();
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
                textViewitemname.setText(data.data.items.get(i).order_name + " " + data.data.items.get(i).order_name_detail);
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

    public void closeDetail(View view) {
        dialog.dismiss();
    }

    public void _continue(View view) {
        dopayment();
    }

    private void getBankList() {
        NetworkManager.getNetworkService(getApplication())
                .getBankList(((AppController) getApplication())
                        .getSessionManager().getToken(), new Callback<BankListModel>() {
                    @Override
                    public void success(final BankListModel bankListModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).
                                isTokenValid(bankListModel.messages, response, PaymentBankTransferSelectionActivity.this, new RefreshTokenCallback() {
                                    @Override
                                    public void onRefreshTokenComplete() {
                                        getBankList();
                                    }


                                });
                        if (isTokenValid) {
                            btnnext.setVisibility(View.VISIBLE);
                            layoutloading.setVisibility(View.GONE);
                            refresh.setVisibility(View.GONE);
                            l_view.setVisibility(View.VISIBLE);
                            bankList.setAdapter(new BankListAdapter(getApplicationContext(), bankListModel.results, R.layout.item_bank_membership, new BankListAdapter.OnBankClicked() {
                                @Override
                                public void OnBankClicked(int bankid) {
                                    bank_id = bankid;
                                }

                            }));
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        l_view.setVisibility(View.INVISIBLE);
                        layoutloading.setVisibility(View.GONE);
                        refresh.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void dopayment() {
        String payment_uuid = data.data.payment_uuid;
        if (bank_id == 0) {
            ((AppController) getApplication()).doDialog(PaymentBankTransferSelectionActivity.this, getResources().getString(R.string.text_warning_bank));
        } else {
            btnnext.setClickable(false);
            final ProgressDialog dialogupload = AppController.createProgressDialog(PaymentBankTransferSelectionActivity.this);
            dialogupload.setCancelable(false);
            dialogupload.show();
            NetworkManager.getNetworkService(getApplication())
                    .doPayment(((AppController) getApplication())
                            .getSessionManager().getToken(), payment_uuid, bank_id, new Callback<BillingTransactionModel>() {
                        @Override
                        public void success(BillingTransactionModel billingTransactionModel, Response response) {
                            boolean isTokenValid = ((AppController) getApplication()).
                                    isTokenValid(billingTransactionModel.messages, response, PaymentBankTransferSelectionActivity.this, new RefreshTokenCallback() {
                                        @Override
                                        public void onRefreshTokenComplete() {
                                            dialogupload.dismiss();
                                            dopayment();
                                        }


                                    });
                            if (isTokenValid) {
                                JSONObject props = new JSONObject();
                                try{
                                    props.put("SOURCE", bank_id);
                                    ((AppController) getApplication()).setMixpanel("Bank Source");
                                }catch (Exception e){

                                }
                                dialogupload.dismiss();
                                btnnext.setClickable(true);
                                if (billingTransactionModel.results.status) {
                                    ((AppController) getApplication()).getSessionManager().setDetailMembershipTransfer(billingTransactionModel.results);
                                    Intent intent = new Intent(PaymentBankTransferSelectionActivity.this, PaymentTransferInformationActivity.class);
                                    intent.putExtra(TAG_COUNT_DOWN, totalCountDown);
                                    startActivity(intent);
                                } else {
                                    ((AppController) getApplication()).doDialog(PaymentBankTransferSelectionActivity.this, billingTransactionModel.messages.response_text);
                                }
                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            btnnext.setClickable(true);
                            dialogupload.dismiss();
                            Toast.makeText(PaymentBankTransferSelectionActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void doDialogexpired() {
        textViewContDown.setText("00:00:00");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent intent = new Intent(PaymentBankTransferSelectionActivity.this, MytransactionHistoryActivity.class);
                    intent.putExtra(PaymentSelectionMembershipActivity.TAG_FROM_PAYMENT, true);
                    startActivity(intent);
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PaymentBankTransferSelectionActivity.this);
        builder.setMessage(getResources().getString(R.string.text_dialog_expired)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener).setCancelable(false).show();

    }


}