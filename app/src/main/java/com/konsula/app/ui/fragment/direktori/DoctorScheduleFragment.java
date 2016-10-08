package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.ui.activity.direktori.BookingInformationActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.GeneralCheckModel;
import com.konsula.app.service.model.DoctorScheduleModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.custom.MyWrappableGridView;
import com.konsula.app.util.RefreshTokenCallback;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/14/2015.
 */
@SuppressWarnings("ValidFragment")
public class DoctorScheduleFragment extends Fragment {

    public interface OnArrowClickedListener {
        public void onArrowRightClicked();

        public void onArrowLeftClicked();
    }

    private OnArrowClickedListener listener;

    public void setListener(OnArrowClickedListener listener) {
        this.listener = listener;
    }

    DoctorScheduleModel.Result mResource;

    private GridView morningGridView, dayGridView, noonGridView, eveningGridView;

    /*private String[] morningPeriods = new String[]{"07:00", "08:15", "09:30", "09:45", "10:30"};
    private String[] dayPeriods = new String[]{"11:14", "11:30", "12:00", "12:30", "13:05", "13:15", "13:40"};
    private String[] noonPeriods = new String[]{"15:30", "16:40"};
    private String[] eveningPeriods = new String[]{"19:50", "20:40", "21:15"};*/

    private ArrayList<DoctorScheduleModel.Slot> morningPeriods;
    private ArrayList<DoctorScheduleModel.Slot> dayPeriods;
    private ArrayList<DoctorScheduleModel.Slot> noonPeriods;
    private ArrayList<DoctorScheduleModel.Slot> eveningPeriods;

    public DoctorScheduleFragment() { // default

    }

    public int position;
    public boolean isLast;

    public DoctorScheduleFragment(DoctorScheduleModel.Result result, int position, boolean isLast, OnArrowClickedListener listener) {
        mResource = result;
        this.position = position;
        this.isLast = isLast;
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView scheduleTitle;
    Button arrowLeft;
    Button arrowRight;
    String currentLanguage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_location, container, false);

        scheduleTitle = (TextView) view.findViewById(R.id.location_schedule_date_text_view);
        arrowLeft = (Button) view.findViewById(R.id.location_schedule_arrow_left_view);
        arrowRight = (Button) view.findViewById(R.id.location_schedule_arrow_right_view);
        arrowLeft.setBackground(getResources().getDrawable(R.drawable.shape_edit_text_rounded_pink_appointment));
        arrowRight.setBackground(getResources().getDrawable(R.drawable.shape_edit_text_rounded_pink_appointment));

        morningGridView = (MyWrappableGridView) view.findViewById(R.id.location_period_morning_schedule_grid_view);
        dayGridView = (MyWrappableGridView) view.findViewById(R.id.location_period_day_schedule_grid_view);
        noonGridView = (MyWrappableGridView) view.findViewById(R.id.location_period_noon_schedule_grid_view);
        eveningGridView = (MyWrappableGridView) view.findViewById(R.id.location_period_evening_schedule_grid_view);

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";

