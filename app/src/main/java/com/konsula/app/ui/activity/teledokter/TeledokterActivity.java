package com.konsula.app.ui.activity.teledokter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.ui.activity.estore.EstoreTransactionActivity;
import com.konsula.app.ui.activity.transaction.MytransactionHistoryActivity;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by konsula on 2/25/2016.
 */

public class TeledokterActivity extends AppCompatActivity {
    private static final int RP_ACCESS_LOCATION = 1;
    private static final int RP_CAMERA = 2;
    private Integer rate;
    private LinearLayout btnRequest;
    int doctorId, practiceId;
    private AlertDialog dialogpopup;
    private EditText etTime, etPicture, etPhone, etReason, etSpecialisasi;
    private TextView textViewcontentTeledoc;
    final private int REQUEST_CAMERA = 0001, SELECT_FILE = 0002, SELECT_TIME = 0007;
    private ImageButton backButton;
    private String memberId, uuid, currency;
    private ProgressDialog dialog;
    private AppController appController;
    public static final String BOOK_TIME = "book_time";
    public static final String PHONE = "phone";
    private RelativeLayout l_view;
    private ArrayList<String> mSelectPath;
    StringBuilder textEdittext = new StringBuilder();
    StringBuilder fullPath = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teledoc);
        appController = new AppController();

        ((AppController) getApplication()).setMixpanel("Open Teledoc Page");
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        //Log.e("test", "" + TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS));

        AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
        memberId = "" + userData.member_id;
        uuid = userData.uuid;
        l_view = (RelativeLayout) findViewById(R.id.l_view);
        textViewcontentTeledoc = (TextView) findViewById(R.id.text_content_teledoc);
        etSpecialisasi = (EditText) findViewById(R.id.etSpeciality);
        etReason = (EditText) findViewById(R.id.etReason);
        etTime = (EditText) findViewById(R.id.etTime);
        etPicture = (EditText) findViewById(R.id.etPicture);
        etSpecialisasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppController.checkConnection(getBaseContext())) {
                    etTime.setText("");
                    Intent i = new Intent(TeledokterActivity.this, SpesialisasiActivity.class);
                    startActivityForResult(i, 5);
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }


            }
        });

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String textKet = getString(R.string.teledokter_content).replace("{phone}", "<b>" + userData.phone_number + "." + "</b>");
        textViewcontentTeledoc.setText(Html.fromHtml(textKet));
        etPhone = (EditText) findViewById(R.id.etPhone);
        if (etPhone.getText().toString().length() == 0) {
            etPhone.setText("+62");
            etPhone.setSelection(3);

        }
        etPhone.addTextChangedListener(new TextWatcher() {
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
                String phone = etPhone.getText().toString();
                if (phone.length() < 3) {
                    etPhone.setText("+62");
                    etPhone.setSelection(3);
                }

                if (s.length() == 4 && phone.charAt(3) == '0') {
                    etPhone.setText("+62");
                    etPhone.setSelection(etPhone.getText().length());
                }
            }
        });

        try {
            etPhone.setText(userData.phone_number);
        } catch (Exception e) {
        }

        etPhone.addTextChangedListener(new TextWatcher() {
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
                String phone = etPhone.getText().toString();
                if (phone.length() < 3) {
                    etPhone.setText("+62");
                    etPhone.setSelection(3);

                }

            }

        });
        etPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupGallery();
            }
        });

        btnRequest = (LinearLayout) findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donext();
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etSpecialisasi.getText().toString().equals("")) {
                    Toast.makeText(TeledokterActivity.this, getResources().getString(R.string.title_date_time), Toast.LENGTH_LONG).show();
                } else {
                    if (AppController.checkConnection(getBaseContext())) {
                        Intent i = new Intent(TeledokterActivity.this, AppointmentTeledocActivity.class);
                        i.putExtra("doctorId", doctorId);
                        i.putExtra("practiceId", practiceId);
                        startActivityForResult(i,
                                SELECT_TIME);
                    } else {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void takephoto() {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment
                .getExternalStorageDirectory(), "konsulaPhoto.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(f));
        startActivityForResult(intent,
                REQUEST_CAMERA);
    }

    private void popupGallery() {
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, getThemeColor());
        ListView list = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.view_simple_list_item);

        adapter.add(getResources().getString(R.string.estore_take_photo));
        adapter.add(getResources().getString(R.string.estore_choose_existing));
        adapter.add(getResources().getString(R.string.title_cancel));
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    accessCamera();
                    dialogpopup.dismiss();
                } else if (position == 1) {
                    accessgallery();
                    dialogpopup.dismiss();
                } else if (position == 2) {
                    dialogpopup.dismiss();
                }
            }
        });

        builder.setView(list);
        builder.setTitle(R.string.teledoc_title_action);
        dialogpopup = builder.create();
        dialogpopup.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // TODO Auto-generated method stub
                Dialog d = ((Dialog) dialog);

                int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                View divider = d.findViewById(dividerId);
                if (divider != null) {
                    divider.setBackgroundColor(getThemeColor());
                }
            }
        });
        dialogpopup.show();
    }

    private void takepic() {
        MultiImageSelector selector = MultiImageSelector.create(TeledokterActivity.this);
        selector.count(2);
        selector.origin(mSelectPath);
        selector.start(TeledokterActivity.this, SELECT_FILE);
//        Intent i = new Intent(AppConstant.ACTION_MULTIPLE_PICK);
//        startActivityForResult(i, SELECT_FILE);
    }

    public int getThemeColor() {
        TypedValue value = new TypedValue();
        TeledokterActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("konsulaPhoto.jpg")) {
                        f = temp;
                        break;
                    }
                }

                if (!f.exists()) {
                    Toast.makeText(getBaseContext(),
                            "Error while capturing image", Toast.LENGTH_LONG)
                            .show();
                    return;
                } else {
                    File file = AppController.getInstance().saveBitmapToFile(f);
                    etPicture.setText(file.getName());
                    fullPath.append(file.getPath());
                }

            } catch (OutOfMemoryError E) {
            }
