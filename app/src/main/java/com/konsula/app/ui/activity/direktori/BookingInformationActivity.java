package com.konsula.app.ui.activity.direktori;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.AppointmentModel;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.CheckEmailModel;
import com.konsula.app.service.model.GeneralCheckModel;
import com.konsula.app.service.model.DoctorAppointmentModel;
import com.konsula.app.service.model.RegisterModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.util.RefreshTokenCallback;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/15/2015.
 */
public class BookingInformationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton btnBack;
    private Button btnBooking;
    private LinearLayout btnLogin;

    private ImageView avatarImageView;
    private TextView doctorNameTextView;
    private TextView doctorJobTextView;
    private TextView doctorScheduleTextView;
    private TextView practiceNameTextView;
    private TextView practiceAddressTextView;

    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText birthdateEditText;
    private EditText phoneEditText;
    Calendar birthdate = null;
    private ScrollView mainContainer;
    private ProgressBar progressBar;
    public static final String DOCTOR_ID = "doctorId";
    public static final String PRACTICE_ID = "practiceId";
    public static final String SCHEDULE_DATE = "scheduleDate";
    public static final String HOUR_START = "hour_start";
    public static final String HOUR_END = "hour_end";
    public static final String TIME_ZONE = "timeZone";
    private String currentLanguage;
    private static final int REQUEST_LOGIN = 1;
    private Boolean isExist = false;
    private String doctorId;
    private String practiceId;
    private String scheduleDate;
    private String hourStart;
    private String hourEnd;
    private String timeZone;
    private RadioGroup rboReg;
    private TextView tvMale, tvFemale;
    ProgressDialog dlg;
    LinearLayout footerContainer;
    int counter = 0;
    private Boolean isOkayClicked = false;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    Map<String, String> query = new HashMap<>();
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                String date1 = mFormatter.format(calendar.getTime());
                birthdateEditText.setText(date1);
                birthdateEditText.setError(null);

        }
    };
    private android.app.AlertDialog dialogcancel;
    private TextView btnsyaratdanketentuan, btnkebijakanprivasi, textViewTos1, textViewtos2, textViewtos3, textViewtos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_information);

        ((AppController)getApplication()).setMixpanel("Directory Book Information");

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        Locale locale = new Locale(currentLanguage);
        Locale.setDefault(locale);
        initComponents();
        initListeners();
        getBookingInfo();
    }

    private void initComponents() {
        Intent intent = getIntent();
        doctorId = intent.getStringExtra(DOCTOR_ID);
        practiceId = intent.getStringExtra(PRACTICE_ID);
        scheduleDate = intent.getStringExtra(SCHEDULE_DATE);
        hourStart = intent.getStringExtra(HOUR_START);
        hourEnd = intent.getStringExtra(HOUR_END);
        timeZone = intent.getStringExtra(TIME_ZONE);

        toolbar = (Toolbar) findViewById(R.id.booking_information_header_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainContainer = (ScrollView) findViewById(R.id.booking_information_main_container);
        progressBar = (ProgressBar) findViewById(R.id.booking_information_progress_bar);
        footerContainer = (LinearLayout) findViewById(R.id.booking_information_not_login_container);
        avatarImageView = (ImageView) findViewById(R.id.booking_information_avatar_image_view);
        doctorNameTextView = (TextView) findViewById(R.id.booking_information_doctor_name_text_view);
        doctorJobTextView = (TextView) findViewById(R.id.booking_information_doctor_job_text_view);
        doctorScheduleTextView = (TextView) findViewById(R.id.booking_information_doctor_schedule_text_view);
        practiceAddressTextView = (TextView) findViewById(R.id.booking_information_practice_address_text_view);
        practiceNameTextView = (TextView) findViewById(R.id.booking_information_practice_name_text_view);
        maleRadioButton = (RadioButton) findViewById(R.id.rboMaleA);
        femaleRadioButton = (RadioButton) findViewById(R.id.rboFemaleA);
        rboReg = (RadioGroup) findViewById(R.id.radioBtnReg);
        tvMale = (TextView) findViewById(R.id.tvMale);
        tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rboReg.check(R.id.rboMaleA);
            }
        });
        tvFemale = (TextView) findViewById(R.id.tvFemale);
        tvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rboReg.check(R.id.rboFemaleA);
            }
        });

        emailEditText = (EditText) findViewById(R.id.booking_information_email_edit_text);
        nameEditText = (EditText) findViewById(R.id.booking_information_name_edit_text);
        passwordEditText = (EditText) findViewById(R.id.booking_information_password_edit_text);
        birthdateEditText = (EditText) findViewById(R.id.booking_information_birthdate_edit_text);
        phoneEditText = (EditText) findViewById(R.id.booking_information_phone_edit_text);
        if (phoneEditText.getText().toString().length() == 0) {
            phoneEditText.setText("+62");
            phoneEditText.setSelection(3);

        }
        phoneEditText.addTextChangedListener(new TextWatcher() {
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
                String phone = phoneEditText.getText().toString();
                if (phone.length() < 3) {
                    phoneEditText.setText("+62");
                    phoneEditText.setSelection(3);
                }

                if (s.length() == 4 && phone.charAt(3) == '0') {
                    phoneEditText.setText("+62");
                    phoneEditText.setSelection(phoneEditText.getText().length());
                }
            }
        });

        btnLogin = (LinearLayout) findViewById(R.id.booking_information_btn_login);
        btnBack = (ImageButton) findViewById(R.id.action_close_booking_info_image_button);
        btnBooking = (Button) findViewById(R.id.booking_information_book_action_button);

