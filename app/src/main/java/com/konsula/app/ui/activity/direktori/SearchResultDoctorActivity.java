package com.konsula.app.ui.activity.direktori;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.GPSTracker;
import com.konsula.app.R;
import com.konsula.app.service.model.MultiSearchModel;
import com.konsula.app.service.model.SearchDoctorModel;
import com.konsula.app.service.model.SearchDoctorSliderModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.AutoCompleteAdapter;
import com.konsula.app.ui.adapter.SearchResultDoctorAdapter;
import com.konsula.app.ui.custom.CustomtextView;
import com.konsula.app.ui.filter.MultiSearchFilter;
import com.konsula.app.ui.view.ItemLoading;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by konsula on 12/2/2015.
 */
public class SearchResultDoctorActivity extends AppCompatActivity implements AbsListView.OnScrollListener, Animation.AnimationListener, RadioGroup.OnCheckedChangeListener, View.OnFocusChangeListener, AdapterView.OnItemClickListener {
    protected TextView searchTitleTextView;
    protected TextView locationTitleTextView;
    protected FrameLayout mainContainer;
    protected LinearLayout searchFilterLinearLayout;
    protected RelativeLayout searchFilterResultRelativeLayout;
    protected AutoCompleteTextView locationText;
    protected AutoCompleteTextView keywordText;
    protected ListView resultListView;
    protected ArrayAdapter adapter;
    protected ImageButton searchImageBtn;
    protected ImageButton filterImageBtn;
    protected ItemLoading itemLoadingView;
    //    protected Animation stickyEnter;
//    protected Animation stickyExit;
    protected ArrayList<String> locationArrayList = new ArrayList<String>();
    protected ArrayList<MultiSearchModel.Result> defaultSpesialisasiArrayList = new ArrayList<MultiSearchModel.Result>();
    protected ArrayList<MultiSearchModel.Result> defaultTempatPraktekArrayList = new ArrayList<MultiSearchModel.Result>();
    protected ArrayList<String> multiSearchResultArrayList = new ArrayList<String>();
    //   protected LocationSearchFilter locationSearchFilter;
    protected MultiSearchFilter multiSearchFilter;
    // protected AutoCompleteAdapter locationAdapter;
    protected AutoCompleteAdapter multiSearchResultAdapter;
    protected RadioGroup kategoriRadioGroup;
    protected String country;
    protected boolean from_clinic = false;
    protected String keyword;
    //protected String sortBy = SearchFilterCriteriaActivity.SORT_BY_EXPERIENCE_DESC;
    protected String sortBy = "";
    protected int minRate = 0;
    protected int maxRate;
    private int minExp = 0;
    private int maxExp;
    protected int minOps = 0;
    protected int maxOps;
    protected String minHour = "00.00";
    protected String maxHour = "23.59";
    protected String days = "";
    protected boolean isLoading;
    protected int loadedPage = 1;
    protected int initialHeight = 0;
    protected ImageButton searchBtn;
    protected AppController appController;
    protected String locationState;
    protected String locality;
    private ImageButton backButton;
    protected double latitude, longitude;
    private CustomtextView tvDokter, tvKlinik;
    protected ProgressDialog dialog;
    protected GPSTracker gpsTracker;
    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTracker = new GPSTracker(this);
        appController = new AppController();
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";

        // set content view
        setContentView(R.layout.activity_search_result);

        searchBtn = (ImageButton) findViewById(R.id.search_result_image_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchResultDoctorActivity.this, SearchFilterActivity.class);
                //i.putExtra("location", locationTitleTextView.getText().toString());
                i.putExtra("title", searchTitleTextView.getText().toString());
                i.putExtra("from_result", true);
                startActivity(i);
            }
        });
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        init();
        initAttribute();

    }


    protected void init() {
        dialog = AppController.createProgressDialog(SearchResultDoctorActivity.this);
        backButton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                adapter.clear();
            }
        });
        searchTitleTextView = (TextView) findViewById(R.id.search_title_text_view);
        locationTitleTextView = (TextView) findViewById(R.id.location_title_text_view);
        mainContainer = (FrameLayout) findViewById(R.id.search_filter_main_frame_layout);
        searchFilterLinearLayout = (LinearLayout) findViewById(R.id.search_filter_linear_layout);
        searchFilterResultRelativeLayout = (RelativeLayout) findViewById(R.id.search_filter_result_relative_layout);
        resultListView = (ListView) findViewById(R.id.search_result_list_view);
        adapter = new SearchResultDoctorAdapter(this);
        itemLoadingView = new ItemLoading(this);
