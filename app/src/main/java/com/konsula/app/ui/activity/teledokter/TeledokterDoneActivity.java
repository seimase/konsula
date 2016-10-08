package com.konsula.app.ui.activity.teledokter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.TeledoctorModel;
import com.konsula.app.ui.custom.CircleTransform;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by konsula on 3/3/2016.
 */
public class TeledokterDoneActivity extends AppCompatActivity {
    private ImageButton btnClose;
    TeledoctorModel.Results mResource;
    private TextView name, date, phone, etSpeciality, status, tvReservation, tvNote;
    private String image;
    private ImageView ivDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teledoc_done);
        name = (TextView) findViewById(R.id.appointment_doctor_name_text_view);
        date = (TextView) findViewById(R.id.appointment_date_text_view);
        phone = (TextView) findViewById(R.id.appointment_time_text_view);
        etSpeciality = (TextView) findViewById(R.id.etSpeciality);
        tvReservation = (TextView) findViewById(R.id.tvReservation);
        ivDoctor = (ImageView) findViewById(R.id.ivDoctor);
        status = (TextView) findViewById(R.id.etStatus);
        tvNote = (TextView) findViewById(R.id.tvNote);
        setdatauser();
        btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backhome();
            }
        });

        try{
            name.setText(getIntent().getExtras().getString("nama"));
            etSpeciality.setText(getIntent().getExtras().getString("spesialisasi"));
            date.setText(getIntent().getExtras().getString("waktu") +" WIB");
            phone.setText(getIntent().getExtras().getString("phone"));
            String statustext = getIntent().getExtras().getString("status");
            status.setText(statustext);
            status.setTextColor(statustext.equals("CONFIRMED")?getResources().getColor(R.color.green_xxl):getResources().getColor(R.color.black));
            image = getIntent().getExtras().getString("image");
            Picasso.with(this).load(image).transform(new CircleTransform()).into(ivDoctor);
            if (statustext.trim().equals("CONFIRMED") || statustext.trim().equals("ON REQUEST")){
                tvReservation.setText(getResources().getText(R.string.text_reservation_reservation));
            }else{
                tvReservation.setText(getResources().getText(R.string.teledoc_text_reservation));
            }

            if (statustext.trim().equals("ON REQUEST")){
                ((AppController)getApplication()).setMixpanel("Teledoc On Request");
                tvNote.setText(getResources().getText(R.string.hint_confirm_teledoc));
            }else{
                ((AppController)getApplication()).setMixpanel("Teledoc Confirmed");
                tvNote.setText(getResources().getText(R.string.hint_confirm_teledoc1));
            }

        }catch (Exception e){
            Log.d("dddd",e.toString());
        }
    }

    private void setdatauser() {
        try {
            final AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
            mResource =((AppController)getApplication()).getSessionManager().getTeledoc();
            name.setText(userData.fullname);
            date.setText(date(mResource.data.detail.schedule)+"WIB");
            phone.setText(mResource.data.detail.contact);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error Get Data User", Toast.LENGTH_SHORT);
        }
    }


    public static String date(String date) {
        Date datejoin = null;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat dts = new SimpleDateFormat("d MMM yyyy, hh:mm");
        try {
            datejoin = dt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(dts.format(datejoin));
    }


    @Override
    public void onBackPressed() {
        backhome();
    }

    private void backhome(){
        Intent intent = new Intent(TeledokterDoneActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
