package com.konsula.app.ui.fragment.appointment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.CancelTeledocModel;
import com.konsula.app.service.model.UpcomingTeledocModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.teledokter.TeledocDetailActivity;
import com.konsula.app.ui.adapter.UpcomingTeledocAdapter;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 14/04/2016.
 */
public class UpcomingTeledocFragment extends Fragment {
    private RecyclerView upcomingListView;
    private String currentLanguage;
    private int currentpage = 1;
    private LinearLayout viewNodata;
    private AlertDialog dialog;
    private CancelableCallback cancelableCallback;
    private SwipeRefreshLayout swipeContainer;
    private UpcomingTeledocAdapter upcomingTeledocAdapter;
    private Button refresh;
    private LinearLayout layoutloading;
    OnSuccessLoadMenuListener listener;
    public interface OnSuccessLoadMenuListener {
        public void onSuccessLoadMenu();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnSuccessLoadMenuListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement" + OnSuccessLoadMenuListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_teledoc_history, container, false);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        upcomingListView = (RecyclerView) view.findViewById(R.id.help_list_view);
        viewNodata = (LinearLayout) view.findViewById(R.id.view_nodata);
        upcomingListView.setHasFixedSize(true);
        upcomingListView.setClickable(true);
        upcomingListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        layoutloading = (LinearLayout) view.findViewById(R.id.l_loading);
        refresh = (Button) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setVisibility(View.GONE);
                layoutloading.setVisibility(View.VISIBLE);
                getUpcomingTeledocList();
            }
        });
        getUpcomingTeledocList();
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUpcomingTeledocList();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    public void getUpcomingTeledocList() {

        cancelableCallback = new CancelableCallback<UpcomingTeledocModel>() {
            @Override
            protected void onSuccess(UpcomingTeledocModel upcomingTeledocModel, Response response) {
                boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(upcomingTeledocModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getUpcomingTeledocList();
                    }

                });
                if (isTokenValid) {
                    listener.onSuccessLoadMenu();
                    swipeContainer.setRefreshing(false);
                    layoutloading.setVisibility(View.GONE);
                    if (upcomingTeledocModel.results.data != null) {
                        upcomingListView.setAdapter(null);
                        upcomingTeledocAdapter = new UpcomingTeledocAdapter(getActivity(), upcomingTeledocModel.results.data, R.layout.item_upcoming_teledok, new UpcomingTeledocAdapter.OnTransactionClicked() {
                            @Override
                            public void onTransactionClick(String uuid) {
                                doCancel(uuid);
                            }
                        });
                        upcomingListView.setAdapter(upcomingTeledocAdapter);
                        upcomingTeledocAdapter.notifyDataSetChanged();
                    } else {
                        upcomingListView.setAdapter(null);
                        viewNodata.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                listener.onSuccessLoadMenu();
                swipeContainer.setRefreshing(false);
                refresh.setVisibility(View.GONE);
                layoutloading.setVisibility(View.GONE);
            }
        };
        NetworkManager.getNetworkService(getContext()).getUpcomingTeledoc(((AppController) getActivity().getApplication()).getSessionManager().getToken(), currentLanguage, currentpage, cancelableCallback);
    }

    public void setcancelteledoc(final String uuid) {
        cancelableCallback = new CancelableCallback<CancelTeledocModel>() {

            @Override
            protected void onSuccess(CancelTeledocModel cancelTeledocModel, Response response) {
                dialog.dismiss();
                if (cancelTeledocModel.messages.response_code == 200) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    getUpcomingTeledocList();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(cancelTeledocModel.messages.response_text).setPositiveButton(getActivity().getResources().getString(R.string.general_text_dismiss), dialogClickListener).show();

                } else {
                    ((AppController) ((Activity) getContext()).getApplication()).doDialog(getActivity(), cancelTeledocModel.messages.response_text);

                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                dialog.dismiss();
            }
        };
        dialog = AppController.createProgressDialog(getActivity());
        dialog.show();
        NetworkManager.getNetworkService(getActivity()).cancelTeledoc(((AppController) getActivity().getApplication()).getSessionManager().getToken(), currentLanguage, uuid, cancelableCallback);

    }

    private void doCancel(final String uuid) {
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(getContext(), getActivity().getResources().getColor(R.color.colorPrimaryDark));
        View invoiceDetail = LayoutInflater.from(getContext()).inflate(R.layout.dialog_teledoc_cancel, null);
        builder.setView(invoiceDetail);
        builder.setTitle(R.string.text_teledoc_batal);
        dialog = builder.create();
        dialog.show();
        dialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setcancelteledoc(uuid);

            }
        });
        dialog.findViewById(R.id.negative_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }

}


