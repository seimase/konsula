package com.konsula.app.ui.activity.teledokter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.PromoCodeModel;
import com.konsula.app.service.model.SummaryMembershipModel;
import com.konsula.app.service.model.SummaryTeledocModel;
import com.konsula.app.service.model.TeledoctorModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.direktori.PrivacyPolicyActivity;
import com.konsula.app.ui.activity.direktori.TermAndConditionActivity;
import com.konsula.app.util.RefreshTokenCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Konsula on 21/04/2016.
 */
public class ConfirmTeledocActivity extends AppCompatActivity {
    private TextView textViewname;
    private TextView textViewdate;
    private TextView textViewkeluhan;
    private TextView textViewharga;
    private TextView textViewtotalHarga;
    private TextView textViewfee;
    private TextView textViewPromocodeInfo;
    private ImageButton backButton;
    private ProgressDialog dialog;
    private AppController appController;
    int MAX_IMAGE_SIZE = 400 * 1024;
    private String etReason, etPhone, ettime, uuid;
    private int etHarga, doctorId, practiceId;
    String fullPath;
    String currentLanguage;
    String name;
    private CheckBox cb1;
    private TextView btnsyaratdanketentuan, btnkebijakanprivasi, textViewTos1, textViewtos2, textViewtos3;
    private LinearLayout layoutloading;
    private Button refresh;
    private RelativeLayout svContent;
    private ImageView btn_promocode_cancel;
    private TextView text_total_promocode;
    private LinearLayout viewPromocode;
    private LinearLayout viewPromocodeCancel;
    private RelativeLayout view_promocode;
    private String promo_code = "";
    private TextView view_totalpromo;
    private EditText editTextPromocode;
    private TextView btnPromoCode;
    private Animation bounce;
    private Integer price;
    private Integer confenece_fee;
    private Integer total_price;
    private String currency;
    private String signature_key;
    private FrameLayout btnreq;
    private LinearLayout Layoutpromocode;
    Bitmap bmpPic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teledoc_invoice);


        textViewname = (TextView) findViewById(R.id.tvNamaDokter);
        textViewdate = (TextView) findViewById(R.id.tvDateDokter);
        textViewkeluhan = (TextView) findViewById(R.id.tvkeluhan);
        textViewharga = (TextView) findViewById(R.id.tvharga);
        textViewfee = (TextView) findViewById(R.id.tvhargaDokter);
        Layoutpromocode = (LinearLayout) findViewById(R.id.layoutpromocode);
        backButton = (ImageButton) findViewById(R.id.backButton);
        textViewtotalHarga = (TextView) findViewById(R.id.tvhargatotal);
        textViewPromocodeInfo =(TextView) findViewById(R.id.teledoc_promocode_info);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cb1 = (CheckBox) findViewById(R.id.cb1);
        btnsyaratdanketentuan = (TextView) findViewById(R.id.text_syaratketentuan);
        btnkebijakanprivasi = (TextView) findViewById(R.id.text_kebijakanprivasi);
        textViewTos1 = (TextView) findViewById(R.id.text_tos1);
        textViewtos2 = (TextView) findViewById(R.id.text_tos2);
        textViewtos3 = (TextView) findViewById(R.id.text_tos3);
        textViewtos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    cb1.setChecked(false);
                } else {
                    cb1.setChecked(true);
                }
            }
        });
        textViewTos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    cb1.setChecked(false);
                } else {
                    cb1.setChecked(true);
                }
            }
        });
        textViewtos3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked()) {
                    cb1.setChecked(false);
                } else {
                    cb1.setChecked(true);
                }
            }
        });
        btnsyaratdanketentuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmTeledocActivity.this, TermAndConditionActivity.class);
                startActivity(intent);
            }
        });
        btnkebijakanprivasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmTeledocActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        appController = new AppController();
        try {
            fullPath = getIntent().getExtras().getString("image");
            uuid = getIntent().getExtras().getString("uuid");
            etHarga = getIntent().getExtras().getInt("harga", 0);
            doctorId = getIntent().getExtras().getInt("doctorId", 0);
            practiceId = getIntent().getExtras().getInt("practiceId", 0);
            ettime = getIntent().getExtras().getString("waktu");
            etReason = getIntent().getExtras().getString("keluhan");
            etPhone = getIntent().getExtras().getString("phone");
            name = (getIntent().getExtras().getString("nama"));
            currency = (getIntent().getExtras().getString("currency"));

        } catch (Exception e) {
            Log.d("ddd", e.toString());
        }

        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        svContent = (RelativeLayout) findViewById(R.id.svContent);
        refresh = (Button) findViewById(R.id.refresh);
        btn_promocode_cancel = (ImageView) findViewById(R.id.btn_promocode_cancel);
        text_total_promocode = (TextView) findViewById(R.id.text_total_promocode);
        viewPromocode = (LinearLayout) findViewById(R.id.layout_promocode);
        viewPromocodeCancel = (LinearLayout) findViewById(R.id.layout_promocode_cancel);
        view_promocode = (RelativeLayout) findViewById(R.id.view_promocode);
        view_totalpromo = (TextView) findViewById(R.id.view_totalpromo);
        editTextPromocode = (EditText) findViewById(R.id.text_promocode);
        btnPromoCode = (TextView) findViewById(R.id.btn_promo_code);
        btnreq = (FrameLayout) findViewById(R.id.btn_request);
        bounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_anim);
        btn_promocode_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docancel();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        btnPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.checkConnection(getBaseContext())) {
                    ((AppController)getApplication()).setMixpanel("Use Promo Code");
                    docountpromocode();
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestcall(v);
            }
        });
        getData();
    }

    private void docancel() {
        promo_code = "";
        editTextPromocode.setText("");
        textViewharga.setText(((AppController) getApplication()).getDefaultPriceFormat(currency, (double) total_price));
        textViewfee.setText(((AppController) getApplication()).getDefaultPriceFormat("IDR", (double) confenece_fee));
        view_promocode.setVisibility(View.INVISIBLE);
        textViewtotalHarga.setText(((AppController) getApplication()).getDefaultPriceFormat(currency, (double) total_price));
        viewPromocodeCancel.setVisibility(View.GONE);
        btnPromoCode.setVisibility(View.VISIBLE);
        viewPromocode.setVisibility(View.VISIBLE);
        viewPromocode.startAnimation(bounce);
    }


    private void docountpromocode() {
        final ProgressDialog dialogupload = AppController.createProgressDialog(ConfirmTeledocActivity.this);
        dialogupload.setCancelable(false);
        dialogupload.show();
        final String prmocode = editTextPromocode.getText().toString();
        NetworkManager.getNetworkService(getApplication())
                .getPromocode(((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, prmocode, "teledoc", price, new Callback<PromoCodeModel>() {
                    @Override
                    public void success(PromoCodeModel promoCodeModel, Response response) {
                        dialogupload.dismiss();
                        if (promoCodeModel.results.data != null) {
                            JSONObject props = new JSONObject();

                            try{
                                props.put("SOURCE", prmocode);
                                ((AppController)getApplication()).setMixpanel("Promo Code Type", props);
                            }catch (Exception e){

                            }
                            viewPromocode.setVisibility(View.GONE);
                            viewPromocodeCancel.setVisibility(View.VISIBLE);
                            viewPromocodeCancel.startAnimation(bounce);
                            promo_code = promoCodeModel.results.data.promo_code;
                            btnPromoCode.setVisibility(View.INVISIBLE);
                            view_promocode.setVisibility(View.VISIBLE);
                            Double pricepromo = 0.0;
                            Double totalpromo = 0.0;
                            text_total_promocode.setText(promoCodeModel.results.data.promo_code);
                            if (promoCodeModel.results.data.promo_type.equalsIgnoreCase("amount")) {
                                textViewPromocodeInfo.setText(getResources().getString(R.string.payment_promocode_info).replace("{promo_code}",promoCodeModel.results.data.promo_code.toUpperCase()).replace("{price}",AppController.getInstance().getDefaultPriceFormat(promoCodeModel.results.data.currency,promoCodeModel.results.data.nominal)));
                                view_totalpromo.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) promoCodeModel.results.data.nominal));
                                totalpromo = promoCodeModel.results.data.nominal;
                            } else if (promoCodeModel.results.data.promo_type.equalsIgnoreCase("percentage")) {
                                textViewPromocodeInfo.setText(getResources().getString(R.string.payment_promocode_info).replace("{promo_code}",promoCodeModel.results.data.promo_code.toUpperCase()).replace("{price}",Math.round(promoCodeModel.results.data.nominal)+" %"));
                                totalpromo = getPercentage(price, promoCodeModel.results.data.nominal);
                                view_totalpromo.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) totalpromo));
                            }
                            pricepromo = total_price - totalpromo;
                            pricepromo = pricepromo < 0 ? 0 : pricepromo;
                            if (pricepromo == 0)
                                textViewfee.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) 0));
                            textViewtotalHarga.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) pricepromo));



