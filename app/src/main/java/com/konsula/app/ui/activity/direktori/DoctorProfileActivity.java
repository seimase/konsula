package com.konsula.app.ui.activity.direktori;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DoctorModel;
import com.konsula.app.service.model.DoctorReviewModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.ViewPagerAdapter;
import com.konsula.app.ui.fragment.direktori.DoctorLocationFragment;
import com.konsula.app.ui.fragment.direktori.DoctorProfileFragment;
import com.konsula.app.ui.fragment.direktori.ImagePreviewFragment;
import com.konsula.app.util.RefreshTokenCallback;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/7/2015.
 */
public class DoctorProfileActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView avatarImageView;
    private Button bookButton;
    private ImageButton backImageButton;
    private RelativeLayout rootLayout;
    private ProgressBar progressBar;
    private TextView tvDoctorName;
    private TextView tvDoctorReview;
    private TextView tvDoctorSpeciality;
    private ImageView doctorCover;
    private TextView btn_beri_ulasan;
    private RatingBar doctorRating;
    private String doctorName;
    private Integer doctorId;
    private boolean from_teledokter = false;
    final public static String AVATAR_IMAGE = "avatarImage";
    private String currentLanguage;
    private String doctorUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Window window = getWindow();
        // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_doctor_profile);
        Intent intent = getIntent();
        doctorUri = intent.getStringExtra("doctorUri");
        if (doctorUri == null) doctorUri = "dr-dickson-tan";
        viewPager = (ViewPager) findViewById(R.id.profile_doctor_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.profile_doctor_tab);
        btn_beri_ulasan = (TextView) findViewById(R.id.btn_beri_ulasan);
        btn_beri_ulasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConstant.FROM_DOCTOR = true;
                Intent intent = new Intent(DoctorProfileActivity.this, ReviewDoctorFormActivity.class);
                intent.putExtra("doctor_uri", doctorUri);
                startActivity(intent);
            }
        });
        avatarImageView = (ImageView) findViewById(R.id.doctor_profile_avatar_image_view);
        bookButton = (Button) findViewById(R.id.doctor_profile_book_action_button);
        backImageButton = (ImageButton) findViewById(R.id.doctor_profile_back_image_button);
        rootLayout = (RelativeLayout) findViewById(R.id.doctor_profile_rootlayout);
        progressBar = (ProgressBar) findViewById(R.id.doctor_profile_progressbar);
        doctorCover = (ImageView) findViewById(R.id.doctor_profile_cover_page_image_view);
        doctorRating = (RatingBar) findViewById(R.id.doctor_rating_bar);
        tvDoctorReview = (TextView) findViewById(R.id.doctor_profile_review);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        // set listener


        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvDoctorName = (TextView) findViewById(R.id.profile_doctor_name_text_view);
