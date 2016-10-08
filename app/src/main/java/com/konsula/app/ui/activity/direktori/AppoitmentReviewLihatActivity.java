package com.konsula.app.ui.activity.direktori;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DataReviewAppintmentModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 24/03/2016.
 */
public class AppoitmentReviewLihatActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView skipTextView;
    private RatingBar ratBarkeramahan, ratBarkebersihan, ratBarwaktu;
    private EditText feedBackKonsula;
    private EditText feedBackAppointment;
    private String uniqueKey;
    private Button btnsend;

    public static String UNIQUE_KEY = "uniqueKey";

    ImageView doctorCoverImageView;
    ImageView doctorAvatarImageView;
    TextView doctorNameTextView;
    TextView doctorSpecialityTextView;
    TextView textViewStatusComment;
    ProgressBar progressBar;
    ScrollView reviewMainContentLayout;
    TextView seefeedBackKonsula;
    TextView seefeedBackAppointment;

    String currentLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_review_book_doctor);

        Intent intent = getIntent();
        uniqueKey = intent.getStringExtra(UNIQUE_KEY);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        reviewMainContentLayout = (ScrollView) findViewById(R.id.review_main_content);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        doctorCoverImageView = (ImageView) findViewById(R.id.review_cover_page_image_view);
        doctorAvatarImageView = (ImageView) findViewById(R.id.review_avatar_image_view);
        doctorNameTextView = (TextView) findViewById(R.id.review_doctor_name_text_view);
        doctorSpecialityTextView = (TextView) findViewById(R.id.review_doctor_speciality_text_view);
        seefeedBackKonsula = (TextView) findViewById(R.id.see_review_book_feedback_to_konsula);
        seefeedBackAppointment =(TextView) findViewById(R.id.see_review_book_feedback_from_user);
        btnsend = (Button) findViewById(R.id.book_action_send_button);
        textViewStatusComment =(TextView) findViewById(R.id.book_action_status_button);
        textViewStatusComment.setVisibility(View.VISIBLE);
        btnsend.setVisibility(View.GONE);

        ratBarkeramahan = (RatingBar) findViewById(R.id.rating_keramahan);
        ratBarkebersihan = (RatingBar) findViewById(R.id.rating_kebersihan);
        ratBarwaktu = (RatingBar) findViewById(R.id.rating_waktutunggu);
        backButton = (ImageButton) findViewById(R.id.review_book_doctor_action_close_image_button);
        skipTextView = (TextView) findViewById(R.id.review_book_doctor_action_skip_image_button);
        feedBackAppointment = (EditText) findViewById(R.id.review_book_feedback_from_user);
        feedBackKonsula = (EditText) findViewById(R.id.review_book_feedback_to_konsula);
        feedBackKonsula.setVisibility(View.GONE);
        feedBackAppointment.setVisibility(View.GONE);
        seefeedBackKonsula.setVisibility(View.VISIBLE);
        seefeedBackAppointment.setVisibility(View.VISIBLE);
        ratBarkebersihan.setEnabled(false);
        ratBarkeramahan.setEnabled(false);
        ratBarwaktu.setEnabled(false);

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

    private void getDataAppointment(final String unique_id) {
        progressBar.setVisibility(View.VISIBLE);
        reviewMainContentLayout.setVisibility(View.INVISIBLE);
        NetworkManager.getNetworkService(getApplicationContext()).getDataReviewAppointment(((AppController) getApplication()).getSessionManager().getToken(),currentLanguage, unique_id, new Callback<DataReviewAppintmentModel>() {
                    @Override
                    public void success(DataReviewAppintmentModel dataReviewAppintmentModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).isTokenValid(dataReviewAppintmentModel.messages, response,AppoitmentReviewLihatActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                getDataAppointment(unique_id);
                            }

                        });
                        if (isTokenValid) {
                            progressBar.setVisibility(View.INVISIBLE);

                            if (dataReviewAppintmentModel.messages.response_code == 200) {
                                if (dataReviewAppintmentModel.results != null) {
                                    doctorSpecialityTextView.setText(currentLanguage.equals("en")?dataReviewAppintmentModel.results.profile_doctor.specialities.get(0).specialization.specialization_english:dataReviewAppintmentModel.results.profile_doctor.specialities.get(0).specialization.specialization_bahasa);
                                    doctorNameTextView.setText(dataReviewAppintmentModel.results.profile_doctor.doctor_name);
                                    ((AppController) getApplication()).displayImage(AppoitmentReviewLihatActivity.this,dataReviewAppintmentModel.results.profile_practice.photo.large_image, doctorCoverImageView);
                                    ((AppController) getApplication()).displayImage(AppoitmentReviewLihatActivity.this,dataReviewAppintmentModel.results.profile_doctor.photo.medium_image, doctorAvatarImageView);
                                    ratBarkeramahan.setRating(Float.parseFloat(String.valueOf(dataReviewAppintmentModel.results.reviews.point_friendly)));
                                    ratBarkebersihan.setRating(Float.parseFloat(String.valueOf(dataReviewAppintmentModel.results.reviews.point_facility)));
                                    ratBarwaktu.setRating(Float.parseFloat(String.valueOf(dataReviewAppintmentModel.results.reviews.point_timing)));
                                    seefeedBackAppointment.setText(dataReviewAppintmentModel.results.reviews.feedback_from_user);
                                    seefeedBackKonsula.setText(dataReviewAppintmentModel.results.reviews.feedback_to_konsula);
                                    reviewMainContentLayout.setVisibility(View.VISIBLE);
                                    textViewStatusComment.setText(dataReviewAppintmentModel.results.reviews.status.equals("publish")?getResources().getString(R.string.review_status_publish):getResources().getString(R.string.review_status_unpublish));
                                }
                            }

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }

        );
    }


}
