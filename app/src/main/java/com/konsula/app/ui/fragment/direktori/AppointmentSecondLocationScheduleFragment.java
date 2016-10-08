package com.konsula.app.ui.fragment.direktori;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.konsula.app.ui.activity.direktori.BookingInformationActivity;
import com.konsula.app.R;

/**
 * Created by konsula on 12/14/2015.
 */
public class AppointmentSecondLocationScheduleFragment extends Fragment {

    private GridView morningGridView, dayGridView, noonGridView, eveningGridView;

    private String[] morningPeriods = new String[]{"11:14", "11:30", "12:00", "12:30", "13:05", "13:15", "13:40"};
    private String[] dayPeriods =  new String[]{"19:50", "20:40", "21:15"};
    private String[] noonPeriods = new String[]{"15:30", "16:40"};
    private String[] eveningPeriods = new String[]{"07:00", "08:15", "09:30", "09:45", "10:30"};

    public AppointmentSecondLocationScheduleFragment() { // default
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_location, container, false);

        morningGridView = (GridView) view.findViewById(R.id.location_period_morning_schedule_grid_view);
        dayGridView = (GridView) view.findViewById(R.id.location_period_day_schedule_grid_view);
        noonGridView = (GridView) view.findViewById(R.id.location_period_noon_schedule_grid_view);
        eveningGridView = (GridView) view.findViewById(R.id.location_period_evening_schedule_grid_view);

        // set adapter
        morningGridView.setAdapter(new AppointmentScheduleAdapter(morningPeriods));
        dayGridView.setAdapter(new AppointmentScheduleAdapter(dayPeriods));
        noonGridView.setAdapter(new AppointmentScheduleAdapter(noonPeriods));
        eveningGridView.setAdapter(new AppointmentScheduleAdapter(eveningPeriods));

        return view;
    }

    // create item menu
    private class AppointmentScheduleAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private String[] list;

        // Constructor
        public AppointmentScheduleAdapter(String[] list) {
            this.inflater = LayoutInflater.from(getActivity());
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int position) {
            return list[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String time = list[position];
            final ViewHolder mHolder;
            if (convertView == null) {
                mHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_appointment_period_time, null);
                mHolder.timeLabel = (TextView) convertView.findViewById(R.id.item_appointment_period_time_text_view);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            int activeColor = getActivity().getResources().getColor(R.color.date_active);
            int inactiveColor = getActivity().getResources().getColor(R.color.date_inactive);

            if ((position % 2) == 0)
                mHolder.timeLabel.setTextColor(activeColor);
            else
                mHolder.timeLabel.setTextColor(inactiveColor);
            mHolder.timeLabel.setText(time);

            // set listener
            mHolder.timeLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), BookingInformationActivity.class);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView timeLabel;
        }
    }
}
