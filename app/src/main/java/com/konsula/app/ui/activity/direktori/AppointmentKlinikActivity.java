package com.konsula.app.ui.activity.direktori;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.CommonModel;
import com.konsula.app.ui.fragment.direktori.DoctorScheduleFragment;
import com.konsula.app.ui.fragment.direktori.AppointmentSecondLocationScheduleFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by konsula on 12/14/2015.
 */
public class AppointmentKlinikActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView avatarImageView;
    private TextView doctorNameTextView, spesialisDoctorTextView;

    private ListView locationListView;
    private ViewPager viewPager;
    //private TabLayout tabLayout;

    // dummy data
    ArrayList<CommonModel> locationList = new ArrayList<CommonModel>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_klinik);
        // add dummy data
        locationList.add(createObject("Klinik Gigi Jaya (Lokasi 1)", "Gedung Abadi", "Jl. Sudirman Kav. 49", "Jakarta Pusat", "SENIN", "15:00 - 20:00 WIB", "IDR", "300.000"));

        // set action bar
        toolbar = (Toolbar) findViewById(R.id.appointment_header_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set views
        avatarImageView = (ImageView) findViewById(R.id.appointment_avatar_image_view);
        doctorNameTextView = (TextView) findViewById(R.id.appointment_doctor_name_text_view);
        spesialisDoctorTextView = (TextView) findViewById(R.id.appointment_doctor_spesialis_text_view);

        viewPager = (ViewPager) findViewById(R.id.appointment_viewpager);

        // set adapter
        locationListView = (ListView) findViewById(R.id.appointment_location_list_view);
        locationListView.setAdapter(new ScheduleAdapter(locationList));

        // set viewPager
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DoctorScheduleFragment(), "LOKASI 1");
        adapter.addFragment(new AppointmentSecondLocationScheduleFragment(), "LOKASI 2");
        viewPager.setAdapter(adapter);
    }

    // adapter
    private class ScheduleAdapter extends ArrayAdapter<CommonModel> {

        private ArrayList<CommonModel> locationList;

        public ScheduleAdapter(ArrayList<CommonModel> locationList) {
            super(AppointmentKlinikActivity.this, R.layout.item_appointment_location, locationList);
            this.locationList = locationList;
        }

        @Override
        public int getCount() {
            return locationList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            if (convertView == null) {
                LayoutInflater view = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = view.inflate(R.layout.item_appointment_location, null);

                holder = new ViewHolder();

                holder.locationName = (TextView) convertView.findViewById(R.id.item_appointment_location_name_text_view);
                holder.locationAddress = (TextView) convertView.findViewById(R.id.item_appointment_location_address_text_view);

                convertView.setTag(holder);

            }

            CommonModel item = locationList.get(position);
            holder = (ViewHolder) convertView.getTag();
            holder.locationName.setText(item.getProperty("locationName"));

            String address = String.format("%s \n %s \n %s", item.getProperty("buildingName"),
                    item.getProperty("locationAddress"), item.getProperty("mainLocationAddress"));

            holder.locationAddress.setText(address);

            return convertView;
        }
    }

    private class ViewHolder {
        TextView locationName, locationAddress;
    }

    // set adapter view pager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // create model
    private CommonModel createObject(String locationName, String buildingName, String locationAddress,
                                     String mainLocationAddress, String dayNameSchedule, String period,
                                     String currency, String price) {
        CommonModel model = new CommonModel();
        model.setProperty("locationName", locationName);
        model.setProperty("buildingName", buildingName);
        model.setProperty("locationAddress", locationAddress);
        model.setProperty("mainLocationAddress", mainLocationAddress);
        model.setProperty("dayNameSchedule", dayNameSchedule);
        model.setProperty("period", period);
        model.setProperty("currency", currency);
        model.setProperty("price", price);
        return model;

    }
}
