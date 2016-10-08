package com.konsula.app.ui.activity.pembiayaan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.PembiayaanModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 3/3/2016.
 */
public class ConfirmPembiayaanActivity extends AppCompatActivity {
    private ImageButton backButton;
    private RelativeLayout btnRequest;
    private boolean dengan = false;
    private String besar, lama, installment, bentuk;
    private EditText etName, etEmail, etPhone;
    ProgressDialog dialog;
    private String memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pembiayaan);

        AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
        memberId = userData.uuid;

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPhone = (EditText) findViewById(R.id.etPhone);
        setDataUser();
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

        getIntentData();

        btnRequest = (RelativeLayout) findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().equals("")
                        || etEmail.getText().toString().equals("")
                        || etPhone.getText().toString().equals("")) {
                    Toast.makeText(ConfirmPembiayaanActivity.this, R.string.dialog_register_input_all, Toast.LENGTH_SHORT).show();
                } else {
                    doLoan(
                            ((AppController) getApplication()).getSessionManager().getToken(),
                            memberId,
                            Integer.parseInt(besar),
                            Integer.parseInt(lama),
                            bentuk,
                            "taralite",
                            Integer.parseInt(installment),
                            etName.getText().toString(),
                            etEmail.getText().toString(),
                            etPhone.getText().toString());
                }
            }
        });
    }

    public void getIntentData() {
        Intent data = getIntent();
        dengan = data.getBooleanExtra("dengan", dengan);

        if (dengan) {
            besar = data.getStringExtra("besar");
            String substr = data.getStringExtra("lama").substring(0, 2);
            lama = substr.replace(" ", "");
            installment = data.getStringExtra("installment").replace("IDR ", "");
            if(data.getStringExtra("bentuk").equals(getString(R.string.text_mobil))) bentuk = "car";
            else if(data.getStringExtra("bentuk").equals(getString(R.string.text_rumah))) bentuk = "house";
            else if(data.getStringExtra("bentuk").equals(getString(R.string.text_motor))) bentuk = "motorbike";
        } else if (!dengan) {
            besar = data.getStringExtra("besar");
            String substr = data.getStringExtra("lama").substring(0, 2);
            lama = substr.replace(" ", "");
            installment = data.getStringExtra("installment").replace("IDR ", "");
            bentuk = "none";
        }
    }

    private void setDataUser() {
        try {
            final AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
            etName.setText(userData.fullname);
            etEmail.setText(userData.email);
            etPhone.setText(userData.phone_number);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error Get Data User", Toast.LENGTH_SHORT);
        }
    }

    private void doLoan(final String token, final String uuid, final int loan, final int tenor, final String guarantee, final String provider, final int installment, final String fullname, final String email, final String contact) {
        dialog = ProgressDialog.show(ConfirmPembiayaanActivity.this, getResources().getString(R.string.dialog_title_please_wait), getResources().getString(R.string.dialog_title_req_pembiayaan), true);
        dialog.show();
        NetworkManager.getNetworkService(getApplicationContext()).doLoan(
                token,
                uuid,
                loan,
                tenor,
                guarantee,
                provider,
                installment,
                fullname,
                email,
                contact,
                new Callback<PembiayaanModel>() {
                    @Override
                    public void success(PembiayaanModel pembiayaanModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).isTokenValid(pembiayaanModel.messages, response,ConfirmPembiayaanActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                doLoan(token,
                                        uuid,
                                        loan,
                                        tenor,
                                        guarantee,
                                        provider,
                                        installment,
                                        fullname,
                                        email,
                                        contact);
                            }

                        });
                        if (isTokenValid) {
                            if (pembiayaanModel.messages.response_code == 200) {
                                Intent i = new Intent(ConfirmPembiayaanActivity.this, PembiayaanDoneActivity.class);
                                i.putExtra("name", etName.getText().toString());
                                i.putExtra("email", etEmail.getText().toString());
                                i.putExtra("phone", etPhone.getText().toString());
                                startActivity(i);
                            }else {
                                Toast.makeText(ConfirmPembiayaanActivity.this, pembiayaanModel.messages.response_text, Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(ConfirmPembiayaanActivity.this, "Invalid Request Tenor", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
    }
}
