package com.konsula.app.ui.activity.estore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.GPSTracker;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreCategoryTagModel;
import com.konsula.app.service.model.EstoreSearchProductModel;
import com.konsula.app.service.model.GeneralCheckModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.ViewPagerAdapter;
import com.konsula.app.util.RefreshTokenCallback;

import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EstoreCategoryActivity extends AppCompatActivity {
    ImageButton backButton;
    ImageButton filterButton;
    TabLayout tabLayout;
    ViewPager viewPager;
    EditText searchEditText;
    TextView pageTitleTextView;
    String category_uri = null;
    String currentLanguage;
    String keyword;

    EstorePopularFragment popularFragment;
    EstoreNearestFragment nearestFragment;

    private GPSTracker gpsTracker;

    private int popularLastPage = 0;
    private int nearestLastPage = 0;
    private final int DEFAULT_MAX_PRICE = 15000000;
    private final int DEFAULT_MIN_PRICE = 50000;
    private final int FILTER_REQUEST_CODE = 0;
    private final String SORT_BY_POPULAR = "populars";
    private final String SORT_BY_NEAREST = "nearest";
    public static final String TAG_KEYWORD = "keyword";
    public static final String TAG_FROM_USER_SEARCH = "USER_SEARCH";
    private Integer max_price = null;
    private Integer min_price = null;
    private String tags = "";
    private String tag_id = "";
    public static final String CATEGORY_URI = "category_uri";
    public static final String TITLE = "title";
    private String pageTitle = "";
    private String hint;
    private Boolean isfromsearchuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estore_category);

        Intent intent = getIntent();
        category_uri = intent.getStringExtra(CATEGORY_URI);
        pageTitle = WordUtils.capitalize(intent.getStringExtra(TITLE));
        keyword = (intent.getStringExtra(TAG_KEYWORD));
        isfromsearchuser = (intent.getBooleanExtra(TAG_FROM_USER_SEARCH, false));
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";

        initComponents();
        initListeners();
        setupViewPagerContent(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        initCustomTabLayout();
        getCategoryTag(category_uri);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                this.tags = data.getStringExtra("tags");
                this.tag_id = data.getStringExtra("tag_id");
                this.min_price = data.getIntExtra("min_price", DEFAULT_MIN_PRICE);
                this.max_price = data.getIntExtra("max_price", DEFAULT_MAX_PRICE);
                searchEditText.setText(isfromsearchuser ? keyword : "");
            } else {

            }
        }

    }

    private void initComponents() {
        gpsTracker = new GPSTracker(this);
        pageTitleTextView = (TextView) findViewById(R.id.estore_title);
        backButton = (ImageButton) findViewById(R.id.estore_back_image_button);
        searchEditText = (EditText) findViewById(R.id.estore_category_search);
        tabLayout = (TabLayout) findViewById(R.id.estore_category_tab);
        viewPager = (ViewPager) findViewById(R.id.estore_category_content_viewpager);
        filterButton = (ImageButton) findViewById(R.id.estore_filter_button);
        searchEditText.setText(isfromsearchuser ? keyword : "");
        pageTitleTextView.setText(pageTitle);
        if (currentLanguage.equalsIgnoreCase("en")) {
            hint = "Search " + pageTitle.toLowerCase() + " packages & services";
            searchEditText.setHint(hint);
        } else {
            hint = "Cari paket & layanan " + pageTitle.toLowerCase();
            searchEditText.setHint(hint);
        }


    }

    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (viewPager.getCurrentItem() == 0)
                    getSearchProduct(1, category_uri, s.toString(), SORT_BY_POPULAR, true);
                else
                    getSearchProduct(1, category_uri, s.toString(), SORT_BY_NEAREST, true);
            }
        });

        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (viewPager.getCurrentItem() == 0)
                        getSearchProduct(1, category_uri, searchEditText.getText().toString(), SORT_BY_POPULAR, true);
                    else
                        getSearchProduct(1, category_uri, searchEditText.getText().toString(), SORT_BY_NEAREST, true);
                    return true;
                }
                return false;
            }
        });