//        tvDoctorRec = (TextView)findViewById(R.id.profile_doctor_recommendation_text_view);
        tvDoctorSpeciality = (TextView) findViewById(R.id.profile_doctor_spesialis_text_view);

        if (doctorUri != null)getDoctorProfile();


        try {
            from_teledokter = getIntent().getExtras().getBoolean("from_teledokter");
            if (from_teledokter) {
                bookButton.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    public void saveReview(View view) {
        Log.e("Cannot Review", "Cannot Review");
    }

    public void setupViewPagerContent(ViewPager viewPager, DoctorModel doctorModel) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DoctorProfileFragment(doctorModel.results), getResources().getString(R.string.text_profile));
        int i = 1;
        if (doctorModel.results.practices != null ) {
            for (DoctorModel.Practice practice : doctorModel.results.practices) {
                adapter.addFragment(new DoctorLocationFragment(practice, doctorModel.results.doctor_id, doctorModel.results.doctor_name), getString(R.string.lokasi).toUpperCase() + " " + i);
                i++;
            }
        }
        viewPager.setAdapter(adapter);
    }

    public void btnseereview(View view) {
        DoctorReviewModel.Results mResource = ((AppController) getApplication()).getSessionManager().getDoctorReview();
        if (mResource == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_review), Toast.LENGTH_SHORT).show();
        } else {
            Intent a = new Intent(DoctorProfileActivity.this, DoctorReviewActivity.class);
            a.putExtra(DoctorReviewActivity.DOCTOR_NAME, doctorName);
            a.putExtra(DoctorReviewActivity.DOCTOR_ID, doctorId);
            startActivity(a);
        }
    }

    // set adapter view pager

    private void getDoctorProfile(){
        NetworkManager.getNetworkService(getApplicationContext()).getDoctorProfile(((AppController) getApplication()).getSessionManager().getToken(),currentLanguage, doctorUri, new Callback<DoctorModel>() {
            @Override
            public void success(final DoctorModel doctorModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(doctorModel.messages, response, DoctorProfileActivity.this,new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getDoctorProfile();
                    }

                });
                if (isTokenValid) {
                    try {
                        ((AppController) getApplication()).getSessionManager().setDoctorModel(doctorModel.results);

                        progressBar.setVisibility(View.GONE);
                        rootLayout.setVisibility(View.VISIBLE);
                        setupViewPagerContent(viewPager, doctorModel);
                        tabLayout.setupWithViewPager(viewPager);
                        for (int i = 0; i < tabLayout.getTabCount(); i++) {
                            TabLayout.Tab tab = tabLayout.getTabAt(i);
                            RelativeLayout relativeLayout = (RelativeLayout)
                                    LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, tabLayout, false);

                            TextView tabTitle = (TextView) relativeLayout.findViewById(R.id.tab);
                            tabTitle.setText(tabLayout.getTabAt(i).getText().toString());
                            tab.setCustomView(relativeLayout);
                            tab.select();
                        }
                        final DoctorModel.Results results = doctorModel.results;
                        doctorName = results.doctor_name;
                        doctorId = results.doctor_id;
                        tvDoctorName.setText(results.doctor_name);
//                tvDoctorRec.setText(results.total_review+"");
                        if (results.specialities == null) {
                            if (currentLanguage.equals("en") || currentLanguage.equals("EN")) {
                                tvDoctorSpeciality.setText(results.job_english);
                            } else {
                                tvDoctorSpeciality.setText(results.job_bahasa);
                            }
                        }else {
                            tvDoctorSpeciality.setText(results.specialities != null && (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? results.specialities.get(0).specialization.specialization_english : results.specialities.get(0).specialization.specialization_bahasa);
                        }
                        String image = results.photos.primary.medium_image;

                        ((AppController) getApplication()).displayImage(DoctorProfileActivity.this,image, avatarImageView);
                        avatarImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString(AVATAR_IMAGE, results.photos.primary.large_image);
                                android.app.FragmentManager fm = getFragmentManager();
                                ImagePreviewFragment powerDialog = new ImagePreviewFragment();
                                powerDialog.setArguments(bundle);
                                powerDialog.show(fm, "fragment_power");
                            }
                        });
                        bookButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = null;
                                if (doctorModel.results.practices == null) {
                                    Toast.makeText(getApplicationContext(), R.string.text_no_jadwal, Toast.LENGTH_SHORT).show();
                                } else {
                                    intent = new Intent(DoctorProfileActivity.this, ScheduleDoctorActivity.class);
                                    startActivity(intent);
                                }
//                                } else if (viewPager.getCurrentItem() == 0) {
//                                    intent = new Intent(DoctorProfileActivity.this, ScheduleDoctorActivity.class);
//                                    startActivity(intent);
//                                } else {
//                                    intent = new Intent(DoctorProfileActivity.this, AppointmentDoctorActivity.class);
//                                    intent.putExtra(AppointmentDoctorActivity.PRACTICE_ID, results.practices.get(viewPager.getCurrentItem() - 1).practice_id);
//                                    intent.putExtra(AppointmentDoctorActivity.DOCTOR_ID, results.doctor_id);
//                                    intent.putExtra(AppointmentDoctorActivity.DOCTOR_NAME, results.doctor_name);
//                                    intent.putExtra(AppointmentDoctorActivity.DOCTOR_TITLE, tvDoctorSpeciality.getText().toString());
//                                    intent.putExtra(AppointmentDoctorActivity.DOCTOR_IMAGE, results.photos.primary.medium_image);
//                                    intent.putExtra(AppointmentDoctorActivity.LOCATION_NAME, results.practices.get(viewPager.getCurrentItem() - 1).practice_name);
//                                    intent.putExtra(AppointmentDoctorActivity.LOCATION_ADDRESS, results.practices.get(viewPager.getCurrentItem() - 1).address + " " + results.practices.get(viewPager.getCurrentItem() - 1).location);
//                                    startActivity(intent);
//                                }
                            }
                        });
                        if (doctorModel.results.practices != null) {
                            if (results.practices.get(0).photos.primary.large_image != null)
                                ((AppController) getApplication()).displayImage(DoctorProfileActivity.this,results.practices.get(0).photos.primary.large_image, doctorCover);

                        }

                        btn_beri_ulasan.setVisibility(results.review.equals("Y") ? View.VISIBLE : View.INVISIBLE);

                        tvDoctorReview.setText(String.valueOf(results.total_review));
                        doctorRating.setRating(results.star_rating);

                        viewPager.setCurrentItem(0, false);
                        tabLayout.setScrollPosition(0, 0f, true);
                    } catch (Exception e) {
                        Log.d("dd",e.toString());
                        Toast.makeText(DoctorProfileActivity.this, "Can't load doctor", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }


        });

    }


}