//                            text_total_promocode.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) promoCodeModel.results.data.nominal));
//                            viewPromocode.setVisibility(View.GONE);
//                            viewPromocodeCancel.setVisibility(View.VISIBLE);
//                            viewPromocodeCancel.startAnimation(bounce);
//                            promo_code = promoCodeModel.results.data.promo_code;
//                            btnPromoCode.setVisibility(View.INVISIBLE);
//                            view_promocode.setVisibility(View.VISIBLE);
//                            view_totalpromo.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) promoCodeModel.results.data.nominal));
//                            Float pricepromo = total_price - promoCodeModel.results.data.nominal;
//                            pricepromo = pricepromo < 0 ? 0 : pricepromo;
//                            if (pricepromo == 0)
//                                pricepromo = pricepromo < 0 ? 0 : pricepromo;
//                            if (pricepromo == 0)
//                                textViewfee.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) 0));
//                            textViewharga.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) total_price));
//                            textViewtotalHarga.setText(((AppController) getApplication()).getDefaultPriceFormat(promoCodeModel.results.data.currency, (double) pricepromo));
                        } else {
                            editTextPromocode.setText("");
                            ((AppController) getApplication()).doDialog(ConfirmTeledocActivity.this, promoCodeModel.messages.response_text);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialogupload.dismiss();
                        //Toast.makeText(ConfirmTeledocActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getData() {
        NetworkManager.getNetworkService(getApplication())
                .getSummaryTeledoc(((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, doctorId, practiceId, ettime, currency, etHarga, new Callback<SummaryTeledocModel>() {
                    @Override
                    public void success(SummaryTeledocModel summaryTeledocModel, Response response) {
                        if (summaryTeledocModel.results != null) {
                            layoutloading.setVisibility(View.GONE);
                            svContent.setVisibility(View.VISIBLE);
                            refresh.setVisibility(View.INVISIBLE);

                            textViewname.setText(summaryTeledocModel.results.doctor_name);
                            textViewdate.setText(((AppController) AppController.getAppContext()).setDatefull(summaryTeledocModel.results.schedule));
                            textViewkeluhan.setText(etReason);
                            textViewharga.setText(((AppController) getApplication()).getDefaultPriceFormat(summaryTeledocModel.results.currency, (double) summaryTeledocModel.results.price));
                            /*if (summaryTeledocModel.results.convenience_fee == 0){

                            }else{
                                textViewfee.setText(((AppController) getApplication()).getDefaultPriceFormat("- " + summaryTeledocModel.results.currency, (double) summaryTeledocModel.results.convenience_fee * -1));
                            }*/
                            textViewfee.setText(((AppController) getApplication()).getDefaultPriceFormat("- " + summaryTeledocModel.results.currency, (double) Math.abs(summaryTeledocModel.results.convenience_fee)));

                            textViewtotalHarga.setText(((AppController) getApplication()).getDefaultPriceFormat(summaryTeledocModel.results.currency, (double) summaryTeledocModel.results.total_price));
                            total_price = summaryTeledocModel.results.total_price;
                            price = summaryTeledocModel.results.price;
                            signature_key = summaryTeledocModel.results.signature_key;
                            confenece_fee = summaryTeledocModel.results.convenience_fee;
                            currency = summaryTeledocModel.results.currency;
                            if (price == 0) Layoutpromocode.setVisibility(View.GONE);


                        } else {
                            layoutloading.setVisibility(View.GONE);
                            svContent.setVisibility(View.VISIBLE);
                            refresh.setVisibility(View.INVISIBLE);
                            btnreq.setClickable(false);
                            //Toast.makeText(ConfirmTeledocActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        layoutloading.setVisibility(View.GONE);
                        refresh.setVisibility(View.VISIBLE);
                        //Toast.makeText(ConfirmTeledocActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void closeDetail(View view) {
        finish();
    }

    public void requestcall(View view) {
        if (AppController.checkConnection(getBaseContext())) {
            if (cb1.isChecked()) {
                doDialogrequest();
            } else {
                AppController.getInstance().doDialog(ConfirmTeledocActivity.this, getResources().getString(R.string.text_peringatan_tos));
            }
        } else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

    }


    public void request() {
        btnreq.setClickable(false);
        dialog = ProgressDialog.show(ConfirmTeledocActivity.this, getResources().getString(R.string.dialog_title_please_wait), getResources().getString(R.string.dialog_title_req), true);
        dialog.show();
        System.gc();
        String[] parts = fullPath.split(", ");
        Map<String, TypedFile> files = new HashMap<String, TypedFile>();
        if (fullPath.equals("")) {
            files = null;
        } else {
            for (int i = 0; i < parts.length; i++) {
                BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
                bmpOptions.inSampleSize = 5;
                String s = parts[i];
                bmpPic = BitmapFactory.decodeFile(s, bmpOptions);
                if (bmpPic != null) {
                    if ((bmpPic.getWidth() >= 1200) && (bmpPic.getHeight() >= 1200)) {
                        while ((bmpPic.getWidth() >= 1200) && (bmpPic.getHeight() >= 1200)) {
                            bmpOptions.inSampleSize++;
                            bmpPic = BitmapFactory.decodeFile(parts[i], bmpOptions);
                        }
                        int compressQuality = 104;
                        int streamLength = bmpPic.getByteCount();
                        while (streamLength >= MAX_IMAGE_SIZE) {
                            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
                            compressQuality -= 5;
                            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
                            byte[] bmpPicByteArray = bmpStream.toByteArray();
                            streamLength = bmpPicByteArray.length;
                            Log.e("Hasil Reducing", "Size: " + streamLength);
                        }

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                .format(new Date());
                        String temp = Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                                .getAbsolutePath()
                                + File.separator + "IMG_" + timeStamp + s.substring(s.lastIndexOf("."));
                        File pictureFile = new File(temp);

                        try {
                            FileOutputStream fos = new FileOutputStream(
                                    pictureFile);
                            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, fos);
                            fos.close();
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        Log.e("Isi temp", "" + temp);
                        files.put("attachments[" + i + "]", new TypedFile(s.substring(s.lastIndexOf(".")+1), new File(temp)));

                    }
                    files.put("attachments[" + i + "]", new TypedFile(s.substring(s.lastIndexOf(".")+1), new File(parts[i])));
                } else {
                    Log.e("File cukup", "Skip resizing");
                    files.put("attachments[" + i + "]", new TypedFile(s.substring(s.lastIndexOf(".")+1), new File("" + parts[i])));
                }
            }
        }

        try {
            NetworkManager.getNetworkService(getApplicationContext()).doRequestCall(((AppController) getApplication())
                    .getSessionManager().getToken(), currentLanguage, doctorId, practiceId, etPhone, ettime, currency, etHarga, etReason, promo_code, files, confenece_fee.toString(), signature_key, new Callback<TeledoctorModel>() {
                @Override
                public void success(TeledoctorModel teledoctorModel, Response response) {
                    boolean isTokenValid = ((AppController) getApplication()).isTokenValid(teledoctorModel.messages, response, ConfirmTeledocActivity.this, new RefreshTokenCallback() {
                        @Override
                        public void onRefreshTokenComplete() {
                            request();
                        }


                    });
                    btnreq.setClickable(true);
                    if (isTokenValid) {
                        dialog.dismiss();
                        if (teledoctorModel.results.data != null) {

                            ((AppController) getApplication()).getSessionManager().setTeledoc(teledoctorModel.results);
                            Intent intent = new Intent(ConfirmTeledocActivity.this, TeledokterDoneActivity.class);
                            intent.putExtra("nama", teledoctorModel.results.data.detail.doctor_name);
                            intent.putExtra("spesialisasi", teledoctorModel.results.data.detail.doctor_specialization);
                            intent.putExtra("waktu", teledoctorModel.results.data.detail.schedule);
                            intent.putExtra("phone", teledoctorModel.results.data.detail.contact);
                            intent.putExtra("status", teledoctorModel.results.data.detail.tele_status);
                            intent.putExtra("image", teledoctorModel.results.data.detail.doctor_photo.primary.large_image);

                            Bundle params = new Bundle();
                            params.putString("doctor_name",teledoctorModel.results.data.detail.doctor_name);
                            params.putString("doctor_category",teledoctorModel.results.data.detail.doctor_specialization);
                            params.putString("doctor_id", String.valueOf(teledoctorModel.results.data.detail.doctor_id));
                            params.putString("type","teledoc");
                            params.putString("Value", String.valueOf(teledoctorModel.results.data.total_price));
                            //params.putString("Currency",teledoctorModel.results.data.);
                            ((AppController) getApplication()).setFacebookEvent("RequestForCall", params);
                            startActivity(intent);
                        } else {
                            appController.doDialog(ConfirmTeledocActivity.this, teledoctorModel.messages.response_text);
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    btnreq.setClickable(true);
                    dialog.dismiss();
                    //appController.doDialog(ConfirmTeledocActivity.this, getResources().getString(R.string.no_connection_error_message));
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("CATCH", "CATCH");
            dialog.dismiss();
        }
    }

    private void doDialogrequest() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    request();
					((AppController)getApplication()).setMixpanel("Confirm Call Request");
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ConfirmTeledocActivity.this);
        builder.setMessage(getResources().getString(R.string.teledoc_warning_stay)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener).show();
    }
    public static Double getPercentage(int total_price, Double promo) {
        Double proportion = Math.floor(total_price * (promo / 100));
        return proportion;
    }
}
