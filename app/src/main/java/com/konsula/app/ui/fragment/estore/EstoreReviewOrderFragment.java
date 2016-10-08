package com.konsula.app.ui.fragment.estore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.CreateTransactionModel;
import com.konsula.app.service.model.EstoreProductModel;
import com.konsula.app.service.model.PromoCodeModel;
import com.konsula.app.service.model.SummaryEstoreModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.direktori.PrivacyPolicyActivity;
import com.konsula.app.ui.activity.direktori.TermAndConditionActivity;
import com.konsula.app.ui.activity.estore.EstoreProductActivity;
import com.konsula.app.ui.activity.payment.PaymentConfirmActivity;
import com.konsula.app.ui.activity.payment.PaymentSelectionMembershipActivity;
import com.konsula.app.ui.adapter.InvoiceAdapter;
import com.konsula.app.ui.view.InvoiceFooterView;
import com.konsula.app.ui.view.InvoiceHeaderView;
import com.konsula.app.util.RefreshTokenCallback;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SamuelSonny on 04-May-16.
 */
public class EstoreReviewOrderFragment extends Fragment {
    private ListView invoiceList;
    private InvoiceAdapter adapter;
    private int listViewHeight;
    private int childrenViewHeight;
    private int footerViewHeight;
    private InvoiceFooterView footerView;
    private InvoiceFooterView staticFooterView;
    private InvoiceHeaderView headerView;
    private TextView txtPromoCode;
    private Animation bounce;
    private static Float grandTotal ;
    private LinearLayout viewPromoCodeCancel;
    private LinearLayout viewPromoCode;
    private TextView txtTotalPromoCode;
    private CheckBox chkAgreement;
    private TextView btnpromocode;
    private SummaryEstoreModel.Results results;


    private String currentLanguage;
    ArrayList<String> listContentId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        View view = inflater.inflate(R.layout.fragment_estore_review_order, null);

        invoiceList = (ListView) view.findViewById(R.id.invoice_list);
        adapter = new InvoiceAdapter(getContext());
        footerView = new InvoiceFooterView(getContext());
        headerView = new InvoiceHeaderView((getContext()));
        staticFooterView = (InvoiceFooterView) view.findViewById(R.id.static_footer_view);
        txtPromoCode = (TextView) headerView.findViewById(R.id.txt_promo_code);
        bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_anim);
        viewPromoCodeCancel = (LinearLayout) headerView.findViewById(R.id.layout_promocode_cancel);
        viewPromoCode = (LinearLayout) headerView.findViewById(R.id.layout_promocode);
        txtTotalPromoCode = (TextView) headerView.findViewById(R.id.text_total_promocode);
        btnpromocode = (TextView) headerView.findViewById(R.id.btn_promo_code);
        chkAgreement = (CheckBox) footerView.findViewById(R.id.chk_agreement);


        if (EstoreProductActivity.getData() != null) {
            adapter.clear();
            grandTotal = Float.valueOf(0);
            for (EstoreProductModel.ProductsItem item : EstoreProductActivity.getData().products_items) {
                if (item.amount > 0) {
                    adapter.add(item);
                    grandTotal += item.sell_price * item.amount;
                }
            }
        }
        loadSummaryEstore();

//        if (results != null) {
//            footerView.setConvenienceFee(results.convenience_fee);
//            staticFooterView.setConvenienceFee(results.convenience_fee);
//
//            grandTotal += results.convenience_fee;
//        }
//        if (grandTotal != 0) invoiceList.addHeaderView(headerView);
//        invoiceList.addFooterView(footerView);
//        invoiceList.setAdapter(adapter);
//        footerView.setGrandTotal(grandTotal);
//        staticFooterView.setGrandTotal(grandTotal);

//        invoiceList.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        listViewHeight = invoiceList.getMeasuredHeight();
//        childrenViewHeight = getTotalHeightofListView(invoiceList);
//        footerView.measure(
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        footerViewHeight = footerView.getMeasuredHeight();
        btnpromocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.checkConnection(getActivity())) {
                    ((AppController) getActivity().getApplication()).setMixpanel("Use Promo Code for e-Store");
                    usePromoCode();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }

            }
        });
