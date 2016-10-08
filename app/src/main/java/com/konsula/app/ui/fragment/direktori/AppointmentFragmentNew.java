package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AppointmentListModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.adapter.AppointmentListAdapter;
import com.konsula.app.util.RefreshTokenCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Nullable;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class AppointmentFragmentNew extends Fragment  {
    private ListView descriptionListView;
    private TextView monthTextView;
    private AppointmentListAdapter appointmentListAdapter;
    private ProgressDialog dialog;
    private LinearLayout viewNodata;
    private ArrayList<AppointmentListModel.Result> appointmentList = new ArrayList<AppointmentListModel.Result>();
    private CancelableCallback cancelableCallback;
    private SwipeRefreshLayout swipeContainer;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_new, null, false);
        appointmentListAdapter = new AppointmentListAdapter(getContext());
        monthTextView = (TextView) view.findViewById(R.id.month_text_view);
        descriptionListView = (ListView) view.findViewById(R.id.description_list_view);
        viewNodata = (LinearLayout) view.findViewById(R.id.view_nodata);
        appointmentList();
        descriptionListView.setAdapter(appointmentListAdapter);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                appointmentList();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    public void appointmentList() {
        try {
            dialog = AppController.createProgressDialog(getActivity());
            dialog.setCancelable(false);
            dialog.show();
            cancelableCallback = new CancelableCallback<AppointmentListModel>() {

                @Override
                protected void onSuccess(AppointmentListModel appointmentListModel, Response response) {
                    boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(appointmentListModel.messages, response, getActivity(), new RefreshTokenCallback() {
                        @Override
                        public void onRefreshTokenComplete() {
                            dialog.dismiss();
                            appointmentList();
                        }


                    });
                    if (isTokenValid) {
                        listener.onSuccessLoadMenu();
                        swipeContainer.setRefreshing(false);
                        if (appointmentListModel.results.size() == 0) {
                            viewNodata.setVisibility(View.VISIBLE);
                        } else {
                            appointmentList.clear();
                            appointmentList.addAll(appointmentListModel.results);
                            setMonth(Calendar.getInstance());
                        }
                        dialog.dismiss();
                    }
                }

                @Override
                protected void onFailure(RetrofitError error) {
                    listener.onSuccessLoadMenu();
                    swipeContainer.setRefreshing(false);
                    ((AppController) ((Activity) getContext()).getApplication()).doDialog(getActivity(), getResources().getString(R.string.no_connection_error_message));
                }
            };
            NetworkManager.getNetworkService(getContext()).appointmentList(((AppController) getActivity().getApplication()).getSessionManager().getToken(), cancelableCallback);
        } catch (Exception E) {
            dialog.dismiss();
        }
    }

    private void setMonth(Calendar calendar) {
        appointmentListAdapter.clear();
        for (AppointmentListModel.Result item : appointmentList) {
            try {
                Calendar scheduleDate = Calendar.getInstance();
                scheduleDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(item.schedule_date));
                if (scheduleDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    appointmentListAdapter.add(item);
                }
            } catch (Exception e) {

            }
        }
        Configuration conf = getResources().getConfiguration();
        monthTextView.setText(new SimpleDateFormat("MMMM yyyy", conf.locale.getLanguage().equals("") ? new Locale("in_ID") : conf.locale).format(calendar.getTime()));
    }


//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//
//    @Override
//    public void onPageSelected(int position) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MONTH, position);
//        setMonth(calendar);
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }


}
