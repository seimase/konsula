package com.konsula.app.ui.activity.direktori;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AppointmentListModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.AppointmentListAdapter;
import com.konsula.app.ui.adapter.MonthSliderAdapter;
import com.konsula.app.util.RefreshTokenCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 29/03/2016.
 */
public class AppointmentActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager pager;
    //private GridView calendarGridView;
    private TextView monthTextView;
    private ImageView prevMonthButton;
    private ImageView nextMonthButton;
    private ListView descriptionListView;
    private MonthSliderAdapter monthSliderAdapter;
//    private MonthAdapter monthAdapter;
    private AppointmentListAdapter appointmentListAdapter;
    private ArrayList<AppointmentListModel.Result> appointmentList = new ArrayList<AppointmentListModel.Result>();
    private DisplayMetrics metrics = new DisplayMetrics();

    private ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_appointment);
        pager = (ViewPager) findViewById(R.id.pager);
        monthTextView = (TextView) findViewById(R.id.month_text_view);
        prevMonthButton = (ImageView) findViewById(R.id.prev_month_button);
        nextMonthButton = (ImageView) findViewById(R.id.next_month_button);
        descriptionListView = (ListView) findViewById(R.id.description_list_view);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        appointmentListAdapter = new AppointmentListAdapter(this);
        descriptionListView = (ListView) findViewById(R.id.description_list_view);
        monthSliderAdapter = new MonthSliderAdapter(this, appointmentListAdapter);

        pager.setAdapter(monthSliderAdapter);
        pager.setOnPageChangeListener(this);
        prevMonthButton.setOnClickListener(this);
        nextMonthButton.setOnClickListener(this);
        descriptionListView.setAdapter(appointmentListAdapter);

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setMonth(Calendar.getInstance());
        appointmentList();
    }


    public void appointmentList() {
        try {
            NetworkManager.getNetworkService(getApplicationContext()).appointmentList(((AppController) getApplication()).getSessionManager().getToken(), new Callback<AppointmentListModel>() {
                @Override
                public void success(AppointmentListModel appointmentListModel, Response response) {
                    boolean isTokenValid = ((AppController) getApplication()).
                            isTokenValid(appointmentListModel.messages, response,AppointmentActivity.this, new RefreshTokenCallback() {
                                @Override
                                public void onRefreshTokenComplete() {
                                    appointmentList();
                                }

                            });
                    if (isTokenValid) {
                        appointmentList.clear();
                        appointmentList.addAll(appointmentListModel.results);
                        setMonth(Calendar.getInstance());
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

                    AlertDialog.Builder builder2 = new AlertDialog.Builder(AppointmentActivity.this);
                    builder2.setMessage("Cannot load appointment. Please check your internet connection.").setPositiveButton("Dismiss", dialogClickListener2).show();
                }
            });
        } catch (Exception E) {

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

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, position);
        setMonth(calendar);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
