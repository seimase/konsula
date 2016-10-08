package com.konsula.app.ui.activity.direktori;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DoctorModel;
import com.konsula.app.service.model.DoctorScheduleModel;
import com.konsula.app.service.model.SearchKlinikModel;
import com.konsula.app.ui.adapter.DoctorScheduleAdapter;
import com.konsula.app.ui.fragment.direktori.DoctorLocationFragment;

import java.util.ArrayList;

/**
 * Created by konsula on 12/8/2015.
 */
public class ScheduleDoctorActivity extends AppCompatActivity {
    private ListView scheduleListView;
    private ImageButton closeWindow;
    private TextView doctorNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_doctor);
        scheduleListView = (ListView) findViewById(R.id.schedule_doctor_list_view);
        closeWindow = (ImageButton) findViewById(R.id.action_close_schedule_doctor_image_view);
        doctorNameTextView = (TextView) findViewById(R.id.schedule_doctor_header_name_text_view);
        DoctorModel.Results doctorModel = ((AppController) getApplication()).getSessionManager().getDoctorProfile();
        // set listener
        if (doctorModel != null) {
            doctorNameTextView.setText(doctorModel.doctor_name);
            ScheduleDoctorAdapter adapter = new ScheduleDoctorAdapter(new ArrayList<>(doctorModel.practices), doctorModel);
            scheduleListView.setAdapter(adapter);
            closeWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }


    }

    // adapter
    private class ScheduleDoctorAdapter extends ArrayAdapter<DoctorModel.Practice> {

        private ArrayList<DoctorModel.Practice> listSchedule;
        private DoctorModel.Results doctorModel;

        public ScheduleDoctorAdapter(ArrayList<DoctorModel.Practice> listSchedule, DoctorModel.Results doctorModel) {
            super(ScheduleDoctorActivity.this, R.layout.item_show_schedule, listSchedule);
            this.listSchedule = listSchedule;
            this.doctorModel = doctorModel;
        }

        @Override
        public int getCount() {
            return listSchedule.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            if (convertView == null) {
                LayoutInflater view = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = view.inflate(R.layout.item_show_schedule, null);

                holder = new ViewHolder();
                holder.locationNameTextView = (TextView) convertView.findViewById(R.id.location_name_show_schedule_text_view);
                holder.locationAddressTextView = (TextView) convertView.findViewById(R.id.location_address_show_schedule_text_view);
                holder.priceTextView = (TextView) convertView.findViewById(R.id.price_show_schedule_text_view);
                holder.bookLayout = (LinearLayout) convertView.findViewById(R.id.item_show_schedule_book_text_view);
                holder.scheduleLayout = (RecyclerView) convertView.findViewById(R.id.item_show_schedule_detail_container_linear_layout);
                convertView.setTag(holder);

            }

            holder = (ViewHolder) convertView.getTag();

            final DoctorModel.Practice item = listSchedule.get(position);
            holder.locationNameTextView.setText(item.practice_name);
            //   holder.locationNameTextView.setText(item.practice_name+" ("+getApplicationContext().getResources().getString(R.string.lokasi)+" "+(position+1)+")");
            holder.locationAddressTextView.setText(item.location);
            if (item.schedule.rate.equals("")) {
                item.schedule.rate = SearchKlinikModel.NA;
//                } else if (data.rate.equals("0")) {
//                    holder.priceTextView.setText(convertView.getResources().getString(R.string.mulai_dari) + " " + data.rate);
            } else if (item.schedule.rate.contains(" ")) {
                holder.priceTextView.setText(convertView.getResources().getString(R.string.mulai_dari) + " " + convertView.getResources().getString(R.string.gratis_konsultasi));
            } else if ((item.schedule.rate.equals(SearchKlinikModel.NA))) {
                holder.priceTextView.setText(convertView.getResources().getString(R.string.mulai_dari) + " " + SearchKlinikModel.NA);
            } else {
                holder.priceTextView.setText(convertView.getResources().getString(R.string.mulai_dari) + " " + AppController.getInstance().getDefaultPriceFormat(item.schedule.currency, Double.parseDouble(item.schedule.rate)));
            }
//            if(!item.min_rate.equals("N/A"))
//            holder.priceTextView.setText(getString(R.string.mulai_dari)+" "+((AppController)getApplication()).getDefaultPriceFormat(item.currency,Double.parseDouble(item.schedule.rate)));
//            else
//            holder.priceTextView.setText(item.min_rate);
//            holder.scheduleDayNameTextView.setText(item.schedule.summary_schedule_arr.get(0).day);
//            holder.scheduleTimeTextView.setText(item.schedule.summary_schedule_arr.get(0).hour + " WIB");

            if (item.schedule.summary_schedule_arr != null) {
                holder.scheduleLayout.setLayoutManager(new LinearLayoutManager(ScheduleDoctorActivity.this, LinearLayoutManager.VERTICAL, false));
                holder.scheduleLayout.setHasFixedSize(true);
                holder.scheduleLayout.setAdapter(new DoctorScheduleAdapter(item.schedule.summary_schedule_arr, R.layout.item_doctor_schedule));
            }


            holder.bookLayout.setVisibility(item.online.equals("Y") ? View.VISIBLE : View.GONE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.online.equals("Y")) {
                        Intent intent = new Intent(ScheduleDoctorActivity.this, AppointmentDoctorActivity.class);
                        intent.putExtra(AppointmentDoctorActivity.DOCTOR_NAME, doctorModel.doctor_name);
                        intent.putExtra(AppointmentDoctorActivity.DOCTOR_IMAGE, doctorModel.photos.primary.medium_image);
                        String special = doctorModel.specialities!=null?doctorModel.specialities.get(0).specialization.specialization_bahasa:doctorModel.job_bahasa;
                        if (special == null) {
                            special = "";
                        } else {
                            if (((AppController) getApplication()).getSessionManager().getLanguage().equalsIgnoreCase("en")) {
                                special = doctorModel.specialities!=null?doctorModel.specialities.get(0).specialization.specialization_english:doctorModel.job_english;
                            }
                        }
                        intent.putExtra(AppointmentDoctorActivity.DOCTOR_TITLE, special);
                        intent.putExtra(AppointmentDoctorActivity.LOCATION_NAME, item.practice_name);
                        intent.putExtra(AppointmentDoctorActivity.LOCATION_ADDRESS, item.location);
                        intent.putExtra(AppointmentDoctorActivity.DOCTOR_ID, doctorModel.doctor_id);
                        intent.putExtra(AppointmentDoctorActivity.PRACTICE_ID, item.practice_id);
                        startActivity(intent);
                    }
                }
            });

            return convertView;
        }
    }

    private class ViewHolder {
        TextView locationNameTextView, locationAddressTextView, priceTextView;
        LinearLayout bookLayout;
        RecyclerView scheduleLayout;
    }


    private void setScheduleTextViewColor(TextView view, String schedulePeriod) {
        String noSchedule = getResources().getString(R.string.tutup);

        view.setText(schedulePeriod);
        if (noSchedule.equals(schedulePeriod))
            view.setTextColor(getResources().getColor(R.color.red_deep));
        else
            view.setTextColor(getResources().getColor(R.color.textColorBase));
    }
}