//            if (resultCode == RESULT_OK) {
//                File file = AppController.getInstance().saveBitmapToFile(new File(Environment.getExternalStorageDirectory()
//                        .toString()));
//                etPicture.setText(file.getName());
//                fullPath.append(file.getPath());
//
//            }
        } else if (requestCode == SELECT_FILE) {
            if (resultCode == RESULT_OK) {
                System.gc();
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                fullPath = new StringBuilder();
                for (String p : mSelectPath) {
                    fullPath.append(p + ", ");
                }
                try {
                    ArrayList<String> all_path = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                    for (String string : all_path) {
                        textEdittext.append(string.substring(string.lastIndexOf("/") + 1) + ", ");
                    }
                } catch (Exception e) {

                }
                etPicture.setText(textEdittext.toString());
                etPicture.setSelection(etPicture.getText().length());
                if (etPicture.getText().toString().length() > 1) {
                    appController.doDialog(TeledokterActivity.this, getResources().getString(R.string.saved));
                }
                System.gc();

            }
            /*try {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                        && (null == data.getData())) {
                    // the data is more than 1 images
                    // jika user pilih action photo
                    ClipData clipdata = data.getClipData();
                    fullPath = new StringBuilder();
                    textEdittext = new StringBuilder();

                    for (int i = 0; i < clipdata.getItemCount(); i++) {
                        try {
                            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), clipdata.getItemAt(i).getUri());
                            //DO something
                            ClipData.Item item = clipdata.getItemAt(i);
                            Uri uri = item.getUri();
                            String path = getRealPathFromURI(TeledokterActivity.this, uri);
                            textEdittext.append(path.substring(path.lastIndexOf("/") + 1) + ", ");
                            fullPath.append(path + ", ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    etPicture.setText(textEdittext.toString());
                    etPicture.setSelection(etPicture.getText().length());
                } else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                        && (null != data.getData())) {
                    // jika user pilih action gallery
                    fullPath = new StringBuilder();
                    textEdittext = new StringBuilder();

                    Uri uri = data.getData();
                    Log.e("URI single", "" + uri);
                    String path = getRealPathFromURI(TeledokterActivity.this, uri);
                    File check = new File("" + path);
                    Log.e("File length", "" + check.length());

                    textEdittext.append(path.substring(path.lastIndexOf("/") + 1) + ", ");
                    fullPath.append(path + ", ");
                    etPicture.setText(textEdittext.toString());
                    etPicture.setSelection(etPicture.getText().length());
                } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    fullPath = new StringBuilder();
                    textEdittext = new StringBuilder();

                    Uri uri = data.getData();
                    Log.e("URI single", "" + uri);
                    String path = getRealPathFromURI(TeledokterActivity.this, uri);
                    textEdittext.append(path.substring(path.lastIndexOf("/") + 1) + ", ");
                    fullPath.append(path + ", ");

                    etPicture.setText(textEdittext.toString());
                    etPicture.setSelection(etPicture.getText().length());
                }
            } catch (Exception e) {

            }*/
//            fullPath = new StringBuilder();
//            textEdittext = new StringBuilder();
//            try {
//                String[] all_path = data.getStringArrayExtra("all_path");
//                for (String string : all_path) {
//                    Log.e("Isi satuan", "" + string);
//                    textEdittext.append(string.substring(string.lastIndexOf("/") + 1) + ", ");
//                    fullPath.append(string + ", ");
//                }
//            } catch (Exception e) {
//
//            }
//
//            etPicture.setText(textEdittext.toString());
//            etPicture.setSelection(etPicture.getText().length());
//            Log.e("Fullpath n Edittext", "" + fullPath.toString() + " " + textEdittext.toString());
//
//            if (etPicture.getText().toString().length() > 1) {
//                appController.doDialog(TeledokterActivity.this, getResources().getString(R.string.saved));
//            }


        } else if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                etSpecialisasi.setText(data.getStringExtra("name"));
                doctorId = Integer.parseInt(data.getStringExtra("doctor_id"));
                practiceId = Integer.parseInt(data.getStringExtra("practice_id"));
                if (data.getStringExtra("rate").equals("N/A") || data.getStringExtra("rate").equals("") || data.getStringExtra("rate") == null || data.getStringExtra("rate").toString().equals("null")) {
                    rate = 0;
                } else {
                    rate = Integer.parseInt(data.getStringExtra("rate"));
                }
                currency = data.getStringExtra("currency");
            }
        } else if (requestCode == SELECT_TIME) {
            if (resultCode == RESULT_OK) {
                etTime.setText(data.getStringExtra("date"));
            }
        }
    }


    private void accessgallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2900);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2900);
            }
        } else {
            takepic();
        }

    }

    private void accessCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            }
        } else {
            takephoto();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @Nonnull String[] permissions,
                                           @Nonnull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2909:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takephoto();
                }
                break;
            case 2900:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takepic();
                }
                break;
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


    private void donext() {
        if (etPhone.getText().toString().length() < 10 || etPhone.getText().toString().length() > 15) {
            appController.doDialog(TeledokterActivity.this, getResources().getString(R.string.text_must_phone));
        } else if (!etSpecialisasi.getText().toString().equals("") && !etTime.getText().toString().equals("") && !etReason.getText().toString().equals("")) {
            if (AppController.checkConnection(getBaseContext())) {
                btnRequest.setClickable(false);
                ((AppController)getApplication()).setMixpanel("Request a Call");
                Intent intent = new Intent(TeledokterActivity.this, ConfirmTeledocActivity.class);
                intent.putExtra("nama", etSpecialisasi.getText().toString());
                intent.putExtra("waktu", etTime.getText().toString());
                intent.putExtra("keluhan", etReason.getText().toString());
                intent.putExtra("harga", rate);
                intent.putExtra("phone", etPhone.getText().toString());
                intent.putExtra("uuid", uuid);
                intent.putExtra("practiceId", practiceId);
                intent.putExtra("doctorId", doctorId);
                intent.putExtra("image", fullPath.toString());
                intent.putExtra("currency", currency);
                // intent.putExtra("image", teledoctorModel.results.data.doctor_photo.primary.large_image);
                startActivity(intent);
                btnRequest.setClickable(true);
            } else {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }


            // startActivity(i);
            // request();
        } else {
            appController.doDialog(TeledokterActivity.this, getResources().getString(R.string.text_warning_empty));

        }
    }
}