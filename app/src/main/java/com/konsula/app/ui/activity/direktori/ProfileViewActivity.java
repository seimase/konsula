package com.konsula.app.ui.activity.direktori;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.konsula.app.AppController;
import com.konsula.app.LoginActivity;
import com.konsula.app.R;
import com.konsula.app.HomeActivity;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.ViewAccountModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.fragment.direktori.ImagePreviewFragment;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.Date;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 11/03/2016.
 */
public class ProfileViewActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextView toolbar_name;
    private Button settingButton;
    private Button signOutButton;
    private LinearLayout llEdit;
    private TextView fullNameTextView;
    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView jointdateTextView;
    private TextView genderTextView;
    private TextView birthdateTextView;
    private TextView phoneTextView;
    private TextView locationTextView;
    private TextView userheighttextview;
    private TextView userweighttextview;
    private TextView userbloodtextview;
    private Date datejoin, birthdate;
    private ImageView profileimage;
    private LinearLayout layoutloading;
    private RelativeLayout l_view;
    private Button refresh;
    final public static String AVATAR_IMAGE = "avatarImage";
    String imageurl = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_profile);

        settingButton = (Button) findViewById(R.id.profile_view_setting_button);
        signOutButton = (Button) findViewById(R.id.profile_view_sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((AppController) getApplication()).getSessionManager().setUserAccount(null);
                try {
                    LoginManager.getInstance().logOut();
                    LoginActivity loginActivity = new LoginActivity();
                    loginActivity.signOut();
                } catch (Exception e) {

                }
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(intent, 0);


            }
        });

        llEdit = (LinearLayout) findViewById(R.id.llEdit);
        llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivityForResult(intent, 10001);
            }
        });

        TextView textView = (TextView) findViewById(R.id.profile_view_edit_text_view);
        SpannableString content = new SpannableString("" + textView.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        fullNameTextView = (TextView) findViewById(R.id.user_profile_fullname_text_view);
        usernameTextView = (TextView) findViewById(R.id.user_profile_username_text_view);
        jointdateTextView = (TextView) findViewById(R.id.user_profile_join_date_text_view);
        nameTextView = (TextView) findViewById(R.id.user_profile_name_text_view);
        genderTextView = (TextView) findViewById(R.id.user_profile_gender_text_view);
        birthdateTextView = (TextView) findViewById(R.id.user_profile_birthdate_text_view);
        phoneTextView = (TextView) findViewById(R.id.user_profile_phone_text_view);
        locationTextView = (TextView) findViewById(R.id.user_profile_location_text_view);
        userheighttextview = (TextView) findViewById(R.id.user_profile_height_text_view);
        userweighttextview = (TextView) findViewById(R.id.user_profile_weight_text_view);
        userbloodtextview = (TextView) findViewById(R.id.user_profile_blood_type_text_view);
        profileimage = (ImageView) findViewById(R.id.profileimage);
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageurl != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AVATAR_IMAGE, imageurl);
                    android.app.FragmentManager fm = getFragmentManager();
                    ImagePreviewFragment powerDialog = new ImagePreviewFragment();
                    powerDialog.setArguments(bundle);
                    powerDialog.show(fm, "fragment_power");
                }
            }
        });
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (RelativeLayout) findViewById(R.id.l_view);
        refresh = (Button) findViewById(R.id.refresh);
        getdata();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getdata();
            }
        });
        backButton = (ImageButton) findViewById(R.id.edit_profile_back_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar_name = (TextView) findViewById(R.id.text_toolbarname);
        final AuthModel.Results userData = ((AppController) getApplication()).getSessionManager().getUserAccount();
        toolbar_name.setText(userData.fullname);
    }

    private void getdata() {
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage= (currentLanguage.equals("en") || currentLanguage.equals("EN")) ?"en":"id";
        final String finalCurrentLanguage = currentLanguage;
        NetworkManager.getNetworkService(getApplicationContext()).getviewAccount(
                ((AppController) getApplication())
                        .getSessionManager().getToken(),currentLanguage,
                new Callback<ViewAccountModel>() {
                    @Override
                    public void success(ViewAccountModel viewAccountModel, Response response) {
                        boolean isTokenValid = ((AppController) getApplication()).isTokenValid(viewAccountModel.messages, response,ProfileViewActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                getdata();
                            }

                        });
                        if (isTokenValid) {
                            ViewAccountModel.Results rs = viewAccountModel.results;
                            layoutloading.setVisibility(View.GONE);
                            l_view.setVisibility(View.VISIBLE);
                            fullNameTextView.setText(rs.fullname);
                            nameTextView.setText(rs.fullname);
                            usernameTextView.setText(rs.email);
                            try {
                                if (rs.join_time != null)
                                    jointdateTextView.setText(((AppController) getApplication()).dateJoin(rs.join_time, finalCurrentLanguage));
                            } catch (Exception e) {

                            }
                            imageurl = rs.photo.primary.large_image;
                            nameTextView.setText(rs.fullname);
                            try {
                                if (rs.gender.equals("M")) {
                                    genderTextView.setText("Pria");
                                } else {
                                    genderTextView.setText("Wanita");
                                }
                            } catch (Exception e) {

                            }

                            if (rs.birth_date != null)
                                birthdateTextView.setText(((AppController) getApplication()).dateBirth(rs.birth_date));
                            if (rs.phone_number != null)
                                phoneTextView.setText(rs.phone_number);
                            locationTextView.setText(rs.address);
                            userheighttextview.setText(rs.height + " cm");
                            userweighttextview.setText(rs.weight + " Kg");
                            userbloodtextview.setText(rs.blood_type);
                            try {
                                ((AppController) getApplication()).displayImage(ProfileViewActivity.this,rs.photo.primary.thumb_image, profileimage);
                            } catch (Exception e) {

                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        layoutloading.setVisibility(View.GONE);
                        refresh.setVisibility(View.VISIBLE);

                        DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(ProfileViewActivity.this);
                        builder2.setMessage("Failed to load profile. Please check your internet connection.").setPositiveButton("Dismiss", dialogClickListener2).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK))
            getdata();
    }
}
