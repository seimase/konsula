package com.konsula.app.ui.activity.direktori;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.GeneralCheckModel;
import com.konsula.app.service.model.VerificationModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/14/2015.
 */
public class BookConfirmationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView confirmation;
    private TextView phoneTextView;
    private EditText verificationCodeEditText;
    private TextView resendBtn;
    private String uniqueId;
    private String phone;

    public static final String UNIQUE_ID = "uniqueId";
    public static final String PHONE = "phone";
    ProgressDialog dlg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);
        Intent intent = getIntent();
        uniqueId = intent.getStringExtra(UNIQUE_ID);
        phone = intent.getStringExtra(PHONE);


        toolbar = (Toolbar) findViewById(R.id.toolbar_booking_information);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        phoneTextView = (TextView)findViewById(R.id.confirmation_booking_phone_text_view);
        verificationCodeEditText = (EditText)findViewById(R.id.confirmation_booking_verification_code_edit_text);
        resendBtn = (TextView)findViewById(R.id.confirmation_booking_resend_text_view);
        phoneTextView.setText(phone);

        confirmation = (TextView) findViewById(R.id.confirmation_booking_text_view);
        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificationCodeEditText.getText().toString().equals(""))
                {
                    verificationCodeEditText.setError("Required");
                }
                else
                {
                    doMakeAppointment();
                }
            }
        });

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doResendVerification();
            }
        });

    }

    private void doResendVerification() {
        dlg = ProgressDialog.show(BookConfirmationActivity.this, "Please wait", "Sending your verification code", true);
        dlg.show();
        NetworkManager.getNetworkService(getApplicationContext()).resendVerification(((AppController) getApplication()).getSessionManager().getToken(), uniqueId, new Callback<GeneralCheckModel>() {
            @Override
            public void success(GeneralCheckModel generalCheckModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(generalCheckModel.messages, response, BookConfirmationActivity.this,new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        doResendVerification();
                    }


                });
                if (isTokenValid) {
                    if(generalCheckModel.results)
                        Toast.makeText(BookConfirmationActivity.this, "A Verification Code has been sent", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(BookConfirmationActivity.this, generalCheckModel.messages.response_text, Toast.LENGTH_SHORT).show();
                    dlg.dismiss();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                dlg.dismiss();
            }
        });
    }
    private void doMakeAppointment(){
        dlg = ProgressDialog.show(BookConfirmationActivity.this, "Please wait", "Saving your appointment", true);
        dlg.show();
        NetworkManager.getNetworkService(getApplicationContext()).makeAppointment(
                ((AppController) getApplication()).getSessionManager().getToken(),
                uniqueId,
                verificationCodeEditText.getText().toString(),
                new Callback<VerificationModel>() {
                    @Override
                    public void success(VerificationModel verificationModel, Response response) {
                        boolean isTokenValid = ((AppController)getApplication()).isTokenValid(verificationModel.messages, response,BookConfirmationActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                doMakeAppointment();
                            }


                        });
                        if(isTokenValid) {
                            if (verificationModel.messages.response_code == 200) {
                                if (verificationModel.results.appointment_code != null) {
                                    Intent intent = new Intent(BookConfirmationActivity.this, AppointmentDoneActivity.class);
                                    intent.putExtra(AppointmentDoneActivity.APPOINTMENT_CODE, verificationModel.results.appointment_code);
                                    startActivity(intent);
                                }
                            } else {
                                String err = verificationModel.messages.response_text;
                                Toast.makeText(BookConfirmationActivity.this, err, Toast.LENGTH_SHORT).show();
                                dlg.dismiss();
                            }
                            dlg.dismiss();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        String err = NetworkManager.getInstance().getErrorMessage(error);
                        Toast.makeText(BookConfirmationActivity.this, err, Toast.LENGTH_SHORT).show();
                        dlg.dismiss();
                    }
                }
        );
    }
}
