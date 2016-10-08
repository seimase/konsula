package com.konsula.app.ui.activity.membership;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.MembershipCreateTransactionModel;
import com.konsula.app.service.model.PromoCodeModel;
import com.konsula.app.service.model.SummaryMembershipModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.direktori.PrivacyPolicyActivity;
import com.konsula.app.ui.activity.direktori.TermAndConditionActivity;
import com.konsula.app.ui.activity.payment.PaymentConfirmActivity;
import com.konsula.app.ui.activity.payment.PaymentSelectionMembershipActivity;
import com.konsula.app.util.RefreshTokenCallback;

import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 25/04/2016.
 */
public class SummaryMembershipActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView btnsyaratdanketentuan, btnkebijakanprivasi, textViewTos1, textViewtos2, textViewtos3;
    public static String plan_id = "plan_id";
    public static String subplan_id = "subplan_id";
    private String currentLanguage;
    private int plan;
    private int subplan;
    private TextView itemName, itemharga, itemconfenencefee;
    private TextView itemDate, itemTotalHarga;
    private EditText editTextPromocode;
    private TextView btnPromoCode;
    private int price;
    private Float total_price;
    private String signature_key;
    private int confenece_fee;
    private String currency;
    private String promo_code = "";
    private LinearLayout layoutloading;
    private RelativeLayout l_view;
    private Button refresh;
    private CheckBox checkBoxTos;
    private Animation bounce;
    private ImageView btn_promocode_cancel;
    private TextView text_total_promocode;
    private LinearLayout viewPromocode;
    private LinearLayout viewPromocodeCancel;
    private LinearLayout Layoutpromocode;
    private RelativeLayout view_promocode;
    private TextView view_totalpromo;
    private TextView textViewPromocodeInfo;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_invoice);
        initid();
        getData();

    }

    public void Transactionmembership(View view) {
        if (checkBoxTos.isChecked()) {
            ((AppController) getApplication()).setMixpanel("Click Finalize Transaction");
            createTransaction();
        } else {
            ((AppController) getApplication()).doDialog(SummaryMembershipActivity.this, getResources().getString(R.string.text_peringatan_tos));

        }
    }

    private void initid() {
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        bounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_anim);
        plan = getIntent().getExtras().getInt(plan_id);
        subplan = getIntent().getExtras().getInt(subplan_id);
        itemName = (TextView) findViewById(R.id.tvItemNameMembership);
        itemDate = (TextView) findViewById(R.id.tvdatemembership);
        itemharga = (TextView) findViewById(R.id.tvharga);
        Layoutpromocode = (LinearLayout) findViewById(R.id.layoutpromocode);
        itemconfenencefee = (TextView) findViewById(R.id.tvconfenence_fee);
        itemTotalHarga = (TextView) findViewById(R.id.tvhargatotal);
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        editTextPromocode = (EditText) findViewById(R.id.text_promocode);
        textViewPromocodeInfo = (TextView) findViewById(R.id.membership_promocode_info);
        btnPromoCode = (TextView) findViewById(R.id.btn_promocode);
        textViewTos1 = (TextView) findViewById(R.id.text_tos1);
        textViewtos2 = (TextView) findViewById(R.id.text_tos2);
        textViewtos3 = (TextView) findViewById(R.id.text_tos3);
        checkBoxTos = (CheckBox) findViewById(R.id.cb1);
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (RelativeLayout) findViewById(R.id.l_view);
        refresh = (Button) findViewById(R.id.refresh);
        btn_promocode_cancel = (ImageView) findViewById(R.id.btn_promocode_cancel);
        text_total_promocode = (TextView) findViewById(R.id.text_total_promocode);
        viewPromocode = (LinearLayout) findViewById(R.id.layout_promocode);
        viewPromocodeCancel = (LinearLayout) findViewById(R.id.layout_promocode_cancel);
        view_promocode = (RelativeLayout) findViewById(R.id.view_promocode);
        view_totalpromo = (TextView) findViewById(R.id.view_totalpromo);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        textViewtos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxTos.isChecked()) {
                    checkBoxTos.setChecked(false);
                } else {
                    checkBoxTos.setChecked(true);
                }
            }
        });
        textViewTos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxTos.isChecked()) {
                    checkBoxTos.setChecked(false);
                } else {
                    checkBoxTos.setChecked(true);
                }
            }
        });
        textViewtos3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxTos.isChecked()) {
                    checkBoxTos.setChecked(false);
                } else {
                    checkBoxTos.setChecked(true);
                }
            }
        });

        btn_promocode_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docancel();
            }
        });
        btnsyaratdanketentuan = (TextView) findViewById(R.id.text_syaratketentuan);
        btnkebijakanprivasi = (TextView) findViewById(R.id.text_kebijakanprivasi);
        btnsyaratdanketentuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryMembershipActivity.this, TermAndConditionActivity.class);
                startActivity(intent);
            }
        });
        btnkebijakanprivasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryMembershipActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
        btnPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docountpromocode();
            }
        });
    }

    private void getData() {
        NetworkManager.getNetworkService(getApplication())
                .getSummaryMembership(((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, plan, subplan, new Callback<SummaryMembershipModel>() {
                    @Override
                    public void success(SummaryMembershipModel summaryMembershipModel, Response response) {
                        if (summaryMembershipModel.results != null) {
                            layoutloading.setVisibility(View.GONE);
                            l_view.setVisibility(View.VISIBLE);
                            refresh.setVisibility(View.INVISIBLE);
                            itemName.setText(getResources().getString(R.string.membership) + " " + summaryMembershipModel.results.plan_name);
                            itemDate.setText(((AppController) getApplication()).dateBirth(summaryMembershipModel.results.expired_date));
                            itemharga.setText(((AppController) getApplication()).getDefaultPriceFormat(summaryMembershipModel.results.currency, (double) summaryMembershipModel.results.price));
                            itemconfenencefee.setText(((AppController) getApplication()).getDefaultPriceFormat("- " + summaryMembershipModel.results.currency, (double) summaryMembershipModel.results.convenience_fee * -1));
                            itemTotalHarga.setText(((AppController) getApplication()).getDefaultPriceFormat(summaryMembershipModel.results.currency, (double) summaryMembershipModel.results.total_price));
                            total_price = summaryMembershipModel.results.total_price;
                            price = summaryMembershipModel.results.price;
                            signature_key = summaryMembershipModel.results.signature_key;
                            confenece_fee = summaryMembershipModel.results.convenience_fee;
                            currency = summaryMembershipModel.results.currency;
                            if (price == 0) Layoutpromocode.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        layoutloading.setVisibility(View.GONE);
                        refresh.setVisibility(View.VISIBLE);
                    }
                });
    }


    private void docountpromocode() {
        ((AppController) getApplication()).setMixpanel("Use Promo Code");
        final ProgressDialog dialogupload = AppController.createProgressDialog(SummaryMembershipActivity.this);
        dialogupload.setCancelable(false);
        dialogupload.show();
        final String prmocode = editTextPromocode.getText().toString();
        NetworkManager.getNetworkService(getApplication())
                .getPromocode(((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, prmocode, "membership", price, new Callback<PromoCodeModel>() {
                    @Override
                    public void success(PromoCodeModel promoCodeModel, Response response) {
                        dialogupload.dismiss();
                        if (promoCodeModel.results.data != null) {
                            viewPromocode.setVisibility(View.GONE);
                            viewPromocodeCancel.setVisibility(View.VISIBLE);
                            viewPromocodeCancel.startAnimation(bounce);
                            promo_code = promoCodeModel.results.data.promo_code;
                            btnPromoCode.setVisibility(View.INVISIBLE);
                            view_promocode.setVisibility(View.VISIBLE);
                            Double pricepromo = 0.0;
                            Double totalpromo = 0.0;
                            text_total_promocode.setText(promoCodeModel.results.data.promo_code);
                            if (promoCodeModel.results.data.promo_type.equalsIgnoreCase("amount")) {
                                textViewPromocodeInfo.setText(getResources().getString(R.string.payment_promocode_info).replace("{promo_code}", promoCodeModel.results.data.promo_code.toUpperCase()).replace("{price}", AppController.getInstance().getDefaultPriceFormat(promoCodeModel.results.data.currency, promoCodeModel.results.data.nominal)));
                                view_totalpromo.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) promoCodeModel.results.data.nominal));
                                totalpromo = promoCodeModel.results.data.nominal;
                            } else if (promoCodeModel.results.data.promo_type.equalsIgnoreCase("percentage")) {
                                textViewPromocodeInfo.setText(getResources().getString(R.string.payment_promocode_info).replace("{promo_code}", promoCodeModel.results.data.promo_code.toUpperCase()).replace("{price}", Math.round(promoCodeModel.results.data.nominal) + " %"));
                                totalpromo = getPercentage(price, promoCodeModel.results.data.nominal);
                                view_totalpromo.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) totalpromo));
                            }
                            pricepromo = total_price - totalpromo;
                            pricepromo = pricepromo < 0 ? 0 : pricepromo;
                            if (pricepromo == 0)
                                itemconfenencefee.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) 0));
                            itemTotalHarga.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) pricepromo));

                            JSONObject props = new JSONObject();
                            try {
                                props.put("SOURCE", prmocode);
                                ((AppController) getApplication()).setMixpanel("Promo Code ", props);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            editTextPromocode.setText("");
                            ((AppController) getApplication()).doDialog(SummaryMembershipActivity.this, promoCodeModel.messages.response_text);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialogupload.dismiss();
                        Toast.makeText(SummaryMembershipActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createTransaction() {
        final ProgressDialog dialogupload = AppController.createProgressDialog(SummaryMembershipActivity.this);
        dialogupload.setCancelable(false);
        dialogupload.show();
        NetworkManager.getNetworkService(getApplication())
                .CreateTransaction(((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, plan, subplan, currency, price, confenece_fee, promo_code, signature_key, new Callback<MembershipCreateTransactionModel>() {

                    @Override
                    public void success(MembershipCreateTransactionModel membershipCreateTransactionModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).
                                isTokenValid(membershipCreateTransactionModel.messages, response, SummaryMembershipActivity.this, new RefreshTokenCallback() {
                                    @Override
                                    public void onRefreshTokenComplete() {
                                        dialogupload.dismiss();
                                        createTransaction();
                                    }

                                });
                        if (isTokenValid) {
                            dialogupload.dismiss();
                            if (membershipCreateTransactionModel.results.status) {
                                if (membershipCreateTransactionModel.results.data.total_price == 0) {
                                    Intent intent = new Intent(SummaryMembershipActivity.this, PaymentConfirmActivity.class);
                                    intent.putExtra(PaymentConfirmActivity.payment_uuid, membershipCreateTransactionModel.results.data.payment_uuid);
                                    intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, true);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(SummaryMembershipActivity.this, PaymentSelectionMembershipActivity.class);
                                    intent.putExtra(PaymentSelectionMembershipActivity.TAG_PAYMENT_UUID, membershipCreateTransactionModel.results.data.payment_uuid);
                                    startActivity(intent);
                                }
                            } else {
                                ((AppController) getApplication()).doDialog(SummaryMembershipActivity.this, membershipCreateTransactionModel.messages.response_text);

                            }
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialogupload.dismiss();
                        Toast.makeText(SummaryMembershipActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void docancel() {
        promo_code = "";
        editTextPromocode.setText("");
        view_promocode.setVisibility(View.INVISIBLE);
        //textViewPromocode.setText(getResources().getString(R.string.estore_promotion_code));
        itemconfenencefee.setText(((AppController) getApplication()).getDefaultPriceFormat("- " + currency, (double) confenece_fee * -1));
        itemTotalHarga.setText(((AppController) getApplication()).getDefaultPriceFormat(currency, (double) total_price));
        viewPromocodeCancel.setVisibility(View.GONE);
        btnPromoCode.setVisibility(View.VISIBLE);
        viewPromocode.setVisibility(View.VISIBLE);
        viewPromocode.startAnimation(bounce);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static Double getPercentage(int total_price, Double promo) {
        Double proportion = Math.floor(total_price * (promo / 100));
        return proportion;
    }
}