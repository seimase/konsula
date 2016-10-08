package com.konsula.app.ui.fragment.direktori;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.ContactUsModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.direktori.GoogleMapActivity;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 15/03/2016.
 */
public class ContactUsFragment extends Fragment {

    public interface OnSendListener {
        public void onSuccessSend();


    }

    private OnSendListener listener;

    private ImageButton backButton;
    private ProgressDialog dialog;
    private EditText etFullname, etEmail, etPhone, etMessage;
    private Button btnSend;
    private TextView tvSeeMap;
    private Double latitude = -6.1815108;
    private Double longitude = 106.7283656106;
    private String title = "Konsula";
    private CancelableCallback cancelableCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSendListener) {
            listener = (OnSendListener) context;
        } else {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.activity_contact_us, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        etFullname = (EditText) view.findViewById(R.id.etFullname);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etPhone.getText().toString().equals("")) {
                        etPhone.setText("+62");
                    }
                }
            }
        });
        etMessage = (EditText) view.findViewById(R.id.etMessage);

        tvSeeMap = (TextView) view.findViewById(R.id.tvSeeMap);
        SpannableString content = new SpannableString("" + tvSeeMap.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvSeeMap.setText(content);
        tvSeeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GoogleMapActivity.class);
                i.putExtra("longitude", longitude);
                i.putExtra("latitude", latitude);
                i.putExtra("title", title);
                startActivity(i);
            }
        });


        btnSend = (Button) view.findViewById(R.id.send_contact_us_button);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etFullname.getText().toString().equals("") ||
                        etEmail.getText().toString().equals("") ||
                        etPhone.getText().toString().equals("") ||
                        etMessage.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Harap isi semua field", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(etEmail.getText().toString())) {
                    Toast.makeText(getActivity(), "Harap isi email sesuai format", Toast.LENGTH_SHORT).show();
                } else if (!isValidCellPhone(etPhone.getText().toString())) {
                    Toast.makeText(getActivity(), "Harap isi nomor telepon dengan angka", Toast.LENGTH_SHORT).show();
                } else {
                    doContactUs(etFullname.getText().toString(), etEmail.getText().toString(), etPhone.getText().toString()
                            , etMessage.getText().toString());
                }
            }
        });

        return view;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isValidCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    private void doContactUs(final String full_name, final String email, final String phone_number, final String message) {
        dialog = ProgressDialog.show(getActivity(), "Please wait", "Sending Information", true);
        dialog.show();
        cancelableCallback = new CancelableCallback<ContactUsModel>() {


            @Override
            protected void onSuccess(ContactUsModel contactUsModel, Response response) {
                dialog.dismiss();
                ((AppController) getActivity().getApplication()).doDialog(getActivity(), "Your message has sent. We will contact you soon.");

            }

            @Override
            protected void onFailure(RetrofitError error) {
                dialog.dismiss();
                ((AppController) getActivity().getApplication()).doDialog(getActivity(), getResources().getString(R.string.no_connection_error_message));

            }
        };
        NetworkManager.getNetworkService(getActivity()).doContact(((AppController) getActivity().getApplication()).getSessionManager().getToken(), full_name, email, phone_number, message, cancelableCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }

}


