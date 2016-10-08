package com.konsula.app.ui.activity.direktori;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AppointmentDataModel;
import com.konsula.app.service.model.GeneralCheckModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/7/2015.
 */
public class AppointmentReviewActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView skipTextView;
    private RatingBar ratBarkeramahan, ratBarkebersihan, ratBarwaktu;
    private Button sendButton;
    private EditText feedBackKonsula;
    private EditText feedBackAppointment;
    private String memberId;
    private String appointmentId;
    private String practiceId;
    private String doctorId;
    private String uniqueKey;
    private AppController appController;
    public static String MEMBER_ID = "memberId";
    public static String APPOINTMENT_ID = "appointmentId";
    public static String PRACTICE_ID = "practiceId";
    public static String DOCTOR_ID = "doctorId";
    public static String UNIQUE_KEY = "uniqueKey";

    ImageView doctorCoverImageView;
    ImageView doctorAvatarImageView;
    TextView doctorNameTextView;
    TextView doctorSpecialityTextView;
    ProgressBar progressBar;
    ScrollView reviewMainContentLayout;
    String currentLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_review_book_doctor);
        appController = new AppController();
        Intent intent = getIntent();
        memberId = intent.getStringExtra(MEMBER_ID);
        appointmentId = intent.getStringExtra(APPOINTMENT_ID);
        practiceId = intent.getStringExtra(PRACTICE_ID);
        doctorId = intent.getStringExtra(DOCTOR_ID);
        uniqueKey = intent.getStringExtra(UNIQUE_KEY);

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        reviewMainContentLayout = (ScrollView) findViewById(R.id.review_main_content);

        doctorCoverImageView = (ImageView) findViewById(R.id.review_cover_page_image_view);
        doctorAvatarImageView = (ImageView) findViewById(R.id.review_avatar_image_view);
        doctorNameTextView = (TextView) findViewById(R.id.review_doctor_name_text_view);
        doctorSpecialityTextView = (TextView) findViewById(R.id.review_doctor_speciality_text_view);


        ratBarkeramahan = (RatingBar) findViewById(R.id.rating_keramahan);
        ratBarkebersihan = (RatingBar) findViewById(R.id.rating_kebersihan);
        ratBarwaktu = (RatingBar) findViewById(R.id.rating_waktutunggu);
        backButton = (ImageButton) findViewById(R.id.review_book_doctor_action_close_image_button);
        skipTextView = (TextView) findViewById(R.id.review_book_doctor_action_skip_image_button);
        sendButton = (Button) findViewById(R.id.book_action_send_button);
        feedBackAppointment = (EditText) findViewById(R.id.review_book_feedback_from_user);
        feedBackKonsula = (EditText) findViewById(R.id.review_book_feedback_to_konsula);


        // set listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ratBarkeramahan.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });

        getDataAppointment(uniqueKey);

    }

    ProgressDialog dialog;

    public void kirim(final View view) {

                /*if(feedBackAppointment.getText().toString().equals(""))feedBackAppointment.setError("Required");
                if(feedBackKonsula.getText().toString().equals(""))feedBackKonsula.setError("Required");
*/
        if (ratBarkeramahan.getRating() == 0 || ratBarkebersihan.getRating() == 0 || ratBarwaktu.getRating() == 0) {
            appController.doDialog(AppointmentReviewActivity.this, getResources().getString(R.string.text_please_review));
        } else if (feedBackAppointment.getText().toString().equals("") || feedBackAppointment.getText().toString().length() < 50) {
            appController.doDialog(AppointmentReviewActivity.this, getResources().getString(R.string.text_feedback));
        } else {
            if (!(feedBackAppointment.getText().toString().equals("") && feedBackKonsula.getText().toString().equals(""))) {
                sendAppointmentReview(
                        uniqueKey,
                        doctorId,
                        practiceId,
                        memberId,
                        ratBarkeramahan.getNumStars(),
                        ratBarkebersihan.getNumStars(),
                        ratBarwaktu.getNumStars(),
                        feedBackAppointment.getText().toString(),
                        feedBackKonsula.getText().toString(),
                        "N");
            }
        }


    }

    private void sendAppointmentReview(final String uniqueKey, final String doctor_id, final String practice_id, final String memberId, final int point_friendly, final int point_facility, final int point_timing, final String feedback_from_user, final String feedback_to_konsula, final String anonim) {
        dialog = ProgressDialog.show(AppointmentReviewActivity.this, "Please wait", "Saving your review", true);
        dialog.show();
        NetworkManager.getNetworkService(getApplicationContext()).saveAppointmentReview1(((AppController) getApplication()).getSessionManager().getToken(),
                uniqueKey,
                doctor_id,
                practice_id,
                uniqueKey,
                memberId,
                point_friendly,
                point_facility,
                point_timing,
                feedback_from_user,
                feedback_to_konsula,
                anonim,
                new Callback<GeneralCheckModel>() {
                    @Override
                    public void success(GeneralCheckModel generalCheckModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).isTokenValid(generalCheckModel.messages, response, AppointmentReviewActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                sendAppointmentReview(uniqueKey, doctor_id, practice_id, memberId, point_friendly, point_facility, point_timing, feedback_from_user, feedback_to_konsula, anonim);
                            }


                        });
                        if (isTokenValid) {
                            if (generalCheckModel.messages.response_code == 200) {
                                Toast.makeText(AppointmentReviewActivity.this, generalCheckModel.messages.response_text, Toast.LENGTH_LONG).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(AppointmentReviewActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }

    private void getDataAppointment(String unique_id) {
        progressBar.setVisibility(View.VISIBLE);
        reviewMainContentLayout.setVisibility(View.INVISIBLE);
        NetworkManager.getNetworkService(getApplicationContext()).getDataForAppointment(((AppController) getApplication()).getSessionManager().getToken(),currentLanguage, unique_id, new Callback<AppointmentDataModel>() {
            @Override
            public void success(AppointmentDataModel appointmentDataModel, Response response) {
                progressBar.setVisibility(View.INVISIBLE);

                if (appointmentDataModel.messages.response_code == 200) {
                    if (appointmentDataModel.results != null) {
                        doctorSpecialityTextView.setText(currentLanguage.equals("en")?appointmentDataModel.results.profile_doctor.specialities.get(0).specialization.specialization_english:appointmentDataModel.results.profile_doctor.specialities.get(0).specialization.specialization_bahasa);
                        doctorNameTextView.setText(appointmentDataModel.results.profile_doctor.doctor_name);
                        ((AppController) getApplication()).displayImage(AppointmentReviewActivity.this, appointmentDataModel.results.profile_practice.photo.large_image, doctorCoverImageView);
                        ((AppController) getApplication()).displayImage(AppointmentReviewActivity.this, appointmentDataModel.results.profile_doctor.photo.medium_image, doctorAvatarImageView);
                        reviewMainContentLayout.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


}