//        maleRadioButton = (RadioButton) findViewById(R.id.booking_information_male);
//        femaleRadioButton = (RadioButton) findViewById(R.id.booking_information_female);

        mainContainer.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        btnBooking.setVisibility(View.INVISIBLE);


        query.put("doctor_id", doctorId);
        query.put("practice_id", practiceId);
        query.put("schedule_date", scheduleDate);
        query.put("hour_start", hourStart);
        query.put("hour_end", hourEnd);

        btnsyaratdanketentuan = (TextView) findViewById(R.id.text_syaratketentuan);
        btnkebijakanprivasi = (TextView) findViewById(R.id.text_kebijakanprivasi);
        textViewTos1 = (TextView) findViewById(R.id.text_tos1);
        textViewtos2 = (TextView) findViewById(R.id.text_tos2);
        textViewtos3 = (TextView) findViewById(R.id.text_tos3);
        textViewtos = (TextView) findViewById(R.id.text_tos);
        btnsyaratdanketentuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookingInformationActivity.this, TermAndConditionActivity.class);
                startActivity(intent);
            }
        });
        btnkebijakanprivasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookingInformationActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initListeners() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!((AppController) getApplication()).getSessionManager().isUserLogon())
                    if (isEmailValid(s)) {
                        checkEmailExist(s.toString());
                    } else emailEditText.setError("Wrong format");
            }
        });


        birthdateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthdate.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(BookingInformationActivity.this, R.style.ThemeTranslucentPickers, mDateSetListener, Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR), Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH), Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpd.show();
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // set listener
        // set listener
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 0;
                if (emailEditText.getText().toString().equals(""))
                    emailEditText.setError("Required");
                else {
                    counter += 1;
                    emailEditText.setError(null);
                }
                if (birthdateEditText.getText().toString().equals(""))
                    birthdateEditText.setError("Required");
                else {
                    counter += 1;
                    birthdateEditText.setError(null);
                }
                if (nameEditText.getText().toString().equals(""))
                    nameEditText.setError("Required");
                else {
                    counter += 1;
                    nameEditText.setError(null);
                }
                if (phoneEditText.getText().toString().length() < 10 || phoneEditText.getText().toString().length() > 15)
                    phoneEditText.setError("Required");
                else {
                    counter += 1;
                    phoneEditText.setError(null);
                }
                if (!((AppController) getApplication()).getSessionManager().isUserLogon()) {
                    if (passwordEditText.getText().toString().equals(""))
                        passwordEditText.setError("Required");
                    else {
                        counter += 1;
                        passwordEditText.setError(null);
                    }
                }

                if (((AppController) getApplication()).getSessionManager().isUserLogon()) {
                    if (counter == 4) {
                        showConfirmDialog();
                    }
                } else {
                    if (counter == 5) {
                        showConfirmDialog();
                    }
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookingInformationActivity.this, MainActivity.class);
                intent.putExtra("fromAppointment", 1);
                startActivityForResult(intent, REQUEST_LOGIN);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                getUserData();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void getBookingInfo() {
        NetworkManager.getNetworkService(getApplicationContext()).getDoctorAppointment(((AppController) getApplication()).getSessionManager().getToken(),currentLanguage, query, new Callback<DoctorAppointmentModel>() {
            @Override
            public void success(DoctorAppointmentModel doctorAppointmentModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(doctorAppointmentModel.messages, response, BookingInformationActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getBookingInfo();
                    }

                });
                if (isTokenValid) {
                    mainContainer.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    btnBooking.setVisibility(View.VISIBLE);
                    DoctorAppointmentModel.Results result = doctorAppointmentModel.results;
                    ((AppController) getApplication()).displayImage(BookingInformationActivity.this,result.doctor.photos.primary.medium_image, avatarImageView);
                    doctorNameTextView.setText(result.doctor.name);
                    if (result.doctor.specialities == null) {
                            doctorJobTextView.setText(result.doctor.job_category);
                    }else {
                        doctorJobTextView.setText(result.doctor.specialities != null && (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? result.doctor.specialities.get(0).specialization.specialization_english:result.doctor.specialities.get(0).specialization.specialization_bahasa);
                    }
                    doctorScheduleTextView.setText(result.schedule.date + " - " + result.schedule.hour_start);
                    practiceNameTextView.setText(result.practice.name);
                    practiceAddressTextView.setText(result.practice.address);
                    ((AppController) getApplication()).getSessionManager().setDoctorAppointment(result);
                    if (((AppController) getApplication()).getSessionManager().isUserLogon()) {
                        //set all the info
                        getUserData();
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(BookingInformationActivity.this, "Failed to get info", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void getUserData() {
        final AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
        emailEditText.setText(userData.email);
        emailEditText.setEnabled(false);
        birthdateEditText.setText(userData.birth_date);
        phoneEditText.setText(userData.phone_number);
        if (userData.gender != null) {
            if (userData.gender.equals("F")) femaleRadioButton.setChecked(true);
            else maleRadioButton.setChecked(true);
        }
        Selection.setSelection(phoneEditText.getText(), phoneEditText.getText().length());
        phoneEditText.addTextChangedListener(new TextWatcher() {
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
                String phone = phoneEditText.getText().toString();
                if (phone.length() == 0) {
                    phoneEditText.setText("+62");
                    phoneEditText.setSelection(3);

                }

            }

        });
        nameEditText.setText(userData.fullname);
        passwordEditText.setVisibility(View.GONE);
        footerContainer.setVisibility(View.GONE);
    }

    private void checkEmailExist(final String email) {
        NetworkManager.getNetworkService(getApplicationContext()).checkEmail(((AppController) getApplication()).getSessionManager().getToken(), email, new Callback<CheckEmailModel>() {
            @Override
            public void success(CheckEmailModel checkEmailModel, Response response) {
                isExist = checkEmailModel.results;
                if (isExist) emailEditText.setError("Email already exists!");
            }

            @Override
            public void failure(RetrofitError error) {
                isExist = false;
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void showConfirmDialog() {
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(BookingInformationActivity.this, (new AppController()).getThemeColor(BookingInformationActivity.this));
        View cancelView = LayoutInflater.from(BookingInformationActivity.this).inflate(R.layout.view_transaction_cancel, null);
        TextView btnCancel = (TextView) cancelView.findViewById(R.id.btn_cancel);
        TextView btnOk = (TextView) cancelView.findViewById(R.id.btn_ok);
        TextView message = (TextView) cancelView.findViewById(R.id.detail_item_name);
        message.setText(R.string.message_dialog_booking_confirmation);
        btnOk.setText(R.string.dialog_accept);
        btnCancel.setText(R.string.dialog_decline);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogcancel.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogcancel.dismiss();
                if (AppController.checkConnection(getBaseContext())) {
                    dlg = ProgressDialog.show(BookingInformationActivity.this, "Please wait", "Saving your appointment", true);
                    dlg.show();
                    if (((AppController) getApplication()).getSessionManager().isUserLogon())
                        doUpdateProfile();
                    else
                        doRegister();

                }else{
                    Toast.makeText(BookingInformationActivity.this, getResources().getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(cancelView);
        builder.setTitle(R.string.title_dialog_booking_confirmation);
        dialogcancel = builder.create();
        dialogcancel.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // TODO Auto-generated method stub
                Dialog d = ((Dialog) dialog);

                int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                View divider = d.findViewById(dividerId);
                if (divider != null) {
                    divider.setBackgroundColor((new AppController()).getThemeColor(BookingInformationActivity.this));
                }
            }
        });
        dialogcancel.show();

//        new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
//                .setTitle(getString(R.string.title_dialog_booking_confirmation))
//                .setMessage(getString(R.string.message_dialog_booking_confirmation))
//                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (AppController.checkConnection(getBaseContext())){
//                            dlg = ProgressDialog.show(BookingInformationActivity.this, "Please wait", "Saving your appointment", true);
//                            dlg.show();
//                            if (((AppController) getApplication()).getSessionManager().isUserLogon())
//                                doUpdateProfile();
//                            else
//                                doRegister();
//
//                        }else{
//                            Toast.makeText(BookingInformationActivity.this, getResources().getText(R.string.no_connection), Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                })
//                .setNegativeButton(R.string.dialog_decline, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                })
//                .show();
    }

    public void doRegister() {
        NetworkManager.getNetworkService(getApplicationContext()).doRegister(((AppController) getApplication()).getSessionManager().getToken(), nameEditText.getText().toString(), birthdateEditText.getText().toString(),
                phoneEditText.getText().toString(), emailEditText.getText().toString(),
                passwordEditText.getText().toString(), "express", maleRadioButton.isChecked() ? "M" : "F", new Callback<RegisterModel>() {
                    @Override
                    public void success(RegisterModel registerModel, Response response) {
                        if (registerModel.messages.response_code == 201) {
                            if (!((AppController) getApplication()).getSessionManager().isUserLogon()) {
                                doLogin(emailEditText.getText().toString(), passwordEditText.getText().toString());
                            } else {
                                doSubmitAppointment();
                            }
                        } else {
                            Toast.makeText(BookingInformationActivity.this, registerModel.messages.response_text, Toast.LENGTH_SHORT).show();
                            dlg.dismiss();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dlg.dismiss();

                        DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        finish();
                                        break;
                                }
                            }
                        };

                        android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(BookingInformationActivity.this);
                        builder2.setMessage("Failed to proceed. Please check your internet connection.").setPositiveButton("Dismiss", dialogClickListener2).show();
                    }
                });
    }

    private void doLogin(String email, String password) {

        NetworkManager.getNetworkService(getApplicationContext()).getToken(currentLanguage,email, password, new Callback<AuthModel>() {
            @Override
            public void success(AuthModel authModel, Response response) {
                if (authModel.results != null) {
                    ((AppController) getApplication()).getSessionManager().setUserAccount(authModel.results);
                    ((AppController) getApplication()).getSessionManager().setToken(authModel.results.token);
                    doSubmitAppointment();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                String err = NetworkManager.getInstance().getErrorMessage(error);
                Toast.makeText(BookingInformationActivity.this, err, Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
    }

    private void doUpdateProfile() {
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        NetworkManager.getNetworkService(getApplicationContext()).doUpdateProfile(
                ((AppController) getApplication()).getSessionManager().getToken(), currentLanguage,
                nameEditText.getText().toString(),
                birthdateEditText.getText().toString(),
                phoneEditText.getText().toString(),
                maleRadioButton.isChecked() ? "M" : "F",
                new Callback<GeneralCheckModel>() {
                    @Override
                    public void success(GeneralCheckModel generalCheckModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).isTokenValid(generalCheckModel.messages, response, BookingInformationActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                doUpdateProfile();
                            }


                        });
                        if (isTokenValid) {
                            if (generalCheckModel.results) {
                                AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
                                userData.fullname = nameEditText.getText().toString();
                                userData.birth_date = birthdateEditText.getText().toString();
                                userData.phone_number = phoneEditText.getText().toString();
                                userData.gender = maleRadioButton.isChecked() ? "M" : "F";
                                ((AppController) getApplication()).getSessionManager().setUserAccount(userData);
                                doSubmitAppointment();
                            } else {
                                String err = generalCheckModel.messages.response_text;
                                Toast.makeText(BookingInformationActivity.this, err, Toast.LENGTH_SHORT).show();
                                dlg.dismiss();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        String err = NetworkManager.getInstance().getErrorMessage(error);
                        Toast.makeText(BookingInformationActivity.this, err, Toast.LENGTH_SHORT).show();
                        dlg.dismiss();
                    }
                });
    }

    private void doSubmitAppointment() {
        final AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
        NetworkManager.getNetworkService(getApplicationContext()).saveAppointment(((AppController) getApplication()).getSessionManager().getToken(), userData.member_id + "",
                doctorId,
                practiceId,
                scheduleDate,
                hourStart,
                hourEnd,
                timeZone,
                userData.phone_number,
                "",
                new Callback<AppointmentModel>() {
                    @Override
                    public void success(AppointmentModel appointmentModel, Response response) {
                        if (appointmentModel.messages.response_code == 200) {
                            if (appointmentModel.results.appointment_code != null) {
                                Intent intent = new Intent(BookingInformationActivity.this, AppointmentDoneActivity.class);
                                intent.putExtra(AppointmentDoneActivity.APPOINTMENT_CODE, appointmentModel.results.appointment_code);
                                startActivity(intent);
                            }
                        } else if (appointmentModel.messages.response_code == 201) {
                            if (appointmentModel.results.unique_id != null) {
                                Intent intent = new Intent(BookingInformationActivity.this, BookConfirmationActivity.class);
                                intent.putExtra(BookConfirmationActivity.UNIQUE_ID, appointmentModel.results.unique_id);
                                intent.putExtra(BookConfirmationActivity.PHONE, userData.phone_number);
                                startActivity(intent);
                            }

                        }else if (appointmentModel.results==null){
                            ((AppController) getApplication()).doDialog(BookingInformationActivity.this, appointmentModel.messages.response_text);
                        }

                        dlg.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        String err = NetworkManager.getInstance().getErrorMessage(error);
                        Toast.makeText(BookingInformationActivity.this, err, Toast.LENGTH_SHORT).show();
                        dlg.dismiss();
                    }
                });
    }
}
