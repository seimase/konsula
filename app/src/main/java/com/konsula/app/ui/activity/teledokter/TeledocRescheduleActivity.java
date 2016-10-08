package com.konsula.app.ui.activity.teledokter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.CancelTeledocModel;
import com.konsula.app.service.model.RescheduleConfirmTeledocModel;
import com.konsula.app.service.model.RescheduleRequestModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 28/04/2016.
 */
public class TeledocRescheduleActivity extends AppCompatActivity {
    private TextView tvNamaDokter;
    private TextView etSpeciality;
    private TextView tvDate;
    private TextView btnConfirm;
    private TextView btnReschedule;
    private TextView btnCancelSchedule;
    private AppController appController;

    private ProgressDialog dialog;
    private String currentLanguage;
    private String tele_uuid;
    private String doc_name;
    private String doc_specialization;
    private String doc_schedule;
    public static String TAG_TELE_UUID = "tele_uuid";
    public static String TAG_DOC_NAME = "doc_name";
    public static String TAG_DOC_SCHEDULE = "schedule";
    public static String TAG_DOC_SPECIALIZATION = "specialization";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teledoc_reschedule);
        init();
        setData();
    }

    private void init() {
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        appController = new AppController();
        tvNamaDokter = (TextView) findViewById(R.id.tvNamaDokter);
        etSpeciality = (TextView) findViewById(R.id.etSpeciality);
        tvDate = (TextView) findViewById(R.id.tvDate);
        btnConfirm = (TextView) findViewById(R.id.btn_confirm_reschedule);
        btnReschedule =(TextView) findViewById(R.id.btn_reschedule);
        btnCancelSchedule =(TextView) findViewById(R.id.btn_cancel_chedule);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescheduleconfirm();
            }
        });
        btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reschedule();
            }
        });
        btnCancelSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void setData() {
        try {
            tele_uuid = getIntent().getExtras().getString(TAG_TELE_UUID);
            doc_name = getIntent().getExtras().getString(TAG_DOC_NAME);
            doc_specialization = getIntent().getExtras().getString(TAG_DOC_SPECIALIZATION);
            doc_schedule = getIntent().getExtras().getString(TAG_DOC_SCHEDULE);
            tvNamaDokter.setText(doc_name);
            etSpeciality.setText(doc_specialization);
            tvDate.setText(doc_schedule);

        } catch (Exception e) {

        }
    }

    public void cancel() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        setcancelteledoc(tele_uuid);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(TeledocRescheduleActivity.this);
        builder.setMessage(getResources().getString(R.string.text_batal_teledok)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.text_no), dialogClickListener).show();
    }

    private void reschedule() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        doRequestReschedule(tele_uuid);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(TeledocRescheduleActivity.this);
        builder.setMessage(getResources().getString(R.string.text_teledoc_reschedule)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.text_no), dialogClickListener).show();
    }

    private void rescheduleconfirm() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        doRescheduleConfirm(tele_uuid);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(TeledocRescheduleActivity.this);
        String s = getString(R.string.text_teledoc_reschedule_confirm, ((AppController) getApplicationContext()).fulldate(doc_schedule, ((AppController) getApplicationContext()).getSessionManager().getLanguage()), ((AppController) getApplicationContext()).fulltime(doc_schedule));
        builder.setMessage(s).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.text_no), dialogClickListener).show();
    }

    private void doRequestReschedule(final String tele_uuid) {
        dialog = ProgressDialog.show(TeledocRescheduleActivity.this, getResources().getString(R.string.dialog_title_please_wait), getResources().getString(R.string.dialog_title_req), true);
        dialog.show();
        NetworkManager.getNetworkService(getApplicationContext()).doReqReschedule(((AppController) getApplication())
                .getSessionManager().getToken(), currentLanguage, tele_uuid, new Callback<RescheduleRequestModel>() {
            @Override
            public void success(RescheduleRequestModel rescheduleRequestModel, Response response) {
                    dialog.dismiss();
                boolean isTokenValid = ((AppController) getApplication()).
                        isTokenValid(rescheduleRequestModel.messages, response,TeledocRescheduleActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                dialog.dismiss();
                                doRequestReschedule(tele_uuid);
                            }


                        });
                if (isTokenValid) {
                    if (rescheduleRequestModel.results.success) {
                        doDialog(TeledocRescheduleActivity.this, rescheduleRequestModel.messages.response_text);
                    } else {
                        appController.doDialog(TeledocRescheduleActivity.this, rescheduleRequestModel.messages.response_text);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
                appController.doDialog(TeledocRescheduleActivity.this, getResources().getString(R.string.no_connection_error_message));
            }
        });
    }

    private void doRescheduleConfirm(final String tele_uuid) {
        dialog = ProgressDialog.show(TeledocRescheduleActivity.this, getResources().getString(R.string.dialog_title_please_wait), getResources().getString(R.string.dialog_title_req), true);
        dialog.show();
        NetworkManager.getNetworkService(getApplicationContext()).doRescheduleConfirm(((AppController) getApplication())
                .getSessionManager().getToken(), currentLanguage, tele_uuid, new Callback<RescheduleConfirmTeledocModel>() {
            @Override
            public void success(RescheduleConfirmTeledocModel rescheduleConfirmTeledocModel, Response response) {
                dialog.dismiss();
                boolean isTokenValid = ((AppController) getApplication()).
                        isTokenValid(rescheduleConfirmTeledocModel.messages, response,TeledocRescheduleActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                dialog.dismiss();
                                doRescheduleConfirm(tele_uuid);
                            }

                        });
                if (isTokenValid) {
                    if (rescheduleConfirmTeledocModel.results.success) {
                        doDialog(TeledocRescheduleActivity.this, rescheduleConfirmTeledocModel.messages.response_text);
                    } else {
                        appController.doDialog(TeledocRescheduleActivity.this, rescheduleConfirmTeledocModel.messages.response_text);
                    }
                }


            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
                appController.doDialog(TeledocRescheduleActivity.this, getResources().getString(R.string.no_connection_error_message));
            }
        });
    }

    public void doDialog(Context context, String message) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        finish();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setPositiveButton("Dismiss", dialogClickListener).show();
    }

    public void setcancelteledoc(final String uuid) {
        dialog = ProgressDialog.show(TeledocRescheduleActivity.this, getResources().getString(R.string.dialog_title_please_wait), getResources().getString(R.string.dialog_title_req), true);
        dialog.show();
        NetworkManager.getNetworkService(getApplicationContext()).cancelTeledoc(((AppController) getApplication()).getSessionManager().getToken(), currentLanguage, uuid, new Callback<CancelTeledocModel>() {
            @Override
            public void success(CancelTeledocModel cancelTeledocModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).
                        isTokenValid(cancelTeledocModel.messages, response,TeledocRescheduleActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                setcancelteledoc(uuid);
                            }


                        });
                if (isTokenValid) {
                    dialog.dismiss();
                    if (cancelTeledocModel.results.data != null) {
                        doDialog(TeledocRescheduleActivity.this, cancelTeledocModel.messages.response_text);
                    }else {
                        appController.doDialog(TeledocRescheduleActivity.this, cancelTeledocModel.messages.response_text);
                    }

                }

            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
                appController.doDialog(TeledocRescheduleActivity.this, getResources().getString(R.string.no_connection_error_message));
            }
        });

    }
}
