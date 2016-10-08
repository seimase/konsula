package com.konsula.app.ui.activity.pembiayaan;

import android.app.Activity;
import android.content.Intent;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 3/3/2016.
 */

public class PembiayaanDenganJaminanActivity extends Activity {
    private RelativeLayout btnRequest;
    private EditText etBesarJaminan, etLamaJaminan, etBentukJaminan;
    private int lama_jaminan = 2;
    private CustomtextView tvIdr;
    private int bentuk_jaminan = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pembiayaan_dengan_jaminan);
        initData();
        tvIdr.setText("IDR -");

        etLamaJaminan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etBentukJaminan.getText().toString().equals("")) {
                    Toast.makeText(PembiayaanDenganJaminanActivity.this, "Silakan isi bentuk jaminan terlebih dahulu", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(PembiayaanDenganJaminanActivity.this, BentukJaminanActivity.class);
                    i.putExtra("title", getResources().getString(R.string.tab2b));
                    i.putExtra("bentuk", "" + etBentukJaminan.getText().toString());
                    startActivityForResult(i, lama_jaminan);
                }
            }
        });

        etBentukJaminan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PembiayaanDenganJaminanActivity.this, BentukJaminanActivity.class);
                i.putExtra("title", getResources().getString(R.string.tab2c));
                startActivityForResult(i, bentuk_jaminan);
            }
        });



        etBesarJaminan.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
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
                //   ((AppController) getApplication()).getDecimalFormattedString();
                if (etLamaJaminan.getText().toString().equals("")
                        || etBentukJaminan.getText().toString().equals("")) {
                    tvIdr.setText("IDR -");
                } else {
                    if (etBesarJaminan.getText().toString().equals("")) {
                        tvIdr.setText("IDR -");
                    } else {
                        doCount();
                    }
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
                if (etBesarJaminan.getText().toString().equals("")
                        || etBentukJaminan.getText().toString().equals("")) {
                    tvIdr.setText("IDR -");
                } else {
                    if (etLamaJaminan.getText().toString().equals("")) {
                        tvIdr.setText("IDR -");
                    } else {
                        doCount();
                    }
                }
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etBesarJaminan.getText().toString().equals("")
                        || etBentukJaminan.getText().toString().equals("")
                        || etLamaJaminan.getText().toString().equals("")
                        || tvIdr.getText().toString().equals("IDR -")) {
                    Toast.makeText(PembiayaanDenganJaminanActivity.this, R.string.dialog_register_input_all, Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(PembiayaanDenganJaminanActivity.this, ConfirmPembiayaanActivity.class);
                    i.putExtra("dengan", true);
                    i.putExtra("besar", ((AppController) getApplication()).trimCommaOfString(etBesarJaminan.getText().toString()));
                    i.putExtra("lama", etLamaJaminan.getText().toString());
                    i.putExtra("bentuk", ((AppController) getApplication()).trimCommaOfString(etBentukJaminan.getText().toString()));
                    i.putExtra("installment", tvIdr.getText().toString().replace(".", "").replace("IDR ", ""));
                    startActivity(i);
                }
            }
        });
    }

    public void initData() {
        tvIdr = (CustomtextView) findViewById(R.id.tvIdr);
        btnRequest = (RelativeLayout) findViewById(R.id.btnRequest);
        etBesarJaminan = (EditText) findViewById(R.id.etBesarJaminan);
        etLamaJaminan = (EditText) findViewById(R.id.etLamaJaminan);
        etBentukJaminan = (EditText) findViewById(R.id.etBentukJaminan);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                etLamaJaminan.setText("" + data.getStringExtra("text"));
            }
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                etBentukJaminan.setText("" + data.getStringExtra("text"));
                etLamaJaminan.setText("");
            }
        }
    }

    private void doCount() {
        String bentuk = etBentukJaminan.getText().toString();
        if (bentuk.equals(getString(R.string.text_mobil))) bentuk = "car";
        else if (bentuk.equals(getString(R.string.text_rumah))) bentuk = "house";
        else if (bentuk.equals(getString(R.string.text_motor))) bentuk = "motorbike";
        else if (bentuk.equals("None")) bentuk = "none";
        String substr = etLamaJaminan.getText().toString().substring(0, 2);
        String substrNew = substr.replace(" ", "");
        NetworkManager.getNetworkService(getApplicationContext()).doCalculate(((AppController) getApplication()).getSessionManager().getToken(), 0, Long.parseLong(((AppController) getApplication()).trimCommaOfString(etBesarJaminan.getText().toString())), Integer.parseInt(substrNew), bentuk, new Callback<LoanModel>() {
            @Override
            public void success(LoanModel loanModel, Response response) {
                tvIdr.setText(loanModel.results.installment_format);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("ResultModel", "ERROR");
            }
        });

    }


}