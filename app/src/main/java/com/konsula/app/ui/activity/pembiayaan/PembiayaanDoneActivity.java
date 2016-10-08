package com.konsula.app.ui.activity.pembiayaan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.ui.custom.CustomtextView;

/**
 * Created by konsula on 3/3/2016.
 */
public class PembiayaanDoneActivity extends AppCompatActivity {
    private ImageButton btnClose;
    private CustomtextView tvName, tvPhone, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembiayaan_done);

        tvName = (CustomtextView) findViewById(R.id.tvName);
        tvPhone = (CustomtextView) findViewById(R.id.tvPhone);
        tvEmail = (CustomtextView) findViewById(R.id.tvEmail);

        setDataUser();

        btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PembiayaanDoneActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setDataUser() {
        Intent data = getIntent();

        tvName.setText(data.getStringExtra("name"));
        tvEmail.setText(data.getStringExtra("email"));
        tvPhone.setText(data.getStringExtra("phone"));
    }
}