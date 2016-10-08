package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AppointmentMemberModel;
import com.konsula.app.service.model.CommonModel;
import com.konsula.app.service.model.Messages;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Owner on 11/17/2015.
 */
public class CallendarUserFragment extends Fragment {

    private ListView descriptionListView;

    private static List<String> dayString = new ArrayList<String>();

    public GregorianCalendar month, itemmonth;// calendar instances.

    public CalendarAdapter adapter;// adapter instance
    public Handler handler;// for grabbing some event values for showing the dot
    // marker.
    public ArrayList<String> items; // container to store calendar items which
    // needs showing the event marker

    // dummy data
    private ArrayList<CommonModel> descriptionList = new ArrayList<CommonModel>();
    //private String[] dateDescriptionList = new String[]{"2015-12-23", "2015-12-24","2015-12-25","2015-12-26" };
    // end dummy data

    public CallendarUserFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_callendar_user, container, false);
        descriptionListView = (ListView) root.findViewById(R.id.description_calendar_user_list_view);

        Locale.setDefault(Locale.US);
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        itemmonth = (GregorianCalendar) month.clone();

        items = new ArrayList<String>();
        adapter = new CalendarAdapter(month);

        GridView gridview = (GridView) root.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        handler = new Handler();
        handler.post(calendarUpdater);

        TextView title = (TextView) root.findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        RelativeLayout previous = (RelativeLayout) root.findViewById(R.id.previous);

        previous.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                setPreviousMonth();
                refreshCalendar(root);
            }
        });

        RelativeLayout next = (RelativeLayout) root.findViewById(R.id.next);
        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                setNextMonth();
                refreshCalendar(root);

            }
        });
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");
        pd.show();


       /* gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // set selected text color

                ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                String selectedGridDate = dayString.get(position);
                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*", "");// taking last part of date. ie; 2 from 2012-12-02.

                int gridvalue = Integer.parseInt(gridvalueString);
                // navigate to next or previous month on clicking offdays.
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar(root);
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar(root);
                }
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);

                showToast(selectedGridDate);

            }
        });*/

        getAppointmentMember(pd);

        return root;
    }

    public void getAppointmentMember(final ProgressDialog pd) {
        NetworkManager.getNetworkService(getActivity()).getAppointmentMember(((AppController) getActivity().getApplication())
                .getSessionManager().getToken(), new Callback<AppointmentMemberModel>() {
            @Override
            public void success(AppointmentMemberModel appointmentMemberModel, Response response) {
                boolean isTokenValid = ((AppController)((Activity)getContext()).getApplication()).isTokenValid(appointmentMemberModel.messages, response,getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getAppointmentMember(pd);
                    }


                });
                if(isTokenValid) {
                    if (appointmentMemberModel.results.isEmpty()) {
                        Messages messages = appointmentMemberModel.messages;
                        Toast.makeText(getActivity(), messages.response_text, Toast.LENGTH_SHORT).show();
                    } else {
                        descriptionList.clear();
                        for (AppointmentMemberModel.Result item : appointmentMemberModel.results) {
                            descriptionList.add(createDescription(item.doctor_name, item.schedule_date, item.reason, item.practice_name, item.hour_start + " " + item.time_abbreviation));
                            items.add("" + item.schedule_date);
                        }

                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();

                        // set listener
                        DescriptionAdapter descriptionAdapter = new DescriptionAdapter(descriptionList);
                        descriptionListView.setAdapter(descriptionAdapter);
                    }
                    pd.dismiss();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }

    }

    protected void showToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

    }

    public void refreshCalendar(View view) {
        TextView title = (TextView) view.findViewById(R.id.title);

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some calendar items

        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            //items.clear();

            // Print dates of the current week
            //DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            //String itemvalue;
            //for (int i = 0; i < dateDescriptionList.length ; i++) {
             //   itemvalue = df.format(itemmonth.getTime());
              //  itemmonth.add(GregorianCalendar.DATE, 1);
               // items.add(dateDescriptionList[i]);
                //items.add("2016-02-24");
                //items.add("2016-02-25");
                //items.add("2016-02-26");
            //}

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    };

    private class CalendarAdapter extends BaseAdapter {

        private java.util.Calendar month;
        public GregorianCalendar pmonth; // calendar instance for previous month
        /**
         * calendar instance for previous month for getting complete view
         */
        public GregorianCalendar pmonthmaxset;
        private GregorianCalendar selectedDate;
        int firstDay;
        int maxWeeknumber;
        int maxP;
        int calMaxP;
        int lastWeekDay;
        int leftDays;
        int mnthlength;
        String itemvalue, curentDateString;
        DateFormat df;

        private ArrayList<String> items;
        private View previousView;

        public CalendarAdapter(GregorianCalendar monthCalendar) {
            Locale.setDefault(Locale.US);
            month = monthCalendar;
            selectedDate = (GregorianCalendar) monthCalendar.clone();
            month.set(GregorianCalendar.DAY_OF_MONTH, 1);
            this.items = new ArrayList<String>();
            df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            curentDateString = df.format(selectedDate.getTime());
            refreshDays();
        }

        public void setItems(ArrayList<String> items) {
            for (int i = 0; i != items.size(); i++) {
                if (items.get(i).length() == 1) {
                    items.set(i, "0" + items.get(i));
                }
            }
            this.items = items;
        }

        public int getCount() {
            return dayString.size();
        }

        public Object getItem(int position) {
            return dayString.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new view for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            TextView dayView;
            if (convertView == null) { // if it's not recycled, initialize some
                // attributes
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.item_callendar_user, null);

            }
            dayView = (TextView) v.findViewById(R.id.date);
            // separates daystring into parts.
            String[] separatedTime = dayString.get(position).split("-");
            // taking last part of date. ie; 2 from 2012-12-02
            String gridvalue = separatedTime[2].replaceFirst("^0*", "");
            // checking whether the day is in current month or not.
            if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
                // setting offdays to white color.
                dayView.setTextColor(getResources().getColor(R.color.grey_xxl));
                dayView.setClickable(false);
                dayView.setFocusable(false);
            } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
                dayView.setTextColor(getResources().getColor(R.color.grey_xxl));
                dayView.setClickable(false);
                dayView.setFocusable(false);
            } else {
                // setting curent month's days in blue color.
                dayView.setTextColor(getResources().getColor(R.color.textColorBase));
            }

            if (dayString.get(position).equals(curentDateString)) {
                setSelected(v);
                previousView = v;
            } else {
                v.setBackgroundResource(R.drawable.item_background);
            }
            dayView.setText(gridvalue);

            // create date string for comparison
            String date = dayString.get(position);

            if (date.length() == 1) {
                date = "0" + date;
            }
            String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
            if (monthStr.length() == 1) {
                monthStr = "0" + monthStr;
            }

            // show icon if date is not empty and it exists in the items array
            ImageView iw = (ImageView) v.findViewById(R.id.date_icon);
            if (date.length() > 0 && items != null && items.contains(date)) {
                iw.setVisibility(View.VISIBLE);
            } else {
                iw.setVisibility(View.INVISIBLE);
            }
            return v;
        }

        public View setSelected(View view) {
            // set selected background
            if (previousView != null) {
                previousView.setBackgroundResource(R.drawable.item_background);

                // set unselected text color
                TextView prevDayView = (TextView) previousView.findViewById(R.id.date);
                if(prevDayView != null) prevDayView.setTextColor(getResources().getColor(R.color.textColorBase));
            }

            previousView = view;
            view.setBackgroundResource(R.drawable.bg_red_circle);

            // set selected text color
            TextView curDayView = (TextView) view.findViewById(R.id.date);
            if(curDayView != null) curDayView.setTextColor(getResources().getColor(R.color.white));
            return view;
        }

        public void refreshDays() {
            // clear items
            //items.clear();
            dayString.clear();
            Locale.setDefault(Locale.US);
            pmonth = (GregorianCalendar) month.clone();
            // month start day. ie; sun, mon, etc
            firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
            // finding number of weeks in current month.
            maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
            // allocating maximum row number for the gridview.
            mnthlength = maxWeeknumber * 7;
            maxP = getMaxP(); // previous month maximum day 31,30....
            calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
            /**
             * Calendar instance for getting a complete gridview including the three
             * month's (previous,current,next) dates.
             */
            pmonthmaxset = (GregorianCalendar) pmonth.clone();
            /**
             * setting the start date as previous month's required date.
             */
            pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

            /**
             * filling calendar gridview.
             */
            for (int n = 0; n < mnthlength; n++) {

                itemvalue = df.format(pmonthmaxset.getTime());
                pmonthmaxset.add(GregorianCalendar.DATE, 1);
                dayString.add(itemvalue);

            }
        }

        private int getMaxP() {
            int maxP;
            if (month.get(GregorianCalendar.MONTH) == month
                    .getActualMinimum(GregorianCalendar.MONTH)) {
                pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                        month.getActualMaximum(GregorianCalendar.MONTH), 1);
            } else {
                pmonth.set(GregorianCalendar.MONTH,
                        month.get(GregorianCalendar.MONTH) - 1);
            }
            maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

            return maxP;
        }

    }

    // create adapter for description
    private class DescriptionAdapter extends ArrayAdapter<CommonModel> {

        private ArrayList<CommonModel> listDescription;

        public DescriptionAdapter(ArrayList<CommonModel> listDescription) {
            super(getActivity(), R.layout.item_callendar_description_user, listDescription);
            this.listDescription = listDescription;
        }

        @Override
        public int getCount() {
            return listDescription.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //Log.v("ConvertView", convertView.setBackground(R.drawable.bg_red_circle));
            if (convertView == null) {
                LayoutInflater view = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = view.inflate(R.layout.item_callendar_description_user, null);

                holder = new ViewHolder();

                holder.doctorNameDescriptionTextView = (TextView) convertView.findViewById(R.id.doctor_name_item_callendar_description_text_view);
                holder.dateDescriptionTextView = (TextView) convertView.findViewById(R.id.doctor_date_item_callendar_description_text_view);
                holder.purposeDescriptionTextView = (TextView) convertView.findViewById(R.id.doctor_purpose_item_callendar_description_text_view);
                holder.locationDescriptionTextView = (TextView) convertView.findViewById(R.id.location_name_item_callendar_description_text_view);
                holder.timeDescriptionTextView = (TextView) convertView.findViewById(R.id.doctor_time_item_callendar_description_text_view);

                convertView.setTag(holder);

            }

            CommonModel item = listDescription.get(position);

            holder = (ViewHolder) convertView.getTag();

            holder.doctorNameDescriptionTextView.setText(item.getProperty("name"));
            holder.dateDescriptionTextView.setText(item.getProperty("date"));
            holder.purposeDescriptionTextView.setText(item.getProperty("purpose"));
            holder.locationDescriptionTextView.setText(item.getProperty("location"));
            holder.timeDescriptionTextView.setText(item.getProperty("time"));

            return convertView;
        }
    }

    private class ViewHolder {
        TextView doctorNameDescriptionTextView, dateDescriptionTextView, purposeDescriptionTextView,
                locationDescriptionTextView, timeDescriptionTextView;
    }

    // dummy data
    // todo : remove after get data from api
    private CommonModel createDescription(String name, String date, String purpose, String location, String time){
        CommonModel commonModel = new CommonModel();
        commonModel.setProperty("name", name);
        commonModel.setProperty("date", date);
        commonModel.setProperty("purpose", purpose);
        commonModel.setProperty("location", location);
        commonModel.setProperty("time", time);

        return commonModel;
    }
}
