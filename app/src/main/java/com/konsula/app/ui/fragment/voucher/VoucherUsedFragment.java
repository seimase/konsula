package com.konsula.app.ui.fragment.voucher;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.BannerHomeModel;
import com.konsula.app.service.model.MyVoucherModel;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.adapter.VoucherAdapter;
import com.konsula.app.ui.adapter.VoucherUsedAdapter;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 27/05/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.BannerHomeModel;
import com.konsula.app.service.model.MyVoucherModel;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.adapter.VoucherAdapter;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 27/05/2016.
 */
public class VoucherUsedFragment extends Fragment {
    private RecyclerView voucherListView;
    private LinearLayout layoutloading;
    private Button refresh;
    private LinearLayout layout_no_data;
    private SwipeRefreshLayout swipeContainer;
    private CancelableCallback cancelableCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_voucher_valid, container, false);
        layoutloading = (LinearLayout) view.findViewById(R.id.l_loading);
        refresh = (Button) view.findViewById(R.id.refresh);
        voucherListView = (RecyclerView) view.findViewById(R.id.voucher_list_view);
        layout_no_data = (LinearLayout) view.findViewById(R.id.view_nodata);
        voucherListView.setHasFixedSize(true);
        voucherListView.setClickable(true);
        voucherListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutloading.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
                getdata();
            }
        });
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
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
        return view;
    }

    public void getdata() {
        swipeContainer.setRefreshing(false);
        cancelableCallback = new CancelableCallback<MyVoucherModel>() {
            @Override
            protected void onSuccess(MyVoucherModel myVoucherModel, Response response) {
                final boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(myVoucherModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getdata();
                    }


                });
                if (isTokenValid) {
                    layoutloading.setVisibility(View.GONE);
                    if (myVoucherModel.results.used == null) {
                        layout_no_data.setVisibility(View.VISIBLE);
                    } else {
                        voucherListView.setAdapter(new VoucherUsedAdapter(getActivity(), myVoucherModel.results.used, R.layout.item_voucher));
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                swipeContainer.setRefreshing(false);
                swipeContainer.setVisibility(View.GONE);
                layoutloading.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            }
        };

        NetworkManager.getNetworkService(getActivity()).getVoucher(
                ((AppController) getActivity().getApplication())
                        .getSessionManager().getToken(),
                cancelableCallback);


    }

}
