package com.konsula.app.ui.activity.direktori;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DoctorScheduleModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.fragment.direktori.DoctorScheduleFragment;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/14/2015.
 */
public class AppointmentDoctorActivity extends AppCompatActivity {


    public static final String DOCTOR_ID = "doctorId";
    public static final String PRACTICE_ID = "practiceId";

    public static final String DOCTOR_NAME = "doctorName";
    public static final String DOCTOR_IMAGE = "doctorImage";
    public static final String DOCTOR_TITLE = "doctorTitle";
    public static final String LOCATION_ADDRESS = "locationAddress";
    public static final String LOCATION_NAME = "locationName";

    private Toolbar toolbar;
    private ImageView avatarImageView;
    private TextView doctorNameTextView, spesialisDoctorTextView;
    private ImageButton backButton;
    private ViewPager viewPager;
    String currentLanguage;

    DoctorScheduleModel mResource;
    TextView locationNameTextView;
    TextView locationAddressTextView;
    int doctorId;
    int practiceId;
    private LinearLayout layoutloading;
    private LinearLayout layoutcontaint;
    private ProgressBar progressBar;
    private Button refresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_doctor);
        Intent a = getIntent();
        String doctorName = a.getStringExtra(DOCTOR_NAME);
        String doctorImage = a.getStringExtra(DOCTOR_IMAGE);
        String doctorTitle = a.getStringExtra(DOCTOR_TITLE);
        String locationAddress = a.getStringExtra(LOCATION_ADDRESS);
        String locationName = a.getStringExtra(LOCATION_NAME);
        doctorId = a.getIntExtra(DOCTOR_ID, 0);
        practiceId = a.getIntExtra(PRACTICE_ID, 0);
        // set action bar
        toolbar = (Toolbar) findViewById(R.id.appointment_header_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        getSchedule();
        // set views
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        layoutcontaint = (LinearLayout) findViewById(R.id.layout_appointment_doctor);
        avatarImageView = (ImageView) findViewById(R.id.appointment_avatar_image_view);
        doctorNameTextView = (TextView) findViewById(R.id.appointment_doctor_name_text_view);
        spesialisDoctorTextView = (TextView) findViewById(R.id.appointment_doctor_spesialis_text_view);
        locationNameTextView = (TextView) findViewById(R.id.appointment_location_name_text_view);
        locationAddressTextView = (TextView) findViewById(R.id.appointment_location_address_text_view);
        backButton = (ImageButton) findViewById(R.id.appointment_header_cover_back_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //doctor info
        doctorNameTextView.setText(doctorName);
        spesialisDoctorTextView.setText(doctorTitle);
        ((AppController) getApplication()).displayImage(this,doctorImage, avatarImageView);
        locationNameTextView.setText(locationName);
        locationAddressTextView.setText(locationAddress);
        //
        viewPager = (ViewPager) findViewById(R.id.appointment_viewpager);
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setVisibility(View.GONE);
                layoutloading.setVisibility(View.VISIBLE);
                getSchedule();
            }
        });

    }

    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < mResource.results.size(); i++) {
            final Integer currentI = i;
            adapter.addFragment(new DoctorScheduleFragment(mResource.results.get(i),
                    i,
                    (mResource.results.size() - 1 == i),
                    new DoctorScheduleFragment.OnArrowClickedListener() {
                        @Override
                        public void onArrowRightClicked() {
                            viewPager.setCurrentItem(currentI + 1, true);
                        }

                        @Override
                        public void onArrowLeftClicked() {
                            viewPager.setCurrentItem(currentI - 1, true);
                        }
                    }), "LOKASI " + (i + 1));
        }
        viewPager.setAdapter(adapter);
    }
/*
    // adapter
    private class ScheduleAdapter extends ArrayAdapter<CommonModel> {

        private ArrayList<CommonModel> locationList;

        public ScheduleAdapter(ArrayList<CommonModel> locationList) {
            super(AppointmentDoctorActivity.this, R.layout.item_appointment_location, locationList);
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
    }*/

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

    private void getSchedule() {
        NetworkManager.getNetworkService(getApplicationContext()).getScheduleDoctor(((AppController) getApplication()).getSessionManager().getToken(), currentLanguage, doctorId, practiceId, "", new Callback<DoctorScheduleModel>() {
            @Override
            public void success(DoctorScheduleModel doctorScheduleModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).
                        isTokenValid(doctorScheduleModel.messages, response,AppointmentDoctorActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                getSchedule();
                            }

                        });
                if (isTokenValid) {
                    progressBar.setVisibility(View.GONE);
                    layoutcontaint.setVisibility(View.VISIBLE);
                    layoutloading.setVisibility(View.GONE);
                    if (doctorScheduleModel.messages.response_code == 200) {

                        mResource = doctorScheduleModel;
                        // set viewPager
                        setupViewPager(viewPager);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.text_no_jadwal, Toast.LENGTH_SHORT).show();

                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {
                layoutloading.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            }
        });

    }
}
