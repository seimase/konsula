package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.service.model.DoctorReviewModel;
import com.konsula.app.service.model.Messages;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.direktori.AppointmentReviewActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.HistoryModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.direktori.AppoitmentReviewLihatActivity;
import com.konsula.app.ui.activity.direktori.DoctorReviewActivity;
import com.konsula.app.ui.activity.direktori.DoctorReviewHistoryActivity;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/2/2015.
 */
public class HistoryFragment extends Fragment {

    private ListView historyListView;
    private ArrayList<HistoryModel.Result> listSchedule = new ArrayList<HistoryModel.Result>();
    private HistoryAdapter adapter;

    private RelativeLayout layoutloading, layout_refresh;
    private Button refresh;
    private String uuid;
    private LinearLayout viewNodata;
    private SwipeRefreshLayout swipeContainer;
    private String currentLanguage;

    public HistoryFragment() {
        // Required empty public constructor
    }

    private CancelableCallback cancelableCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout for this fragment

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        historyListView = (ListView) view.findViewById(R.id.history_list_view);
        layoutloading = (RelativeLayout) view.findViewById(R.id.l_loading);
        layout_refresh = (RelativeLayout) view.findViewById(R.id.layout_refresh);
        refresh = (Button) view.findViewById(R.id.refresh);
        viewNodata = (LinearLayout) view.findViewById(R.id.view_nodata);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });
        // set listener


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

    // adapter
    private class HistoryAdapter extends ArrayAdapter<HistoryModel.Result> {

        private ArrayList<HistoryModel.Result> listHistory;

        public HistoryAdapter(ArrayList<HistoryModel.Result> listHistory) {
            super(getActivity(), R.layout.item_history, listHistory);
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
                LayoutInflater view = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            holder.dateHistoryTextView.setText(((AppController) getActivity().getApplication()).datehistory(item.schedule_date));
            holder.purposeHistoryTextView.setText(item.reason);
            holder.locationHistoryTextView.setText(item.practice_name);
            holder.timeHistoryTextView.setText(((AppController) getActivity().getApplication()).timehistory(item.hour_start));
            uuid = item.unique_key;
            if (item.ready_to_review) {
                if (item.review_id == null && item.allow_to_review) {
                    holder.reviewHistoryTextView.setVisibility(View.VISIBLE);
                    holder.reviewHistoryTextView.setText(getResources().getString(R.string.berikan_review));
                    holder.reviewHistoryTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (AppController.checkConnection(getActivity())) {
                                Intent intent = new Intent(getActivity(), AppointmentReviewActivity.class);
                                intent.putExtra(AppointmentReviewActivity.PRACTICE_ID, String.valueOf(item.practice_id));
                                intent.putExtra(AppointmentReviewActivity.APPOINTMENT_ID, String.valueOf(item.appointment_id));
                                intent.putExtra(AppointmentReviewActivity.MEMBER_ID, String.valueOf(item.member_id));
                                intent.putExtra(AppointmentReviewActivity.DOCTOR_ID, String.valueOf(item.doctor_id));
                                intent.putExtra(AppointmentReviewActivity.UNIQUE_KEY, item.unique_key);
                                startActivityForResult(intent, 10001);
                            } else {
                                ((AppController) ((Activity) getContext()).getApplication()).doDialog(getActivity(), getActivity().getResources().getString(R.string.no_connection));
                            }

                        }
                    });
                } else if (item.review_id != null) {
                    holder.reviewHistoryTextView.setVisibility(View.VISIBLE);
                    holder.reviewHistoryTextView.setText(getResources().getString(R.string.lihat_review));
                    holder.reviewHistoryTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), AppoitmentReviewLihatActivity.class);
                            intent.putExtra(AppointmentReviewActivity.UNIQUE_KEY, item.unique_key);
                            startActivityForResult(intent, 10001);
                        }
                    });
                } else {
                    holder.reviewHistoryTextView.setVisibility(View.GONE);
                }

            } else {
                holder.reviewHistoryTextView.setVisibility(View.GONE);
                /*holder.reviewHistoryTextView.setText(getResources().getString(R.string.lihat_review));
                SpannableString content = new SpannableString("" + holder.reviewHistoryTextView.getText().toString());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                holder.reviewHistoryTextView.setText(content);
                holder.reviewHistoryTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), AppoitmentReviewLihatActivity.class);
                        intent.putExtra(AppointmentReviewActivity.UNIQUE_KEY, item.unique_key);
                        startActivityForResult(intent, 10001);
                        //  getDoctorReview(item.doctor_id, item.doctor_name);
                    }
                });*/
            }


            return convertView;
        }
    }

    private class ViewHolder {
        TextView doctorNameHistoryTextView, dateHistoryTextView, purposeHistoryTextView,
                locationHistoryTextView, timeHistoryTextView, reviewHistoryTextView;
    }

    public void getdata() {
        cancelableCallback = new CancelableCallback<HistoryModel>() {


            @Override
            protected void onSuccess(HistoryModel historyModel, Response response) {
                boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(historyModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getdata();
                    }


                });
                if (isTokenValid) {
                    swipeContainer.setRefreshing(false);
                    layoutloading.setVisibility(View.GONE);
                    historyListView.setVisibility(View.VISIBLE);
                    layout_refresh.setVisibility(View.GONE);


                    if (historyModel.results.isEmpty()) {
                        viewNodata.setVisibility(View.VISIBLE);
                        Messages messages = historyModel.messages;
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
            protected void onFailure(RetrofitError error) {
                swipeContainer.setRefreshing(false);
                ((AppController) ((Activity) getContext()).getApplication()).doDialog(getActivity(), getActivity().getResources().getString(R.string.no_connection));

            }
        };
        refresh.setVisibility(View.GONE);
        NetworkManager.getNetworkService(getActivity()).getHistory(
                ((AppController) getActivity().getApplication())
                        .getSessionManager().getToken(), cancelableCallback);
    }

    private void getDoctorReview(final int doctorId, final String doctorName) {
        cancelableCallback = new CancelableCallback<DoctorReviewModel>() {

            @Override
            protected void onSuccess(DoctorReviewModel doctorReviewModel, Response response) {
                boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(doctorReviewModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getDoctorReview(doctorId, doctorName);
                    }

                });
                if (isTokenValid) {
                    ((AppController) getActivity().getApplication()).getSessionManager().setDoctorReview(doctorReviewModel.results);
                    if (doctorReviewModel.results == null) {
                        Intent intent = new Intent(getActivity(), AppoitmentReviewLihatActivity.class);
                        intent.putExtra(AppointmentReviewActivity.UNIQUE_KEY, uuid);
                        startActivityForResult(intent, 10001);
                    } else {
                        Intent a = new Intent(getActivity(), DoctorReviewHistoryActivity.class);
                        a.putExtra(DoctorReviewActivity.DOCTOR_NAME, doctorName);
                        a.putExtra(DoctorReviewActivity.DOCTOR_ID, doctorId);
                        getActivity().startActivity(a);
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                ((AppController) ((Activity) getContext()).getApplication()).doDialog(getActivity(), getActivity().getResources().getString(R.string.no_connection));


            }
        };
        NetworkManager.getNetworkService(getActivity().getApplicationContext()).getDoctorReview(((AppController) getActivity().getApplication()).getSessionManager().getToken(), currentLanguage, doctorId,0,
                cancelableCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            getdata();
        }
        // recreate your fragment here
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }
}
