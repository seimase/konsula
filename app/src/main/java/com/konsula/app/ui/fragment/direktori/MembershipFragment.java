package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.CheckPendingTransactionModel;
import com.konsula.app.service.model.MembershipModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.payment.PaymentConfirmActivity;
import com.konsula.app.ui.activity.membership.MembershipSubActivity;
import com.konsula.app.ui.activity.payment.PaymentSelectionMembershipActivity;
import com.konsula.app.ui.activity.membership.RenewActivity;
import com.konsula.app.ui.custom.CustomtextView;
import com.konsula.app.util.RefreshTokenCallback;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/2/2015.
 */
public class MembershipFragment extends Fragment {
    private Button btnUpgrade;
    private CustomtextView tvStatus, tvDate;
    private LinearLayout layoutloading;
    private LinearLayout l_view;
    private Button refresh;
    private AppController appController;
    private String currentLanguage;
    private CancelableCallback cancelableCallback;
    private LinearLayout view_promo;
    private TextView promo_header1;
    private TextView promo_code;
    private TextView promo_header2;
    private ProgressBar progressBar;

    public MembershipFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_membership, container, false);
        layoutloading = (LinearLayout) view.findViewById(R.id.l_loading);
        l_view = (LinearLayout) view.findViewById(R.id.l_view);
        refresh = (Button) view.findViewById(R.id.refresh);
        tvStatus = (CustomtextView) view.findViewById(R.id.tvStatus);
        tvDate = (CustomtextView) view.findViewById(R.id.tvDate);
        view_promo =(LinearLayout) view.findViewById(R.id.view_promo);
        promo_header1 =(TextView) view.findViewById(R.id.promo_header1);
        promo_code =(TextView) view.findViewById(R.id.promo_code);
        promo_header2 =(TextView) view.findViewById(R.id.promo_header2);
        appController = new AppController();
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        btnUpgrade = (Button) view.findViewById(R.id.btnUpgrade);
        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getdatapending();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l_view.setVisibility(View.INVISIBLE);
                layoutloading.setVisibility(View.VISIBLE);
                getData();
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setProgress(20);
        progressBar.setSecondaryProgress(50);

        getData();

        ((AppController) getActivity().getApplication()).setMixpanel("Click Upgrade Membership Summary");

        return view;
    }

    private void getData() {
        cancelableCallback = new CancelableCallback<MembershipModel>() {
            @Override
            protected void onSuccess(MembershipModel membershipModel, Response response) {
                final boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(membershipModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getData();
                    }


                });
                if (isTokenValid) {
                    layoutloading.setVisibility(View.GONE);
                    refresh.setVisibility(View.GONE);
                    l_view.setVisibility(View.VISIBLE);
                    tvStatus.setText(membershipModel.results.plan_name);
                    if (membershipModel.results.end_date.equals("")) {
                        tvStatus.setText(membershipModel.results.plan_name);
                        tvDate.setText("-");
                    } else {
                        tvDate.setText(appController.dateBirth(membershipModel.results.end_date));
                    }
                    if (membershipModel.results.plan_name.equals("BASIC")) {
                        btnUpgrade.setText("UPGRADE");
                        //  tvStatus.setBackgroundResource(R.drawable.text_border_membership_white);
                    } else if (membershipModel.results.plan_name.contains("PLUS")) {
                        btnUpgrade.setText(getResources().getString(R.string.text_renew));
                        //  tvStatus.setBackgroundResource(R.drawable.text_border_membership);
                    }
                    if (membershipModel.results.promocode != null){
                        view_promo.setVisibility(View.VISIBLE);
                        promo_header1.setText(membershipModel.results.promocode.start_message);
                        promo_code.setText(membershipModel.results.promocode.promo_code);
                        promo_header2.setText(membershipModel.results.promocode.end_message);
                    }else {
                        view_promo.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                layoutloading.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            }
        };
        NetworkManager.getNetworkService(getActivity().getApplication())
                .getCurrentPlan(((AppController) getActivity().getApplication())
                        .getSessionManager().getToken(),currentLanguage, cancelableCallback);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }

    private void getdatapending() {
        doloading();
        cancelableCallback = new CancelableCallback<CheckPendingTransactionModel>() {

            @Override
            protected void onSuccess(CheckPendingTransactionModel checkPendingTransactionModel, Response response) {
                final boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(checkPendingTransactionModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        doneloading();
                        getdatapending();
                    }


                });
                if (isTokenValid) {
                    doneloading();
                    if (checkPendingTransactionModel.results != null) {
                        if (checkPendingTransactionModel.results.payment_status.equals("unpaid")){
                            Intent intent = new Intent(getActivity(), PaymentSelectionMembershipActivity.class);
                            intent.putExtra(PaymentSelectionMembershipActivity.TAG_PAYMENT_UUID, checkPendingTransactionModel.results.payment_uuid);
                            startActivity(intent);
                        }else if (checkPendingTransactionModel.results.payment_status.equals("verifying")){
                            Intent intent = new Intent(getActivity(), PaymentConfirmActivity.class);
                            intent.putExtra(PaymentConfirmActivity.payment_uuid, checkPendingTransactionModel.results.payment_uuid);
                            intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, false);
                            startActivity(intent);
                        }else if (checkPendingTransactionModel.results.payment_status.equals("confirmed")){
                            Intent intent = new Intent(getActivity(), PaymentConfirmActivity.class);
                            intent.putExtra(PaymentConfirmActivity.payment_uuid, checkPendingTransactionModel.results.payment_uuid);
                            intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, false);
                            startActivity(intent);
                        }

                    } else {
                        if (btnUpgrade.getText().toString().equals("UPGRADE")) {
                            //  Toast.makeText(getActivity(),getResources().getString(R.string.hit_segera_hadir),Toast.LENGTH_SHORT).show();
                            ((AppController) getActivity().getApplication()).setMixpanel("Click Upgrade Membership Detail");
                            Intent i = new Intent(getActivity(), RenewActivity.class);
                            i.putExtra("title", "" + btnUpgrade.getText().toString());
                            startActivity(i);
                        } else {
                            //  Intent i = new Intent(getActivity(), RenewActivity.class);
                            Intent i = new Intent(getActivity(), MembershipSubActivity.class);
                            i.putExtra("title", "" + btnUpgrade.getText().toString());
                            startActivity(i);
//                    ((AppController) ((Activity) getContext()).getApplication()).doDialog(getActivity(),getResources().getString(R.string.hit_segera_hadir));

                        }
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                doneloading();
                l_view.setVisibility(View.INVISIBLE);
                layoutloading.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            }
        };

        NetworkManager.getNetworkService(getActivity()).getPendingMembershipTransaction(
                ((AppController) getActivity().getApplication())
                        .getSessionManager().getToken(), currentLanguage,
                cancelableCallback);
    }

    private void doloading() {
        setClickableRecursive(getActivity().findViewById(android.R.id.content), false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void doneloading() {
        setClickableRecursive(getActivity().findViewById(android.R.id.content), true);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public static void setClickableRecursive(View view, boolean isClickable) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setClickableRecursive(group.getChildAt(i), isClickable);
            }
        } else {
            view.setClickable(isClickable);
        }
    }

}
