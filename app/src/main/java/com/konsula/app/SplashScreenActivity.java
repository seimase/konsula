package com.konsula.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by Konsula on 24/06/2016.
 */
public class SplashScreenActivity extends AppCompatActivity {
    private ImageView imageViewLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_splash_screen);
        imageViewLogo = (ImageView) findViewById(R.id.image_logo);
        Animation animfadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageViewLogo.startAnimation(animfadein);

        ((AppController) getApplication()).setMixpanel("Open Application");
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                checkLogin();
            }
        }.start();
    }

    private void checkLogin() {
        finish();
        if (!((AppController) getApplication()).getSessionManager().isUserLogon()) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }
    }

}
