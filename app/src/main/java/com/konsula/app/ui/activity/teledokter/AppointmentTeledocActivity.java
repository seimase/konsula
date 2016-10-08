package com.konsula.app.ui.activity.teledokter;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DoctorScheduleModel;
import com.konsula.app.service.net.NetworkManager;
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
public class AppointmentTeledocActivity extends AppCompatActivity {


    public static final String DOCTOR_ID = "doctorId";
    public static final String PRACTICE_ID = "practiceId";

    public static final String DOCTOR_NAME = "doctorName";
    public static final String DOCTOR_IMAGE = "doctorImage";
    public static final String DOCTOR_TITLE = "doctorTitle";
    public static final String LOCATION_ADDRESS = "locationAddress";
    public static final String LOCATION_NAME = "locationName";

    private ImageView avatarImageView;
    private TextView doctorNameTextView, spesialisDoctorTextView;
    private ImageButton backButton;
    private ViewPager viewPager;
    private ProgressDialog dialog;

    DoctorScheduleModel mResource;
    TextView locationNameTextView;
    TextView locationAddressTextView;
    String currentLanguage;
    int doctorId;
    int practiceId;

    RelativeLayout progressBarLayout;
    ScrollView ContentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_teledoc);
        doctorId = getIntent().getIntExtra("doctorId", 0);
        practiceId = getIntent().getIntExtra("practiceId", 0);

        // set views
        progressBarLayout = (RelativeLayout) findViewById(R.id.progressBarLayout);
        ContentLayout = (ScrollView) findViewById(R.id.ContentLayout);
        avatarImageView = (ImageView) findViewById(R.id.appointment_avatar_image_view);
        doctorNameTextView = (TextView) findViewById(R.id.appointment_doctor_name_text_view);
        spesialisDoctorTextView = (TextView) findViewById(R.id.appointment_doctor_spesialis_text_view);
        locationNameTextView = (TextView) findViewById(R.id.appointment_location_name_text_view);
        locationAddressTextView = (TextView) findViewById(R.id.appointment_location_address_text_view);
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.appointment_viewpager);

        //dialog = AppController.createProgressDialog(AppointmentTeledocActivity.this);
        //dialog.show();
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        getScheduleDoc();
    }

    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < mResource.results.size(); i++) {
            final Integer currentI = i;
            adapter.addFragment(new DoctorScheduleFragmentTeledoc(mResource.results.get(i),
                    i,
                    (mResource.results.size() - 1 == i),
                    new DoctorScheduleFragmentTeledoc.OnArrowClickedListener() {
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

        progressBarLayout.setVisibility(View.GONE);
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

    private void getScheduleDoc() {
        progressBarLayout.setVisibility(View.VISIBLE);
        NetworkManager.getNetworkService(getApplicationContext()).getScheduleDoctor(((AppController) getApplication()).getSessionManager().getToken(), currentLanguage, doctorId, practiceId, "teledoc", new Callback<DoctorScheduleModel>() {
                    @Override
                    public void success(DoctorScheduleModel doctorScheduleModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).
                                isTokenValid(doctorScheduleModel.messages, response,AppointmentTeledocActivity.this, new RefreshTokenCallback() {
                                    @Override
                                    public void onRefreshTokenComplete() {
                                        getScheduleDoc();
                                    }

                                });
                        if (isTokenValid) {
                            mResource = doctorScheduleModel;
                            // set viewPager
                            setupViewPager(viewPager);
                            //dialog.dismiss();

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //dialog.dismiss();
                        progressBarLayout.setVisibility(View.GONE);

                    }
                }

        );
    }
}
