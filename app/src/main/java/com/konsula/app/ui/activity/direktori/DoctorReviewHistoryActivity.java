package com.konsula.app.ui.activity.direktori;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DoctorReviewModel;
import com.konsula.app.ui.adapter.DoctorReviewAdapter;
import com.konsula.app.ui.custom.MyLinearLayoutManager;

/**
 * Created by Konsula on 14/03/2016.
 */
public class DoctorReviewHistoryActivity extends AppCompatActivity {
    RatingBar ratingBar1;
    RatingBar ratingBar2;
    RatingBar ratingBar3;
    RatingBar ratingBar4;
    RatingBar ratingBar5;
    DoctorReviewModel.Results mResource;
    RecyclerView mRecyclerReview;
    ImageButton backButtonImage;
    TextView titleTextView;
    private String doctorName;
    public static String DOCTOR_NAME = "doctor_name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_doctor_review);

        Intent intent = getIntent();
        doctorName = intent.getStringExtra(DOCTOR_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        backButtonImage = (ImageButton) toolbar.findViewById(R.id.doctor_review_close_image_button);
        titleTextView = (TextView)toolbar.findViewById(R.id.doctor_review_title);
        titleTextView.setText(doctorName);

        mResource =((AppController)getApplication()).getSessionManager().getDoctorReview();

        initRating();
        initRecycler();

        backButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initRating(){
        ratingBar1 = (RatingBar)findViewById(R.id.doctor_rating_bar1);
        ratingBar2 = (RatingBar)findViewById(R.id.doctor_rating_bar2);
        ratingBar3 = (RatingBar)findViewById(R.id.doctor_rating_bar3);
        ratingBar4 = (RatingBar)findViewById(R.id.doctor_rating_bar4);

        ratingBar1.setRating(Float.parseFloat(mResource.summary.total_point_facility));
        ratingBar2.setRating(Float.parseFloat(mResource.summary.total_point_friendly));
        ratingBar3.setRating(Float.parseFloat(mResource.summary.total_point_overall));
        ratingBar4.setRating(Float.parseFloat(mResource.summary.total_point_timing));
    }

    private  void initRecycler() {
        mRecyclerReview = (RecyclerView)findViewById(R.id.doctor_reviews_recycler_view);
        mRecyclerReview.setHasFixedSize(true);
        mRecyclerReview.setAdapter(new DoctorReviewAdapter(mResource.review_list));
        mRecyclerReview.setClickable(true);
        mRecyclerReview.setLayoutManager(new MyLinearLayoutManager(this));
    }

}
