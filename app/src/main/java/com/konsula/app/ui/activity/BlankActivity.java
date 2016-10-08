package com.konsula.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.konsula.app.MainActivity;
import com.konsula.app.R;

import javax.annotation.Nullable;

/**
 * Created by Konsula on 19/04/2016.
 */
public class BlankActivity extends AppCompatActivity {
private TextView textViewtoolbar;
    private ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        textViewtoolbar =(TextView) findViewById(R.id.toolbar_main_name_text_view);
        backButton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
        textViewtoolbar.setText(String.valueOf(getIntent().getExtras().getString("title")));
    }
}
