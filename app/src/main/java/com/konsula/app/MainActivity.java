package com.konsula.app;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonArray;
import com.konsula.app.gcm.RegistrationIntentService;
import com.konsula.app.service.model.AccessTeledocModel;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.CheckVersionModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.direktori.SettingActivity;
import com.konsula.app.ui.activity.payment.PaymentConfirmActivity;
import com.konsula.app.ui.activity.teledokter.TeledocRescheduleActivity;
import com.konsula.app.ui.activity.teledokter.TeledocReviewActivity;
import com.konsula.app.ui.activity.teledokter.TeledokterActivity;
import com.konsula.app.ui.activity.teledokter.TeledokterDoneActivity;
import com.konsula.app.ui.activity.teledokter.TeledokterFailActivity;
import com.konsula.app.ui.fragment.MainMenuFragment;
import com.konsula.app.ui.fragment.TransactionFragment;
import com.konsula.app.ui.fragment.VerificationFragment;
import com.konsula.app.ui.fragment.voucher.VoucherFragment;
import com.konsula.app.ui.fragment.direktori.AppointnewFragment;
import com.konsula.app.ui.fragment.direktori.ContactUsFragment;
import com.konsula.app.ui.fragment.direktori.HelpFragment;
import com.konsula.app.ui.fragment.direktori.MembershipFragment;
import com.konsula.app.ui.fragment.direktori.NavMenuFragment;
import com.konsula.app.ui.fragment.direktori.ProfileViewFragment;
import com.konsula.app.ui.fragment.direktori.ShareFragment;
import com.konsula.app.ui.fragment.voucher.VouchervalidFragment;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.util.RefreshTokenCallback;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.vistrav.ask.Ask;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by user on 12/1/2015.
 */
public class MainActivity extends AppCompatActivity implements NavMenuFragment.FragmentDrawerListener, ContactUsFragment.OnSendListener, VerificationFragment.OnVerificationListener, ProfileViewFragment.OnPhoneChangeListener, ProfileViewFragment.OnPhotoChangeListener, VouchervalidFragment.OnSuccessLoadMenuListener {
    private Toolbar mToolbar;
    private NavMenuFragment drawerFragment;
    private ImageView backImageView;
    private TextView toolbarTitle;
    private ImageView toolbarImage;
    private boolean isMainmenu = false;
    AuthModel.Results userData;
    int versionapp;
    String appPackageName;
    private android.app.AlertDialog dialog;
    public static String bundle = "bunde";
    public static String togo = "type";
    private static final String TAG_STATUS = "status";
    private static final String TAG_SCHEDULE = "schedule";
    private static final String TAG_PAGE = "page";
    private static final String TAG_TYPE = "type";
    private static final String TAG_SPECIALIZATION = "specialization";
    private static final String TAG_TELE_UUID = "tele_uuid";
    private static final String TAG_DOC_NAME = "doctor_name";
    private static final String TAG_MESSAGE = "message";


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public VoucherFragment voucherFragment = new VoucherFragment();

    //MixpanelAPI mixpanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
        toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_main_name_text_view);
        toolbarImage = (ImageView) mToolbar.findViewById(R.id.toolbar_mini_konsula_image_view);
        backImageView = (ImageView) findViewById(R.id.toolbar_main_setting_image_view);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        userData = ((AppController) getApplication()).getSessionManager().getUserAccount();

        JSONObject props = new JSONObject();
        try {
            props.put("$first_name", userData.fullname);
            props.put("$username", userData.email);
            props.put("$email", userData.email);
            props.put("Gender", userData.gender);
            props.put("Birth Date", userData.birth_date);
            props.put("Phone Number", userData.phone_number);
            props.put("Address", userData.address);
            props.put("Height", userData.height == 0 ? "" : userData.height + " cm");
            props.put("Weight", userData.weight == 0 ? "" : userData.weight + " kg");
            props.put("Blood Type", userData.blood_type);
            props.put("Country", userData.country);

            ((AppController) getApplication()).setMixpanelPeople(userData.email, props);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent intent = getIntent();
        int fromAppointment = intent.getIntExtra("fromMembership", 0);
        int fromsetting = intent.getIntExtra(SettingActivity.TAG_FROM_PROFILE, 0);
        // set home menu
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String gcm = ((AppController) getApplication()).getSessionManager().getGcmKey();
        Log.e("gcm", gcm);
        drawerFragment = (NavMenuFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        if (userData.photo != null) drawerFragment.setImage(userData.photo);
        // display the first navigation drawer view on app launch
        AppConstant.CURRENT_POSITION = -1;
        if (fromsetting == 10) {
            displayView(1);
        } else if (fromAppointment == 3) {
            //AppConstant.CURRENT_POSITION = 3;
            displayView(3);
        } else if (fromAppointment == 2) {
            AppConstant.FROM_MEMBERSHIP = true;
            //AppConstant.CURRENT_POSITION = 2;
            displayView(2);
        } else {
            //AppConstant.CURRENT_POSITION = 0;
            displayView(0);
        }


        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionapp = packageInfo.versionCode;
            appPackageName = getPackageName();
        } catch (PackageManager.NameNotFoundException e) {
            //Handle exception
        }
        initVersion();
        checkPermision();

        //mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
            }
        };
        registerReceiver();
        if (checkPlayServices()) {
            if (((AppController) getApplication()).getSessionManager().getGcmKey().equals("")) {
                Intent intent2 = new Intent(this, RegistrationIntentService.class);
                startService(intent2);
            }
        }


