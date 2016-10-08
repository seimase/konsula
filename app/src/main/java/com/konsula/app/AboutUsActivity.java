package com.konsula.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import javax.annotation.Nullable;

/**
 * Created by konsula on 12/14/2015.
 */
public class AboutUsActivity extends AppCompatActivity {

    private ImageButton backButton;
    TextView textAboutusHeader, textAboutus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        textAboutusHeader = (TextView) findViewById(R.id.text_about_us_header);
        textAboutus = (TextView) findViewById(R.id.text_about_us);

       backButton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