//        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    searchEditText.setHint("");
//                    searchEditText.requestFocus();
//                } else {
//                    searchEditText.setHint(hint);
//                }
//            }
//        });


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterButton.setClickable(false);
                ((AppController) getApplication()).setMixpanel("Open Filter Page");
                Intent a = new Intent(EstoreCategoryActivity.this, EstoreFilterActivity.class);
                a.putExtra(EstoreFilterActivity.MAX_RATE, max_price);
                a.putExtra(EstoreFilterActivity.MIN_RATE, min_price);
                a.putExtra(EstoreFilterActivity.SELECTED_TAG_ID, tag_id);
                startActivityForResult(a, FILTER_REQUEST_CODE);
                filterButton.setClickable(true);
            }
        });
    }

    private void initCustomTabLayout() {
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (i == 0) {
                RelativeLayout relativeLayout = (RelativeLayout)
                        LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_estore_left, tabLayout, false);

                TextView tabTitle = (TextView) relativeLayout.findViewById(R.id.tab);
                tabTitle.setText(tabLayout.getTabAt(i).getText().toString());
                tab.setCustomView(relativeLayout);
                tab.select();
            } else {
                RelativeLayout relativeLayout = (RelativeLayout)
                        LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab_estore_right, tabLayout, false);

                TextView tabTitle = (TextView) relativeLayout.findViewById(R.id.tab);
                tabTitle.setText(tabLayout.getTabAt(i).getText().toString());
                tab.setCustomView(relativeLayout);
                tab.select();
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getSearchProduct(1, category_uri, keyword, SORT_BY_POPULAR, true);
                } else {
                    getSearchProduct(1, category_uri, keyword, SORT_BY_NEAREST, true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
    }

    private void getCategoryTag(String category_uri) {
        NetworkManager.getNetworkService(getApplicationContext()).getCategoryTag(((AppController) getApplicationContext()).getSessionManager().getToken(), category_uri, new Callback<EstoreCategoryTagModel>() {
            @Override
            public void success(EstoreCategoryTagModel estoreCategoryTagModel, Response response) {
                ((AppController) getApplicationContext()).getSessionManager().setEstoreCategoryTags(estoreCategoryTagModel);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(EstoreCategoryActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
    }

    private void getSearchProduct(final int page, final String category_uri, final String keyword, final String sort, final boolean onType) {

        Map<String, String> params = new HashMap<>();
        params.put("category_uri", category_uri);
        params.put("sort_by", sort);
        params.put("limit", 6 + "");
        params.put("page", page + "");
        params.put("max_price", max_price + "");
        params.put("min_price", min_price + "");
        params.put("tags", tag_id);

        if (viewPager.getCurrentItem() == 1) {
            if (gpsTracker.canGetLocation()) {
                params.put("latitude", gpsTracker.getLatitude() + "");
                params.put("longitude", gpsTracker.getLongitude() + "");
            }
        }
        params.put("keywords", keyword);

        NetworkManager.getNetworkService(getApplicationContext())
                .getSearchProduct(
                        ((AppController) getApplicationContext()).getSessionManager().getToken(),
                        params,
                        new Callback<EstoreSearchProductModel>() {
                            @Override
                            public void success(EstoreSearchProductModel estoreSearchProductModel, Response response) {
                                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(estoreSearchProductModel.messages, response, EstoreCategoryActivity.this, new RefreshTokenCallback() {
                                    @Override
                                    public void onRefreshTokenComplete() {
                                        getSearchProduct(page, category_uri, keyword, sort, onType);
                                    }

                                });

                                if (isTokenValid)

                                {

                                    if (viewPager.getCurrentItem() == 0) {
                                        if (estoreSearchProductModel.results!=null){
                                            //refresh popular
                                            if (onType) popularFragment.clearItems();
                                            popularFragment.refreshItems(estoreSearchProductModel.results.items);
                                            popularLastPage = estoreSearchProductModel._meta.total_page;
                                        }else {
                                            popularFragment.nodata();
                                        }
                                    } else {

                                        if (onType) nearestFragment.clearItems();
                                        if (estoreSearchProductModel.results!=null){
                                            //refresh nearest
                                            nearestFragment.refreshItems(estoreSearchProductModel.results.items, estoreSearchProductModel.results.maps);
                                            nearestLastPage = estoreSearchProductModel._meta.total_page;

                                        }else {
                                            nearestFragment.nodata();
                                        }
                                        if (page==1)nearestFragment.settoTop();
                                    }
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                error.printStackTrace();
                                Toast.makeText(EstoreCategoryActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                );

    }

    private void setupViewPagerContent(ViewPager viewPager) {

        nearestFragment = new EstoreNearestFragment();
        nearestFragment.setListener(new EstoreNearestFragment.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int currentPage) {
                if (currentPage <= nearestLastPage)
                    getSearchProduct(currentPage, category_uri, searchEditText.getText().toString(), SORT_BY_NEAREST, false);
            }
        });
        popularFragment = new EstorePopularFragment();
        popularFragment.setListener(new EstorePopularFragment.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int currentPage) {
                if (currentPage <= popularLastPage)
                    getSearchProduct(currentPage, category_uri, searchEditText.getText().toString(), SORT_BY_POPULAR, false);
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(popularFragment, getString(R.string.estore_tab_popular));
        adapter.addFragment(nearestFragment, getString(R.string.estore_tab_nearest));
        viewPager.setAdapter(adapter);

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
