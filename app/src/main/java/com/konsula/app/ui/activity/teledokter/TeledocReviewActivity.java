package com.konsula.app.ui.activity.teledokter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.ReviewTeledocModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.payment.PaymentConfirmActivity;
import com.konsula.app.ui.activity.payment.PaymentSelectionMembershipActivity;
import com.konsula.app.ui.custom.CircleTransform;
import com.konsula.app.util.RefreshTokenCallback;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 21/04/2016.
 */
public class TeledocReviewActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {
    private ImageButton backButton;
    private String currentLanguage;
    private ProgressDialog dialog;
    private RatingBar ratingBarteledoc;
    private EditText editTextReason;
    private LinearLayout btnstartreview;
    private AppController appController;
    private TextView doctorname;
    private TextView reviewdate;
    private TextView textViewreason;
    String teleuuid, image, docname, date, reason;
    private ImageView ivDoctor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_teledoc);

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        backButton = (ImageButton) findViewById(R.id.backButton);
        ratingBarteledoc = (RatingBar) findViewById(R.id.point_friendly_rating);
        editTextReason = (EditText) findViewById(R.id.text_comment_teledoc);
        btnstartreview = (LinearLayout) findViewById(R.id.btnstartreview);
        doctorname = (TextView) findViewById(R.id.appointment_doctor_name_text_view);
        reviewdate = (TextView) findViewById(R.id.appointment_date_text_view);
        textViewreason = (TextView) findViewById(R.id.appointment_reason_text_view);
        ivDoctor = (ImageView) findViewById(R.id.ivDoctor);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnstartreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBarteledoc.getRating() == 0) {
                    appController.doDialog(TeledocReviewActivity.this, getResources().getString(R.string.text_warning_review));
                } else if (ratingBarteledoc.getRating() < 4) {
                    if (isEmpty(editTextReason) || editTextReason.getText().toString().trim().length() < 25){
                        appController.doDialog(TeledocReviewActivity.this, getResources().getString(R.string.text_warning_review_comment));
                    }else {
                        if(AppController.checkConnection(getBaseContext())){
                            dosendreview();
                        }else{
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    if(AppController.checkConnection(getBaseContext())){
                        dosendreview();
                    }else{
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        appController = new AppController();
        setData();
        ratingBarteledoc.setOnRatingBarChangeListener(this);
    }

    private void setData() {
        try {
            image = getIntent().getExtras().getString("image");
            teleuuid = getIntent().getExtras().getString("uuid");
            date = getIntent().getExtras().getString("waktu");
            reason = getIntent().getExtras().getString("keluhan");
            docname = (getIntent().getExtras().getString("nama"));
            doctorname.setText(docname);
            reviewdate.setText(((AppController) AppController.getAppContext()).setDatefull(date));
            textViewreason.setText(reason);
            Picasso.with(this).load(image).transform(new CircleTransform()).into(ivDoctor);
        } catch (Exception e) {

        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void dosendreview() {
        dialog = ProgressDialog.show(TeledocReviewActivity.this, getResources().getString(R.string.dialog_title_please_wait), getResources().getString(R.string.dialog_title_req), true);
        dialog.show();
        NetworkManager.getNetworkService(getApplicationContext()).doReviewTeledoc(((AppController) getApplication())
                .getSessionManager().getToken(), currentLanguage, teleuuid, ratingBarteledoc.getRating(), editTextReason.getText().toString(), new Callback<ReviewTeledocModel>() {
            @Override
            public void success(ReviewTeledocModel reviewTeledocModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(reviewTeledocModel.messages, response, TeledocReviewActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        dosendreview();
                    }


                });
                if (isTokenValid) {
                    dialog.dismiss();
                    if (reviewTeledocModel.results.status) {
                        ((AppController)getApplication()).setMixpanel("Write Teledoc Review");
                        ((AppController)getApplication()).setFacebookEvent("WriteReview");
                        if (reviewTeledocModel.results.data.payment_status.equals("paid")) {
                            Intent intent = new Intent(TeledocReviewActivity.this, PaymentConfirmActivity.class);
                            intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, true);
                            intent.putExtra(PaymentConfirmActivity.payment_uuid, reviewTeledocModel.results.data.payment_uuid);
                            intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, "teledoctor");
                            startActivity(intent);
                        } else if (reviewTeledocModel.results.data.payment_status.equals("unpaid")) {
                            Intent intent = new Intent(TeledocReviewActivity.this, PaymentSelectionMembershipActivity.class);
                            intent.putExtra(PaymentSelectionMembershipActivity.TAG_SUBCATEGORY, "teledoctor");
                            intent.putExtra(PaymentSelectionMembershipActivity.TAG_PAYMENT_UUID, reviewTeledocModel.results.data.payment_uuid);
                            startActivity(intent);
                        }
                    } else {
                        appController.doDialog(TeledocReviewActivity.this, reviewTeledocModel.messages.response_text);
                    }
                }
                Log.d("err", reviewTeledocModel.results.status.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("err", error.toString());
                dialog.dismiss();
                appController.doDialog(TeledocReviewActivity.this, getResources().getString(R.string.no_connection_error_message));
            }
        });
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (rating < 1) {
            ratingBar.setRating(1);
        }
    }
}
