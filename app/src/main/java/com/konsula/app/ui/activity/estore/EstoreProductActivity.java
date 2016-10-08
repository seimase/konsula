package com.konsula.app.ui.activity.estore;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreProductModel;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.direktori.GoogleMapActivity;
import com.konsula.app.ui.activity.direktori.KlinikProfileActivity;
import com.konsula.app.ui.adapter.CoverImageAdapter;
import com.konsula.app.ui.adapter.EstoreRelatedProductAdapter;
import com.konsula.app.ui.custom.widget.CircleIndicator;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EstoreProductActivity extends AppCompatActivity {

    public static String IDENTITY_URI = "identity_uri";
    RelativeLayout descriptionLayout;
    RelativeLayout reviewLayout;
    ImageView descriptionArrow;
    ImageView reviewArrow;
    LinearLayout reviewLinearLayout;
    TextView descriptionText;
    ImageButton backButton;
    TextView seeMapTxt;

    TextView productNameTxt;
    TextView productPriceTxt;
    TextView productGimmickPriceTxt;
    TextView productExpiredTxt;
    TextView productPracticeNameTxt;
    WebView productHowToTxt;
    TextView productPeriodTxt;
    LinearLayout productPackageContainer;
    RatingBar productPracticeRatingBar;
    LinearLayout descriptionContainer;
    LinearLayout contactContainer;
    LinearLayout contactTextView;

    String currentLanguage;
    String identity_uri;
    boolean isDescriptionSpan = true;
    boolean isReviewSpan = true;
    ViewPager viewPagerHeader;
    CircleIndicator viewPagerIndicator;
    ProgressBar progressBar;
    RelativeLayout productContent,refreshLayout;
    private TextView btnShare;
    private static EstoreProductModel.Data data;

    ImageButton searchBtn;
    Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estore_product);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";

        Intent intent = getIntent();
        identity_uri = intent.getStringExtra(IDENTITY_URI);

        initComponents();
        initListeners();
        getProductDetail();
    }

    private void getProductDetail() {
        refreshLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        productContent.setVisibility(View.GONE);
        NetworkManager.getNetworkService(getApplicationContext())
                .getProductDetail(
                        ((AppController) getApplicationContext()).getSessionManager().getToken(),currentLanguage, identity_uri, new Callback<EstoreProductModel>() {
                            @Override
                            public void success(final EstoreProductModel estoreProductModel, Response response) {
                                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(estoreProductModel.messages, response, EstoreProductActivity.this, new RefreshTokenCallback() {
                                    @Override
                                    public void onRefreshTokenComplete() {
                                        getProductDetail();
                                    }

                                });

                                if (isTokenValid) {
                                    progressBar.setVisibility(View.GONE);
                                    if (estoreProductModel.messages.response_code == 200) {
                                        if (estoreProductModel.results != null) {
                                            data = estoreProductModel.results.data;

                                            Bundle params = new Bundle();
                                            params.putString("content_ids", String.valueOf(data.product_id));
                                            params.putString("content_type", "product");
                                            params.putString("content_name", data.product_name);
                                            params.putString("value", String.valueOf(data.sell_price));
                                            params.putString("currency","IDR");

                                            ((AppController)getApplication()).setFacebookEvent("ViewContent", params);
                                            productContent.setVisibility(View.VISIBLE);
                                            descriptionText.setText(Html.fromHtml(estoreProductModel.results.data.description));
                                            productNameTxt.setText(estoreProductModel.results.data.product_name);
                                            if (estoreProductModel.results.data.sell_price != estoreProductModel.results.data.gimmick_price) {
                                                productPriceTxt.setText(((AppController) getApplicationContext()).getDefaultPriceFormat("IDR", Double.parseDouble(estoreProductModel.results.data.sell_price + "")));
                                                productGimmickPriceTxt.setText(((AppController) getApplicationContext()).getDefaultPriceFormat("IDR", Double.parseDouble(estoreProductModel.results.data.gimmick_price + "")));

                                            } else {
                                                productPriceTxt.setText(((AppController) getApplicationContext()).getDefaultPriceFormat("IDR", Double.parseDouble(estoreProductModel.results.data.sell_price + "")));
                                                productPriceTxt.setTextColor(getResources().getColor(R.color.black));
                                                productGimmickPriceTxt.setText(((AppController) getApplicationContext()).getDefaultPriceFormat("IDR", Double.parseDouble(estoreProductModel.results.data.gimmick_price + "")));
                                                productGimmickPriceTxt.setVisibility(View.GONE);
                                            }
                                            productExpiredTxt.setText(getString(R.string.estore_expired_product).replace("{datetime}", ((AppController) getApplicationContext()).datehistory(estoreProductModel.results.data.expiry_date)));
                                            productPracticeNameTxt.setText(estoreProductModel.results.data.practice.practice_name);
                                            productPracticeNameTxt.setPaintFlags(productPracticeNameTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                            productPracticeNameTxt.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (AppController.checkConnection(getBaseContext())){
                                                        //open direktori
                                                        Intent intent = new Intent(getApplicationContext(), KlinikProfileActivity.class);
                                                        intent.putExtra("praticeUri", estoreProductModel.results.data.practice.identity_uri);
                                                        startActivity(intent);
                                                    }else{
                                                        Toast.makeText(EstoreProductActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                                                    }


                                                }
                                            });
                                            seeMapTxt.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (AppController.checkConnection(getBaseContext())){
                                                        Intent i = new Intent(getApplicationContext(), GoogleMapActivity.class);
                                                        i.putExtra("longitude", Double.parseDouble(estoreProductModel.results.data.practice.longitude));
                                                        i.putExtra("latitude", Double.parseDouble(estoreProductModel.results.data.practice.latitude));
                                                        i.putExtra("title", estoreProductModel.results.data.practice.practice_name);
                                                        startActivity(i);
                                                    }else{
                                                        Toast.makeText(EstoreProductActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });
                                            if (estoreProductModel.results.data.products_items != null)
                                                for (int i = 0; i < estoreProductModel.results.data.products_items.size(); i++) {
                                                    View v = getLayoutInflater().inflate(R.layout.item_product_items, null, false);
                                                    TextView textView = (TextView) v.findViewById(R.id.item_estore_product_name);
                                                    textView.setText((i + 1) + ". " +
                                                            estoreProductModel.results.data.products_items.get(i).item_name +
                                                            ": " + ((AppController) getApplicationContext()).getDefaultPriceFormat("IDR", Double.parseDouble(estoreProductModel.results.data.products_items.get(i).sell_price + "")));
                                                    productPackageContainer.addView(v);
                                                }

                                             if(estoreProductModel.results.data.practice.contact==null){
                                                 contactContainer.setVisibility(View.GONE);
                                             }else {
                                                 for (int i = 0; i < estoreProductModel.results.data.practice.contact.size(); i++) {
                                                     View contactItem = getLayoutInflater().inflate(R.layout.item_estore_contact_detail, null, false);
                                                     TextView TextViewcontact = (TextView) contactItem.findViewById(R.id.tv_estore_contact);
                                                     TextViewcontact.setText(estoreProductModel.results.data.practice.contact.get(i));
                                                     contactTextView.addView(contactItem);
                                                 }
                                             }



                                            WebSettings webSettings = productHowToTxt.getSettings();
                                            Resources res = getResources();
                                            float fontSize = res.getDimension(R.dimen.text_size_webview);
                                            webSettings.setDefaultFontSize((int)fontSize);

                                            productPracticeRatingBar.setRating(Float.parseFloat(estoreProductModel.results.data.practice.rating_score));
                                            productHowToTxt.loadDataWithBaseURL(null, estoreProductModel.results.data.howto, "text/html", "utf-8", null);
                                            productPeriodTxt.setText(getString(R.string.estore_validity_product).replace("{days}", estoreProductModel.results.data.valid_days + ""));
                                            initRecycler(estoreProductModel.results.data.practice_review);
                                            if (estoreProductModel.results.data.similar_product != null)
                                                initProductRelatedRecycler((ArrayList<EstoreProductModel.SimilarProduct>) estoreProductModel.results.data.similar_product);
                                            if (estoreProductModel.results.data.product_photo != null) {
                                                setupViewPagerHeader(viewPagerHeader, estoreProductModel.results.data.product_photo);
                                            }
                                        }
                                    } else {
                                        Toast.makeText(EstoreProductActivity.this, estoreProductModel.messages.response_text, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.d("err", error.getMessage());
                                Toast.makeText(EstoreProductActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                                //finish();

                                progressBar.setVisibility(View.GONE);
                                refreshLayout.setVisibility(View.VISIBLE);
                            }
                        });
    }

    private void initComponents() {
        seeMapTxt = (TextView) findViewById(R.id.estore_product_see_map);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        productContent = (RelativeLayout) findViewById(R.id.estore_product_content);
        btnShare = (TextView) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.checkConnection(getBaseContext())){
                    mailto();
                }else{
                    Toast.makeText(EstoreProductActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                }

            }
        });
        viewPagerHeader = (ViewPager) findViewById(R.id.estore_product_header_viewpager);
        viewPagerIndicator = (CircleIndicator) findViewById(R.id.indicator);

        backButton = (ImageButton) findViewById(R.id.estore_product_back_button);
        descriptionLayout = (RelativeLayout) findViewById(R.id.estore_product_description_layout);
        reviewLayout = (RelativeLayout) findViewById(R.id.estore_product_review_layout);
        descriptionArrow = (ImageView) findViewById(R.id.estore_product_description_arrow);
        reviewArrow = (ImageView) findViewById(R.id.estore_product_review_arrow);
        descriptionText = (TextView) findViewById(R.id.estore_product_description_text);
        descriptionContainer = (LinearLayout) findViewById(R.id.estore_product_description_container);
        reviewLinearLayout = (LinearLayout) findViewById(R.id.estore_product_review_container);
        productPackageContainer = (LinearLayout) findViewById(R.id.estore_product_package_container);

        productNameTxt = (TextView) findViewById(R.id.estore_product_name);
        productPriceTxt = (TextView) findViewById(R.id.estore_product_price);
        productGimmickPriceTxt = (TextView) findViewById(R.id.estore_product_gimmick_price);
        productGimmickPriceTxt.setPaintFlags(productGimmickPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        productExpiredTxt = (TextView) findViewById(R.id.estore_product_expire);
        productPracticeNameTxt = (TextView) findViewById(R.id.estore_product_practice_name);
        productPracticeRatingBar = (RatingBar) findViewById(R.id.estore_product_rating_bar);
        productHowToTxt = (WebView) findViewById(R.id.estore_product_howto);
        productPeriodTxt = (TextView) findViewById(R.id.estore_product_period);
        contactContainer =(LinearLayout) findViewById(R.id.estore_contact_description_layout);
        contactTextView =(LinearLayout) findViewById(R.id.estore_contact_detail_layout);

        btnRefresh = (Button)findViewById(R.id.btn_refresh);
        refreshLayout = (RelativeLayout)findViewById(R.id.estore_main_refresh);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.checkConnection(getBaseContext())){
                    progressBar.setVisibility(View.VISIBLE);
                    getProductDetail();
                }else{
                    Toast.makeText(EstoreProductActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void setupViewPagerHeader(ViewPager viewPager, List<EstoreProductModel.ProductPhoto> resources) {
        ArrayList<PracticeModel.Others> coverImagesList = new ArrayList<>();
        for (EstoreProductModel.ProductPhoto data : resources) {
            PracticeModel model = new PracticeModel();
            PracticeModel.Others temp = model.new Others();
            temp.medium_image = data.url;
            coverImagesList.add(temp);
        }
        CoverImageAdapter adapter = new CoverImageAdapter(getApplicationContext(), coverImagesList, R.layout.item_estore_photos);
        viewPager.setAdapter(adapter);
        viewPagerIndicator.setViewPager(viewPagerHeader);
    }


    private void initListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        descriptionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDescriptionSpan) {
                    descriptionContainer.setVisibility(View.GONE);
                    descriptionArrow.setImageResource(R.drawable.arrow_right_grey);
                    isDescriptionSpan = false;
                } else {
                    descriptionContainer.setVisibility(View.VISIBLE);
                    descriptionArrow.setImageResource(R.drawable.arrow_down_grey);
                    isDescriptionSpan = true;
                }
            }
        });

        reviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReviewSpan) {
                    reviewLinearLayout.setVisibility(View.GONE);
                    reviewArrow.setImageResource(R.drawable.arrow_right_grey);
                    isReviewSpan = false;

                } else {
                    reviewLinearLayout.setVisibility(View.VISIBLE);
                    reviewArrow.setImageResource(R.drawable.arrow_down_grey);
                    isReviewSpan = true;
                }
            }
        });
    }

    private void initRecycler(List<EstoreProductModel.PracticeReview> reviewData) {
        if (reviewData != null) {
            for (int i = 0; i < reviewData.size(); i++) {
                View v = getLayoutInflater().inflate(R.layout.item_review_product, null, false);
                TextView dateTextView = (TextView) v.findViewById(R.id.item_review_date_text_view);
                TextView nameTextView = (TextView) v.findViewById(R.id.item_review_name_text_view);
                TextView messageTextView = (TextView) v.findViewById(R.id.item_review_message_text_view);
                RatingBar ratingBar = (RatingBar) v.findViewById(R.id.item_result_detail_profile_total_rating);
                dateTextView.setText(reviewData.get(i).timestamp);
                nameTextView.setText(reviewData.get(i).reviewer_name);
                messageTextView.setText(reviewData.get(i).feedback);
                ratingBar.setRating(reviewData.get(i).point_total);
                reviewLinearLayout.addView(v);

            }
        } else {
            View v = getLayoutInflater().inflate(R.layout.item_dp_general, null, false);
            TextView tv = (TextView) v.findViewById(R.id.tv_dp_general);
            tv.setText(getString(R.string.no_review));
            reviewLinearLayout.addView(v);
        }
    }


    private void initProductRelatedRecycler(ArrayList<EstoreProductModel.SimilarProduct> similarProductsList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.estore_related_product_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new EstoreRelatedProductAdapter(getApplicationContext(), similarProductsList));

    }

    public void buy(View view) {
        if (AppController.checkConnection(this)){
            Bundle params = new Bundle();
            params.putString("content_ids", String.valueOf(data.product_id));
            params.putString("content_type", "product");
            params.putString("content_name", data.product_name);
            params.putString("value", String.valueOf(data.sell_price));
            params.putString("currency","IDR");

            ((AppController)getApplication()).setFacebookEvent("AddToCart", params);

            Intent intent = new Intent(this, EstoreTransactionActivity.class);
            intent.putExtra(EstoreTransactionActivity.TAG_PRODUCT_UUID, data.product_uuid);
            startActivity(intent);
        }else{
            Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

    }

    public void mailto() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.setPackage("com.google.android.gm");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"cs@konsula.com"});
        /*intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");*/
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    public static EstoreProductModel.Data getData() {
        return data;
    }

}
