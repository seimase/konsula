package com.konsula.app.ui.activity.direktori;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DoctorReviewModel;
import com.konsula.app.service.model.HistoryModel;
import com.konsula.app.service.model.Messages;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 29/03/2016.
 */
public class AppointmentHistoryActivity extends AppCompatActivity {
    private ListView historyListView;
    private ArrayList<HistoryModel.Result> listSchedule = new ArrayList<HistoryModel.Result>();
    private HistoryAdapter adapter;
    private ImageButton backButton;

    private RelativeLayout layoutloading, layout_refresh;
    private Button refresh;
    private String uuid;
    private String currentLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_history);

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        historyListView = (ListView) findViewById(R.id.history_list_view);
        layoutloading = (RelativeLayout) findViewById(R.id.l_loading);
        layout_refresh = (RelativeLayout) findViewById(R.id.layout_refresh);
        refresh = (Button) findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // set listener

        getdata();
    }

    // adapter
    private class HistoryAdapter extends ArrayAdapter<HistoryModel.Result> {

        private ArrayList<HistoryModel.Result> listHistory;

        public HistoryAdapter(ArrayList<HistoryModel.Result> listHistory) {
            super(getApplicationContext(), R.layout.item_history, listHistory);
            this.listHistory = listHistory;
        }

        @Override
        public int getCount() {
            return listHistory.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater view = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = view.inflate(R.layout.item_history, null);

                holder = new ViewHolder();

                holder.doctorNameHistoryTextView = (TextView) convertView.findViewById(R.id.doctor_name_item_history_text_view);
                holder.dateHistoryTextView = (TextView) convertView.findViewById(R.id.doctor_date_item_history_text_view);
                holder.purposeHistoryTextView = (TextView) convertView.findViewById(R.id.doctor_purpose_item_history_text_view);
                holder.locationHistoryTextView = (TextView) convertView.findViewById(R.id.location_name_item_history_text_view);
                holder.timeHistoryTextView = (TextView) convertView.findViewById(R.id.doctor_time_item_history_text_view);
                holder.reviewHistoryTextView = (TextView) convertView.findViewById(R.id.doctor_action_review_item_history_text_view);

                convertView.setTag(holder);

            }

            final HistoryModel.Result item = listSchedule.get(position);

            holder = (ViewHolder) convertView.getTag();

            holder.doctorNameHistoryTextView.setText(item.doctor_name);
            holder.dateHistoryTextView.setText(((AppController) getApplication()).datehistory(item.schedule_date));
            holder.purposeHistoryTextView.setText(item.reason);
            holder.locationHistoryTextView.setText(item.practice_name);
            holder.timeHistoryTextView.setText(((AppController) getApplication()).timehistory(item.hour_start));
            uuid = item.unique_key;
            if (item.ready_to_review) {
                if (item.review_id == null) {
                    holder.reviewHistoryTextView.setVisibility(View.VISIBLE);
                    holder.reviewHistoryTextView.setText(getResources().getString(R.string.berikan_review));
                    holder.reviewHistoryTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(AppointmentHistoryActivity.this, AppointmentReviewActivity.class);
                            intent.putExtra(AppointmentReviewActivity.PRACTICE_ID, String.valueOf(item.practice_id));
                            intent.putExtra(AppointmentReviewActivity.APPOINTMENT_ID, String.valueOf(item.appointment_id));
                            intent.putExtra(AppointmentReviewActivity.MEMBER_ID, String.valueOf(item.member_id));
                            intent.putExtra(AppointmentReviewActivity.DOCTOR_ID, String.valueOf(item.doctor_id));
                            intent.putExtra(AppointmentReviewActivity.UNIQUE_KEY, item.unique_key);
                            startActivityForResult(intent, 10001);


                        }
                    });
                } else {
                    holder.reviewHistoryTextView.setVisibility(View.VISIBLE);
                    holder.reviewHistoryTextView.setText(getResources().getString(R.string.lihat_review));
                    holder.reviewHistoryTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getDoctorReview(item.doctor_id, item.doctor_name);

                        }
                    });
                }

            } else {
                holder.reviewHistoryTextView.setVisibility(View.VISIBLE);
                holder.reviewHistoryTextView.setText(getResources().getString(R.string.lihat_review));
                holder.reviewHistoryTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AppointmentHistoryActivity.this, AppoitmentReviewLihatActivity.class);
                        intent.putExtra(AppointmentReviewActivity.UNIQUE_KEY, item.unique_key);
                        startActivityForResult(intent, 10001);
                        //  getDoctorReview(item.doctor_id, item.doctor_name);
                    }
                });
            }


            return convertView;
        }
    }

    private class ViewHolder {
        TextView doctorNameHistoryTextView, dateHistoryTextView, purposeHistoryTextView,
                locationHistoryTextView, timeHistoryTextView, reviewHistoryTextView;
    }

    private void getdata() {
        refresh.setVisibility(View.GONE);
        NetworkManager.getNetworkService(getApplicationContext()).getHistory(
                ((AppController) getApplication())
                        .getSessionManager().getToken(),
                new Callback<HistoryModel>() {
                    @Override
                    public void success(HistoryModel historyModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).isTokenValid(historyModel.messages, response, AppointmentHistoryActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                getdata();
                            }


                        });
                        if (isTokenValid) {
                            layoutloading.setVisibility(View.GONE);
                            historyListView.setVisibility(View.VISIBLE);
                            layout_refresh.setVisibility(View.GONE);


                            if (historyModel.results.isEmpty()) {
                                Messages messages = historyModel.messages;
                                Toast.makeText(getApplicationContext(), messages.response_text, Toast.LENGTH_SHORT).show();
                            } else {
                                listSchedule.clear();
                                listSchedule.addAll(historyModel.results);
                                HistoryAdapter adapter = new HistoryAdapter(listSchedule);
                                historyListView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        layoutloading.setVisibility(View.GONE);
                        refresh.setVisibility(View.VISIBLE);

                        DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(AppointmentHistoryActivity.this);
                        builder2.setMessage("Cannot load history. Please check your internet connection.").setPositiveButton("Dismiss", dialogClickListener2).show();

                    }
                });
    }

    private void getDoctorReview(final int doctorId, final String doctorName) {
        NetworkManager.getNetworkService(getApplicationContext()).getDoctorReview(((AppController) getApplication()).getSessionManager().getToken(),currentLanguage , doctorId,0,
                new Callback<DoctorReviewModel>() {
                    @Override
                    public void success(DoctorReviewModel doctorReviewModel, Response response) {
                        ((AppController) getApplication()).getSessionManager().setDoctorReview(doctorReviewModel.results);
                        if (doctorReviewModel.results != null) {
                            Intent a = new Intent(AppointmentHistoryActivity.this, DoctorReviewHistoryActivity.class);
                            a.putExtra(DoctorReviewActivity.DOCTOR_NAME, doctorName);
                            a.putExtra(DoctorReviewActivity.DOCTOR_ID, doctorId);
                            startActivity(a);
                        } else {
                            Intent intent = new Intent(AppointmentHistoryActivity.this, AppoitmentReviewLihatActivity.class);
                            intent.putExtra(AppointmentReviewActivity.UNIQUE_KEY, uuid);
                            startActivityForResult(intent, 10001);
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error Get Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            getdata();
        }
        // recreate your fragment here
    }
}