//        Log.d("ss",String.valueOf(((AppController) getApplication()).getSessionManager().getGcmKey()));
//        if ( ((AppController) getApplication()).getSessionManager().getGcmKey() == null) {
//            Log.d("sds","masuk");
//            Intent intent2 = new Intent(this, RegistrationIntentService.class);
//            startService(intent2);
//        }

        handleGCM();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_search){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        /*if (position == 0 && AppConstant.FROM_PROFILE){
            position = 1;
        } else{
            AppConstant.FROM_PROFILE = false;
        }*/
        if (AppConstant.FROM_PROFILE) position = 1;
        if (AppConstant.FROM_MEMBERSHIP) position = 2;
        displayView(position);
    }

    private void displayView(int position) {
       /* if (AppConstant.FROM_PROFILE) {
            position = 1;
            AppConstant.FROM_PROFILE = false;
        }*/
        AppConstant.FROM_INDEX = position;
        Fragment fragment = null;
        String title = "";
        switch (position) {
            case 0:
                if (AppConstant.CURRENT_POSITION != position) {
                    AppConstant.CURRENT_POSITION = position;
                    if (userData.authentication.equals("pending")) {
                        title = getString(R.string.tittle_verifikasi);
                        toolbarImage.setVisibility(View.INVISIBLE);
                        backImageView.setVisibility(View.INVISIBLE);
                        fragment = new VerificationFragment();
                    } else {
                        ((AppController) getApplication()).setMixpanel("Home");
                        toolbarImage.setVisibility(View.VISIBLE);
                        backImageView.setVisibility(View.VISIBLE);
                        fragment = new MainMenuFragment();
                    }
                    isMainmenu = true;
                }
                break;
            case 1:
                if (AppConstant.CURRENT_POSITION != position) {
                    ((AppController) getApplication()).setMixpanel("My Account");
                    AppConstant.CURRENT_POSITION = position;
                    toolbarImage.setVisibility(View.INVISIBLE);
                    backImageView.setVisibility(View.INVISIBLE);
                    title = getString(R.string.nav_myaccount);
                    fragment = new ProfileViewFragment();
                    isMainmenu = false;
                }


                break;
            case 2:
                if (AppConstant.CURRENT_POSITION != position) {
                    ((AppController) getApplication()).setMixpanel("Membership");
                    AppConstant.CURRENT_POSITION = position;
/*
                    Bundle params = new Bundle();
                    params.putString("doctor_name","docter test");
                    params.putString("doctor_category","docter category");

                    //params.putString("Currency",teledoctorModel.results.data.);
                    ((AppController) getApplication()).setFacebookEvent("testEvent", params);*/
                    title = getString(R.string.membership);
                    toolbarImage.setVisibility(View.INVISIBLE);
                    backImageView.setVisibility(View.INVISIBLE);
                    fragment = new MembershipFragment();
                    isMainmenu = false;
                }

                break;
            case 3:
                ((AppController) getApplication()).setMixpanel("My Transactions");
                AppConstant.CURRENT_POSITION = position;
                toolbarImage.setVisibility(View.INVISIBLE);
                backImageView.setVisibility(View.INVISIBLE);
                title = getString(R.string.billing);
                fragment = new TransactionFragment();
                isMainmenu = false;
                break;
            case 4:
                ((AppController) getApplication()).setMixpanel("My Vouchers");
                AppConstant.CURRENT_POSITION = position;
                toolbarImage.setVisibility(View.INVISIBLE);
                backImageView.setVisibility(View.INVISIBLE);
                title = getString(R.string.purchase);
                fragment = voucherFragment;
                isMainmenu = false;
                break;
            case 5:
                ((AppController) getApplication()).setMixpanel("My Schedules");
                AppConstant.CURRENT_POSITION = position;
                toolbarImage.setVisibility(View.INVISIBLE);
                backImageView.setVisibility(View.INVISIBLE);
                title = getString(R.string.appointments);
                fragment = new AppointnewFragment();
                isMainmenu = false;
                break;
            case 6:
                ((AppController) getApplication()).setMixpanel("Share App");
                AppConstant.CURRENT_POSITION = position;
                toolbarImage.setVisibility(View.INVISIBLE);
                backImageView.setVisibility(View.INVISIBLE);
                title = getString(R.string.share);
                fragment = new ShareFragment();
                isMainmenu = false;
                break;
            case 7:
                ((AppController) getApplication()).setMixpanel("Rate Us");
                AppConstant.CURRENT_POSITION = position;
                toolbarImage.setVisibility(View.INVISIBLE);
                backImageView.setVisibility(View.INVISIBLE);
                title = getString(R.string.contactus);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                isMainmenu = false;
                break;
            case 8:
                ((AppController) getApplication()).setMixpanel("About");
                AppConstant.CURRENT_POSITION = position;
                toolbarImage.setVisibility(View.INVISIBLE);
                backImageView.setVisibility(View.INVISIBLE);
                title = getString(R.string.title_about_us);
                fragment = new HelpFragment();
                isMainmenu = false;
                break;

            default:
                AppConstant.CURRENT_POSITION = 0;
                toolbarImage.setVisibility(View.VISIBLE);
                backImageView.setVisibility(View.VISIBLE);
                title = getString(R.string.app_name);
                fragment = new MainMenuFragment();
                isMainmenu = true;
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_tabcontent, fragment);
            fragmentTransaction.commit();
            toolbarTitle.setText(title);
        }
    }


    @Override
    public void onBackPressed() {
        if (isMainmenu) {
            doDialog();
        } else {
            AppConstant.FROM_BACKMENU = true;
            displayView(0);
        }
    }


    @Override
    public void onSuccessSend() {
        displayView(0);
    }

    @Override
    public void onSuccessVerification() {
        userData.authentication = "valid";
        AppConstant.CURRENT_POSITION = -1;
        displayView(0);
    }

    @Override
    public void onSuccessPhoneChangeListener() {
        userData.authentication = "pending";
        displayView(0);
    }


    private void initVersion() {
        NetworkManager.getNetworkService(getApplicationContext()).initVersionApp(
                new Callback<CheckVersionModel>() {
                    @Override
                    public void success(CheckVersionModel checkVersionModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).isTokenValid(checkVersionModel.messages, response, MainActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                initVersion();
                            }


                        });
                        if (isTokenValid) {
                            if (checkVersionModel.messages.response_code == 200) {
                                if (checkVersionModel.results.version > versionapp) {
                                    new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle)
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

    private void doDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getResources().getString(R.string.text_close)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.text_no), dialogClickListener).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermision() {
        Ask.on(this)
                .forPermissions(
                        android.Manifest.permission.CHANGE_NETWORK_STATE,
                        android.Manifest.permission.SYSTEM_ALERT_WINDOW)
                .go();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

    }

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConstant.REGISTRATION_COMPLETE));

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("Test", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void handleGCM() {
        try {
            Bundle data = getIntent().getExtras().getBundle(bundle);
            String message = data.getString(TAG_MESSAGE, "");
            String doc_name = data.getString(TAG_DOC_NAME, "");
            String schedule = data.getString(TAG_SCHEDULE, "");
            String specialization = data.getString(TAG_SPECIALIZATION, "");
            String status = data.getString(TAG_STATUS, "");
            String tele_uuid = data.getString(TAG_TELE_UUID, "");
            String page = data.getString(TAG_PAGE, "");
            String type = data.getString(TAG_TYPE, "");
            String myJSONString = data.getString("doctor_photos");
            JsonArray jsonArray = new JsonArray();

            final int color = ((AppController) getApplication()).getThemeColor(MainActivity.this);
            if (type.equals("dialog")) {
                if (page.equals("teledoc-confirmed")) {

                    ((AppController)getApplication()).setMixpanel("Teledoc Confirmed");
                    CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, getResources().getColor(R.color.green_xxl));
                    View invoiceDetail = LayoutInflater.from(this).inflate(R.layout.dialog_teledoc_confirm, null);
                    TextView textViewname = (TextView) invoiceDetail.findViewById(R.id.tvNamaDokter);
                    TextView textViewspeciality = (TextView) invoiceDetail.findViewById(R.id.etSpeciality);
                    TextView textViewdate = (TextView) invoiceDetail.findViewById(R.id.tvDate);
                    TextView textViewstatus = (TextView) invoiceDetail.findViewById(R.id.text_status);
                    TextView btnCancel = (TextView) invoiceDetail.findViewById(R.id.positive_button);
                    btnCancel.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                    btnCancel.setText(getResources().getString(R.string.mdtp_ok));
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    textViewname.setText(doc_name);
                    textViewspeciality.setText(specialization);
                    textViewdate.setText(schedule);
                    textViewstatus.setText(status.toUpperCase());
                    builder.setView(invoiceDetail);
                    builder.setTitle(getResources().getString(R.string.title_teledokter));
                    /*dialog = builder.create();
                    dialog.show();
*/
                    dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            Dialog d = ((Dialog) dialog);

                            int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = d.findViewById(dividerId);
                            if (divider != null) {
                                divider.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                            }
                        }
                    });
                    dialog.show();
                }
                if (page.equals("teledoc-cancel")) {
                    CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, getResources().getColor(R.color.green_xxl));
                    View invoiceDetail = LayoutInflater.from(this).inflate(R.layout.dialog_teledoc_cancel_confirm, null);
                    TextView textView = (TextView) invoiceDetail.findViewById(R.id.message);
                    TextView btnCancel = (TextView) invoiceDetail.findViewById(R.id.positive_button);
                    btnCancel.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                    btnCancel.setText(getResources().getString(R.string.mdtp_ok));
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    textView.setText(getResources().getString(R.string.text_teledoc_cancel));
                    builder.setView(invoiceDetail);
                    builder.setTitle(getResources().getString(R.string.title_teledokter));
                    dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            Dialog d = ((Dialog) dialog);

                            int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = d.findViewById(dividerId);
                            if (divider != null) {
                                divider.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                            }
                        }
                    });
                    dialog.show();
                }
                if (page.equals("teledoc-reschedule")) {
                    CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, getResources().getColor(R.color.green_xxl));
                    View invoiceDetail = LayoutInflater.from(this).inflate(R.layout.dialog_teledoc_cancel_confirm, null);
                    TextView textView = (TextView) invoiceDetail.findViewById(R.id.message);
                    TextView btnCancel = (TextView) invoiceDetail.findViewById(R.id.positive_button);
                    btnCancel.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                    btnCancel.setText(getResources().getString(R.string.mdtp_ok));
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    textView.setText(getResources().getString(R.string.text_reschedule_information));
                    builder.setView(invoiceDetail);
                    builder.setTitle(getResources().getString(R.string.title_teledokter));
                    dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            Dialog d = ((Dialog) dialog);

                            int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                            View divider = d.findViewById(dividerId);
                            if (divider != null) {
                                divider.setBackgroundColor(getResources().getColor(R.color.green_xxl));
                            }
                        }
                    });
                    dialog.show();
                }
                if (page.equals("teledoc-review")) {
                    getdataTeledocRetriction();
                }
            } else if (type.equals("intent") && (page.equals("teledoc-confirmation-reschedule"))) {
                Intent intent = new Intent(this, TeledocRescheduleActivity.class);
                intent.putExtra(TeledocRescheduleActivity.TAG_TELE_UUID, tele_uuid);
                intent.putExtra(TeledocRescheduleActivity.TAG_DOC_NAME, doc_name);
                intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SPECIALIZATION, specialization);
                intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SCHEDULE, schedule);
                startActivity(intent);
            }


        } catch (Exception e) {

        }
    }

    private void getdataTeledocRetriction() {
        dialog = AppController.createProgressDialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.show();
        NetworkManager.getNetworkService(getApplication())
                .getAccessTeledoc(((AppController) getApplication())
                        .getSessionManager().getToken(), new Callback<AccessTeledocModel>() {
                    @Override
                    public void success(AccessTeledocModel accessTeledocModel, Response response) {
                        final boolean isTokenValid = ((AppController) getApplication()).isTokenValid(accessTeledocModel.messages, response, MainActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                dialog.dismiss();
                                getdataTeledocRetriction();
                            }

                        });
                        if (isTokenValid) {
                            Intent intent = null;
                            dialog.dismiss();
                            if (accessTeledocModel.results.show_page != null) {
                                switch (accessTeledocModel.results.show_page) {
                                    case "confirmation":
                                        intent = new Intent(MainActivity.this, PaymentConfirmActivity.class);
                                        intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, false);
                                        intent.putExtra(PaymentConfirmActivity.payment_uuid, accessTeledocModel.results.data.payment_uuid);
                                        intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, "teledoctor");
                                        startActivity(intent);
                                        break;
                                    case "payment":
                                        intent = new Intent(MainActivity.this, TeledokterFailActivity.class);
                                        intent.putExtra("payment_uuid", accessTeledocModel.results.data.payment_uuid);
                                        intent.putExtra("name", accessTeledocModel.results.data.doctor_name);
                                        intent.putExtra("date", accessTeledocModel.results.data.schedule);
                                        intent.putExtra("duration", accessTeledocModel.results.data.duration);
                                        intent.putExtra("reason", accessTeledocModel.results.data.reason);
                                        intent.putExtra("spesialisasi", accessTeledocModel.results.data.doctor_specialization);
                                        startActivity(intent);
                                        break;

                                    case "teledoctor":
                                        intent = new Intent(MainActivity.this, TeledokterActivity.class);
                                        startActivity(intent);
                                        break;

                                    case "thankyou":
                                        intent = new Intent(MainActivity.this, TeledokterDoneActivity.class);
                                        intent.putExtra("nama", accessTeledocModel.results.data.doctor_name);
                                        intent.putExtra("spesialisasi", accessTeledocModel.results.data.doctor_specialization);
                                        intent.putExtra("waktu", accessTeledocModel.results.data.schedule);
                                        intent.putExtra("phone", accessTeledocModel.results.data.contact);
                                        intent.putExtra("status", accessTeledocModel.results.data.tele_status);
                                        intent.putExtra("image", accessTeledocModel.results.data.doctor_photo.primary.large_image);
                                        startActivity(intent);
                                        break;
                                    case "review":
                                        intent = new Intent(MainActivity.this, TeledocReviewActivity.class);
                                        intent.putExtra("nama", accessTeledocModel.results.data.doctor_name);
                                        intent.putExtra("uuid", accessTeledocModel.results.data.tele_uuid);
                                        intent.putExtra("waktu", accessTeledocModel.results.data.schedule);
                                        intent.putExtra("keluhan", accessTeledocModel.results.data.reason);
                                        intent.putExtra("image", accessTeledocModel.results.data.doctor_photo.primary.large_image);
                                        startActivity(intent);
                                        break;
                                    case "waiting_reschedule":
                                        intent = new Intent(MainActivity.this, PaymentConfirmActivity.class);
                                        intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, false);
                                        intent.putExtra(PaymentConfirmActivity.payment_uuid, accessTeledocModel.results.data.payment_uuid);
                                        intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, "teledoctor");
                                        startActivity(intent);
                                        break;
                                    case "pending_reschedule":
                                        intent = new Intent(MainActivity.this, TeledocRescheduleActivity.class);
                                        intent.putExtra(TeledocRescheduleActivity.TAG_TELE_UUID, accessTeledocModel.results.data.tele_uuid);
                                        intent.putExtra(TeledocRescheduleActivity.TAG_DOC_NAME, accessTeledocModel.results.data.doctor_name);
                                        intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SPECIALIZATION, accessTeledocModel.results.data.doctor_specialization);
                                        intent.putExtra(TeledocRescheduleActivity.TAG_DOC_SCHEDULE, accessTeledocModel.results.data.schedule);
                                        startActivity(intent);
                                        break;
                                }
                            } else {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.text_gagal), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failed load data, check internet connection", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public void onSuccessOnPhotoChangeListener(String photo) {
        drawerFragment.setImage(photo);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onSuccessLoadMenu() {
        voucherFragment.expiredFragment.getdata();
    }

}
