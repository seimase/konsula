package com.konsula.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.konsula.app.service.model.CheckVersionModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.fragment.SplashFragment;
import com.konsula.app.util.RefreshTokenCallback;
import com.nineoldandroids.view.ViewHelper;
import com.vistrav.ask.Ask;

import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends AppCompatActivity {
    static final int NUM_PAGES = 4;
    Locale firstSetupLocale;
    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    Button btnLogin;
    Button btnRegister;
    ImageView imageView1, imageView2, imageView3, imageViewlogo, btnSetting;
    boolean isOpaque = true;
    boolean fromSetting = false, fromSignout = false;
    int versionapp;
    String appPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_home_screen);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionapp = packageInfo.versionCode;
            appPackageName = getPackageName();
        } catch (PackageManager.NameNotFoundException e) {
            //Handle exception
        }
        try {
            fromSetting = getIntent().getBooleanExtra("fromSetting", false);
            fromSignout = getIntent().getBooleanExtra("fromSignout", false);
        } catch (Exception e) {

        }

        if (fromSetting) {
            ((AppController) getApplication()).getSessionManager().setLocale(getResources().getString(R.string.locale).equals("english") ? "en" : "id");
        } else {
            if (fromSignout) {
                ((AppController) getApplication()).getSessionManager().setLocale(getResources().getString(R.string.locale).equals("english") ? "en" : "id");
            } else {
                if (((AppController) getApplication()).getSessionManager().getLanguage().isEmpty()) {
                    ((AppController) getApplication()).getSessionManager().setLocale("id");
//                    ((AppController) getApplication()).getSessionManager().setCurrLocale("id");
                } else {
                    ((AppController) getApplication()).getSessionManager().setLocale(((AppController) getApplication()).getSessionManager().getLanguage().equals("en") ? "en" : "id");
                }
            }
        }

        imageViewlogo = (ImageView) findViewById(R.id.image_logo);
        imageView1 = (ImageView) findViewById(R.id.image_splash1);
        imageView2 = (ImageView) findViewById(R.id.image_splash2);
        imageView3 = (ImageView) findViewById(R.id.image_splash3);
        btnSetting = (ImageView) findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(HomeActivity.this, SettingNonMemberActivity.class);
                startActivity(intent);
            }
        });

        Animation animsideinleft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        Animation animfadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView2.startAnimation(animsideinleft);
        imageView3.startAnimation(animfadein);
        imageViewlogo.startAnimation(animfadein);

        initcomponent();
        initVersion();
        checkPermision();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pager != null) {
            pager.clearOnPageChangeListeners();
        }
    }

    private void buildCircles() {
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (scale + 0.01f);

        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.bg_grey_circle);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, 2, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index) {
        if (index < NUM_PAGES) {
            for (int i = 0; i < NUM_PAGES; i++) {
                ImageView circle = (ImageView) circles.getChildAt(i);
                if (i == index) {
                    circle.setColorFilter(getResources().getColor(R.color.grey_dark));
                } else {
                    circle.setColorFilter(getResources().getColor(R.color.grey_xl));
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SplashFragment tp = null;
            switch (position) {
                case 0:
                    tp = SplashFragment.newInstance(R.layout.fragment_welcome);
                    break;
                case 1:
                    tp = SplashFragment.newInstance(R.layout.fragment_welcome1);
                    break;
                case 2:
                    tp = SplashFragment.newInstance(R.layout.fragment_welcome2);
                    break;
                case 3:
                    tp = SplashFragment.newInstance(R.layout.fragment_welcome3);
                    break;
            }
            return tp;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View text_head = page.findViewById(R.id.heading);
            View text_content = page.findViewById(R.id.content);

            if (0 <= position && position < 1) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if (-1 < position && position < 0) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if (position <= -1.0f || position >= 1.0f) {
            } else if (position == 0.0f) {
            } else {
                if (backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));

                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head, pageWidth * position);
                    ViewHelper.setAlpha(text_head, 1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content, pageWidth * position);
                    ViewHelper.setAlpha(text_content, 1.0f - Math.abs(position));
                }
            }
        }
    }


//    public void setLocale(String lang) {
//        firstSetupLocale = new Locale(lang);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = firstSetupLocale;
//        res.updateConfiguration(conf, dm);
//    }

    private void initcomponent() {
        btnLogin = (Button) findViewById(R.id.btn_Login);
        btnRegister = (Button) findViewById(R.id.btn_Register);
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == NUM_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppController) getApplication()).setMixpanel("Open Register Page");
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    private void initVersion() {
        NetworkManager.getNetworkService(getApplicationContext()).initVersionApp(
                new Callback<CheckVersionModel>() {
                    @Override
                    public void success(CheckVersionModel checkVersionModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).isTokenValid(checkVersionModel.messages, response, HomeActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                initVersion();
                            }


                        });
                        if (isTokenValid) {
                            Log.d("ddd",String.valueOf(versionapp));
                            if (checkVersionModel.messages.response_code == 200) {
                                if (checkVersionModel.results.version > versionapp) {
                                    Log.d("ddss",String.valueOf(versionapp));
                                    new AlertDialog.Builder(HomeActivity.this, R.style.AppCompatAlertDialogStyle)
                                            .setTitle(getString(R.string.text_update_header))
                                            .setMessage(getString(R.string.text_update))
                                            .setPositiveButton(R.string.button_update, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        finish();
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                        finish();
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }

                                                }
                                            })
                                            .setNegativeButton(R.string.text_keluar, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                    // do nothing
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                } else {


                                }

                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermision() {
        Ask.on(this)
                .forPermissions(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_SETTINGS)
                .withRationales("Location permission need for map to work properly",
                        "In order to save file you will need to grant storage permission",
                        "To remind your Appointment We need to Write Set Alarm")
                .go();

    }




}