package com.konsula.app.ui.activity.chatdokter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.json.Json;
import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.ChatRetrictionModel;
import com.konsula.app.service.model.Chatmodel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.ChatTimeoutReceiver;
import com.konsula.app.util.RefreshTokenCallback;
import com.zopim.android.sdk.api.Chat;
import com.zopim.android.sdk.api.ChatSession;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.data.observers.ConnectionObserver;
import com.zopim.android.sdk.model.Connection;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 26/02/2016.
 */
public class ChatDocterActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private EditText editTextnama, editTextemail, editTextnomortelpon, editTextreason;
    private LinearLayout btnStratChat;
    private RelativeLayout layoutreason;
    private ImageView imageViewchat;
    private TextView textViewheader, textViewmessage;
    String fullname, email, phone_number, reason;
    ChatRetrictionModel.Results mResource;
    AuthModel.Results userData;
    String gender;
    private TextView textchat;
    ChatTimeoutReceiver chatTimeoutReceiver;
    ConnectionObserver myConnectionObserver;

    private LinearLayout view_promo;
    private TextView promo_header1;
    private TextView promo_code;
    private TextView promo_header2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dokter);
        ((AppController) getApplication()).setMixpanel("Open Chat Page");
        btnBack = (ImageButton) findViewById(R.id.backButton);
        editTextnama = (EditText) findViewById(R.id.text_nama_chat);
        editTextemail = (EditText) findViewById(R.id.text_email_chat);
        editTextnomortelpon = (EditText) findViewById(R.id.text_notel_chat);
        imageViewchat = (ImageView) findViewById(R.id.imageView1);
        textViewheader = (TextView) findViewById(R.id.header_chatdocter);
        textViewmessage = (TextView) findViewById(R.id.message_chatdocter);
        layoutreason = (RelativeLayout) findViewById(R.id.layoutreason);
        textchat = (TextView) findViewById(R.id.textchat);
        view_promo =(LinearLayout) findViewById(R.id.view_promo);
        promo_header1 =(TextView) findViewById(R.id.promo_header1);
        promo_code =(TextView) findViewById(R.id.promo_code);
        promo_header2 =(TextView) findViewById(R.id.promo_header2);
        editTextnama.setKeyListener(null);
        editTextemail.setKeyListener(null);
        editTextnomortelpon.setKeyListener(null);

        editTextnomortelpon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    if (editTextnomortelpon.getText().toString().equals("")) {
                        editTextnomortelpon.setText("+62");
                    }
                }
            }
        });

        editTextreason = (EditText) findViewById(R.id.text_reason_chat);
        editTextreason.requestFocus();
        editTextreason.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScrollView sv = (ScrollView) findViewById(R.id.scrollview1);
                    sv.fullScroll(View.FOCUS_DOWN);
                }
            }
        });

        btnStratChat = (LinearLayout) findViewById(R.id.btnstartchat);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setdatauser();
        myConnectionObserver = new ConnectionObserver() {

            public void update(Connection connection) {
            }
        };
        ZopimChat.getDataSource().addConnectionObserver(myConnectionObserver);

    }


    private void setdatauser() {
        try {
            userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
            editTextnama.setText(userData.fullname);
            editTextemail.setText(userData.email);
            editTextnomortelpon.setText(userData.phone_number);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error Get Data User", Toast.LENGTH_SHORT);
        }

        mResource = ((AppController) getApplication()).getSessionManager().getChatRetriction();
        if (mResource.status.equals("online")) {
            if (!mResource.message.equals("")) {
                sadmembership();
            } else {
                btnStratChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fullname = editTextnama.getText().toString();
                        email = editTextemail.getText().toString();
                        phone_number = editTextnomortelpon.getText().toString();
                        reason = editTextreason.getText().toString();
                        if (reason.equals("")) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_warning_empty), Toast.LENGTH_SHORT).show();
                        } else {
                            doregisterChat();
                        }
                    }
                });
            }
        } else if (mResource.status.equals("offline")) {
            if (mResource.category.equals("membership")) {
                sadmembership();
            } else {
                sadjamkerja();
            }
        }
        if (userData.gender != null) {
            if (userData.gender.equals("F")) gender = getResources().getString(R.string.reg_female);
            else gender = getResources().getString(R.string.reg_male);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ZopimChat.getDataSource().addConnectionObserver(myConnectionObserver);
        registerReceiver(chatTimeoutReceiver, new IntentFilter(ChatSession.ACTION_CHAT_SESSION_TIMEOUT));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void doregisterChat() {
        AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
        String uuid = userData.uuid;

        final ProgressDialog dialog = ProgressDialog.show(this, "Please wait", "Login to server", true);
        dialog.show();

        NetworkManager.getNetworkService(this).registerChat(((AppController) getApplication())
                .getSessionManager().getToken(), fullname, "doctor", email, phone_number, reason, new Callback<Chatmodel>() {
            @Override
            public void success(Chatmodel chatmodel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(chatmodel.messages, response, ChatDocterActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        doregisterChat();
                    }


                });
                if (isTokenValid) {
                    if (chatmodel.results) {
                        startChat();

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

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(ChatDocterActivity.this);
                        builder2.setMessage(chatmodel.messages.response_text).setPositiveButton("Dismiss", dialogClickListener2).show();

                    }

                    dialog.dismiss();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();

                DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder2 = new AlertDialog.Builder(ChatDocterActivity.this);
                builder2.setMessage("Failed to login. Please check your internet connection.").setPositiveButton("Dismiss", dialogClickListener2).show();
            }
        });
    }

    private void startChat() {
//        ChatWidgetService.disable();
        ((AppController) getApplication()).setMixpanel("Do Chat");
        VisitorInfo visitorInfo = new VisitorInfo.Builder()
                .name(editTextnama.getText().toString())
                .email(editTextemail.getText().toString())
                .phoneNumber(editTextnomortelpon.getText().toString())
                .build();
        startActivity(new Intent(ChatDocterActivity.this, ZopimChatActivity.class));
        ZopimChat.setVisitorInfo(visitorInfo);
        Chat chat = new ZopimChat.SessionConfig()
                .build(this);
        String message = getResources().getString(R.string.hint_nama) + "=" + fullname + "\n" + getResources().getString(R.string.hint_jenis_kelamin) + "=" + gender + "\n" + getResources().getString(R.string.hint_tanggallahir) + "=" + userData.birth_date + "\n" + getResources().getString(R.string.hint_tinggibadan) + "=" + userData.height + "\n" + getResources().getString(R.string.hint_beratbadan) + "=" + userData.weight + "\n" + getResources().getString(R.string.hint_golongan_darah) + "=" + userData.blood_type + " " + userData.blood_rhesus + "\n" + getResources().getString(R.string.reason_teledoc) + "=" + reason;
        chat.send(message);
        ZopimChat.trackEvent("Started chat with optional pre-chat form");
        finish();
    }

    private void sadmembership() {
        imageViewchat.setImageDrawable(getResources().getDrawable(R.drawable.ic_doksad_chat));
        textViewheader.setText(getResources().getString(R.string.chatdokter_header3));
        textViewmessage.setText(mResource.message);
        layoutreason.setVisibility(View.INVISIBLE);
        textchat.setText(getResources().getString(R.string.upgradenow));
        btnStratChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppController) getApplication()).setMixpanel("Click Upgrade Chat Page");
                Intent intent = new Intent(ChatDocterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("fromMembership", 2);
                startActivity(intent);
            }
        });
        if (mResource.promocode != null){
            view_promo.setVisibility(View.VISIBLE);
            promo_header1.setText(mResource.promocode.start_message);
            promo_code.setText(mResource.promocode.promo_code);
            promo_header2.setText(mResource.promocode.end_message);
        }else {
            view_promo.setVisibility(View.GONE);
        }
    }

    private void sadjamkerja() {
        ((AppController) getApplication()).setMixpanel("Chat Not Available");
        imageViewchat.setImageDrawable(getResources().getDrawable(R.drawable.ic_doksad_chat));
        textViewheader.setText(getResources().getString(R.string.title_chatdokter));
        textViewmessage.setText(mResource.message.replace("\\n", "\n"));
        btnStratChat.setVisibility(View.INVISIBLE);
        layoutreason.setVisibility(View.INVISIBLE);

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}