//        if(childrenViewHeight > listViewHeight){
//            invoiceList.removeFooterView(footerView);
//            ((LinearLayout.LayoutParams)invoiceList.getLayoutParams()).setMargins(0, 0, 0, footerViewHeight);
//            staticFooterView.setVisibility(View.VISIBLE);
//        }

        return view;
    }

    public int getTotalHeightofListView(ListView listView) {
        ListAdapter adapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, listView);

            view.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += view.getMeasuredHeight();
        }
        return totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
    }

    public static Float getGrandTotal() {
        return grandTotal;
    }

    public void showTOS(View view) {
        Intent intent = new Intent(getContext(), TermAndConditionActivity.class);
        startActivity(intent);
    }

    public void showPrivacyPolicy(View view) {
        Intent intent = new Intent(getContext(), PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    public void checkAgreement(View view) {
        chkAgreement.setChecked(!chkAgreement.isChecked());
    }

    public void usePromoCode() {
        int grandTotal = 0;
        EstoreProductModel.Data data = EstoreProductActivity.getData();
        int size = data.products_items.size();
        for (int i = 0, j = 0; i < size; i++) {
            if (data.products_items.get(i).amount > 0) {
                grandTotal += data.products_items.get(i).amount * data.products_items.get(i).sell_price;
            }
        }
        NetworkManager.getNetworkService(getContext()).getPromocode(
                ((AppController) ((Activity) getContext()).getApplication()).getSessionManager().getToken(), currentLanguage,
                txtPromoCode.getText().toString(),
                "estore",
                grandTotal,
                new Callback<PromoCodeModel>() {
                    @Override
                    public void success(PromoCodeModel promoCodeModel, Response response) {
                        boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).
                                isTokenValid(promoCodeModel.messages, response, getContext(), new RefreshTokenCallback() {
                                    @Override
                                    public void onRefreshTokenComplete() {
                                        usePromoCode();
                                    }
                                });
                        if (isTokenValid) {
                            if (promoCodeModel.results.data != null) {
                                JSONObject props = new JSONObject();
                                try {
                                    props.put("SOURCE", txtPromoCode.getText().toString().trim());
                                    ((AppController) getActivity().getApplication()).setMixpanel("Promo Code Type", props);
                                } catch (Exception e) {

                                }

                                viewPromoCode.setVisibility(View.GONE);
                                viewPromoCodeCancel.setVisibility(View.VISIBLE);
                                viewPromoCodeCancel.startAnimation(bounce);
                                txtTotalPromoCode.setText(promoCodeModel.results.data.promo_code);
                                Double pricepromo = 0.0;
                                Double totalpromo = 0.0;
                                footerView.setPromoCodeInfo(promoCodeModel.results.data.promo_type,promoCodeModel.results.data.promo_code,promoCodeModel.results.data.nominal);
                                if (promoCodeModel.results.data.promo_type.equalsIgnoreCase("amount")) {
                                    footerView.setPromoCode((double) promoCodeModel.results.data.nominal);
                                    staticFooterView.setPromoCode((double) promoCodeModel.results.data.nominal);
                                    totalpromo = promoCodeModel.results.data.nominal;
                                } else if (promoCodeModel.results.data.promo_type.equalsIgnoreCase("percentage")) {
                                    totalpromo = Math.floor(getGrandTotal() * (promoCodeModel.results.data.nominal / 100));
                                    footerView.setPromoCode(totalpromo);
                                    staticFooterView.setPromoCode(totalpromo);
                                }
                                pricepromo = getGrandTotal() - totalpromo - Math.abs(results.convenience_fee);
                                pricepromo = pricepromo <= 0 ? 0 : pricepromo;
                                if (pricepromo == 0)
                                    footerView.setConvenienceFee(0);
                                footerView.setGrandTotal(pricepromo);
                                staticFooterView.setGrandTotal(pricepromo);
                                footerView.showPromoCode(true);
                                staticFooterView.showPromoCode(true);
                            } else {
                                ((AppController) ((Activity) getContext()).getApplication()).doDialog(getContext(), promoCodeModel.messages.response_text);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }
        );
    }

    public void cancelPromoCode(View view) {
        txtPromoCode.setText("");
        viewPromoCodeCancel.setVisibility(View.GONE);
        viewPromoCode.setVisibility(View.VISIBLE);
        viewPromoCode.startAnimation(bounce);

        if (results != null) {
            footerView.setConvenienceFee(results.convenience_fee);
            staticFooterView.setConvenienceFee(results.convenience_fee);
//            grandTotal += results.convenience_fee;
        }

        footerView.setGrandTotal(grandTotal + results.convenience_fee);
        staticFooterView.setGrandTotal(grandTotal + results.convenience_fee);
        footerView.setPromoCode(0);
        footerView.showPromoCode(false);
        staticFooterView.setPromoCode(0);
        staticFooterView.showPromoCode(false);
    }

    public void createTransaction(View view) {
        if (chkAgreement.isChecked()) {
            EstoreProductModel.Data data = EstoreProductActivity.getData();
            int size = data.products_items.size();
            int orderedSize = 0;
            for (int i = 0; i < size; i++) {
                if (data.products_items.get(i).amount > 0) {
                    orderedSize++;
                }
            }

            String[] itemUuid = new String[orderedSize];
            int[] quantities = new int[orderedSize];
            Float[] prices = new Float[orderedSize];
            String[] currencies = new String[orderedSize];
            Double grandTotal = 0.0;

            for (int i = 0, j = 0; i < size; i++) {
                if (data.products_items.get(i).amount > 0) {
                    itemUuid[j] = data.products_items.get(i).item_uuid;
                    quantities[j] = data.products_items.get(i).amount;
                    prices[j] = data.products_items.get(i).sell_price;
                    currencies[j] = data.products_items.get(i).currency;
                    grandTotal += quantities[j] * prices[j];
                    j++;
                }
            }
            Log.d("ddd", String.valueOf(grandTotal));
            final ProgressDialog progressDialog = AppController.createProgressDialog(getContext());
            progressDialog.setCancelable(false);
            progressDialog.show();
            NetworkManager.getNetworkService(getContext()).transactionEstoreCreate(
                    ((AppController) ((Activity) getContext()).getApplication()).getSessionManager().getToken(),
                    currentLanguage,
                    data.product_uuid,
                    itemUuid,
                    quantities,
                    currencies,
                    prices,
                    grandTotal,
                    results.convenience_fee,
                    viewPromoCodeCancel.getVisibility() == View.VISIBLE ? txtPromoCode.getText().toString() : "",
                    results.signature_key,
                    new Callback<CreateTransactionModel>() {
                        @Override
                        public void success(CreateTransactionModel createTransactionModel, Response response) {
                            boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).
                                    isTokenValid(createTransactionModel.messages, response, getContext(), new RefreshTokenCallback() {
                                        @Override
                                        public void onRefreshTokenComplete() {
                                            createTransaction(null);
                                        }
                                    });
                            if (isTokenValid) {
                                progressDialog.dismiss();
                                if (createTransactionModel.results.status) {
                                    ((AppController) getActivity().getApplication()).setMixpanel("Click Finalize e-Store Transaction");
                                    if (createTransactionModel.results.data.total_price == 0) {
                                        Intent intent = new Intent(getActivity(), PaymentConfirmActivity.class);
                                        intent.putExtra(PaymentConfirmActivity.payment_uuid, createTransactionModel.results.data.payment_uuid);
                                        intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, "estore");
                                        intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, true);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getActivity(), PaymentSelectionMembershipActivity.class);
                                        intent.putExtra(PaymentSelectionMembershipActivity.TAG_PAYMENT_UUID, createTransactionModel.results.data.payment_uuid);
                                        intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, "estore");
                                        startActivity(intent);
                                    }

                                } else {
                                    ((AppController) ((Activity) getContext()).getApplication()).doDialog(getActivity(), createTransactionModel.messages.response_text);

                                }

//                                    Intent intent = new Intent(getContext(), PaymentSelectionMembershipActivity.class);
//                                    intent.putExtra(PaymentSelectionMembershipActivity.TAG_SUBCATEGORY, "estore");
//                                    intent.putExtra(PaymentSelectionMembershipActivity.TAG_PAYMENT_UUID, createTransactionModel.results.data.payment_uuid);
//                                    startActivity(intent);
//                                    getActivity().finish();
                                //   }
                                // }
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("ddf", String.valueOf(error));
                            progressDialog.dismiss();
                            ((AppController) ((Activity) getContext()).getApplication()).doDialog(getActivity(), error.getMessage());
                        }
                    }
            );
        } else {
            ((AppController) ((Activity) getContext()).getApplication()).doDialog(getContext(), getString(R.string.text_peringatan_tos));
        }
    }

    public void loadSummaryEstore() {
        EstoreProductModel.Data data = EstoreProductActivity.getData();

        int size = data.products_items.size();
        String[] itemUuid = new String[size];
        int[] quantities = new int[size];
        Float[] prices = new Float[size];

        for (int i = 0; i < size; i++) {
            itemUuid[i] = data.products_items.get(i).item_uuid;
            quantities[i] = data.products_items.get(i).quota;
            prices[i] = data.products_items.get(i).sell_price;
        }

        final ProgressDialog progressDialog = AppController.createProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.show();
        NetworkManager.getNetworkService(getContext()).transactionSummaryEstore(
                ((AppController) ((Activity) getContext()).getApplication()).getSessionManager().getToken(),
                getResources().getConfiguration().locale.getLanguage(),
                data.product_uuid,
                itemUuid,
                quantities,
                prices,
                new Callback<SummaryEstoreModel>() {
                    @Override
                    public void success(SummaryEstoreModel summaryEstoreModel, Response response) {
                        progressDialog.dismiss();
                        boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).
                                isTokenValid(summaryEstoreModel.messages, response, getContext(), new RefreshTokenCallback() {
                                    @Override
                                    public void onRefreshTokenComplete() {
                                        loadSummaryEstore();
                                    }
                                });
                        if (isTokenValid) {
                            progressDialog.dismiss();
                            results = summaryEstoreModel.results;
                            Bundle params;
                            for (int i = 0; i < adapter.getCount(); i++) {
                                params = new Bundle();
                                adapter.getItem(i).description = summaryEstoreModel.results.items.get(i).description;
                                params.putString("content_ids", String.valueOf(adapter.getItem(i).product_id));
                                params.putString("content_type", "product");
                                params.putString("content_name", adapter.getItem(i).description);
                                params.putString("value", String.valueOf(adapter.getItem(i).sell_price));
                                params.putString("currency", adapter.getItem(i).currency);
                                params.putString("num_items", String.valueOf(adapter.getItem(i).amount));
                                ((AppController) getActivity().getApplication()).setFacebookEvent("InitiateCheckout", params);

                            }

                            if (results != null) {
                                footerView.setConvenienceFee(results.convenience_fee);
                                staticFooterView.setConvenienceFee(results.convenience_fee);

//                                grandTotal += results.convenience_fee;
                            }
                            if (grandTotal != 0) invoiceList.addHeaderView(headerView);
                            invoiceList.addFooterView(footerView);
                            invoiceList.setAdapter(adapter);
                            footerView.setGrandTotal(grandTotal + results.convenience_fee);
                            staticFooterView.setGrandTotal(grandTotal + results.convenience_fee);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("ddf", String.valueOf(error));
                        progressDialog.dismiss();
                        new AppController().doDialog(getContext(), error.getMessage());
                    }
                });
    }
}
