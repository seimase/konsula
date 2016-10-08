package com.konsula.app.ui.activity.direktori;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DoctorReviewModel;
import com.konsula.app.service.model.PracticeReviewModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.adapter.DoctorReviewAdapter;
import com.konsula.app.ui.adapter.KlinikReviewAdapter;
import com.konsula.app.ui.custom.EndlessRecyclerOnScrollListener;
import com.konsula.app.ui.custom.ItemOffsetDecoration;
import com.konsula.app.ui.custom.MyLinearLayoutManager;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DoctorReviewActivity extends AppCompatActivity {
    RatingBar ratingBar1;
    RatingBar ratingBar2;
    RatingBar ratingBar3;
    RatingBar ratingBar4;
    private ArrayList<DoctorReviewModel.ReviewList> ReviewListModels;
    DoctorReviewModel.Results mResource;
    private DoctorReviewAdapter adapter;
    RecyclerView mRecyclerReview;
    ImageButton backButtonImage;
    TextView titleTextView;
    private String doctorName;
    private Integer doctorId;
    private Integer currpage =1;
    public static String DOCTOR_NAME = "doctor_name";
    public static String DOCTOR_ID = "doctor_id";
    private String currentLanguage;
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private CancelableCallback cancelableCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_review);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        Intent intent = getIntent();
        doctorName = intent.getStringExtra(DOCTOR_NAME);
        doctorId = intent.getIntExtra(DOCTOR_ID, 0);
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

        ratingBar1.setRating(Float.parseFloat(mResource.summary.total_point_overall));
        ratingBar2.setRating(Float.parseFloat(mResource.summary.total_point_friendly));
        ratingBar3.setRating(Float.parseFloat(mResource.summary.total_point_facility));
        ratingBar4.setRating(Float.parseFloat(mResource.summary.total_point_timing));
    }

    private  void initRecycler() {
        ReviewListModels = new ArrayList<>();
        mRecyclerReview = (RecyclerView) findViewById(R.id.doctor_reviews_recycler_view);
        mRecyclerReview.setHasFixedSize(true);
        ReviewListModels.addAll(mResource.review_list);
        adapter = new DoctorReviewAdapter(ReviewListModels);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerReview.setLayoutManager(gridLayoutManager);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getDoctorReview(currpage);
            }
        };
        mRecyclerReview.setAdapter(adapter);
        mRecyclerReview.setClickable(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.space_10);
        mRecyclerReview.addItemDecoration(itemDecoration);
        mRecyclerReview.setOnScrollListener(endlessRecyclerOnScrollListener);
    }


    private void getDoctorReview(final int currentpage) {
        cancelableCallback = new CancelableCallback<DoctorReviewModel>() {
            @Override
            protected void onSuccess(DoctorReviewModel doctorReviewModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(doctorReviewModel.messages, response, DoctorReviewActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getDoctorReview(currentpage);
                    }

                });
                if (isTokenValid) {
                    if (doctorReviewModel.results != null) {
                        currpage = currpage+1;
                        ReviewListModels.addAll(doctorReviewModel.results.review_list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {

            }
        };
        NetworkManager.getNetworkService(getApplicationContext()).getDoctorReview(
                ((AppController) getApplication())
                        .getSessionManager().getToken(),currentLanguage , doctorId,currentpage*5, cancelableCallback);

    }
}
