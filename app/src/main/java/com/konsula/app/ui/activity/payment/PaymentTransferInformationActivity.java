package com.konsula.app.ui.activity.payment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.BillingTransactionModel;
import com.konsula.app.service.model.DetailMembershipTransactionModel;
import com.konsula.app.service.model.MembershipVerifyingModel;
import com.konsula.app.service.model.PaymentProfModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.transaction.MytransactionHistoryActivity;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.ui.view.EstorePaymentStepView;
import com.konsula.app.util.RefreshTokenCallback;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Konsula on 25/04/2016.
 */
public class PaymentTransferInformationActivity extends AppCompatActivity {
    private AlertDialog dialog;
    final private int REQUEST_CAMERA = 2;
    BillingTransactionModel.Results datapayment;
    TextView textViewBillingId;
    TextView textViewTotalPayment;
    TextView textViewBankName;
    TextView textViewBankHolderName;
    TextView textViewBankBranch;
    TextView textViewBankNumber;
    TextView textViewCountDown;
    TextView btnUploadProf;
    private long totalCountDown;
    private ProgressDialog dialogupload;
    private FrameLayout btnDopayment;
    private String currentLanguage;
    private EstorePaymentStepView paymentStepView;
    private LinearLayout llCountDown;
    private ImageView imgArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datapayment = ((AppController) getApplication()).getSessionManager().getDetailMembershipTransfer();
        if (datapayment.subcategory.equalsIgnoreCase("estore")) {
            setTheme(R.style.MaterialTheme_Estore);
        } else if (datapayment.subcategory.equalsIgnoreCase("teledoctor")) {
            setTheme(R.style.MaterialTheme_teledokter);
        } else {
            setTheme(R.style.MaterialTheme_Main);
        }
        setContentView(R.layout.activity_payment_transferinformation);
        init();
    }

    private void init() {

        imgArrow = (ImageView) findViewById(R.id.imgArrow);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        totalCountDown = getIntent().getLongExtra(PaymentBankTransferSelectionActivity.TAG_COUNT_DOWN, 0);
        paymentStepView = (EstorePaymentStepView) findViewById(R.id.payment_step_view);
        paymentStepView.setCurrentStep(2);
        paymentStepView.setVisibility(datapayment.subcategory.equalsIgnoreCase("estore") ? View.VISIBLE : View.GONE);
        textViewBillingId = (TextView) findViewById(R.id.item_billing_id);
        textViewTotalPayment = (TextView) findViewById(R.id.item_total_payment);
        textViewBankName = (TextView) findViewById(R.id.item_bank_name);
        textViewBankHolderName = (TextView) findViewById(R.id.item_bank_holder_name);
        textViewBankBranch = (TextView) findViewById(R.id.item_bank_branch);
        textViewBankNumber = (TextView) findViewById(R.id.item_bank_number);
        textViewCountDown = (TextView) findViewById(R.id.text_count_down);
        textViewBillingId.setText(String.valueOf(datapayment.invoice_number));
        textViewTotalPayment.setText(((AppController) getApplication()).getDefaultPriceFormat(datapayment.currency, Double.parseDouble(datapayment.total_payment)));
        textViewBankName.setText(datapayment.bank_transfer_info.bank_name);
        textViewBankHolderName.setText(datapayment.bank_transfer_info.account_holder_name);
        textViewBankBranch.setText(datapayment.bank_transfer_info.branch_name);
        textViewBankNumber.setText(datapayment.bank_transfer_info.account_number);
        btnUploadProf = (TextView) findViewById(R.id.btn_upload_prof);
        if (datapayment.subcategory.equalsIgnoreCase("estore")) {
            btnUploadProf.setBackground(getResources().getDrawable(R.drawable.shape_rounded_violet));
            btnUploadProf.setPadding((int) getResources().getDimension(R.dimen.space_16), (int) getResources().getDimension(R.dimen.space_8), (int) getResources().getDimension(R.dimen.space_16), (int) getResources().getDimension(R.dimen.space_8));
        } else if (datapayment.subcategory.equalsIgnoreCase("teledoctor")) {
            btnUploadProf.setBackground(getResources().getDrawable(R.drawable.shape_rounded_green));
            btnUploadProf.setPadding((int) getResources().getDimension(R.dimen.space_16), (int) getResources().getDimension(R.dimen.space_8), (int) getResources().getDimension(R.dimen.space_16), (int) getResources().getDimension(R.dimen.space_8));

        } else {
            btnUploadProf.setBackground(getResources().getDrawable(R.drawable.shape_rounded_softblue));
            btnUploadProf.setPadding((int) getResources().getDimension(R.dimen.space_16), (int) getResources().getDimension(R.dimen.space_8), (int) getResources().getDimension(R.dimen.space_16), (int) getResources().getDimension(R.dimen.space_8));

        }

        btnDopayment = (FrameLayout) findViewById(R.id.btn_doPayment);
        btnUploadProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppController) getApplication()).setMixpanel("Upload Payment Prove");
                uploadPOP();
            }
        });
        btnDopayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPayment();
            }
        });

        if (totalCountDown != 0) {

            new CountDownTimer(totalCountDown, 1000) {

                public void onTick(long millisUntilFinished) {
                    textViewCountDown.setText(((AppController) getApplication()).formatTime(millisUntilFinished, ":", ":", "", true));
                    totalCountDown = millisUntilFinished;
                }

                public void onFinish() {
                    doDialogexpired();
                }

            }.start();
        }

        if (datapayment.subcategory.equalsIgnoreCase("teledoctor")) {
            //llCountDown.setVisibility(View.GONE);
            imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_green));
        } else if (datapayment.subcategory.equalsIgnoreCase("estore")) {
            imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_chat));
        } else {
            imgArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_pembiayaan));
        }

        llCountDown = (LinearLayout) findViewById(R.id.llCountDown);
        if (datapayment.subcategory.equalsIgnoreCase("teledoctor"))
            llCountDown.setVisibility(View.GONE);
    }

    private void doPayment() {
        doverifying();
    }

    public int getThemeColor() {
        TypedValue value = new TypedValue();
        PaymentTransferInformationActivity.this.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    private void uploadPOP() {
        final int color = getThemeColor();
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(this, color);
        ListView list = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.view_simple_list_item);

        adapter.add(getResources().getString(R.string.estore_take_photo));
        adapter.add(getResources().getString(R.string.estore_choose_existing));
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    accessCamera();
                    dialog.dismiss();
                } else if (position == 1) {
                    accessgallery();
                    dialog.dismiss();
                }
            }
        });

        builder.setView(list);
        builder.setTitle(R.string.estore_upload_proof_of_payment);
        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // TODO Auto-generated method stub
                Dialog d = ((Dialog) dialog);

                int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                View divider = d.findViewById(dividerId);
                if (divider != null) {
                    divider.setBackgroundColor(color);
                }
            }
        });
        dialog.show();
    }

    public void back(View view) {
        finish();
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
            getpic();
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
            getphoto();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2909:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getphoto();
                }
                break;
            case 2900:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getpic();
                }
                break;
        }
    }

    private void getpic() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    private void getphoto() {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment
                .getExternalStorageDirectory(), "konsulaPhoto.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(f));
        startActivityForResult(intent,
                REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("dd", String.valueOf(requestCode) + " " + String.valueOf(resultCode));
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Log.d("dd", "masuk galery");
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                File file = new File(picturePath);
                douploadImage(file, datapayment.payment_uuid);


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(PaymentTransferInformationActivity.this, "Pilih Image Lain", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
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
                    douploadImage(f, datapayment.payment_uuid);
                }

            } catch (OutOfMemoryError E) {
            }
        }
    }

    private void douploadImage(final File file, final String payment_uuid) {
        dialogupload = AppController.createProgressDialog(PaymentTransferInformationActivity.this);
        dialogupload.setCancelable(false);
        dialogupload.show();
        File file1 = AppController.getInstance().saveBitmapToFile(file);
        TypedFile typedFile = new TypedFile("jpg", file1);
        NetworkManager.getNetworkService(getApplicationContext()).uploadPaymentProf(((AppController) getApplication()).getSessionManager().getToken(), currentLanguage, payment_uuid, typedFile, new Callback<PaymentProfModel>() {
            @Override
            public void success(PaymentProfModel paymentProfModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(paymentProfModel.messages, response, PaymentTransferInformationActivity.this, new RefreshTokenCallback() {

                    @Override
                    public void onRefreshTokenComplete() {
                        douploadImage(file, payment_uuid);
                        dialogupload.dismiss();
                    }


                });
                if (isTokenValid) {
                    dialogupload.dismiss();
                    if (paymentProfModel.results.status) {
                        btnUploadProf.setBackgroundColor(getResources().getColor(R.color.grey_dark));
                        btnUploadProf.setText(getResources().getString(R.string.text_sukses_upload));
                        btnUploadProf.setKeyListener(null);
                        ((AppController) getApplication()).setMixpanel("Click Verify Payment");
                        dotrackFacebook();

                        Intent intent = new Intent(PaymentTransferInformationActivity.this, PaymentConfirmActivity.class);
                        intent.putExtra(PaymentConfirmActivity.payment_uuid, datapayment.payment_uuid);
                        intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, true);
                        intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, datapayment.subcategory);
                        startActivity(intent);
                    } else {
                        ((AppController) getApplication()).doDialog(PaymentTransferInformationActivity.this, paymentProfModel.messages.response_text);

                    }

                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialogupload.dismiss();
                Toast.makeText(PaymentTransferInformationActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void doverifying() {
        dialogupload = AppController.createProgressDialog(PaymentTransferInformationActivity.this);
        dialogupload.setCancelable(false);
        dialogupload.show();
        NetworkManager.getNetworkService(getApplicationContext()).PostVerifyMembership(((AppController) getApplication()).getSessionManager().getToken(), currentLanguage, datapayment.payment_uuid, new Callback<MembershipVerifyingModel>() {
            @Override
            public void success(MembershipVerifyingModel membershipVerifyingModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(membershipVerifyingModel.messages, response, PaymentTransferInformationActivity.this, new RefreshTokenCallback() {

                    @Override
                    public void onRefreshTokenComplete() {
                        dialogupload.dismiss();
                        doverifying();
                    }


                });
                if (isTokenValid) {
                    dialogupload.dismiss();
                    if (membershipVerifyingModel.results != null) {
                        ((AppController) getApplication()).setMixpanel("Click Verify Payment");
                        dotrackFacebook();
                        Intent intent = new Intent(PaymentTransferInformationActivity.this, PaymentConfirmActivity.class);
                        intent.putExtra(PaymentConfirmActivity.payment_uuid, datapayment.payment_uuid);
                        intent.putExtra(PaymentConfirmActivity.TAG_FROM_PAYMENT, true);
                        intent.putExtra(PaymentConfirmActivity.TAG_SUBCATEGORY, membershipVerifyingModel.results.data.subcategory);
                        startActivity(intent);
                    } else {
                        ((AppController) getApplication()).doDialog(PaymentTransferInformationActivity.this, membershipVerifyingModel.messages.response_text);
                    }

                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialogupload.dismiss();
                Toast.makeText(PaymentTransferInformationActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void doDialogexpired() {
        textViewCountDown.setText("00:00:00");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent intent = new Intent(PaymentTransferInformationActivity.this, MytransactionHistoryActivity.class);
                    intent.putExtra(PaymentSelectionMembershipActivity.TAG_FROM_PAYMENT, true);
                    startActivity(intent);
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PaymentTransferInformationActivity.this);
        builder.setMessage(getResources().getString(R.string.text_dialog_expired)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener).setCancelable(false).show();

    }

    private void dotrackFacebook() {
        if (datapayment.subcategory.equalsIgnoreCase("teledoctor")) {
            Bundle params = new Bundle();
            params.putString("bank_name", textViewBankName.getText().toString());
            params.putString("bank_type", "Bank Transfer");
            ((AppController) getApplication()).setFacebookEvent("AddTeledocPaymentInfo", params);

        }
        if (datapayment.subcategory.equalsIgnoreCase("estore")) {
            DetailMembershipTransactionModel.Results data = ((AppController) getApplication()).getSessionManager().getDetailMembershipTransaction();
            for (int i = 0; i < data.data.items.size(); i++) {
                Bundle params = new Bundle();
                params.putString("content_ids", String.valueOf(data.data.items.get(i).detail.item_id));
                params.putString("bank_type", "product");
                params.putString("content_name", data.data.items.get(i).order_name_detail);
                params.putString("value", String.valueOf(data.data.items.get(i).price));
                params.putString("currency", data.data.items.get(i).currency);
                params.putString("num_items", String.valueOf(data.data.items.get(i).detail.quantity));
                ((AppController) getApplication()).setFacebookEvent("Purchase", params);

            }
        }
    }
}
