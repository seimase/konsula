package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AppointmentListModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.AppointmentListAdapter;
import com.konsula.app.ui.adapter.MonthAdapter;
import com.konsula.app.util.Converter;
import com.konsula.app.util.RefreshTokenCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AppointmentFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener, View.OnClickListener {
    private GridView calendarGridView;
    private TextView monthTextView;
    private ImageView prevMonthButton;
    private ImageView nextMonthButton;
    private ListView descriptionListView;
    private MonthAdapter monthAdapter;
    private AppointmentListAdapter appointmentListAdapter;
    private ArrayList<AppointmentListModel.Result> appointmentList = new ArrayList<AppointmentListModel.Result>();
    private DisplayMetrics metrics = new DisplayMetrics();
    private Calendar calendar;
    private boolean gridViewResized;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, null, false);

        calendarGridView = (GridView) view.findViewById(R.id.calendar_grid_view);
        monthTextView = (TextView) view.findViewById(R.id.month_text_view);
        prevMonthButton = (ImageView) view.findViewById(R.id.prev_month_button);
        nextMonthButton = (ImageView) view.findViewById(R.id.next_month_button);
        descriptionListView = (ListView) view.findViewById(R.id.description_list_view);
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        calendar = Calendar.getInstance();
        appointmentListAdapter = new AppointmentListAdapter(getContext());
        descriptionListView = (ListView) view.findViewById(R.id.description_list_view);

        calendarGridView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        prevMonthButton.setOnClickListener(this);
        nextMonthButton.setOnClickListener(this);
        descriptionListView.setAdapter(appointmentListAdapter);

        setMonth();
        appointmentList();
        return view;
    }

    public void appointmentList() {
        try {
            NetworkManager.getNetworkService(getContext()).appointmentList(((AppController) getActivity().getApplication()).getSessionManager().getToken(), new Callback<AppointmentListModel>() {
                @Override
                public void success(AppointmentListModel appointmentListModel, Response response) {
                    boolean isTokenValid = ((AppController)((Activity)getContext()).getApplication()).isTokenValid(appointmentListModel.messages, response,getActivity(), new RefreshTokenCallback() {
                        @Override
                        public void onRefreshTokenComplete() {
                            appointmentList();
                        }

                    });
                    if(isTokenValid) {
                        appointmentList.clear();
                        appointmentList.addAll(appointmentListModel.results);
                        setMonth();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                    builder2.setMessage("Cannot load appointment. Please check your internet connection.").setPositiveButton("Dismiss", dialogClickListener2).show();
                }
            });
        } catch (Exception E) {

        }
    }

    private void setMonth() {
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
        monthAdapter = new MonthAdapter(getContext(), appointmentListAdapter, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), metrics) {
            @Override
            protected void onDate(int[] date, int position, View item) {

            }
        };
        calendarGridView.setAdapter(monthAdapter);
        gridViewResized = false;
    }

    @Override
    public void onGlobalLayout() {
        if (!gridViewResized && monthAdapter != null) {
            gridViewResized = true;
            resizeGridView(calendarGridView, monthAdapter.getDaysShown(), 7);
        }
    }

    private void resizeGridView(GridView gridView, int items, int columns) {
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        int oneRowHeight = (gridView.getWidth() / 7);
        int rows = (int) (items / columns);
        params.height = oneRowHeight * rows - Converter.dpToPx(getContext(), 27);
        gridView.setLayoutParams(params);
    }

    @Override
    public void onClick(View view) {
        if (view == prevMonthButton) {
            calendar.add(Calendar.MONTH, -1);
            setMonth();
        } else if (view == nextMonthButton) {
            calendar.add(Calendar.MONTH, 1);
            setMonth();
        }
    }
}
