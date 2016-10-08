package com.konsula.app.ui.activity.direktori;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.ChangePasswordModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.Locale;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/8/2015.
 */
public class SettingActivity extends AppCompatActivity {
    private Button btnSaveSetting;
    private ImageView btnbahasa, btnlanguage;
    private EditText txtOldPassword, txtNewPassword;
    public static final String TAG_FROM_PROFILE = "profile";
    private ImageButton backButton;
    private ProgressDialog dialog;
    private SwitchCompat switchCompatLocation;
    private SwitchCompat switchCompatNotif;
    int fromsetting;
    String currentLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        fromsetting = getIntent().getIntExtra(TAG_FROM_PROFILE, 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        backButton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setfinishsetting();

            }
        });

        txtOldPassword = (EditText) findViewById(R.id.etCurrPass);
        txtNewPassword = (EditText) findViewById(R.id.etNewPass);
        btnbahasa = (ImageView) findViewById(R.id.btnbahasa);
        btnlanguage = (ImageView) findViewById(R.id.btnlanguage);
        switchCompatLocation = (SwitchCompat) findViewById(R.id.setting_autolocation_switch);
        switchCompatNotif = (SwitchCompat) findViewById(R.id.setting_notification_switch);
        btnSaveSetting = (Button) findViewById(R.id.setting_action_save_button);
        btnSaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtOldPassword.getText().toString().isEmpty() && txtNewPassword.getText().toString().isEmpty()) {
                    ((AppController) getApplication()).doDialog(SettingActivity.this, getResources().getString(R.string.validasi_nullAll));
                } else if (txtOldPassword.getText().toString().isEmpty()) {
                    ((AppController) getApplication()).doDialog(SettingActivity.this, getResources().getString(R.string.validasi_nullOld));
                    Toast.makeText(getApplicationContext(), R.string.validasi_nullOld, Toast.LENGTH_SHORT).show();
                } else if (txtOldPassword.getText().toString().isEmpty()) {
                    ((AppController) getApplication()).doDialog(SettingActivity.this, getResources().getString(R.string.validasi_nullNew));
                } else {
                    doChangePassword(txtOldPassword.getText().toString(), txtNewPassword.getText().toString());
                }
            }
        });

        if (getResources().getString(R.string.locale).equals("indonesia")) {
            btnbahasa.setBackground(getResources().getDrawable(R.drawable.bahasa));
            btnlanguage.setBackground(getResources().getDrawable(R.drawable.english_non));
        } else if (getResources().getString(R.string.locale).equals("english")) {
            btnbahasa.setBackground(getResources().getDrawable(R.drawable.bahasa_non));
            btnlanguage.setBackground(getResources().getDrawable(R.drawable.english));
        }

        btnlanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppController) getApplication()).getSessionManager().setLocale("en");
                ((AppController) getApplication()).getSessionManager().setLanguage("en");
//                ((AppController) getApplication()).getSessionManager().setCurrLocale("en");
                finish();
                startActivity(getIntent());
            }
        });
        btnbahasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppController) getApplication()).getSessionManager().setLocale("id");
                ((AppController) getApplication()).getSessionManager().setLanguage("id");
//                ((AppController) getApplication()).getSessionManager().setCurrLocale("id");
                finish();
                startActivity(getIntent());
            }
        });
        switchCompatNotif.setChecked(
                ((AppController) getApplication()).getSessionManager().getReminder());
        switchCompatNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((AppController) getApplication()).getSessionManager().setReminder(isChecked);

            }
        });
        switchCompatLocation.setChecked(((AppController) getApplication()).isGPSON(getApplicationContext()));
        switchCompatLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                startActivity(new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        });

    }

    private void setfinishsetting() {
        finish();
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.putExtra(TAG_FROM_PROFILE, fromsetting);
        startActivity(intent);
    }

    private void doChangePassword(final String oldpassword, final String newpassword) {
        dialog = AppController.createProgressDialog(SettingActivity.this);
        dialog.show();

        NetworkManager.getNetworkService(getApplication()).changePassword(((AppController) getApplication())
                .getSessionManager().getToken(), currentLanguage, oldpassword, newpassword, new Callback<ChangePasswordModel>() {
            @Override
            public void success(ChangePasswordModel changePasswordModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(changePasswordModel.messages, response, SettingActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        doChangePassword(oldpassword, newpassword);
                    }


                });
                if (isTokenValid) {
                    dialog.dismiss();

                    if (changePasswordModel.messages.response_code == 200) {
                        ((AppController) getApplication()).doDialog(SettingActivity.this, changePasswordModel.messages.response_text);
                        txtOldPassword.setText("");
                        txtNewPassword.setText("");
                    } else {
                        ((AppController) getApplication()).doDialog(SettingActivity.this, changePasswordModel.messages.response_text);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
                try {
                    Toast.makeText(SettingActivity.this, "Failed to get response", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(SettingActivity.this, "Failed to get response", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        setfinishsetting();

    }

    private void setserver() {
//        if (spinnerServer.getSelectedItemPosition() == 0) {
//            AppConstant.DOMAIN_URL = "https://staging-api.konsula.com";
////            AppConstant.DOMAIN_URL.replaceAll("(.*)konsula(.*)", "https://staging-api.konsula.com");
//        } else {
//            AppConstant.DOMAIN_URL = "https://api.konsula.com";
//        }
    }

}
