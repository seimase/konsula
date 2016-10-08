package com.konsula.app.ui.activity.teledokter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.ui.activity.payment.PaymentSelectionMembershipActivity;
import com.konsula.app.ui.custom.CustomtextView;

/**
 * Created by konsula on 2/25/2016.
 */

public class TeledokterFailActivity extends AppCompatActivity {
    private ImageButton backButton;
    private RelativeLayout btnRenew;
    private CustomtextView etName, etDate, etSpeciality, etReason;
    String payment_uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teledoc_fail);

        etName = (CustomtextView) findViewById(R.id.etName);
        etDate = (CustomtextView) findViewById(R.id.etDate);
        etSpeciality = (CustomtextView) findViewById(R.id.etSpeciality);
        etReason = (CustomtextView) findViewById(R.id.etReasonFail);
        try{
            payment_uuid = getIntent().getExtras().getString("payment_uuid");
            etName.setText(getIntent().getExtras().getString("name"));
            etDate.setText(getIntent().getExtras().getString("date"));
            String sSpsilas = getIntent().getExtras().getString("spesialisasi");

            //Toast.makeText(getBaseContext(), sSpsilas, Toast.LENGTH_LONG).show();
            etSpeciality.setText(getIntent().getExtras().getString("spesialisasi"));
            etReason.setText("" + getIntent().getExtras().getString("reason"));
        }catch(Exception e){

        }



        btnRenew = (RelativeLayout) findViewById(R.id.btnRenew);
        btnRenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppController.checkConnection(getBaseContext())){
                    Intent intent = new Intent(TeledokterFailActivity.this, PaymentSelectionMembershipActivity.class);
                    intent.putExtra(PaymentSelectionMembershipActivity.TAG_PAYMENT_UUID, payment_uuid);
                    intent.putExtra(PaymentSelectionMembershipActivity.TAG_SUBCATEGORY, "teledoctor");
                    startActivity(intent);
                }else{
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }

            }
        });

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}