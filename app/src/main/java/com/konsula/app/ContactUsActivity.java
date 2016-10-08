package com.konsula.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.service.model.ContactUsModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.direktori.GoogleMapActivity;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/14/2015.
 */
public class ContactUsActivity extends AppCompatActivity {
    private ImageButton backButton;
    private ProgressDialog dialog;
    private EditText etFullname, etEmail, etPhone, etMessage;
    private Button btnSend;
    private RelativeLayout tvSeeMap;
    private Double latitude = -6.1815108;
    private Double longitude = 106.7283656106;
    private String title = "Konsula";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        etFullname = (EditText) findViewById(R.id.etFullname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etMessage = (EditText) findViewById(R.id.etMessage);

        if (etPhone.getText().toString().length() == 0) {
            etPhone.setText("+62");
            etPhone.setSelection(3);

        }
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = etPhone.getText().toString();
                if (phone.length() < 3) {
                    etPhone.setText("+62");
                    etPhone.setSelection(3);

                }

            }

        });

        tvSeeMap = (RelativeLayout) findViewById(R.id.tvSeeMap);
        tvSeeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ContactUsActivity.this, GoogleMapActivity.class);
                i.putExtra("longitude", longitude);
                i.putExtra("latitude", latitude);
                i.putExtra("title", title);
                startActivity(i);
            }
        });

        backButton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSend = (Button) findViewById(R.id.send_contact_us_button);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etFullname.getText().toString().equals("") ||
                        etEmail.getText().toString().equals("") ||
                        etPhone.getText().toString().equals("") ||
                        etMessage.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(etEmail.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Harap isi email sesuai format", Toast.LENGTH_SHORT).show();
                } else if (!isValidCellPhone(etPhone.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Harap isi nomor telepon dengan angka", Toast.LENGTH_SHORT).show();
                } else {
                    doContactUs(etFullname.getText().toString(), etEmail.getText().toString(), etPhone.getText().toString()
                            , etMessage.getText().toString());
                }
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isValidCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    private void doContactUs(final String full_name, final String email, final String phone_number, final String message) {
        dialog = ProgressDialog.show(ContactUsActivity.this, "Please wait", "Sending Information", true);
        dialog.show();

        NetworkManager.getNetworkService(getApplicationContext()).doContact(((AppController) getApplication()).getSessionManager().getToken(), full_name, email, phone_number, message, new Callback<ContactUsModel>() {
            @Override
            public void success(ContactUsModel contactUsModel, Response response) {
                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();

                try {
                    if (error.getResponse().getStatus() == 200) {
                        Log.e("MASUK SUCCESS", "MASUK SUCCESS");
                        DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        finish();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(ContactUsActivity.this);
                        builder2.setMessage("Your message has sent. We will contact you soon.").setPositiveButton(ContactUsActivity.this.getResources().getString(R.string.general_text_dismiss), dialogClickListener2).show();
                    } else {
                        Log.e("MASUK FAILURE", "MASUK FAILURE");
                        DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        finish();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(ContactUsActivity.this);
                        builder2.setMessage("Your message failed to sent. Please check your internet connection.").setPositiveButton(ContactUsActivity.this.getResources().getString(R.string.general_text_dismiss), dialogClickListener2).show();
                    }
                } catch (Exception e) {
                    Log.e("MASUK FAILURE", "MASUK FAILURE");
                    DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder2 = new AlertDialog.Builder(ContactUsActivity.this);
                    builder2.setMessage("Your message failed to sent. Please check your internet connection.").setPositiveButton(ContactUsActivity.this.getResources().getString(R.string.general_text_dismiss), dialogClickListener2).show();
                }
            }
        });
    }
}