//        stickyEnter = AnimationUtils.loadAnimation(this, R.anim.sticky_enter);
//        stickyExit = AnimationUtils.loadAnimation(this, R.anim.sticky_exit);
        locationText = (AutoCompleteTextView) findViewById(R.id.search_filter_doctor_location_box_edit_text);
        //  locationAdapter = new AutoCompleteAdapter(this, R.layout.item_location_autocomplete, R.id.item_location_text_view, locationArrayList);
        //   locationSearchFilter = new LocationSearchFilter(this, locationArrayList);
        multiSearchFilter = new MultiSearchFilter(this, multiSearchResultArrayList);
        keywordText = (AutoCompleteTextView) findViewById(R.id.search_filter_doctor_spesialisasi_box_edit_text);
        kategoriRadioGroup = (RadioGroup) findViewById(R.id.radio_search_filter);
        multiSearchResultAdapter = new AutoCompleteAdapter(this, R.layout.item_location_autocomplete, R.id.item_location_text_view, multiSearchResultArrayList);
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
    }

    protected void initAttribute() {
        resultListView.setAdapter(adapter);
        resultListView.setOnScrollListener(this);
        resultListView.addFooterView(itemLoadingView);


//        stickyEnter.setAnimationListener(this);
//        stickyExit.setAnimationListener(this);

        //  locationAdapter.setFilter(locationSearchFilter);
        // locationText.setAdapter(locationAdapter);
        locationText.setThreshold(1);
        locationText.setOnFocusChangeListener(this);


        multiSearchResultAdapter.setFilter(multiSearchFilter);
        keywordText.setAdapter(multiSearchResultAdapter);
        keywordText.setThreshold(1);
        keywordText.setOnItemClickListener(this);
        keywordText.setOnFocusChangeListener(this);

        kategoriRadioGroup.check(R.id.radio_search_filter_doctor);
        kategoriRadioGroup.setOnCheckedChangeListener(this);

        if (getIntent().getExtras() != null) {
            locationText.setText(getIntent().getStringExtra("location_text"));
            keywordText.setText(getIntent().getStringExtra("keyword_name"));

            if (getIntent().getStringExtra("location_text").equals(getResources().getString(R.string.currentLoc))) {
                if (gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();
                } else {
                    appController.doDialog(SearchResultDoctorActivity.this, "Cannot get current location, please activate your GPS.");
                }
            }
            keyword = getIntent().getStringExtra("keyword");
            country = getIntent().getStringExtra("country");
            locationState = getIntent().getStringExtra("location_state");
            locality = getIntent().getStringExtra("locality");

            searchTitleTextView.setSelected(true);
            locationTitleTextView.setSelected(true);

            loadItem();
        }
    }

    protected void loadItem() {
        loadItem(false);
    }

    protected void loadItem(final boolean afterSearch) {
        if (!isLoading) {
            isLoading = true;
            if (loadedPage == 1) {
                dialog.show();
            } else {
                itemLoadingView.start();
            }

            getSearchDoc(afterSearch);
        }
    }

    public void search(View view) {
        /*searchFilterLinearLayout.setVisibility(View.VISIBLE);
        searchFilterLinearLayout.startAnimation(stickyEnter);
        searchFilterLinearLayout.post(new Runnable() {
            @Override
            public void run() {
                searchFilterResultRelativeLayout.animate()
                        .translationY(searchFilterLinearLayout.getMeasuredHeight())
                        .setDuration(stickyEnter.getDuration())
                        .setInterpolator(new DecelerateInterpolator());
            }
        });*/
    }

    public void filter(View view) {
        Intent intent = new Intent(this, SearchFilterCriteriaActivity.class);
        intent.putExtra("type", getClass().getName());
        intent.putExtra("sort_by", sortBy);
        intent.putExtra("min_rate", minRate);
        intent.putExtra("max_rate", maxRate);
        intent.putExtra("min_hour", minHour);
        intent.putExtra("max_hour", maxHour);
        intent.putExtra("min_exp", minExp);
        intent.putExtra("max_exp", maxExp);
        intent.putExtra("min_ops", minOps);
        intent.putExtra("max_ops", maxOps);
        intent.putExtra("days", days);
        if (from_clinic) intent.putExtra("from_clinic", true);
        startActivityForResult(intent, 0);
    }

    public void closeSearch(View view) {
        //    searchFilterLinearLayout.startAnimation(stickyExit);
//        searchFilterLinearLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                searchFilterResultRelativeLayout.animate()
//                        .translationY(Converter.dpToPx(SearchResultDoctorActivity.this, 52))
//                        .setDuration(stickyEnter.getDuration())
//                        .setInterpolator(new DecelerateInterpolator());
//            }
//        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            loadItem();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) searchFilterResultRelativeLayout.getLayoutParams();
//        if(animation == stickyExit){
//            mainContainer.post(new Runnable() {
//                @Override
//                public void run() {
//                    params.height = initialHeight;
//                    searchFilterResultRelativeLayout.requestLayout();
//                }
//            });
//        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) searchFilterResultRelativeLayout.getLayoutParams();
//        if(animation == stickyExit){
//            searchFilterLinearLayout.setVisibility(View.GONE);
//            searchFilterResultRelativeLayout.setTranslationY(Converter.dpToPx(this, 52));
//        }else if(animation == stickyEnter){
//            searchFilterResultRelativeLayout.setTranslationY(searchFilterLinearLayout.getHeight());
//            if(initialHeight == 0){
//                initialHeight = searchFilterResultRelativeLayout.getHeight();
//            }
//            mainContainer.post(new Runnable() {
//                @Override
//                public void run() {
//                    params.height = mainContainer.getHeight() - searchFilterLinearLayout.getHeight();
//                }
//            });
//            locationSearchFilter.showDefaultList();
//            keywordText.requestFocus();
//        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup == kategoriRadioGroup) {
            if (i == R.id.radio_search_filter_doctor) {
                keywordText.setHint("Spesialisasi");
                multiSearchFilter.setSearchType(MultiSearchModel.SEARCH_TYPE_DOCTOR);
            } else if (i == R.id.radio_search_filter_klinik) {
                keywordText.setHint("Tempat Praktek");
                multiSearchFilter.setSearchType(MultiSearchModel.SEARCH_TYPE_PRACTICE);
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (view == locationText) {
            if (b && locationText.getText().toString().equals("")) {
                locationText.showDropDown();
            }
        } else if (view == keywordText) {
            if (b) {
                onCheckedChanged(kategoriRadioGroup, kategoriRadioGroup.getCheckedRadioButtonId());
                multiSearchFilter.setLocation(locationText.getText().toString());
                if (keywordText.getText().toString().equals("")) {
                    showDefaultSearchList();
                }
            }
        }
    }

    public void showDefaultSearchList() {
        multiSearchResultArrayList.clear();
        if (kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_doctor) {
            for (MultiSearchModel.Result item : defaultSpesialisasiArrayList) {
                multiSearchResultArrayList.add(item.bahasa_name);
            }
        } else if (kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_klinik) {
            for (MultiSearchModel.Result item : defaultTempatPraktekArrayList) {
                multiSearchResultArrayList.add(item.bahasa_name);
            }
        }
        multiSearchFilter.setDefaultList(multiSearchResultArrayList);
        keywordText.showDropDown();
    }

    @Override
    public final void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        keyword = multiSearchResultArrayList.get(i).toLowerCase().replace(" ", "-");
        if (kategoriRadioGroup.getCheckedRadioButtonId() == (getClass() == SearchResultDoctorActivity.class ? R.id.radio_search_filter_doctor : R.id.radio_search_filter_klinik)) {
            loadedPage = 1;
            isLoading = false;
            loadItem(true);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } else {
            Intent intent = new Intent(this, (getClass() == SearchResultDoctorActivity.class ? SearchResultKlinikActivity.class : SearchResultDoctorActivity.class));
            intent.putExtra("location_state", locationState);
            intent.putExtra("location_text", locationText.getText().toString());
            intent.putExtra("keyword", keyword);
            intent.putExtra("keyword_name", keywordText.getText().toString());
            intent.putExtra("country", "Indonesia");
            startActivity(intent);
            finish();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            sortBy = data.getStringExtra("sort_by");
            minRate = data.getIntExtra("min_rate", 0);
            maxRate = data.getIntExtra("max_rate", SearchFilterCriteriaActivity.MAX_RATE);
            minHour = data.getStringExtra("min_hour");
            maxHour = data.getStringExtra("max_hour");
            minExp = data.getIntExtra("min_exp", 0);
            maxExp = data.getIntExtra("max_exp", SearchFilterCriteriaActivity.MAX_EXP);
            minOps = data.getIntExtra("min_ops", 0);
            maxOps = data.getIntExtra("max_ops", SearchFilterCriteriaActivity.MAX_EXP);
            days = data.getStringExtra("days");
            loadedPage = 1;
            isLoading = false;
            loadItem();
        }
    }

    public void clearLokasi(View view) {
        locationText.setText("");
        locationText.requestFocus();
    }


    private void getSearchDoc(final boolean afterSearch) {
        NetworkManager.getNetworkService(this).searchDoctor(((AppController) getApplication()).getSessionManager().getToken(), locationState, locality, keyword, country, latitude, longitude, loadedPage, sortBy, minRate, maxRate, minHour, maxHour.equals("23.59") ? "" : maxHour, minExp, maxExp, days, new Callback<SearchDoctorModel>() {
            @Override
            public void success(SearchDoctorModel searchDoctorModel, retrofit.client.Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(searchDoctorModel.messages, response, SearchResultDoctorActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getSearchDoc(afterSearch);
                    }


                });
                if (isTokenValid) {

                    String current = getResources().getConfiguration().locale.getLanguage();
                    try {
                        if (current.equals("en") || current.equals("EN")) {
                            searchTitleTextView.setText("" + searchDoctorModel.results.label.keyword.en);
                        } else {
                            searchTitleTextView.setText("" + searchDoctorModel.results.label.keyword.id);
                        }
                    } catch (Exception e) {

                    }

                    locationTitleTextView.setText(getIntent().getStringExtra("location_text"));

//                    if(loadedPage == 1) {
//
//                    }else{
                    dialog.dismiss();
//                    }

                    try {
                        if (loadedPage == 1) {
                            Log.d("adater", "Clear");
                            adapter.clear();

                            if (searchDoctorModel.results.doctors == null) {
                                DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder2 = new AlertDialog.Builder(SearchResultDoctorActivity.this);
                                builder2.setMessage("Sorry, no available search result data doctor.").setPositiveButton("Dismiss", dialogClickListener2).show();
                            }
                        }
                        if (searchDoctorModel.results.doctors != null) {
                            //searchDoctorModel.results.doctors
                            adapter.addAll(SearchDoctorSliderModel.convertDoctorsToSliders(searchDoctorModel.results.doctors));
                            isLoading = false;
                            loadedPage++;
                            if (afterSearch) {
                                closeSearch(null);
                            }

                            if (loadedPage == 1 && latitude != 0.0 && locationState.equals("")) {
                                appController.doDialog(SearchResultDoctorActivity.this, "GPS Location is off");
                                dialog.dismiss();
                            }

                        } else {
                            if (loadedPage == 1) {
                                dialog.dismiss();
                            } else {
                                resultListView.removeFooterView(itemLoadingView);
                            }
                        }
                    } catch (Exception ex) {
                        dialog.dismiss();
                        Log.e("error", ex.getMessage());
                    }
                    itemLoadingView.stop();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("error", error.getMessage());
                dialog.dismiss();
                itemLoadingView.stop();
            }
        });
    }


}