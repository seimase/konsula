package com.konsula.app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.RegisterModel;
import com.konsula.app.service.model.RegisterSosmedModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.direktori.PrivacyPolicyActivity;
import com.konsula.app.ui.activity.direktori.TermAndConditionActivity;
import com.konsula.app.ui.custom.CustomtextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 2/25/2016.
 */
public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private TextView btnsyaratdanketentuan, btnkebijakanprivasi, textViewTos1, textViewtos2, textViewtos3;
    private TextView btnFacebook, btnLoginGoogle;
    private static final String TAG = "GoogleSignIn";
    private ImageButton backButton;
    private RadioGroup rboReg;
    private EditText etName, etDate, etEmail, etPassword, etPhone;
    private Button btnReg, btnRegsosmed;
    private AppController appController;
    private CustomtextView tvMale, tvFemale;
    private ProgressDialog dialog;
    Calendar birthdate = null;
    private RadioButton maleRadioButton, femaleRadioButton;
    private CheckBox cb1;
    private LoginButton loginButton;
    private CallbackManager mFacebookCallbackManager;
    private String PhoneNumber;
    private SignInButton btnGoogle;
    private String gender;
    private GoogleApiClient mGoogleApiClient;
    private LinearLayout layoutloading;
    private ScrollView l_view;
    private Button refresh;
    private RelativeLayout layoutpassword;
    private LinearLayout layoutRegsosmed;
    private Boolean islogingoogle;
    private String profileImageUrl;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    String currentLanguage;

    private Boolean isOkayClicked = false;
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            String date1 = mFormatter.format(calendar.getTime());
            etDate.setText(date1);
            etDate.setError(null);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_register);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        Locale locale = new Locale(currentLanguage);
        Locale.setDefault(locale);
        initData();
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

                if (s.length() == 4 && phone.charAt(3) == '0') {
                    etPhone.setText("+62");
                    etPhone.setSelection(etPhone.getText().length());
                }
            }
        });

        birthdate = Calendar.getInstance();


        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthdate.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(RegisterActivity.this, R.style.ThemeTranslucentPickers, mDateSetListener, Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR), Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH), Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpd.setCancelable(false);
                dpd.show();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().equals("") ||
                        etDate.getText().toString().equals("") ||
                        etEmail.getText().toString().equals("") ||
                        etPassword.getText().toString().equals("") ||
                        etPhone.getText().toString().equals("")) {
                    appController.doDialog(RegisterActivity.this, getResources().getString(R.string.dialog_register_input_all));
                } else if (!maleRadioButton.isChecked() && !femaleRadioButton.isChecked()) {
                    appController.doDialog(RegisterActivity.this, getResources().getString(R.string.dialog_register_gender_all));
                } else if (etPhone.getText().toString().length() <10 || etPhone.getText().toString().length() > 15  ) {
                    appController.doDialog(RegisterActivity.this, getResources().getString(R.string.register_wrong_phone));
                } else {
                    if (cb1.isChecked()) {

                        JSONObject props = new JSONObject();
                        try {
                            props.put("SOURCE", "email");
                            ((AppController) getApplication()).setMixpanel("Register Source", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ((AppController) getApplication()).setFacebookEvent("CompleteRegistration");

                        doRegister(
                                etName.getText().toString(),
                                etDate.getText().toString(),
                                etEmail.getText().toString(),
                                etPassword.getText().toString(),
                                etPhone.getText().toString(),
                                maleRadioButton.isChecked() ? "M" : "F",
                                "register"
                        );
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_peringatan_tos), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        cb1 = (CheckBox) findViewById(R.id.cb1);
        rboReg = (RadioGroup) findViewById(R.id.radioBtnReg);
        tvMale = (CustomtextView) findViewById(R.id.tvMale);
        tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rboReg.check(R.id.rboMale);
            }
        });
        tvFemale = (CustomtextView) findViewById(R.id.tvFemale);
        tvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rboReg.check(R.id.rboFemale);
            }
        });
    }

    public void doRegister(String name, String date, final String email, final String password, String phone, String gender, String source) {
        dialog = ProgressDialog.show(RegisterActivity.this, getResources().getString(R.string.dialog_title_please_wait), getResources().getString(R.string.dialog_title_send_data), true);
        dialog.show();
        NetworkManager.getNetworkService(getApplication()).registerUser(currentLanguage, email, password, phone, date, gender, name, source, "Y", new Callback<RegisterModel>() {
            @Override
            public void success(RegisterModel registerModel, Response response) {
                dialog.dismiss();
                if (registerModel.messages.response_code == 200) {
                    doLogin(email, password);
                } else {
                    appController.doDialog(RegisterActivity.this, registerModel.messages.response_text);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
                try {
                    appController.doDialog(RegisterActivity.this, getResources().getString(R.string.something_wrong));
                } catch (Exception e) {
                    appController.doDialog(RegisterActivity.this, getResources().getString(R.string.something_wrong));
                }
            }
        });
    }


    private void doLogin(String email, String password) {
        dialog = ProgressDialog.show(RegisterActivity.this, getResources().getString(R.string.dialog_title_please_wait), getResources().getString(R.string.login_to_server), true);
        dialog.show();

        NetworkManager.getNetworkService(getApplication()).getToken(currentLanguage, email, password, new Callback<AuthModel>() {
            @Override
            public void success(AuthModel authModel, Response response) {
                dialog.dismiss();
                if (authModel.results != null) {
                    ((AppController) getApplication()).getSessionManager().setUserAccount(authModel.results);
                    ((AppController) getApplication()).getSessionManager().setToken(authModel.results.token);
                    ((AppController) getApplication()).getSessionManager().setRefreshToken(authModel.results.refresh_token);
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    appController.doDialog(RegisterActivity.this, authModel.messages.response_text);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
                String err = NetworkManager.getInstance().getErrorMessage(error);
                appController.doDialog(RegisterActivity.this, err);
            }
        });
    }


    public void initData() {
        appController = new AppController();
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (ScrollView) findViewById(R.id.l_view);
        layoutpassword = (RelativeLayout) findViewById(R.id.layoutpassword);
        layoutRegsosmed = (LinearLayout) findViewById(R.id.llRegContentContainer);
        refresh = (Button) findViewById(R.id.refresh);
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        etName = (EditText) findViewById(R.id.etName);
        etDate = (EditText) findViewById(R.id.etDate);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPhone = (EditText) findViewById(R.id.etPhone);
        maleRadioButton = (RadioButton) findViewById(R.id.rboMale);
        femaleRadioButton = (RadioButton) findViewById(R.id.rboFemale);
        btnReg = (Button) findViewById(R.id.registration_action_button);
        btnRegsosmed = (Button) findViewById(R.id.registration_button_sosmed);
        btnGoogle = (SignInButton) findViewById(R.id.google_sign_in_button);
        btnsyaratdanketentuan = (TextView) findViewById(R.id.text_syaratketentuan);
        btnkebijakanprivasi = (TextView) findViewById(R.id.text_kebijakanprivasi);
        textViewTos1 = (TextView) findViewById(R.id.text_tos1);
        textViewtos2 = (TextView) findViewById(R.id.text_tos2);
        textViewtos3 = (TextView) findViewById(R.id.text_tos3);
        textViewtos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    cb1.setChecked(false);
                } else {
                    cb1.setChecked(true);
                }
            }
        });
        textViewTos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    cb1.setChecked(false);
                } else {
                    cb1.setChecked(true);
                }
            }
        });
        textViewtos3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    cb1.setChecked(false);
                } else {
                    cb1.setChecked(true);
                }
            }
        });
        btnsyaratdanketentuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, TermAndConditionActivity.class);
                startActivity(intent);
            }
        });
        btnkebijakanprivasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
        try {
            if (getIntent().getExtras().getBoolean("fromlogin")) {
                islogingoogle = getIntent().getExtras().getBoolean("islogingoogle");
                etEmail.setText(getIntent().getExtras().getString("email"));
                etName.setText(getIntent().getExtras().getString("name"));
                layoutloading.setVisibility(View.INVISIBLE);
                l_view.setVisibility(View.VISIBLE);
                layoutpassword.setVisibility(View.GONE);
                btnReg.setVisibility(View.GONE);
                btnRegsosmed.setVisibility(View.VISIBLE);
                layoutRegsosmed.setVisibility(View.GONE);
            }

        } catch (Exception e) {

        }

        btnFacebook = (TextView) findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutloading.setVisibility(View.VISIBLE);
                l_view.setVisibility(View.INVISIBLE);
                loginButton.performClick();

                JSONObject props = new JSONObject();
                try {
                    props.put("SOURCE", "facebook");
                    ((AppController) getApplication()).setMixpanel("Register Source", props);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        btnLoginGoogle = (TextView) findViewById(R.id.btn_login_google);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject props = new JSONObject();
                try {
                    props.put("SOURCE", "google");
                    ((AppController) getApplication()).setMixpanel("Register Source", props);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                layoutloading.setVisibility(View.VISIBLE);
                l_view.setVisibility(View.INVISIBLE);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        loginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        backButton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        btnGoogle.setSize(SignInButton.SIZE_STANDARD);
        btnGoogle.setScopes(gso.getScopeArray());
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        btnRegsosmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().equals("") ||
                        etDate.getText().toString().equals("") ||
                        etEmail.getText().toString().equals("") ||
                        etPhone.getText().toString().equals("")) {
                    appController.doDialog(RegisterActivity.this, getResources().getString(R.string.dialog_register_input_all));
                } else {
                    if (cb1.isChecked()) {
                        ((AppController) getApplication()).setFacebookEvent("CompleteRegistration");
                        if (islogingoogle) {

                            dologinsosmed(
                                    etEmail.getText().toString(), "",
                                    etName.getText().toString(),
                                    etPhone.getText().toString(),
                                    etDate.getText().toString(),
                                    maleRadioButton.isChecked() ? "M" : "F",
                                    "googleplus", profileImageUrl);
                        } else {
                            dologinsosmed(
                                    etEmail.getText().toString(), "",
                                    etName.getText().toString(),
                                    etPhone.getText().toString(),
                                    etDate.getText().toString(),
                                    maleRadioButton.isChecked() ? "M" : "F",
                                    "facebook", profileImageUrl);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_peringatan_tos), Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        List<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_birthday");
        loginButton.setReadPermissions(permissions);
        loginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {


                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.i("LoginActivity", response.toString());
                                        // Get facebook data from login
                                        Bundle bFacebookData = getFacebookData(object);
                                        try {
                                            String first_name = object.getString("first_name");
                                            String last_name = object.getString("last_name");
                                            String email = object.getString("email");
                                            //   Date date1 = datetostring(object.getString("birthday"));
                                            //   Log.i("err",String.valueOf(date1));
                                            //   String birth_date = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                                            String gen = object.getString("gender");
                                            if (gen.equals("female")) {
                                                gender = "F";
                                                femaleRadioButton.isChecked();
                                            } else {
                                                gender = "M";
                                                maleRadioButton.isChecked();
                                            }
                                            islogingoogle = false;
                                            layoutloading.setVisibility(View.INVISIBLE);
                                            l_view.setVisibility(View.VISIBLE);
                                            layoutpassword.setVisibility(View.GONE);
                                            btnReg.setVisibility(View.GONE);
                                            btnRegsosmed.setVisibility(View.VISIBLE);
                                            layoutRegsosmed.setVisibility(View.GONE);
                                            etName.setText(first_name + " " + last_name);
                                            etEmail.setText(email);
                                            LoginManager.getInstance().logOut();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                }

                        );
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Par?metros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        layoutloading.setVisibility(View.INVISIBLE);
                        l_view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Log.i("err", String.valueOf(e));
                        layoutloading.setVisibility(View.INVISIBLE);
                        l_view.setVisibility(View.VISIBLE);
                    }
                }
        );
    }


    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                profileImageUrl = String.valueOf(profile_pic);
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            Log.i("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private void dologinsosmed(final String email, final String Password, final String name, final String phone_number, final String birth_date, final String gender, final String source, final String imagelink) {
        dialog = ProgressDialog.show(RegisterActivity.this, "Please wait", "Login User", true);
        dialog.show();
        NetworkManager.getNetworkService(getApplication()).registerUsersosmed(currentLanguage, email, Password, name, phone_number, birth_date, gender, source, "Y", imagelink, new Callback<RegisterSosmedModel>() {
            @Override
            public void success(RegisterSosmedModel registerSosmedModel, Response response) {

                if (registerSosmedModel.results.data != null) {
                    ((AppController) getApplication()).getSessionManager().setUserSosmed(registerSosmedModel.results);
                    ((AppController) getApplication()).getSessionManager().setToken(registerSosmedModel.results.data.token);
                    ((AppController) getApplication()).getSessionManager().setRefreshToken(registerSosmedModel.results.data.refresh_token);
                    ((AppController) getApplication()).getSessionManager().setLanguage(registerSosmedModel.language);
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(i, 0);

                } else {
                    finish();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                    Toast.makeText(getApplicationContext(), registerSosmedModel.messages.response_text, Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            // G+
            if (mGoogleApiClient.hasConnectedApi(Plus.API)) {
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                if (person != null) {
                    if (person.getGender() == 0) {
                        gender = "M";
                    } else {
                        gender = "F";
                    }
                } else {
                    Log.e(TAG, "Error!");
                }
            } else {
                layoutloading.setVisibility(View.INVISIBLE);
                l_view.setVisibility(View.VISIBLE);
                Log.e(TAG, "Google+ not connected");
            }
        } else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            islogingoogle = true;
            GoogleSignInAccount acct = result.getSignInAccount();
            layoutloading.setVisibility(View.INVISIBLE);
            l_view.setVisibility(View.VISIBLE);
            layoutpassword.setVisibility(View.GONE);
            btnReg.setVisibility(View.GONE);
            btnRegsosmed.setVisibility(View.VISIBLE);
            layoutRegsosmed.setVisibility(View.GONE);
            signOut();
            etName.setText(acct.getDisplayName());
            etEmail.setText(acct.getEmail());
            profileImageUrl = String.valueOf(acct.getPhotoUrl());
            //maleRadioButton.isChecked() ? "M" : "F"
            // dologinsosmed(acct.getEmail(), "", acct.getDisplayName(), PhoneNumber, "", gender, "googleplus");
        } else {
        }
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        layoutloading.setVisibility(View.INVISIBLE);
        l_view.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}
