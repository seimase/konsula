package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.LoginActivity;
import com.konsula.app.R;
import com.konsula.app.HomeActivity;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.ViewAccountModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.direktori.EditProfileActivity;
import com.konsula.app.ui.activity.direktori.SettingActivity;
import com.konsula.app.util.RefreshTokenCallback;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Owner on 12/3/2015.
 */
public class ProfileViewFragment extends Fragment {
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
    private ImageView profileimage;
    private AppController appController;
    private LinearLayout layoutloading;
    private Button refresh;
    private RelativeLayout l_view;
    final public static String AVATAR_IMAGE = "avatarImage";
    String imageurl = null;
    OnPhoneChangeListener listener;
    String currentLanguage;
    CancelableCallback cancelableCallback;
    OnPhotoChangeListener photoChangeListener;
    AuthModel.Results data;

    public interface OnPhoneChangeListener {
        public void onSuccessPhoneChangeListener();
    }

    public interface OnPhotoChangeListener {
        public void onSuccessOnPhotoChangeListener(String photo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnPhoneChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement" + OnPhoneChangeListener.class.getSimpleName());
        }
        try {
            photoChangeListener = (OnPhotoChangeListener) (context);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement" + OnPhotoChangeListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_view_profile, container, false);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        settingButton = (Button) layout.findViewById(R.id.profile_view_setting_button);
        signOutButton = (Button) layout.findViewById(R.id.profile_view_sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                ((AppController) getActivity().getApplication()).getSessionManager().setUserAccount(null);
                                ((AppController) getActivity().getApplication()).getSessionManager().setGcmKey(null);
                                ((AppController) getActivity().getApplication()).getSessionManager().setRefreshToken("");
                                ((AppController) getActivity().getApplication()).getSessionManager().removeStringData("banner_home");
                                try {
                                    LoginManager.getInstance().logOut();
                                    LoginActivity loginActivity = new LoginActivity();
                                    loginActivity.signOut();
                                } catch (Exception e) {

                                }

                                JSONObject props = new JSONObject();
                                try {
                                    props.put("$first_name", "");
                                    props.put("$username", "");
                                    props.put("$email", "");

                                    ((AppController) getActivity().getApplication()).setMixpanelPeople("", props);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.putExtra("fromSignout", true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivityForResult(intent, 0);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getResources().getString(R.string.text_logout)).setPositiveButton(getResources().getString(R.string.text_yes), dialogClickListener)
                        .setNegativeButton(getResources().getString(R.string.text_no), dialogClickListener).show();

            }
        });

        llEdit = (LinearLayout) layout.findViewById(R.id.llEdit);
        llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivityForResult(intent, 10001);
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                fm.beginTransaction().replace(R.id.main_tabcontent, new ProfileEditFragment()).commit();
            }
        });

        TextView textView = (TextView) layout.findViewById(R.id.profile_view_edit_text_view);
        SpannableString content = new SpannableString("" + textView.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                AppConstant.FROM_PROFILE = true;
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra(SettingActivity.TAG_FROM_PROFILE, 10);
                startActivity(intent);
            }
        });

        fullNameTextView = (TextView) layout.findViewById(R.id.user_profile_fullname_text_view);
        usernameTextView = (TextView) layout.findViewById(R.id.user_profile_username_text_view);
        jointdateTextView = (TextView) layout.findViewById(R.id.user_profile_join_date_text_view);
        nameTextView = (TextView) layout.findViewById(R.id.user_profile_name_text_view);
        genderTextView = (TextView) layout.findViewById(R.id.user_profile_gender_text_view);
        birthdateTextView = (TextView) layout.findViewById(R.id.user_profile_birthdate_text_view);
        phoneTextView = (TextView) layout.findViewById(R.id.user_profile_phone_text_view);
        locationTextView = (TextView) layout.findViewById(R.id.user_profile_location_text_view);
        userheighttextview = (TextView) layout.findViewById(R.id.user_profile_height_text_view);
        userweighttextview = (TextView) layout.findViewById(R.id.user_profile_weight_text_view);
        userbloodtextview = (TextView) layout.findViewById(R.id.user_profile_blood_type_text_view);
        profileimage = (ImageView) layout.findViewById(R.id.profileimage);
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageurl != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AVATAR_IMAGE, imageurl);
                    android.app.FragmentManager fm = getActivity().getFragmentManager();
                    ImagePreviewFragment powerDialog = new ImagePreviewFragment();
                    powerDialog.setArguments(bundle);
                    powerDialog.show(fm, "fragment_power");
                }
            }
        });
        layoutloading = (LinearLayout) layout.findViewById(R.id.l_loading);
        l_view = (RelativeLayout) layout.findViewById(R.id.l_view);
        refresh = (Button) layout.findViewById(R.id.refresh);
        getdata();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutloading.setVisibility(View.VISIBLE);
                getdata();
            }
        });
        appController = new AppController();
        data = ((AppController) getActivity().getApplication()).getSessionManager().getUserAccount();
        return layout;
    }

    private void getdata() {
        cancelableCallback = new CancelableCallback<ViewAccountModel>() {

            @Override
            protected void onSuccess(ViewAccountModel viewAccountModel, Response response) {
                final boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(viewAccountModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getdata();
                    }


                });
                if (isTokenValid) {
                    photoChangeListener.onSuccessOnPhotoChangeListener(viewAccountModel.results.photo.primary.medium_image);
                    ViewAccountModel.Results rs = viewAccountModel.results;
                    layoutloading.setVisibility(View.GONE);
                    l_view.setVisibility(View.VISIBLE);
                    fullNameTextView.setText(rs.fullname);
                    nameTextView.setText(rs.fullname);
                    usernameTextView.setText(rs.email);
                    try {
                        if (rs.join_time != null)
                            jointdateTextView.setText(appController.dateJoin(rs.join_time, currentLanguage));
                    } catch (Exception e) {

                    }
                    imageurl = rs.photo.primary.large_image;
                    nameTextView.setText(rs.fullname);
                    try {
                        if (rs.gender.equals("M")) {
                            genderTextView.setText(getResources().getString(R.string.reg_male));
                        } else {
                            genderTextView.setText(getResources().getString(R.string.reg_female));
                        }
                    } catch (Exception e) {

                    }

                    if (rs.birth_date != null)
                        birthdateTextView.setText(appController.dateBirth(rs.birth_date));
                    if (rs.phone_number != null)
                        phoneTextView.setText(rs.phone_number);
                    locationTextView.setText(rs.address);
                    userheighttextview.setText(rs.height == 0 ? "" : rs.height + " cm");
                    userweighttextview.setText(rs.weight == 0 ? "" : rs.weight + " Kg");
                    userbloodtextview.setText(rs.blood_type == null ? "-" : rs.blood_type);
                    try {
                        ((AppController) getActivity().getApplication()).displayImage(getActivity(), rs.photo.primary.thumb_image, profileimage);
                    } catch (Exception e) {

                    }
                    if (!data.phone_number.equals(rs.phone_number)){
                        listener.onSuccessPhoneChangeListener();
                        ((AppController) getActivity().getApplication()).getSessionManager().updateStatusUser("pending");
                    }

                }
            }

            @Override
            protected void onFailure(RetrofitError error) {

                layoutloading.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            }
        };

        NetworkManager.getNetworkService(getActivity()).getviewAccount(
                ((AppController) getActivity().getApplication())
                        .getSessionManager().getToken(), currentLanguage,
                cancelableCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK))
            getdata();


    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }

}