        if (position == 0) {
            if (mResource.extra_label.equals("")) {
                scheduleTitle.setText(WordUtils.capitalize(mResource.day_name)
                        + ", " + WordUtils.capitalize(((AppController) getActivity().getApplication()).getDateFormat(mResource.date, false,currentLanguage)));

            } else {
                scheduleTitle.setText(mResource.extra_label
                        + ", " + WordUtils.capitalize(((AppController) getActivity().getApplication()).getDateFormat(mResource.date, false,currentLanguage)));
            }
            arrowLeft.setVisibility(View.GONE);
        }
        else if(isLast)
        {
            scheduleTitle.setText(((AppController) getActivity().getApplication()).getDateFormat(mResource.date, true,currentLanguage));
            arrowRight.setVisibility(View.GONE);
        }
        else
            scheduleTitle.setText(((AppController) getActivity().getApplication()).getDateFormat(mResource.date, true,currentLanguage));

        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onArrowLeftClicked();
            }
        });

        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onArrowRightClicked();
            }
        });

        initTimezone();

        // set adapter
        morningGridView.setAdapter(new AppointmentScheduleAdapter(morningPeriods));
        dayGridView.setAdapter(new AppointmentScheduleAdapter(dayPeriods));
        noonGridView.setAdapter(new AppointmentScheduleAdapter(noonPeriods));
        eveningGridView.setAdapter(new AppointmentScheduleAdapter(eveningPeriods));

        return view;
    }

    private void initTimezone() {
        String[] morning = new String[]{"00:00", "10:59"};
        String[] days = new String[]{"10:59", "14:59"};
        String[] noon = new String[]{"14:59", "18:59"};
        String[] evening = new String[]{"18:59", "23:59"};
        morningPeriods = new ArrayList<>();
        dayPeriods = new ArrayList<>();
        noonPeriods = new ArrayList<>();
        eveningPeriods = new ArrayList<>();

        for (int i = 0; i < mResource.slots.size(); i++) {
            if (((AppController) getActivity().getApplication()).getTimeFormat(mResource.slots.get(i).start, ":").equals("00:00")) {
                morningPeriods.add(mResource.slots.get(i));
            } else if (compareTime(morning[0], morning[1], ((AppController) getActivity().getApplication()).getTimeFormat(mResource.slots.get(i).start, ":"))) {
                morningPeriods.add(mResource.slots.get(i));
            } else if (compareTime(days[0], days[1], ((AppController) getActivity().getApplication()).getTimeFormat(mResource.slots.get(i).start, ":"))) {
                dayPeriods.add(mResource.slots.get(i));
            } else if (compareTime(noon[0], noon[1], ((AppController) getActivity().getApplication()).getTimeFormat(mResource.slots.get(i).start, ":"))) {
                noonPeriods.add(mResource.slots.get(i));
            } else if (compareTime(evening[0], evening[1], ((AppController) getActivity().getApplication()).getTimeFormat(mResource.slots.get(i).start, ":"))) {
                eveningPeriods.add(mResource.slots.get(i));
            }
        }

    }

    private boolean compareTime(String zone1, String zone2, String time) {
        String[] parts = zone1.split(":");
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal1.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

        parts = zone2.split(":");
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal2.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

        parts = time.split(":");
        Calendar cal3 = Calendar.getInstance();
        cal3.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal3.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

        if (cal3.after(cal1) && cal3.before(cal2)) {
            return true;
        } else return false;
    }

    // create item menu
    private class AppointmentScheduleAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<DoctorScheduleModel.Slot> list;

        // Constructor
        public AppointmentScheduleAdapter(ArrayList<DoctorScheduleModel.Slot> listSlot) {
            this.inflater = LayoutInflater.from(getActivity());
            this.list = listSlot;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final DoctorScheduleModel.Slot slot = list.get(position);
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
            if (slot.available) {
                mHolder.timeLabel.setTextColor(activeColor);
                mHolder.timeLabel.setPaintFlags(mHolder.timeLabel.getPaintFlags() | Paint.ANTI_ALIAS_FLAG);
                // set listener
                mHolder.timeLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (AppController.checkConnection(getActivity())){
                            checkValidity(slot);
                        }else{
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } else {
                mHolder.timeLabel.setTextColor(inactiveColor);
                mHolder.timeLabel.setPaintFlags(mHolder.timeLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mHolder.timeLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        builder2.setTitle(getResources().getString(R.string.title_dialog_no_schedule));
                        builder2.setMessage(getResources().getString(R.string.hint_schedule_notavailable)).setPositiveButton("OK", dialogClickListener2).show();

                    }
                });

            }
            mHolder.timeLabel.setText(((AppController) getActivity().getApplication()).getTimeFormat(slot.start, ":"));


            return convertView;
        }

        class ViewHolder {
            TextView timeLabel;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void checkValidity(final DoctorScheduleModel.Slot slot) {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), getString(R.string.dialog_title_please_wait),
                getString(R.string.dialog_checking_validity), true);
        Map<String, String> params = new HashMap<>();
        params.put("doctor_id", mResource.doctor_id);
        params.put("practice_id", mResource.practice_id);
        params.put("schedule_date", mResource.date);
        params.put("hour_start", ((AppController) getActivity().getApplication()).getTimeFormat(slot.start, "."));
        params.put("hour_end", ((AppController) getActivity().getApplication()).getTimeFormat(slot.end, "."));
        params.put("time_zone", mResource.time_zone);
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        NetworkManager.getNetworkService(getActivity()).checkScheduleValidity(((AppController) getActivity().getApplication()).getSessionManager().getToken(),currentLanguage, params, new Callback<GeneralCheckModel>() {
            @Override
            public void success(GeneralCheckModel generalCheckModel, Response response) {
                boolean isTokenValid = ((AppController)((Activity)getContext()).getApplication()).isTokenValid(generalCheckModel.messages, response,getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        checkValidity(slot);
                    }

                });
                if(isTokenValid) {

                    dialog.dismiss();
                    if (generalCheckModel.messages.response_code == 200) {
                        if (generalCheckModel.results) {
                            Intent intent = new Intent(getActivity(), BookingInformationActivity.class);
                            intent.putExtra(BookingInformationActivity.DOCTOR_ID, mResource.doctor_id);
                            intent.putExtra(BookingInformationActivity.TIME_ZONE, mResource.time_zone);
                            intent.putExtra(BookingInformationActivity.PRACTICE_ID, mResource.practice_id);
                            intent.putExtra(BookingInformationActivity.SCHEDULE_DATE, mResource.date);
                            intent.putExtra(BookingInformationActivity.HOUR_START, ((AppController) getActivity().getApplication()).getTimeFormat(slot.start, "."));
                            intent.putExtra(BookingInformationActivity.HOUR_END, ((AppController) getActivity().getApplication()).getTimeFormat(slot.end, "."));
                            startActivity(intent);
                        } else {
                            Toast.makeText(DoctorScheduleFragment.this.getActivity(), getResources().getString(R.string.title_dialog_no_schedule), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(DoctorScheduleFragment.this.getActivity(), generalCheckModel.messages.response_text, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(DoctorScheduleFragment.this.getActivity(), getResources().getString(R.string.title_dialog_no_schedule), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}
