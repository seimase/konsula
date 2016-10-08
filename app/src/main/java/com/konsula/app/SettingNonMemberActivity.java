package com.konsula.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Locale;

import javax.annotation.Nullable;

/**
 * Created by konsula on 12/8/2015.
 */
public class SettingNonMemberActivity extends AppCompatActivity {
    private Button btnSaveSetting;
    private ImageView btnbahasa, btnlanguage;
    private ImageButton backButton;
    private SwitchCompat switchCompatLocation;
    private SwitchCompat switchCompatNotif;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_non_member);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        backButton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(SettingNonMemberActivity.this, HomeActivity.class);
                intent.putExtra("fromSetting", true);
                startActivity(intent);
            }
        });

        switchCompatLocation = (SwitchCompat) findViewById(R.id.setting_autolocation_switch);
        switchCompatNotif = (SwitchCompat) findViewById(R.id.setting_notification_switch);
        btnbahasa = (ImageView) findViewById(R.id.btnbahasa);
        btnlanguage = (ImageView) findViewById(R.id.btnlanguage);
        btnSaveSetting = (Button) findViewById(R.id.setting_action_save_button);

        if (getResources().getString(R.string.locale).equals("indonesia")) {
            btnbahasa.setBackground(getResources().getDrawable(R.drawable.bahasa));
            btnlanguage.setBackground(getResources().getDrawable(R.drawable.english_non));

        } else if (getResources().getString(R.string.locale).equals("english")) {
            btnbahasa.setBackground(getResources().getDrawable(R.drawable.bahasa_non));
            btnlanguage.setBackground(getResources().getDrawable(R.drawable.english));
        }

        btnSaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingNonMemberActivity.this, "Saved", Toast.LENGTH_LONG).show();
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

        btnlanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppController) getApplication()).getSessionManager().setLocale("en");
                ((AppController) getApplication()).getSessionManager().setLanguage("en");
//                setLocale("en");
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
//                setLocale("id");
//                ((AppController) getApplication()).getSessionManager().setCurrLocale("id");
                finish();
                startActivity(getIntent());
            }
        });
    }

//    public void setLocale(String lang) {
//        firstSetupLocale = new Locale(lang);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = firstSetupLocale;
//        res.updateConfiguration(conf, dm);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(SettingNonMemberActivity.this, HomeActivity.class);
        intent.putExtra("fromSetting", true);
        startActivity(intent);
    }
}
