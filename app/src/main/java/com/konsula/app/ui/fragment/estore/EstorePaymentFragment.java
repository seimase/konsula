package com.konsula.app.ui.fragment.estore;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AvailablePaymentMethodModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.AvailablePaymentMethodAdapter;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.ui.view.EstorePaymentStepView;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SamuelSonny on 04-May-16.
 */
public class EstorePaymentFragment extends Fragment{
    private AlertDialog dialog;
    private ListView listPayment;
    private AvailablePaymentMethodAdapter adapter;
    private TextView txtGrandTotal;
    private int selectedPaymentMethod = -1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estore_payment, null);
        listPayment = (ListView) view.findViewById(R.id.list_payment);
        adapter = new AvailablePaymentMethodAdapter(getContext());
        txtGrandTotal = (TextView) view.findViewById(R.id.txt_grand_total);
        listPayment.setAdapter(adapter);
        txtGrandTotal.setText(AppController.getInstance().getDefaultPriceFormatFloat("IDR ", EstoreReviewOrderFragment.getGrandTotal()));
        initPaymentSelection();
        return view;
    }

    public void initPaymentSelection() {
        NetworkManager.getNetworkService(getContext()).availablePaymentMethod(((AppController) ((Activity) getContext()).getApplication()).getSessionManager().getToken(), new Callback<AvailablePaymentMethodModel>() {
            @Override
            public void success(AvailablePaymentMethodModel availablePaymentMethodModel, Response response) {
                boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).
                        isTokenValid(availablePaymentMethodModel.messages, response, getContext(), new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                initPaymentSelection();
                            }
                        });
                if (isTokenValid) {
                    adapter.clear();
                    adapter.addAll(availablePaymentMethodModel.results);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void seeDetail(View view){
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(getContext(), getResources().getColor(R.color.colorPrimaryEstore));
        View invoiceDetail = LayoutInflater.from(getContext()).inflate(R.layout.view_estore_invoice_detail, null);

        builder.setView(invoiceDetail);
        builder.setTitle(R.string.estore_payment_summary);
        dialog = builder.create();
        dialog.show();
    }

    public void closeDetail(View view){
        dialog.dismiss();
    }
}
