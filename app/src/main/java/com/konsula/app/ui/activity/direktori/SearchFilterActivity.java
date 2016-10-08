package com.konsula.app.ui.activity.direktori;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.InitSearchModel;
import com.konsula.app.service.model.LocationSearchModel;
import com.konsula.app.service.model.MultiSearchModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.InitSearchDefaultDoctorAdapter;
import com.konsula.app.ui.adapter.InitSearchDefaultKlinikAdapter;
import com.konsula.app.ui.adapter.InitSearchDefaultLocationAdapter;
import com.konsula.app.ui.adapter.LocationSearchResultAdapter;
import com.konsula.app.ui.adapter.MultiSearchResultAdapter;
import com.konsula.app.ui.custom.CustomtextView;
import com.konsula.app.ui.view.LoadingMenu;
import com.konsula.app.util.RefreshTokenCallback;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/11/2015.
 */
public class SearchFilterActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, TextWatcher, AdapterView.OnItemClickListener {
    private EditText locationText;
    private ListView resultListView, resultLocationListView;
    private EditText keywordText;
    private boolean from_result = false;
    private ImageButton backButtonImage;
    private RadioGroup kategoriRadioGroup;
    private LoadingMenu loading;
    private MultiSearchResultAdapter multiSearchResultAdapter;
    private LocationSearchResultAdapter initSearchResultAdapter;
    private InitSearchDefaultLocationAdapter initSearchDefaultLocationAdapter;
    private InitSearchDefaultDoctorAdapter initSearchDefaultDoctorAdapter;
    private InitSearchDefaultKlinikAdapter initSearchDefaultKlinikAdapter;
    private CustomtextView tvDokter, tvKlinik;
    private ImageView btnclearlokasi, btnclearspesialisasi;
    private boolean from_clinic = false;

