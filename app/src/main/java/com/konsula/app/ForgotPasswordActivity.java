package com.konsula.app;

/**
 * Created by konsula on 2/25/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.service.model.ForgotPasswordModel;
import com.konsula.app.service.model.Messages;
import com.konsula.app.service.net.NetworkManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ImageButton backButton;
    private LinearLayout loginKembali;
    private Button btnSend;
    private EditText etEmail;
    private TextView textLoginBack;
    private AppController appController;
    private ProgressDialog dialog;
    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        appController = new AppController();

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        etEmail = (EditText) findViewById(R.id.etEmail);
        backButton = (ImageButton) findViewById(R.id.backButton);
        textLoginBack = (TextView) findViewById(R.id.textLoginBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loginKembali = (LinearLayout) findViewById(R.id.forget_password_back_login_text_view);
        loginKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        SpannableString content = new SpannableString("" + textLoginBack.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textLoginBack.setText(content);

        btnSend = (Button) findViewById(R.id.registration_action_button);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etEmail.getText().toString().equals("")) {
                    hideKeyboard(ForgotPasswordActivity.this);
                    appController.doDialog(ForgotPasswordActivity.this, getResources().getString(R.string.email_null));
                } else if (validasiemail(etEmail.getText().toString())) {
                    doforgot(etEmail.getText().toString());
                    hideKeyboard(ForgotPasswordActivity.this);
                } else {
                    hideKeyboard(ForgotPasswordActivity.this);
                    appController.doDialog(ForgotPasswordActivity.this, getResources().getString(R.string.email_false));
                }
            }
        });
    }

    public void doforgot(String email) {
        dialog = ProgressDialog.show(ForgotPasswordActivity.this, getResources().getString(R.string.dialog_title_please_wait), getResources().getString(R.string.dialog_title_send_data), true);
        dialog.show();
        NetworkManager.getNetworkService(getApplicationContext()).forgotpassword(currentLanguage,email,
                new Callback<ForgotPasswordModel>() {

                    @Override
                    public void success(ForgotPasswordModel forgotPasswordModel, Response response) {
                        dialog.dismiss();
                        if (response.getStatus() == 200) {
                            Messages rs = forgotPasswordModel.messages;
                            appController.doDialog(ForgotPasswordActivity.this, rs.response_text);
                        } else if (response.getStatus() == 400) {
                            Messages rs = forgotPasswordModel.messages;
                            appController.doDialog(ForgotPasswordActivity.this, rs.response_text);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        appController.doDialog(ForgotPasswordActivity.this, getResources().getString(R.string.no_connection_error_message));
                    }
                });

    }

    public boolean validasiemail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
