package com.konsula.app.ui.activity.direktori;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.DoctorModel;
import com.konsula.app.service.model.SaveOriginalReviewModel;
import com.konsula.app.service.model.SimpleModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.SimpleAdapter;
import com.konsula.app.ui.adapter.spinnerAdapter;
import com.konsula.app.util.RefreshTokenCallback;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReviewDoctorFormActivity extends Activity implements AdapterView.OnItemSelectedListener, RatingBar.OnRatingBarChangeListener {
    protected String reviewUri;
    protected int practiceId;
    protected int doctorId;
    protected int memberId;
    protected ImageView doctorProvileAvatarImageView;
    protected ImageView profileCoverPageImageView;
    protected LinearLayout profileIdentityLinearLayout;
    protected TextView reviewNameTitleTextView;
    protected TextView reviewNameTextView;
    protected TextView reviewDescTextView;
    protected Spinner reviewSpinner;
    private AppController appController;
    protected Button btnBook;
    protected RatingBar pointFriendlyRating;
    protected RatingBar pointFacilityRating;
    protected RatingBar pointTimingRating;
    protected EditText feedbackFromUserEditText;
    protected EditText feedbackToKonsulaEditText;
    protected SimpleAdapter reviewAdapter;
    protected String anonim;
    public String currentLanguage;


    protected LinearLayout layoutloading;
    protected RelativeLayout l_view;
    protected Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);
        appController = new AppController();

        if (!((AppController) getApplication()).getSessionManager().isUserLogon()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fromAppointment", 1);
            startActivity(intent);
            finish();
        }

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        btnBook = (Button) findViewById(R.id.doctor_profile_book_action_button);
        doctorProvileAvatarImageView = (ImageView) findViewById(R.id.doctor_profile_avatar_image_view);
        profileCoverPageImageView = (ImageView) findViewById(R.id.profile_cover_page_image_view);
        profileIdentityLinearLayout = (LinearLayout) findViewById(R.id.profile_identity_linear_layout);
        reviewNameTitleTextView = (TextView) findViewById(R.id.review_name_title_text_view);
        reviewNameTextView = (TextView) findViewById(R.id.review_name_text_view);
        reviewDescTextView = (TextView) findViewById(R.id.review_desc_text_view);
        reviewSpinner = (Spinner) findViewById(R.id.review_spinner);
        pointFriendlyRating = (RatingBar) findViewById(R.id.point_friendly_rating);
        pointFacilityRating = (RatingBar) findViewById(R.id.point_facility_rating);
        pointTimingRating = (RatingBar) findViewById(R.id.point_timing_rating);
        feedbackFromUserEditText = (EditText) findViewById(R.id.feedback_from_user_edit_text);
        feedbackToKonsulaEditText = (EditText) findViewById(R.id.feedback_to_konsula_edit_text);
        reviewAdapter = new SimpleAdapter(this);
        reviewAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        reviewSpinner.setAdapter(reviewAdapter);
        reviewSpinner.setOnItemSelectedListener(this);
        pointFriendlyRating.setOnRatingBarChangeListener(this);
        pointFacilityRating.setOnRatingBarChangeListener(this);
        pointTimingRating.setOnRatingBarChangeListener(this);
        doctorProvileAvatarImageView.requestFocus();

        if (!((AppController) getApplication()).getSessionManager().isUserLogon()) {
            anonim = "Y";
        } else {
            AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
            memberId = userData.member_id;
            anonim = "N";
        }
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (RelativeLayout) findViewById(R.id.l_view);
        refresh = (Button) findViewById(R.id.refresh);
        init();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                l_view.setVisibility(View.INVISIBLE);
                init();
            }
        });
    }

    protected void init() {
        reviewUri = getIntent().getStringExtra("doctor_uri");

        NetworkManager.getNetworkService(this).getDoctorProfile(((AppController) getApplication()).getSessionManager().getToken(), currentLanguage, reviewUri, new Callback<DoctorModel>() {
            @Override
            public void success(DoctorModel doctorModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(doctorModel.messages, response, ReviewDoctorFormActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        init();
                    }

                });
                if (isTokenValid) {
                    layoutloading.setVisibility(View.GONE);
                    l_view.setVisibility(View.VISIBLE);
                    if (doctorModel.results.practices != null) {
                        doctorId = doctorModel.results.doctor_id;
                        practiceId = doctorModel.results.practices.get(0).practice_id;
                        AppController.getInstance().displayImage(ReviewDoctorFormActivity.this, doctorModel.results.photos.primary.thumb_image, doctorProvileAvatarImageView);
                        AppController.getInstance().displayImage(ReviewDoctorFormActivity.this, doctorModel.results.practices.get(0).photos.primary.large_image, profileCoverPageImageView);
                        reviewNameTitleTextView.setText(doctorModel.results.doctor_name);
                        reviewNameTextView.setText(doctorModel.results.doctor_name);
                        reviewDescTextView.setText(doctorModel.results.specialities.get(0).specialization.specialization_bahasa);
                        reviewAdapter.clear();
                        reviewAdapter.add(new SimpleModel(0 + "",getResources().getString(R.string.prac2)));
                        for (DoctorModel.Practice item : doctorModel.results.practices) {
                            reviewAdapter.add(new SimpleModel(item.practice_id + "", item.practice_name));
                        }
                    }
                }


            }

            @Override
            public void failure(RetrofitError error) {
                layoutloading.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                Toast.makeText(ReviewDoctorFormActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void kirim(final View view) {
        String sMessage ;
        if (!AppConstant.FROM_DOCTOR){
            sMessage = getResources().getString(R.string.prac4);
        }else{
            sMessage = getResources().getString(R.string.prac3);
        }
        if (reviewSpinner.getSelectedItemPosition() == 0){
            appController.doDialog(this, sMessage);
        }else if (pointFriendlyRating.getRating() == 0 || pointFacilityRating.getRating() == 0 || pointTimingRating.getRating() == 0) {
            appController.doDialog(this, getResources().getString(R.string.text_please_review));
        } else if (feedbackFromUserEditText.getText().toString().equals("") || feedbackFromUserEditText.getText().toString().length() < 50) {
            appController.doDialog(this, getResources().getString(R.string.text_feedback));
        } else {
            final ProgressDialog dialog = AppController.createProgressDialog(ReviewDoctorFormActivity.this);
            dialog.show();
            NetworkManager.getNetworkService(this).saveOriginalReview(((AppController) getApplication()).getSessionManager().getToken(),
                    memberId,
                    practiceId,
                    doctorId,
                    memberId,
                    pointFriendlyRating.getRating(),
                    pointFacilityRating.getRating(),
                    pointTimingRating.getRating(),
                    feedbackFromUserEditText.getText().toString(),
                    feedbackToKonsulaEditText.getText().toString(),
                    anonim,
                    new Callback<SaveOriginalReviewModel>() {
                        @Override
                        public void success(SaveOriginalReviewModel saveOriginalReviewModel, Response response) {
                            boolean isTokenValid = ((AppController) getApplication()).isTokenValid(saveOriginalReviewModel.messages, response, ReviewDoctorFormActivity.this, new RefreshTokenCallback() {
                                @Override
                                public void onRefreshTokenComplete() {
                                    kirim(view);
                                }


                            });
                            if (isTokenValid) {
                                dialog.dismiss();
                                if (saveOriginalReviewModel.results){
                                    Toast.makeText(ReviewDoctorFormActivity.this, "Review Saved Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    appController.doDialog(ReviewDoctorFormActivity.this, saveOriginalReviewModel.messages.response_text);

                                }
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            dialog.dismiss();
                            Toast.makeText(ReviewDoctorFormActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                        }
                    });
        }


    }

    public void back(View view) {
        finish();
    }

    public void skip(View view) {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        practiceId = Integer.parseInt(reviewAdapter.getItem(i).id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        if (v < 1) {
            ratingBar.setRating(1);
        }
    }
}