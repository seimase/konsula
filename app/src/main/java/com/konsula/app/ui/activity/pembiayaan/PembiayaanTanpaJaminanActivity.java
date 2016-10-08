package com.konsula.app.ui.activity.pembiayaan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.LoanModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.custom.CustomtextView;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 3/3/2016.
 */

public class PembiayaanTanpaJaminanActivity extends Activity {
    private RelativeLayout btnRequest;
    private CustomtextView tvIdr;
    private EditText etBesarJaminan, etLamaJaminan, etLimitcard;
    private int lama_jaminan = 2;
    int installment = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pembiayaan_tanpa_jaminan);
        initData();
        tvIdr.setText("IDR -");

        etBesarJaminan.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable view) {
                try {
                    etBesarJaminan.removeTextChangedListener(this);
                    String value = etBesarJaminan.getText().toString();


                    if (value != null && !value.equals("")) {
                        String str = etBesarJaminan.getText().toString().replaceAll(",", "");
                        if (!value.equals(""))
                            etBesarJaminan.setText(((AppController) getApplication()).getDecimalFormattedString(str));
                        etBesarJaminan.setSelection(etBesarJaminan.getText().toString().length());
                    }
                    etBesarJaminan.addTextChangedListener(this);
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    etBesarJaminan.addTextChangedListener(this);
                }
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etBesarJaminan.getText().toString().equals("") && etLamaJaminan.getText().toString().equals("")) {
                    tvIdr.setText("IDR -");
                } else if (etLimitcard.getText().toString().equals("") && etLamaJaminan.getText().toString().equals("")) {
                    tvIdr.setText("IDR -");
                } else if (etLimitcard.getText().toString().equals("")){
                    tvIdr.setText("IDR -");
                }else {
                    doCount();
                }

            }
        });

        etLamaJaminan.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etLamaJaminan.getText().toString().equals("")) {
                    tvIdr.setText("IDR -");
                } else {
                    if (etBesarJaminan.getText().toString().equals("")) {
                        tvIdr.setText("IDR -");
                    } else if (etLimitcard.getText().toString().equals("")) {
                        tvIdr.setText("IDR -");
                    } else {
                        doCount();
                    }
                }
            }
        });

        etLimitcard.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    etLimitcard.removeTextChangedListener(this);
                    String value = etLimitcard.getText().toString();


                    if (value != null && !value.equals("")) {

                        String str = etLimitcard.getText().toString().replaceAll(",", "");
                        if (!value.equals(""))
                            etLimitcard.setText(((AppController) getApplication()).getDecimalFormattedString(str));
                        etLimitcard.setSelection(etLimitcard.getText().toString().length());
                    }
                    etLimitcard.addTextChangedListener(this);
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    etBesarJaminan.addTextChangedListener(this);
                }
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etLimitcard.getText().toString().equals("")) {
                    tvIdr.setText("IDR -");
                } else {
                    if (etBesarJaminan.getText().toString().equals("")) {
                        tvIdr.setText("IDR -");
                    } else if (etLimitcard.getText().toString().equals("")) {
                        tvIdr.setText("IDR -");
                    } else if (!etLamaJaminan.getText().toString().equals("")){
                        doCount();
                    }
                }
            }
        });

        etLamaJaminan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PembiayaanTanpaJaminanActivity.this, BentukJaminanActivity.class);
                i.putExtra("title", getResources().getString(R.string.tab2b));
                i.putExtra("bentuk", "None");
                startActivityForResult(i, lama_jaminan);
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etBesarJaminan.getText().toString().equals("")
                        || etLamaJaminan.getText().toString().equals("")
                        || tvIdr.getText().toString().equals("IDR -")) {
                    Toast.makeText(PembiayaanTanpaJaminanActivity.this, R.string.dialog_register_input_all, Toast.LENGTH_SHORT).show();
                } else if (installment != 0) {
                    installment = 0;
                    Intent i = new Intent(PembiayaanTanpaJaminanActivity.this, ConfirmPembiayaanActivity.class);
                    i.putExtra("besar", ((AppController) getApplication()).trimCommaOfString(etBesarJaminan.getText().toString()));
                    i.putExtra("lama", etLamaJaminan.getText().toString());
                    i.putExtra("installment", String.valueOf(installment));
                    startActivity(i);
                } else {
                    Toast.makeText(PembiayaanTanpaJaminanActivity.this, R.string.dialog_register_input_all, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void initData() {
        tvIdr = (CustomtextView) findViewById(R.id.tvIdr);
        btnRequest = (RelativeLayout) findViewById(R.id.btnRequest);
        etBesarJaminan = (EditText) findViewById(R.id.etBesarJaminan);
        etLamaJaminan = (EditText) findViewById(R.id.etLamaJaminan);
        etLimitcard = (EditText) findViewById(R.id.etlimit_card);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                etLamaJaminan.setText("" + data.getStringExtra("text"));
            }
        }
    }

    private void doCount() {
        if (etLimitcard.getText().toString() != null || etLimitcard.getText().toString() != "" && etLamaJaminan.getText().toString() != null){
            String substr = etLamaJaminan.getText().toString().substring(0, 2);
            NetworkManager.getNetworkService(getApplicationContext()).doCalculate(((AppController) getApplication()).getSessionManager().getToken(), Long.parseLong(((AppController) getApplication()).trimCommaOfString(etLimitcard.getText().toString())), Long.parseLong(((AppController) getApplication()).trimCommaOfString(etLimitcard.getText().toString())), Integer.parseInt(substr), "none", new Callback<LoanModel>() {
                @Override
                public void success(LoanModel loanModel, Response response) {
                    if (loanModel.results != null) {
                        tvIdr.setTypeface(null, Typeface.BOLD);
                        tvIdr.setText(loanModel.results.installment_format);
                        installment = loanModel.results.installment;
                    } else {
                        installment = 0;
                        tvIdr.setTypeface(null, Typeface.NORMAL);
                        tvIdr.setText(loanModel.messages.response_text);
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("ResultModel", "ERROR");
                }
            });

        }
      }



}