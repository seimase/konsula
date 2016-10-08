package com.konsula.app.ui.activity.direktori;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreSearchProductModel;
import com.konsula.app.service.model.PracticeReviewModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.adapter.KlinikReviewAdapter;
import com.konsula.app.ui.custom.EndlessRecyclerOnScrollListener;
import com.konsula.app.ui.custom.ItemOffsetDecoration;
import com.konsula.app.ui.custom.MyLinearLayoutManager;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class KlinikReviewActivity extends AppCompatActivity {
    RatingBar ratingBar1;
    RatingBar ratingBar2;
    RatingBar ratingBar3;
    RatingBar ratingBar4;

    private ArrayList<PracticeReviewModel.ReviewList> ReviewListModels;
    PracticeReviewModel.Results mResource;
    RecyclerView mRecyclerReview;
    ImageButton backButtonImage;
    TextView titleTextView;
    private String practiceName;
    private Integer practiceId;
    private Integer currpage =1;
    private KlinikReviewAdapter adapter;
    public static String PRACTICE_NAME = "practice_name";
    public static String PRACTICE_ID = "practice_id";
    private CancelableCallback cancelableCallback;
    private String currentLanguage;
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klinik_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        practiceName = intent.getStringExtra(PRACTICE_NAME);
        practiceId = intent.getIntExtra(PRACTICE_ID, 0);
        backButtonImage = (ImageButton) toolbar.findViewById(R.id.klinik_review_close_image_button);
        mResource = ((AppController) getApplication()).getSessionManager().getKlinikReview();

        titleTextView = (TextView) toolbar.findViewById(R.id.klinik_review_title_text_view);
        titleTextView.setText(practiceName);

        initRating();
        initRecycler();

        backButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initRating() {
        ratingBar1 = (RatingBar) findViewById(R.id.doctor_rating_bar1);
        ratingBar2 = (RatingBar) findViewById(R.id.doctor_rating_bar2);
        ratingBar3 = (RatingBar) findViewById(R.id.doctor_rating_bar3);
        ratingBar4 = (RatingBar) findViewById(R.id.doctor_rating_bar4);

        ratingBar1.setRating(Float.parseFloat(mResource.summary.total_point_overall));
        ratingBar2.setRating(Float.parseFloat(mResource.summary.total_point_friendly));
        ratingBar3.setRating(Float.parseFloat(mResource.summary.total_point_facility));
        ratingBar4.setRating(Float.parseFloat(mResource.summary.total_point_timing));
    }

    private void initRecycler() {
        ReviewListModels = new ArrayList<>();
        mRecyclerReview = (RecyclerView) findViewById(R.id.doctor_reviews_recycler_view);
        mRecyclerReview.setHasFixedSize(true);
        ReviewListModels.addAll(mResource.review_list);
        adapter = new KlinikReviewAdapter(ReviewListModels);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerReview.setLayoutManager(gridLayoutManager);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getpracticereview(currpage);
            }
        };
        mRecyclerReview.setAdapter(adapter);
        mRecyclerReview.setClickable(true);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.space_10);
        mRecyclerReview.addItemDecoration(itemDecoration);
        mRecyclerReview.setOnScrollListener(endlessRecyclerOnScrollListener);


    }

    private void getpracticereview(final Integer page) {
        cancelableCallback = new CancelableCallback<PracticeReviewModel>() {
            @Override
            protected void onSuccess(PracticeReviewModel practiceReviewModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(practiceReviewModel.messages, response, KlinikReviewActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getpracticereview(page);
                    }

                });
                if (isTokenValid) {
                    if (practiceReviewModel.results.review_list!=null){
                        currpage = currpage+1;
                        ReviewListModels.addAll(practiceReviewModel.results.review_list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {

            }
        };
        NetworkManager.getNetworkService(getApplicationContext()).getPracticeReview(
                ((AppController) getApplication())
                        .getSessionManager().getToken(), practiceId, page * 5, currentLanguage, cancelableCallback);

    }
}