    private String locationState;
    private String locality;
    private String currentLanguage;
    private String firstkeyword;
    private boolean initdone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);
        loading = new LoadingMenu(this);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        resultListView = (ListView) findViewById(R.id.search_filter_doctor_list_view);
        resultListView.addFooterView(loading);
        resultLocationListView = resultListView;
        //resultLocationListView.addFooterView(loading);
        btnclearlokasi = (ImageView) findViewById(R.id.btn_clearlokasi);
        btnclearspesialisasi = (ImageView) findViewById(R.id.btn_clearspesialisasi);

        keywordText = (EditText) findViewById(R.id.search_filter_doctor_spesialisasi_box_edit_text);
        locationText = (EditText) findViewById(R.id.search_filter_doctor_location_box_edit_text);

        kategoriRadioGroup = (RadioGroup) findViewById(R.id.radio_search_filter);
        backButtonImage = (ImageButton) findViewById(R.id.search_filter_doctor_action_close_image_button);

        tvDokter = (CustomtextView) findViewById(R.id.tvDokter);
        tvDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategoriRadioGroup.check(R.id.radio_search_filter_doctor);
            }
        });

        tvKlinik = (CustomtextView) findViewById(R.id.tvKlinik);
        tvKlinik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategoriRadioGroup.check(R.id.radio_search_filter_klinik);
            }
        });

        multiSearchResultAdapter = new MultiSearchResultAdapter(this);
        initSearchResultAdapter = new LocationSearchResultAdapter(this);
        initSearchDefaultLocationAdapter = new InitSearchDefaultLocationAdapter(this);
        initSearchDefaultDoctorAdapter = new InitSearchDefaultDoctorAdapter(this);
        initSearchDefaultKlinikAdapter = new InitSearchDefaultKlinikAdapter(this);

        kategoriRadioGroup.check(R.id.radio_search_filter_doctor);
        kategoriRadioGroup.setOnCheckedChangeListener(this);
        final String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        locationText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (locationText.getText().toString().equals("")) {
                        //when user change focus from keyword, and keyword location null
                        initDefaultAutocompleteLocation();
                        resultListView.setAdapter(initSearchDefaultLocationAdapter);

                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                locationText.setText("" + initSearchDefaultLocationAdapter.getItem(i).location_name);
                                locality = initSearchDefaultLocationAdapter.getItem(i).locality_uri;
                                locationState = initSearchDefaultLocationAdapter.getItem(i).state_uri;
                                initSearchDefaultLocationAdapter.clear();

                                keywordText.requestFocus();
                            }
                        });
                    } else {
                        //when user change focus from keyword, and keyword location not null
                        initSearch(locationText.getText().toString());
                        resultLocationListView.setAdapter(initSearchResultAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                locationText.setText("" + initSearchResultAdapter.getItem(i).location_name);
                                locality = initSearchResultAdapter.getItem(i).locality_uri;
                                locationState = initSearchResultAdapter.getItem(i).state_uri;
                                initSearchResultAdapter.clear();

                                keywordText.requestFocus();
                            }
                        });
                    }
                }
            }
        });

        keywordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_doctor) {
                    if (keywordText.getText().toString().equals("") && keywordText.getText().toString().equals(firstkeyword)) {
                        initDefaultAutocompleteDoctor();
                        resultListView.setAdapter(initSearchDefaultDoctorAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultDoctorAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (initSearchDefaultDoctorAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultDoctorAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {
                                    if (initSearchDefaultDoctorAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultDoctorAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("keyword", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultDoctorAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });

                        initSearchDefaultDoctorAdapter.clear();
                    } else {
                        String sKeyword = keywordText.getText().toString();
                        if (sKeyword.equals("")){
                            initDefaultAutocompleteDoctor();
                        }else{
                            multiSearch(sKeyword);
                        }

                        resultListView.setAdapter(multiSearchResultAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            Log.e("iden uri", "" + locality);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }

                } else if (hasFocus && kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_klinik) {
                    if (keywordText.getText().toString().equals("")) {
                        initDefaultAutocompleteKlinik();
                        resultListView.setAdapter(multiSearchResultAdapter);

                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {

                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });

                        multiSearchResultAdapter.clear();
                    } else {
                        multiSearch(keywordText.getText().toString());
                        resultListView.setAdapter(multiSearchResultAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {

                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        locationText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (locationText.getText().toString().equals("")) {
                    //default when user delete text until null
                    initDefaultAutocompleteLocation();
                    resultListView.setAdapter(initSearchDefaultLocationAdapter);

                    resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            locationText.setText("" + initSearchDefaultLocationAdapter.getItem(i).location_name);
                            locality = initSearchDefaultLocationAdapter.getItem(i).locality_uri;
                            locationState = initSearchDefaultLocationAdapter.getItem(i).state_uri;
                            initSearchDefaultLocationAdapter.clear();

                            keywordText.requestFocus();
                        }
                    });
                } else {
                    btnclearlokasi.setVisibility(View.VISIBLE);
                    //init autocomplete search when text not null
                    initSearch(s);
                    resultLocationListView.setAdapter(initSearchResultAdapter);
                    resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            locationText.setText("" + initSearchResultAdapter.getItem(i).location_name);
                            locality = initSearchResultAdapter.getItem(i).locality_uri;
                            locationState = initSearchResultAdapter.getItem(i).state_uri;
                            initSearchResultAdapter.clear();

                            keywordText.requestFocus();
                        }
                    });
                }
            }
        });

        keywordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!keywordText.getText().toString().equals(""))
                    btnclearspesialisasi.setVisibility(View.VISIBLE);
                if (kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_doctor) {
//                    if (locationText.getText().toString().equals("")) {
//                     //   locationText.requestFocus();
//
//                    } else
                    if (keywordText.getText().toString().equals("")) {
                        if (initdone) initDefaultAutocompleteDoctor();
                        resultListView.setAdapter(multiSearchResultAdapter);

                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                        multiSearchResultAdapter.clear();
                    } else {
                        multiSearch(s);
                        resultListView.setAdapter(multiSearchResultAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {

                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                }

                if (kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_klinik) {
//                    if (locationText.getText().toString().equals("")) {
//                      ///  locationText.requestF.ocus.();
//
//                    } else
                    if (keywordText.getText().toString().equals("")) {
                        initDefaultAutocompleteKlinik();
                        resultListView.setAdapter(multiSearchResultAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {

                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                        multiSearchResultAdapter.clear();
                    } else {
                        multiSearch(s);
                        resultListView.setAdapter(multiSearchResultAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {

                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        // set listener
        backButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            from_result = getIntent().getExtras().getBoolean("from_result");
            from_clinic = getIntent().getExtras().getBoolean("from_clinic");
        } catch (Exception e) {

        }
        if (from_result) {
            //locationText.setText(getIntent().getExtras().getString("location"));
            firstkeyword = getIntent().getExtras().getString("title");
            keywordText.setText(getIntent().getExtras().getString("title"));
        }

        if (from_clinic) {
            kategoriRadioGroup.check(R.id.radio_search_filter_klinik);
            keywordText.setText(getIntent().getExtras().getString("title"));
        }


    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        final String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        if (radioGroup == kategoriRadioGroup) {
            keywordText.setText("");
            if (i == R.id.radio_search_filter_doctor) {
                keywordText.setHint(getResources().getString(R.string.spesialisasi));
                if (keywordText.hasFocus()) {
                    if (keywordText.getText().toString().equals("")) {
                        if (initdone) initDefaultAutocompleteDoctor();
                        //when radio doctor selected, and when keywordtext has focus (location have no focus), and keywordtext null
                        resultListView.setAdapter(initSearchDefaultDoctorAdapter);

                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultDoctorAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (initSearchDefaultDoctorAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultDoctorAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {

                                    if (initSearchDefaultDoctorAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultDoctorAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultDoctorAdapter.getItem(i).english_name : initSearchDefaultDoctorAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", initSearchDefaultDoctorAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });

                        initSearchDefaultDoctorAdapter.clear();
                    } else {
                        multiSearch(keywordText.getText().toString());
                        resultListView.setAdapter(multiSearchResultAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultDoctorActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_doctor ?
                                                SearchResultDoctorActivity.class :
                                                SearchResultKlinikActivity.class);
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intentt = new Intent(getApplicationContext(), kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_doctor ?
                                                    SearchResultDoctorActivity.class :
                                                    SearchResultKlinikActivity.class);
                                            intentt.putExtra("location_text", locationText.getText().toString());
                                            intentt.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intentt.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intentt.putExtra("country", "indonesia");
                                            startActivity(intentt);
                                        } else {
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), DoctorProfileActivity.class);
                                        intent.putExtra("doctorUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                } else {
                    //if no focus, didnt do anything
                    initDefaultAutocompleteLocation();
                    resultListView.setAdapter(initSearchDefaultLocationAdapter);
                }

            } else if (i == R.id.radio_search_filter_klinik) {
                keywordText.setHint(getResources().getString(R.string.search_lokasi));

                if (keywordText.hasFocus()) {
                    if (keywordText.getText().toString().equals("")) {
                        //when radio klinik selected, and when keywordtext has focus (not location has focus), and keywordtext null
                        initDefaultAutocompleteKlinik();
                        resultListView.setAdapter(initSearchDefaultKlinikAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultKlinikAdapter.getItem(i).english_name : initSearchDefaultKlinikAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (initSearchDefaultKlinikAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", initSearchDefaultKlinikAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultKlinikAdapter.getItem(i).english_name : initSearchDefaultKlinikAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", initSearchDefaultKlinikAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {

                                    if (initSearchDefaultKlinikAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", initSearchDefaultKlinikAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultKlinikAdapter.getItem(i).english_name : initSearchDefaultKlinikAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", initSearchDefaultKlinikAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? initSearchDefaultKlinikAdapter.getItem(i).english_name : initSearchDefaultKlinikAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }

                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", initSearchDefaultKlinikAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });

                        initSearchDefaultKlinikAdapter.clear();
                    } else {
                        multiSearch(keywordText.getText().toString());
                        resultListView.setAdapter(multiSearchResultAdapter);
                        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                keywordText.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                if (locationText.getText().toString().equals("")) {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                        intent.putExtra("location_text", "Jakarta");
                                        intent.putExtra("location_state", "jakarta");
                                        intent.putExtra("locality", "");
                                        intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                        intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                        intent.putExtra("country", "indonesia");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                } else {
                                    if (multiSearchResultAdapter.getItem(i).target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
                                        if (locationText.getText().toString().equals(getResources().getString(R.string.currentLoc))) {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), SearchResultKlinikActivity.class);
                                            intent.putExtra("location_text", locationText.getText().toString());
                                            intent.putExtra("location_state", locationState);
                                            intent.putExtra("locality", locality);
                                            intent.putExtra("keyword", multiSearchResultAdapter.getItem(i).identity_uri);
                                            intent.putExtra("keyword_name", (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? multiSearchResultAdapter.getItem(i).english_name : multiSearchResultAdapter.getItem(i).bahasa_name);
                                            intent.putExtra("country", "indonesia");
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                        intent.putExtra("praticeUri", multiSearchResultAdapter.getItem(i).identity_uri);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                } else {
                    //if no focus, didnt do anything
                    initDefaultAutocompleteLocation();
                    resultListView.setAdapter(initSearchDefaultLocationAdapter);
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    public void multiSearch(final CharSequence s) {
        if (!s.equals(firstkeyword) && !"".equals(s)) {
            loading.start();
            String currentLanguage = getResources().getConfiguration().locale.getLanguage();
            currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
            NetworkManager.getNetworkService(this).multiSearch(((AppController) getApplication()).getSessionManager().getToken(), currentLanguage, kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_doctor ?
                            MultiSearchModel.SEARCH_TYPE_DOCTOR : MultiSearchModel.SEARCH_TYPE_PRACTICE,
                    s.toString(), locationText.getText().toString(), new Callback<MultiSearchModel>() {
                        @Override
                        public void success(MultiSearchModel multiSearchModel, Response response) {
                            boolean isTokenValid = ((AppController) getApplication()).isTokenValid(multiSearchModel.messages, response, SearchFilterActivity.this, new RefreshTokenCallback() {
                                @Override
                                public void onRefreshTokenComplete() {
                                    initSearch(s);
                                }


                            });
                            if (isTokenValid) {
                                multiSearchResultAdapter.clear();
                                for (MultiSearchModel.Result item : multiSearchModel.results) {
                                    multiSearchResultAdapter.add(item);
                                }
                                multiSearchResultAdapter.notifyDataSetChanged();
                                loading.stop();
                            }
                        }


                        @Override
                        public void failure(RetrofitError error) {
                            loading.stop();
                        }
                    });
        }
    }

    public void initSearch(final CharSequence s) {
        loading.start();

        final LocationSearchModel.Result curr;
        curr = new LocationSearchModel().new Result();
        curr.location_name = getResources().getString(R.string.currentLoc);

        NetworkManager.getNetworkService(this).locationSearch(((AppController) getApplication()).getSessionManager().getToken(), currentLanguage, s.toString(), new Callback<LocationSearchModel>() {
            @Override
            public void success(LocationSearchModel locationSearchModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(locationSearchModel.messages, response, SearchFilterActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        initSearch(s);
                    }


                });
                if (isTokenValid) {
                    initSearchResultAdapter.clear();
                    initSearchResultAdapter.add(curr);

                    for (LocationSearchModel.Result item : locationSearchModel.results) {
                        initSearchResultAdapter.add(item);
                    }
                    initSearchResultAdapter.notifyDataSetChanged();
                    loading.stop();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                loading.stop();
            }
        });
    }

    public void initDefaultAutocompleteLocation() {
        loading.start();

        final InitSearchModel.LocationDropdown curr;
        curr = new InitSearchModel().new LocationDropdown();
        curr.location_name = getResources().getString(R.string.currentLoc);

        NetworkManager.getNetworkService(this).initSearch(((AppController) getApplication())
                .getSessionManager().getToken(), currentLanguage, new Callback<InitSearchModel>() {
            @Override
            public void success(InitSearchModel initSearchModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(initSearchModel.messages, response, SearchFilterActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        initDefaultAutocompleteLocation();
                    }


                });
                if (isTokenValid) {
                    initdone = true;
                    initSearchDefaultLocationAdapter.clear();
                    initSearchDefaultLocationAdapter.add(curr);

                    for (InitSearchModel.LocationDropdown item : initSearchModel.results.location_dropdown) {
                        initSearchDefaultLocationAdapter.add(item);
                    }
                    initSearchDefaultLocationAdapter.notifyDataSetChanged();
                    loading.stop();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                loading.stop();
            }
        });
    }

    public void initDefaultAutocompleteDoctor() {
        loading.start();
        NetworkManager.getNetworkService(this).initSearch(((AppController) getApplication())
                .getSessionManager().getToken(), currentLanguage, new Callback<InitSearchModel>() {
            @Override
            public void success(InitSearchModel initSearchModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(initSearchModel.messages, response, SearchFilterActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        initDefaultAutocompleteDoctor();
                    }


                });
                if (isTokenValid) {
                    multiSearchResultAdapter.clear();
                    for (MultiSearchModel.Result item : initSearchModel.results.speciality_dropdown) {
                        multiSearchResultAdapter.add(item);
                    }
                    multiSearchResultAdapter.notifyDataSetChanged();
                    loading.stop();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                loading.stop();
            }
        });
    }

    public void initDefaultAutocompleteKlinik() {
        loading.start();
        NetworkManager.getNetworkService(this).initSearch(((AppController) getApplication())
                .getSessionManager().getToken(), currentLanguage, new Callback<InitSearchModel>() {
            @Override
            public void success(InitSearchModel initSearchModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(initSearchModel.messages, response, SearchFilterActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        initDefaultAutocompleteKlinik();
                    }


                });
                if (isTokenValid) {
                    multiSearchResultAdapter.clear();
                    for (MultiSearchModel.Result item : initSearchModel.results.practice_dropdown) {
                        multiSearchResultAdapter.add(item);
                    }
                    multiSearchResultAdapter.notifyDataSetChanged();
                    loading.stop();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                loading.stop();

            }
        });
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void clearLokasi(View view) {
        locationText.setText("");
        locationText.requestFocus();
    }

    public void clearspesialisasi(View view) {
        keywordText.setText("");
        keywordText.requestFocus();
    }


}