package com.konsula.app.ui.activity.teledokter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.InitSpecialityModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 3/4/2016.
 */
public class SpesialisasiActivity extends Activity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild, listDataDoctorId, listDataChildPracticeId, listDataChildProfileLink, listDataChildIdentityUri, listDataChildCurrency, listDataChildRate;
    private ImageButton backButton;
    private ProgressDialog dialog;
    private String currentLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spesialisasi_teledoc);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        } else {
            expListView.setIndicatorBoundsRelative(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        }

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        prepareListData();
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private void prepareListData() {
        dialog = AppController.createProgressDialog(SpesialisasiActivity.this);
        dialog.show();
        NetworkManager.getNetworkService(this).initSpeciality(((AppController) getApplication()).getSessionManager().getToken(), currentLanguage, new Callback<InitSpecialityModel>() {
            @Override
            public void success(InitSpecialityModel initSpecialityModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).
                        isTokenValid(initSpecialityModel.messages, response,SpesialisasiActivity.this, new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                prepareListData();
                            }


                        });
                if (isTokenValid) {

                    listDataChild = new HashMap<String, List<String>>();
                    listDataDoctorId = new HashMap<String, List<String>>();
                    listDataChildPracticeId = new HashMap<String, List<String>>();
                    listDataChildProfileLink = new HashMap<String, List<String>>();
                    listDataChildIdentityUri = new HashMap<String, List<String>>();
                    listDataChildCurrency = new HashMap<String, List<String>>();
                    listDataChildRate = new HashMap<String, List<String>>();
                    listDataHeader = new ArrayList<String>();

                    // Adding child data



                    int i = 0;

                    for (InitSpecialityModel.Result item : initSpecialityModel.results) {
                        listDataHeader.add("" + item.specialization.specialization_name);
                        final List<String> list1 = new ArrayList<String>();
                        final List<String> listDoctorId = new ArrayList<String>();
                        final List<String> listPracticeId = new ArrayList<String>();
                        final List<String> listProfileLink = new ArrayList<String>();
                        final List<String> listIdentityUri = new ArrayList<String>();
                        final List<String> listCurrency = new ArrayList<String>();
                        final List<String> listRate = new ArrayList<String>();

                        for(int j=0;j<item.doctors.size();j++){
                            list1.add("" + item.doctors.get(j).doctor_name);
                            listDoctorId.add("" + item.doctors.get(j).doctor_id);
                            listPracticeId.add("" + item.doctors.get(j).practice_id);
                            listProfileLink.add("" + item.doctors.get(j).profile_link);
                            listIdentityUri.add("" + item.doctors.get(j).identity_uri);
                            listCurrency.add("" + item.doctors.get(j).currency);
                            listRate.add("" + item.doctors.get(j).rate);
                        }
                        listDataChild.put(listDataHeader.get(i), list1);
                        listDataDoctorId.put(listDataHeader.get(i), listDoctorId);
                        listDataChildPracticeId.put(listDataHeader.get(i), listPracticeId);
                        listDataChildProfileLink.put(listDataHeader.get(i), listProfileLink);
                        listDataChildIdentityUri.put(listDataHeader.get(i), listIdentityUri);
                        listDataChildCurrency.put(listDataHeader.get(i), listCurrency);
                        listDataChildRate.put(listDataHeader.get(i), listRate);
                        i++;
                    }

                    Log.e("asdasdasd", "" + listDataChild.size() + " " + listDataChild.toString());

                    listAdapter = new ExpandableListAdapter(SpesialisasiActivity.this, listDataHeader, listDataChild, listDataDoctorId, listDataChildPracticeId, listDataChildProfileLink, listDataChildIdentityUri, listDataChildCurrency, listDataChildRate);
                    expListView.setAdapter(listAdapter);
                    dialog.dismiss();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
            }
        });
    }
}
