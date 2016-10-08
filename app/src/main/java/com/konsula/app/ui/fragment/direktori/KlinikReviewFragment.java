package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.direktori.KlinikReviewActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.PracticeReviewModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/16/2015.
 */
@SuppressWarnings("ValidFragment")
public class KlinikReviewFragment extends Fragment{

    int practiceId;
    String practiceName;
    public KlinikReviewFragment(int practiceId,String practiceName){
        this.practiceId = practiceId;
        this.practiceName = practiceName;
    }
    private CancelableCallback cancelableCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    TextView tvOtherReview;
    LinearLayout reviewContainer;

    public TextView dateTextView;
    public TextView nameTextView;
    public TextView messageTextView;
    String currentLanguage;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_klinik_review, null, false);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";

        reviewContainer = (LinearLayout)view.findViewById(R.id.klinik_review_container_layout);
        tvOtherReview = (TextView)view.findViewById(R.id.klinik_review_other_review_text_view);

        tvOtherReview.setText(getString(R.string.no_review));
        tvOtherReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
        getpracticereview(inflater);
        return view;
    }

    private void addReview(LayoutInflater inflater,PracticeReviewModel.ReviewList item) {
        View holder = inflater.inflate(R.layout.item_review_doctor, null, false);
        dateTextView = (TextView) holder.findViewById(R.id.item_review_date_text_view);
        nameTextView = (TextView) holder.findViewById(R.id.item_review_name_text_view);
        messageTextView = (TextView) holder.findViewById(R.id.item_review_message_text_view);
        dateTextView.setText(item.date_label);
        nameTextView.setText(item.fullname);
        messageTextView.setText(item.feedback);
        reviewContainer.addView(holder);
    }

    private void getpracticereview(final LayoutInflater inflater){
        cancelableCallback = new CancelableCallback<PracticeReviewModel>() {


            @Override
            protected void onSuccess(PracticeReviewModel practiceReviewModel, Response response) {
                boolean isTokenValid = ((AppController)((Activity)getContext()).getApplication()).isTokenValid(practiceReviewModel.messages, response,getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getpracticereview(inflater);
                    }

                });
                if(isTokenValid) {
                    ((AppController) getActivity().getApplication()).getSessionManager().setKlinikReview(practiceReviewModel.results);

                    if (practiceReviewModel.results.review_list.size() > 2) {
                        for (int i = 0; i < practiceReviewModel.results.review_list.size(); i++) {
                            if (i < 2) {
                                addReview(inflater,practiceReviewModel.results.review_list.get(i));
                            }
                        }
                    } else {
                        for (int i = 0; i < practiceReviewModel.results.review_list.size(); i++) {
                            addReview(inflater,practiceReviewModel.results.review_list.get(i));
                        }
                    }

               /* mRecyclerReview.setHasFixedSize(true);
                if (practiceReviewModel.results.review_list.size() > 2)
                    mRecyclerReview.setAdapter(new KlinikReviewAdapter(practiceReviewModel.results.review_list.subList(0, 2), R.layout.item_review_general));
                else
                    mRecyclerReview.setAdapter(new KlinikReviewAdapter(practiceReviewModel.results.review_list, R.layout.item_review_general));
                mRecyclerReview.setClickable(true);
                mRecyclerReview.setLayoutManager(new MyLinearLayoutManager(getActivity()));
                Log.e("REVIEW_SIZE", practiceReviewModel.results.review_list.size() + "");*/
                    if (practiceReviewModel.results.review_list.size() == 0) {
                        tvOtherReview.setText(getString(R.string.no_review));
                        tvOtherReview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //
                            }
                        });
                    } else {
                        tvOtherReview.setText(getString(R.string.lihat_review_lain).replace("{count}", practiceReviewModel.results.summary.review_total + ""));
                        tvOtherReview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent a = new Intent(getActivity(), KlinikReviewActivity.class);
                                a.putExtra(KlinikReviewActivity.PRACTICE_NAME,practiceName);
                                a.putExtra(KlinikReviewActivity.PRACTICE_ID,practiceId);
                                getActivity().startActivity(a);
                            }
                        });
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {

            }
        };
        NetworkManager.getNetworkService(getActivity()).getPracticeReview(((AppController) getActivity().getApplication()).getSessionManager().getToken(),practiceId,0,currentLanguage, cancelableCallback);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }

}
