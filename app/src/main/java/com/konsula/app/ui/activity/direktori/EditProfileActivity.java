package com.konsula.app.ui.activity.direktori;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.PhotoModel;
import com.konsula.app.service.model.UpdateAccountModel;
import com.konsula.app.service.model.ViewAccountModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.adapter.spinnerAdapter;
import com.konsula.app.ui.custom.CustomtextView;
import com.konsula.app.util.RefreshTokenCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Konsula on 10/03/2016.
 */
public class EditProfileActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView toolbar_name;
    private Button saveButton, btnUpload;
    private TextView txtNameedit, txtEmailedit, txtTgledit;
    private EditText txtNamaedit, txtTtledit, txtDaerahedit, txtTinggibdnedit, txtBeratbdnedit;
    private EditText txtNoteledit;
    private ImageView imageViewedit;
    private RadioButton maleRadioButton, femaleRadioButton;
    private RadioGroup rboReg;
    private String imagePath = null;
    private Bitmap imageUpload;
    private LinearLayout layoutloading;
    private LinearLayout l_view;
    private Button refresh;
    private AppController appController;
    private CustomtextView tvMale, tvFemale;
    Calendar birthdate = null;
    ProgressDialog dlg;
    private Spinner spinnerGolonganDarah;
    String currentLanguage;
    String[] spinnergolongandarahValue = {
            "-",
            "A",
            "B", "AB", "O"
    };
    private Boolean isOkayClicked = false;
    private CancelableCallback cancelableCallback;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                String date1 = mFormatter.format(calendar.getTime());
                txtTtledit.setText(date1);
                txtTtledit.setError(null);

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_edit);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        Locale locale = new Locale(currentLanguage);
        Locale.setDefault(locale);
        maleRadioButton = (RadioButton) findViewById(R.id.rboMaleA);
        femaleRadioButton = (RadioButton) findViewById(R.id.rboFemaleA);
        rboReg = (RadioGroup) findViewById(R.id.radioBtnReg);
        tvMale = (CustomtextView) findViewById(R.id.tvMale);
        tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rboReg.check(R.id.rboMaleA);
            }
        });
        tvFemale = (CustomtextView) findViewById(R.id.tvFemale);
        tvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rboReg.check(R.id.rboFemaleA);
            }
        });

        toolbar_name = (TextView) findViewById(R.id.text_toolbarname);
        saveButton = (Button) findViewById(R.id.profile_action_save_text_view);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        txtNameedit = (TextView) findViewById(R.id.txtNameedit);
        txtEmailedit = (TextView) findViewById(R.id.txtEmailedit);
        txtTgledit = (TextView) findViewById(R.id.txtTgledit);
        spinnerGolonganDarah = (Spinner) findViewById(R.id.spinnerGolonganDarah);
        txtNamaedit = (EditText) findViewById(R.id.etName);
        txtTtledit = (EditText) findViewById(R.id.etBirth);
        txtNoteledit = (EditText) findViewById(R.id.etPhone);
        txtDaerahedit = (EditText) findViewById(R.id.etAlamat);
        txtTinggibdnedit = (EditText) findViewById(R.id.etTinggiBadan);
        txtBeratbdnedit = (EditText) findViewById(R.id.etBeratBadan);
        imageViewedit = (ImageView) findViewById(R.id.profileimageedit);
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (LinearLayout) findViewById(R.id.l_view);
        refresh = (Button) findViewById(R.id.refresh);
        appController = new AppController();
        if (txtNoteledit.getText().toString().length() == 0) {
            txtNoteledit.setText("+62");
            txtNoteledit.setSelection(3);

        }
        txtNoteledit.addTextChangedListener(new TextWatcher() {
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
                String phone = txtNoteledit.getText().toString();
                if (phone.length() < 3) {
                    txtNoteledit.setText("+62");
                    txtNoteledit.setSelection(3);
                }

                if (s.length() == 4 && phone.charAt(3) == '0') {
                    txtNoteledit.setText("+62");
                    txtNoteledit.setSelection(txtNoteledit.getText().length());
                }
            }
        });
        getdata();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setVisibility(View.INVISIBLE);
                l_view.setVisibility(View.INVISIBLE);
                getdata();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePath != null) {
                    uploadImage(imagePath);
                } else {
                    Toast.makeText(getApplicationContext(), "please Select Image First", Toast.LENGTH_SHORT).show();
                }

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile();
            }
        });

        imageViewedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessgallery();
            }
        });


        txtTtledit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthdate.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(EditProfileActivity.this, R.style.ThemeTranslucentPickers, mDateSetListener, Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR), Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH), Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpd.setCancelable(false);
                dpd.show();
            }
        });

        spinnerAdapter adaptergol = new spinnerAdapter(getApplicationContext(), R.layout.item_spinner_golongandarah);
        adaptergol.addAll(spinnergolongandarahValue);
        adaptergol.setDropDownViewResource(R.layout.custom_spinner_item);
        spinnerGolonganDarah.setAdapter(adaptergol);


        backButton = (ImageButton) findViewById(R.id.edit_profile_back_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar_name.setText(getResources().getString(R.string.nav_editmyaccount));


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {

            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                imagePath = picturePath;
                Bitmap bitmap = AppController.decodeSampledBitmapFromUri(imagePath, 320, 480);
                AppController.saveBitmapToFile(imagePath, bitmap);
                imageUpload = BitmapFactory.decodeFile(picturePath);
                imageViewedit.setImageBitmap(imageUpload);


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, "Pilih Image Lain", Toast.LENGTH_SHORT).show();


            }
        }
    }

    private void uploadImage(final String image) {
        dlg = ProgressDialog.show(EditProfileActivity.this, "Please wait", "Uploading your Image", true);
        dlg.show();

        Log.e("Path", "" + image);

        File ImageFIle = new File(image);
        TypedFile typedFile = new TypedFile("jpg", ImageFIle);


        NetworkManager.getNetworkService(getApplicationContext()).uploadImage(((AppController) getApplication()).getSessionManager().getToken(), typedFile, new Callback<PhotoModel>() {
            @Override
            public void success(PhotoModel photoModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(photoModel.messages, response, EditProfileActivity.this, new RefreshTokenCallback() {

                    @Override
                    public void onRefreshTokenComplete() {
                        uploadImage(image);
                    }


                });
                if (isTokenValid) {
                    if (photoModel.messages.response_code == 200) {
                        ((AppController) getApplication()).getSessionManager().updateImageUser(photoModel.results.primary.medium_image);
                        dlg.dismiss();
                        DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent returnIntent = new Intent();
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        setResult(Activity.RESULT_OK);
                                        finish();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(EditProfileActivity.this);
                        builder2.setMessage(photoModel.messages.response_text).setPositiveButton("Dismiss", dialogClickListener2).show();


                    } else {
                        String err = photoModel.messages.response_text;
                        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                        dlg.dismiss();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dlg.dismiss();
                Toast.makeText(getApplicationContext(), String.valueOf(error), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateprofile() {
        final String fullname;
        String gender = null;
        final String birtdate;
        final String phone_number;
        final String address;
        final String height;
        final String weight;
        final String blood_type;
        fullname = txtNamaedit.getText().toString();

        phone_number = txtNoteledit.getText().toString();
        address = txtDaerahedit.getText().toString();
        height = txtTinggibdnedit.getText().toString();

        birtdate = txtTtledit.getText().toString();
        weight = txtBeratbdnedit.getText().toString();
        blood_type = spinnerGolonganDarah.getSelectedItem().toString();
       if (phone_number.length() < 10 || phone_number.length() > 15) {
            appController.doDialog(EditProfileActivity.this, getResources().getString(R.string.text_must_phone));
        } else if (spinnerGolonganDarah.getSelectedItemPosition() == 0) {
            appController.doDialog(EditProfileActivity.this, getResources().getString(R.string.text_warning_bloodtype));
        } else if (height.equals("")) {
            appController.doDialog(EditProfileActivity.this, getResources().getString(R.string.text_warning_height_weight));
        } else if (weight.equals("")) {
            appController.doDialog(EditProfileActivity.this, getResources().getString(R.string.text_warning_height_weight));
        } else {
            dlg = ProgressDialog.show(EditProfileActivity.this, "Please wait", "Saving your Update", true);
            dlg.show();
            NetworkManager.getNetworkService(getApplicationContext()).updateAccount(
                    ((AppController) getApplication())
                            .getSessionManager().getToken(), currentLanguage, fullname, maleRadioButton.isChecked() ? "M" : "F", birtdate, phone_number, address, height, weight, blood_type,
                    new Callback<UpdateAccountModel>() {

                        @Override
                        public void success(final UpdateAccountModel viewAccountModel, Response response) {
                            boolean isTokenValid = ((AppController) getApplication()).isTokenValid(viewAccountModel.messages, response, EditProfileActivity.this, new RefreshTokenCallback() {
                                @Override
                                public void onRefreshTokenComplete() {
                                    dlg.dismiss();
                                    updateprofile();
                                }


                            });
                            if (isTokenValid) {
                                dlg.dismiss();
                                if (viewAccountModel.messages.response_code == 200) {
                                    ((AppController) getApplication()).getSessionManager().updateUserAccount(fullname, maleRadioButton.isChecked() ? "M" : "F", birtdate, phone_number, Integer.parseInt(height), Integer.parseInt(weight), blood_type);
                                    DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    Intent returnIntent = new Intent();
                                                    returnIntent.putExtra("result", viewAccountModel.results.data.authentication);
                                                    setResult(Activity.RESULT_OK, returnIntent);
                                                    setResult(Activity.RESULT_OK);
                                                    finish();
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(EditProfileActivity.this);
                                    builder2.setMessage(viewAccountModel.messages.response_text).setPositiveButton("Dismiss", dialogClickListener2).show();


                                } else if (viewAccountModel.messages.response_code == 422) {
                                    finish();
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

                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(EditProfileActivity.this);
                                    builder2.setMessage(viewAccountModel.messages.response_text).setPositiveButton("Dismiss", dialogClickListener2).show();

                                    dlg.dismiss();
                                }
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder2 = new AlertDialog.Builder(EditProfileActivity.this);
                            builder2.setMessage("Failed to update profile. Please check your internet connection.").setPositiveButton("Dismiss", dialogClickListener2).show();

                            dlg.dismiss();
                        }
                    });
        }
    }

    private void getdata() {
        cancelableCallback = new CancelableCallback<ViewAccountModel>() {

            @Override
            protected void onSuccess(ViewAccountModel viewAccountModel, Response response) {
                final boolean isTokenValid = ((AppController) getApplication()).isTokenValid(viewAccountModel.messages, response, EditProfileActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getdata();
                    }


                });
                if (isTokenValid) {
                    ViewAccountModel.Results rs = viewAccountModel.results;
                    layoutloading.setVisibility(View.GONE);
                    l_view.setVisibility(View.VISIBLE);
                    txtNameedit.setText(rs.fullname);
                    txtEmailedit.setText(rs.email);
                    if (rs.join_time != null)
                        txtTgledit.setText(((AppController) getApplication()).dateJoin(rs.join_time, currentLanguage));
                    txtNamaedit.setText(rs.fullname);

                    if (rs.gender != null) {
                        if (rs.gender.equals("M")) maleRadioButton.setChecked(true);
                        else femaleRadioButton.setChecked(true);
                    }

                    try {
                        if (rs.blood_type.equals("A")) {
                            spinnerGolonganDarah.setSelection(1);
                        } else if (rs.blood_type.equals("B")) {
                            spinnerGolonganDarah.setSelection(2);
                        } else if (rs.blood_type.equals("AB")) {
                            spinnerGolonganDarah.setSelection(3);
                        } else if (rs.blood_type.equals("O")) {
                            spinnerGolonganDarah.setSelection(4);
                        } else {
                            spinnerGolonganDarah.setSelection(0);
                        }
                    } catch (Exception e) {

                    }
                    txtTtledit.setText(rs.birth_date);
                    txtNoteledit.setText(rs.phone_number);
                    txtDaerahedit.setText(rs.address);
                    txtTinggibdnedit.setText(rs.height == 0 ? "" : String.valueOf(rs.height));
                    txtBeratbdnedit.setText(rs.weight == 0 ? "" : String.valueOf(rs.weight));
                    ((AppController) getApplication()).displayImage(EditProfileActivity.this, rs.photo.primary.thumb_image, imageViewedit);
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                layoutloading.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            }
        };
        NetworkManager.getNetworkService(getApplicationContext()).getviewAccount(
                ((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, cancelableCallback);
    }

    private void accessgallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            }
        } else {
            getpic();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getpic();
                } else {
                    Snackbar.make(l_view, "Akses Dibutuhkan", Snackbar.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void getpic() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            cancelableCallback.cancel();
        } catch (Exception e) {

        }
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
