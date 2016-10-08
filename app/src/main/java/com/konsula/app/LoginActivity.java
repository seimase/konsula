package com.konsula.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.LoginSosmedModel;
import com.konsula.app.service.net.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 2/25/2016.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private ImageButton backButton;
    private Button btnLogin;
    private TextView btnFacebook, btnLoginGoogle;
    private LinearLayout tvForgotPassword;
    private EditText emailTextView;
    private EditText passwordTextView;
    boolean mVisible = false;
    private LoginButton loginButton;
    private CallbackManager mFacebookCallbackManager;
    private SignInButton btnGoogle;
    private LinearLayout layoutloading;
    private ScrollView l_view;
    // Vars
    private GoogleApiClient mGoogleApiClient;
    private String currentLanguage;
    private AppController appController;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        printKeyHash(this);
        initialButton();
        appController = new AppController();
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";

        ((AppController)getApplication()).setMixpanel("Open Login Page");
    }

    public void initialButton() {
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (ScrollView) findViewById(R.id.l_view);
        btnGoogle = (SignInButton) findViewById(R.id.google_sign_in_button);
        btnFacebook = (TextView) findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutloading.setVisibility(View.VISIBLE);
                l_view.setVisibility(View.INVISIBLE);
                loginButton.performClick();
            }
        });
        btnLoginGoogle = (TextView) findViewById(R.id.btn_login_google);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layoutloading.setVisibility(View.VISIBLE);
                l_view.setVisibility(View.INVISIBLE);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        loginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        backButton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        emailTextView = (EditText) findViewById(R.id.nav_login_email_edit_text);
        passwordTextView = (EditText) findViewById(R.id.nav_login_password_edit_text);
        tvForgotPassword = (LinearLayout) findViewById(R.id.login_forget_password_text_view);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

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
//        imgshowview = (ImageView) findViewById(R.id.iv_visible_pass);
//        imgshowview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShowVisiblePass();
//            }
//        });

        btnLogin = (Button) findViewById(R.id.nav_login_button);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(passwordTextView.getWindowToken(), 0);
                if (emailTextView.getText().toString().equals("") || passwordTextView.getText().toString().equals("")) {
                    //Toast.makeText(getApplicationContext(), getResources().getText(R.string.dialog_register_input_all), Toast.LENGTH_SHORT).show();
                    appController.doDialog(LoginActivity.this, getResources().getString(R.string.dialog_register_input_all));
                } else {
                    doLogin(emailTextView.getText().toString(), passwordTextView.getText().toString());
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
                        String accessToken = loginResult.getAccessToken().getToken();
                        Log.i("accessToken", accessToken);

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
                                            String gender;
                                            if (gen.equals("female")) {
                                                gender = "F";
                                            } else {
                                                gender = "M";
                                            }
                                            doCheckSosmedAkun(false, first_name + " " + last_name, email, "facebook", md5("facebook" + "-" + email + "-konsula"));
                                            LoginManager.getInstance().logOut();
                                            // dologinsosmed(email, "", first_name + " " + last_name, PhoneNumber, "", gender, "facebook");

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

                        JSONObject props = new JSONObject();
                        try {
                            props.put("SOURCE", "facebook");
                            ((AppController) getApplication()).setMixpanel("Login Source", props);
                            ((AppController) getApplication()).setMixpanel("Login Successfully", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {
                        layoutloading.setVisibility(View.INVISIBLE);
                        l_view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(FacebookException e) {
                        layoutloading.setVisibility(View.INVISIBLE);
                        l_view.setVisibility(View.VISIBLE);
                        Log.i("err", String.valueOf(e));

                    }
                }
        );
    }

    private void doLogin(String email, String password) {
        final ProgressDialog dialog = ProgressDialog.show(this, "Please wait", "Login to server", true);
        dialog.show();

        NetworkManager.getNetworkService(this).getToken(currentLanguage,email, password, new Callback<AuthModel>() {
            @Override
            public void success(AuthModel authModel, Response response) {
                Log.e("Mengambil list menu", "Mengambil list menu");
                if (authModel.results != null) {

                    AuthModel.Results userData = authModel.results;

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

                    props = new JSONObject();
                    try {
                        props.put("SOURCE", "email");
                        ((AppController) getApplication()).setMixpanel("Login Source", props);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    finish();
                    ((AppController) getApplication()).getSessionManager().setUserAccount(authModel.results);
                    ((AppController) getApplication()).getSessionManager().setToken(authModel.results.token);
                    ((AppController) getApplication()).getSessionManager().setRefreshToken(authModel.results.refresh_token);


                    ((AppController) getApplication()).setMixpanel("Login Successfully", props);


                    //((AppController) getApplication()).getSessionManager().setLanguage(authModel.language);
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(i, 0);
                } else {
                    DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                    builder2.setMessage(getResources().getString(R.string.login_wrong_password)).setPositiveButton(getResources().getString(R.string.general_text_dismiss), dialogClickListener2).show();
                }
                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();

                DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                builder2.setMessage("Failed to login. Please check your internet connection.").setPositiveButton(getResources().getString(R.string.general_text_dismiss), dialogClickListener2).show();
            }
        });
    }

    private void ShowVisiblePass() {
        if (mVisible == false) {
            mVisible = true;
            passwordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        } else {
            mVisible = false;
            passwordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
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

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            layoutloading.setVisibility(View.INVISIBLE);
            l_view.setVisibility(View.VISIBLE);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

            JSONObject props = new JSONObject();
            try {
                props.put("SOURCE", "google");
                ((AppController) getApplication()).setMixpanel("Login Source", props);
                ((AppController) getApplication()).setMixpanel("Login Successfully", props);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // G+
//            if (mGoogleApiClient.hasConnectedApi(Plus.API)) {
//                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//                if (person != null) {
//                    if (person.getGender() == 0) {
//                        gender = "M";
//                    } else {
//                        gender = "F";
//                    }
//                } else {
//                    Log.e(TAG, "Error!");
//                }
//            } else {
//                Log.e(TAG, "Google+ not connected");
//            }
        } else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private Date datetostring(String date) {
        Date dat = null;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyy");
        try {
            dat = format.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dat;

    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            doCheckSosmedAkun(true, acct.getDisplayName(), acct.getEmail(), "googleplus", md5("googleplus" + "-" + acct.getEmail() + "-konsula"));
            signOut();
            //dologinsosmed(acct.getEmail(), "", acct.getDisplayName(), PhoneNumber, "", gender, "googleplus");
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

    private void doCheckSosmedAkun(final Boolean islogingoogle, final String Name, final String email, String source, String signature_key) {
        NetworkManager.getNetworkService(getApplication()).loginUsersosmed(currentLanguage,source, email, source, signature_key, new Callback<LoginSosmedModel>() {
            @Override
            public void success(LoginSosmedModel loginSosmedModel, Response response) {
                if (loginSosmedModel.results.data != null) {
                    layoutloading.setVisibility(View.INVISIBLE);
                    l_view.setVisibility(View.VISIBLE);
                    ((AppController) getApplication()).getSessionManager().setLoginUserSosmed(loginSosmedModel.results);
                    ((AppController) getApplication()).getSessionManager().setToken(loginSosmedModel.results.data.token);
                    ((AppController) getApplication()).getSessionManager().setLanguage(loginSosmedModel.language);
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(i, 0);

                } else {
                    //Toast.makeText(getApplicationContext(), loginSosmedModel.messages.response_text, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                    i.putExtra("fromlogin", true);
                    i.putExtra("islogingoogle", islogingoogle);
                    i.putExtra("name", Name);
                    i.putExtra("email", email);
                    startActivity(i);

                }
            }

            @Override
            public void failure(RetrofitError error) {
                // dialog.dismiss();
            }
        });

    }


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
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