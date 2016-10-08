package com.konsula.app.ui.activity.direktori;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.DoctorAppointmentModel;

import javax.annotation.Nullable;

/**
 * Created by konsula on 12/14/2015.
 */
public class AppointmentDoneActivity extends AppCompatActivity {

    private ImageButton backButton;
    public static final String APPOINTMENT_CODE = "appointmentCode";
    private EditText appointmentCodeEditText;
    private String appointmentCode;

    private ImageView avatarImageView;
    private TextView doctorNameTextView;
    private TextView practiceNameTextView;
    private TextView practiceAddressTextView;
    private TextView timeScheduleTextView;
    private TextView dateScheduleTextView;
    private TextView appointment_btn_map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_done);

        Intent intent = getIntent();

        appointmentCode = intent.getStringExtra(APPOINTMENT_CODE);
        backButton = (ImageButton) findViewById(R.id.appointment_done_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        avatarImageView = (ImageView)findViewById(R.id.appointment_avatar_image_view);
        doctorNameTextView =(TextView)findViewById(R.id.appointment_doctor_name_text_view);
        practiceNameTextView = (TextView)findViewById(R.id.appointment_practice_name_text_view);
        practiceAddressTextView = (TextView)findViewById(R.id.appointment_practice_address_text_view);
        timeScheduleTextView =(TextView)findViewById(R.id.appointment_time_text_view);
        dateScheduleTextView = (TextView)findViewById(R.id.appointment_date_text_view);

        appointmentCodeEditText = (EditText)findViewById(R.id.appointment_done_code_edit_text);
        appointmentCodeEditText.setText(appointmentCode);

        final DoctorAppointmentModel.Results rs = ((AppController) getApplication()).getSessionManager().getDoctorAppointment();
        doctorNameTextView.setText(rs.doctor.name);
        practiceNameTextView.setText(rs.practice.name);
        practiceAddressTextView.setText(rs.practice.address);
        timeScheduleTextView.setText(rs.schedule.hour_start);
        dateScheduleTextView.setText(rs.schedule.date);
        ((AppController)getApplication()).displayImage(this,rs.doctor.photos.primary.medium_image, avatarImageView);

        appointment_btn_map = (TextView) findViewById(R.id.appointment_btn_map);
        SpannableString content = new SpannableString("" + appointment_btn_map.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        appointment_btn_map.setText(content);
        appointment_btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("" + rs.practice.map_url));
                startActivity(i);
            }
        });


    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close(){
        Intent intent = new Intent(AppointmentDoneActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
