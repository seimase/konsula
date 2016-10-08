package com.konsula.app.ui.activity.transaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.AccessTeledocModel;
import com.konsula.app.service.model.HistoryTransactionModel;
import com.konsula.app.service.model.TransactionCancelModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.payment.PaymentConfirmActivity;
import com.konsula.app.ui.activity.payment.PaymentSelectionMembershipActivity;
import com.konsula.app.ui.activity.teledokter.TeledocRescheduleActivity;
import com.konsula.app.ui.activity.teledokter.TeledocReviewActivity;
import com.konsula.app.ui.activity.teledokter.TeledokterActivity;
import com.konsula.app.ui.activity.teledokter.TeledokterDoneActivity;
import com.konsula.app.ui.activity.teledokter.TeledokterFailActivity;
import com.konsula.app.ui.adapter.MytransactionAdapter;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.util.RefreshTokenCallback;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 26/04/2016.
 */
public class MytransactionHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String currentLanguage;
    ProgressDialog dialog;
    private android.app.AlertDialog dialogpopup;
    private LinearLayout l_nodata;
    private LinearLayout l_loading;
    private Button refresh;
    private AlertDialog dialogcancel;
    private String subcategory;
    private MytransactionAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytransaction);
        l_nodata = (LinearLayout) findViewById(R.id.view_nodata);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        recyclerView = (RecyclerView) findViewById(R.id.mytransaction_list_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getdata();
        l_loading = (LinearLayout) findViewById(R.id.l_loading);
        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getdata();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ((AppController) getApplication()).setMixpanel("Click My Transaction Menu");
    }

    public void back(View view) {
        if (getIntent().getBooleanExtra(PaymentSelectionMembershipActivity.TAG_FROM_PAYMENT, false)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("fromMembership", 3);
            startActivity(intent);
        }
        finish();

    }

    private void getdata() {
        NetworkManager.getNetworkService(getApplication())
                .getHistroryTransaction(((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, new Callback<HistoryTransactionModel>() {
                    @Override
                    public void success(HistoryTransactionModel historyTransactionModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).isTokenValid(historyTransactionModel.messages, response, MytransactionHistoryActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                getdata();
                            }


                        });
                        if (isTokenValid) {
                            swipeContainer.setRefreshing(false);
                            l_loading.setVisibility(View.INVISIBLE);
                            if (historyTransactionModel.results != null) {
                                adapter = new MytransactionAdapter(getApplicationContext(), historyTransactionModel.results, R.layout.item_mytransaction, new MytransactionAdapter.OnTransactionClicked() {


                                    @Override
                                    public void onTransactionClick(String next_action, String payment_uuid, String item_category) {
                                        subcategory = item_category;
                                        doviewTransaction(item_category, next_action, payment_uuid);
                                    }
                                }, new MytransactionAdapter.OnCancelTransactionClicked() {
                                    @Override
                                    public void onCancelTransactionClick(String item_category, String next_action, String payment_status_label, final String payment_uuid) {

                                        if (payment_status_label.equals("unpaid") && !item_category.equals("teledoctor")) {
                                            CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(MytransactionHistoryActivity.this, getResources().getColor(R.color.colorPrimaryDark));
                                            View cancelView = LayoutInflater.from(MytransactionHistoryActivity.this).inflate(R.layout.view_transaction_cancel, null);
                                            TextView btnCancel = (TextView) cancelView.findViewById(R.id.btn_cancel);
                                            TextView btnOk = (TextView) cancelView.findViewById(R.id.btn_ok);
                                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialogcancel.dismiss();
                                                }
                                            });
                                            btnOk.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ((AppController) getApplication()).setMixpanel("Do Cancel Transaction");
                                                    dialogcancel.dismiss();
                                                    doCancelTransaction(payment_uuid);
                                                }
                                            });
                                            builder.setView(cancelView);
                                            builder.setTitle(R.string.text_header_transaction_cancel);
                                            dialogcancel = builder.create();
                                            dialogcancel.show();
                                        } else {
                                            subcategory = item_category;
                                            doviewTransaction(item_category, next_action, payment_uuid);
                                        }

                                    }

                                });
                                recyclerView.setAdapter(adapter);
                            } else {
                                l_loading.setVisibility(View.INVISIBLE);
                                l_nodata.setVisibility(View.VISIBLE);

                            }
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        swipeContainer.setRefreshing(false);
                    }
                });
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getBooleanExtra(PaymentSelectionMembershipActivity.TAG_FROM_PAYMENT, false)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("fromMembership", 3);
            startActivity(intent);
        }
        finish();

    }

    private void doviewTransaction(String item_category, String next_action, String payment_uuid) {
        if (next_action.equals("payment") && item_category.equals("teledoctor")) {
            getdataTeledocRetriction();
        } else if (next_action.equals("thankyou")) {
            Intent intent = new Intent(MytransactionHistoryActivity.this, PaymentConfirmActivity.class);
            intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, false);
            intent.putExtra(PaymentConfirmActivity.payment_uuid, payment_uuid);
            intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, subcategory);
            startActivity(intent);
        } else if (next_action.equals("payment")) {
            Intent intent = new Intent(MytransactionHistoryActivity.this, PaymentSelectionMembershipActivity.class);
            intent.putExtra(PaymentSelectionMembershipActivity.TAG_PAYMENT_UUID, payment_uuid);
            intent.putExtra(PaymentSelectionMembershipActivity.TAG_SUBCATEGORY, subcategory);
            startActivity(intent);

        } else if (next_action.equals("expired")) {
            Intent intent = new Intent(MytransactionHistoryActivity.this, PaymentConfirmActivity.class);
            intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, false);
            intent.putExtra(PaymentConfirmActivity.payment_uuid, payment_uuid);
            intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, subcategory);
            startActivity(intent);
        }
    }

    private void doCancelTransaction(final String payment_uuid) {
        l_loading.setVisibility(View.VISIBLE);
        NetworkManager.getNetworkService(getApplication())
                .PostCancelTransaction(((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, payment_uuid, new Callback<TransactionCancelModel>() {

                    @Override
                    public void success(TransactionCancelModel transactionCancelModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).
                                isTokenValid(transactionCancelModel.messages, response, MytransactionHistoryActivity.this, new RefreshTokenCallback() {
                                    @Override
                                    public void onRefreshTokenComplete() {
                                        l_loading.setVisibility(View.INVISIBLE);
                                        doCancelTransaction(payment_uuid);
                                    }

                                });
                        if (isTokenValid) {
                            l_loading.setVisibility(View.INVISIBLE);
                            if (transactionCancelModel.results) {
                                getdata();
                            } else {
                                ((AppController) getApplication()).doDialog(MytransactionHistoryActivity.this, transactionCancelModel.messages.response_text);

                            }
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        l_loading.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void getdataTeledocRetriction() {
        dialog = AppController.createProgressDialog(MytransactionHistoryActivity.this);
        dialog.setCancelable(false);
        dialog.show();
        NetworkManager.getNetworkService(getApplication())
                .getAccessTeledoc(((AppController) getApplication())
                        .getSessionManager().getToken(), new Callback<AccessTeledocModel>() {
                    @Override
                    public void success(AccessTeledocModel accessTeledocModel, Response response) {
                        final boolean isTokenValid = ((AppController) getApplication()).isTokenValid(accessTeledocModel.messages, response, MytransactionHistoryActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                dialog.dismiss();
                                getdataTeledocRetriction();
                            }

                        });
                        if (isTokenValid) {
                            Intent intent = null;
                            dialog.dismiss();
                            if (accessTeledocModel.results.show_page != null) {
                                switch (accessTeledocModel.results.show_page) {
                                    case "confirmation":
                                        intent = new Intent(MytransactionHistoryActivity.this, PaymentConfirmActivity.class);
                                        intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, false);
                                        intent.putExtra(PaymentConfirmActivity.payment_uuid, accessTeledocModel.results.data.payment_uuid);
                                        intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, "teledoctor");
                                        startActivity(intent);
                                        break;
                                    case "payment":
                                        intent = new Intent(MytransactionHistoryActivity.this, TeledokterFailActivity.class);
                                        intent.putExtra("payment_uuid", accessTeledocModel.results.data.payment_uuid);
                                        intent.putExtra("name", accessTeledocModel.results.data.doctor_name);
                                        intent.putExtra("date", accessTeledocModel.results.data.schedule);
                                        intent.putExtra("duration", accessTeledocModel.results.data.duration);
                                        intent.putExtra("reason", accessTeledocModel.results.data.reason);
                                        intent.putExtra("spesialisasi", accessTeledocModel.results.data.doctor_specialization);
                                        startActivity(intent);
                                        break;

                                    case "teledoctor":
                                        intent = new Intent(MytransactionHistoryActivity.this, TeledokterActivity.class);
                                        startActivity(intent);
                                        break;

                                    case "thankyou":
                                        intent = new Intent(MytransactionHistoryActivity.this, TeledokterDoneActivity.class);
                                        intent.putExtra("nama", accessTeledocModel.results.data.doctor_name);
                                        intent.putExtra("spesialisasi", accessTeledocModel.results.data.doctor_specialization);
                                        intent.putExtra("waktu", accessTeledocModel.results.data.schedule);
                                        intent.putExtra("phone", accessTeledocModel.results.data.contact);
                                        intent.putExtra("status", accessTeledocModel.results.data.tele_status);
                                        intent.putExtra("image", accessTeledocModel.results.data.doctor_photo.primary.large_image);
                                        startActivity(intent);
                                        break;
                                    case "review":
                                        intent = new Intent(MytransactionHistoryActivity.this, TeledocReviewActivity.class);
                                        intent.putExtra("nama", accessTeledocModel.results.data.doctor_name);
                                        intent.putExtra("uuid", accessTeledocModel.results.data.tele_uuid);
                                        intent.putExtra("waktu", accessTeledocModel.results.data.schedule);
                                        intent.putExtra("keluhan", accessTeledocModel.results.data.reason);
                                        intent.putExtra("image", accessTeledocModel.results.data.doctor_photo.primary.large_image);
                                        startActivity(intent);
                                        break;
                                    case "waiting_reschedule":
                                        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(MytransactionHistoryActivity.this, getResources().getColor(R.color.green_xxl));
                                        View invoiceDetail = LayoutInflater.from(MytransactionHistoryActivity.this).inflate(R.layout.dialog_teledoc_cancel_confirm, null);
                                        TextView textView = (TextView) invoiceDetail.findViewById(R.id.message);
                                        TextView btnCancel = (TextView) invoiceDetail.findViewById(R.id.positive_button);
                                        btnCancel.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                                        btnCancel.setText(getResources().getString(R.string.mdtp_ok));
                                        btnCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogpopup.dismiss();
                                            }
                                        });
                                        textView.setText(getResources().getString(R.string.text_reschedule_information));
                                        builder.setView(invoiceDetail);
                                        builder.setTitle(getResources().getString(R.string.title_teledokter));
                                        dialogpopup = builder.create();
                                        dialogpopup.show();
                                        break;
                                    case "pending_reschedule":
                                        intent = new Intent(MytransactionHistoryActivity.this, TeledocRescheduleActivity.class);
                                        intent.putExtra(TeledocRescheduleActivity.TAG_TELE_UUID, accessTeledocModel.results.data.tele_uuid);
                                        intent.putExtra(TeledocRescheduleActivity.TAG_DOC_NAME, accessTeledocModel.results.data.doctor_name);
                                        intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SPECIALIZATION, accessTeledocModel.results.data.doctor_specialization);
                                        intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SCHEDULE, accessTeledocModel.results.data.schedule);
                                        startActivity(intent);
                                        break;
                                }
                            } else {
                                Toast.makeText(MytransactionHistoryActivity.this, getResources().getString(R.string.text_gagal), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        Toast.makeText(MytransactionHistoryActivity.this, "Failed load data, check internet connection", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
