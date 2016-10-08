package com.konsula.app.ui.activity.direktori;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.service.model.PracticeReviewModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.adapter.CoverImageAdapter;
import com.konsula.app.ui.adapter.CoverImagesingleAdapter;
import com.konsula.app.ui.custom.widget.CircleIndicator;
import com.konsula.app.ui.fragment.direktori.KlinikDoctorFragment;
import com.konsula.app.ui.fragment.direktori.KlinikInfoFragment;
import com.konsula.app.ui.fragment.direktori.KlinikLayananFragment;
import com.konsula.app.ui.fragment.direktori.KlinikReviewFragment;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/14/2015.
 */
public class KlinikProfileActivity extends AppCompatActivity {
    int NUM_PAGES = 1;
    private ViewPager viewPagerHeader;
    private ViewPager viewPagerContent;
    private ImageButton backButton;

    private TabLayout tabLayoutContent;
    private RatingBar klinikRatingBar;
    private TextView tvKlinikReview;
    private TextView tvKlinikName;
    private TextView tvKlinikCategory;
    private RelativeLayout contentLayout;
    private ProgressBar progressBar;
    // private ImageView avatarImageView;
    final public static String AVATAR_IMAGE = "avatarImage";
    private String imagelink;
    private List<PracticeModel.Others> othersList;
    private TextView btn_beri_ulasan;

    private String practiceUri;
    private CircleIndicator viewPagerIndicator;
    private String practicename;
    LinearLayout layoutReview;
    private CancelableCallback cancelableCallback;
    int practiceId;
    String currentLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   Window window = getWindow();
        //  window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_klinik_profile);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        Intent intent = getIntent();
        practiceUri = intent.getStringExtra("praticeUri");
        if (practiceUri == null) practiceUri = "rumah-sakit-pondok-indah-puri-indah-kembangan";

        backButton = (ImageButton) findViewById(R.id.klinik_header_cover_back_image_button);
        // set views
        btn_beri_ulasan = (TextView) findViewById(R.id.btn_beri_ulasan);
        btn_beri_ulasan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConstant.FROM_DOCTOR = false;
                Intent intent = new Intent(KlinikProfileActivity.this, ReviewPracticeFormActivity.class);
                intent.putExtra("practice_uri", practiceUri);
                startActivity(intent);
            }
        });
        viewPagerHeader = (ViewPager) findViewById(R.id.klinik_profile_header_viewpager);
        viewPagerContent = (ViewPager) findViewById(R.id.klinik_profile_content_viewpager);
//        viewPagerContent = (ViewPager) findViewById(R.id.klinik_profile_content_viewpager);
        viewPagerIndicator = (CircleIndicator) findViewById(R.id.indicator);
        // set tablayout
        tabLayoutContent = (TabLayout) findViewById(R.id.klinik_profile_content_tab);
        klinikRatingBar = (RatingBar) findViewById(R.id.klinik_profile_rating_bar);
        tvKlinikName = (TextView) findViewById(R.id.klinik_profile_name_text_view);
        tvKlinikCategory = (TextView) findViewById(R.id.klinik_profile_category_text_view);
        tvKlinikReview = (TextView) findViewById(R.id.klinik_profile_review);
        layoutReview = (LinearLayout)findViewById(R.id.layout_review);

        layoutReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.checkConnection(getBaseContext())){
                    getpracticereview();
                }else{
                    Toast.makeText(getBaseContext(),getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }

            }
        });

        contentLayout = (RelativeLayout) findViewById(R.id.klinik_profile_content);
        progressBar = (ProgressBar) findViewById(R.id.klinik_profile_progressbar);
        // set viewPager

        // set listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        practiceProfile();

    }

    private void setupViewPagerHeader(ViewPager viewPager, List<PracticeModel.Others> resourcess) {
        CoverImageAdapter adapter = new CoverImageAdapter(getApplicationContext(), resourcess, R.layout.fragment_klinik_header_cover);
        viewPager.setAdapter(adapter);
        //buildCircles();
    }

    public void saveReview(View view) {
        Log.e("Cannot Review", "Cannot Review");
    }

    private void setupViewPagerHeadersingle(ViewPager viewPager, PracticeModel.Primary resourcess) {
        CoverImagesingleAdapter adapter = new CoverImagesingleAdapter(getApplicationContext(), resourcess);
        viewPager.setAdapter(adapter);
        //buildCircles();
    }

    private void getpracticereview(){
        cancelableCallback = new CancelableCallback<PracticeReviewModel>() {


            @Override
            protected void onSuccess(PracticeReviewModel practiceReviewModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(practiceReviewModel.messages, response,getBaseContext(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getpracticereview();
                    }

                });
                if(isTokenValid) {
                    ((AppController) getApplication()).getSessionManager().setKlinikReview(practiceReviewModel.results);

                    if (practiceReviewModel.results.review_list.size() > 0) {
                        Intent a = new Intent(getBaseContext(), KlinikReviewActivity.class);
                        a.putExtra(KlinikReviewActivity.PRACTICE_NAME,practicename);
                        a.putExtra(KlinikReviewActivity.PRACTICE_ID,practiceId);
                        startActivity(a);
                    }else {
                        AppController.getInstance().doDialog(KlinikProfileActivity.this,getResources().getString(R.string.no_review));
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {

            }
        };
        NetworkManager.getNetworkService(this).getPracticeReview(((AppController) this.getApplication()).getSessionManager().getToken(),practiceId,0, currentLanguage, cancelableCallback);

    }

    private void setupViewPagerContent(final ViewPager viewPager, PracticeModel.Results results) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new KlinikInfoFragment(results, new KlinikInfoFragment.OnKlinikInfoListener() {
            @Override
            public void onReviewClicked() {
                viewPager.setCurrentItem(2, true);
                tabLayoutContent.setScrollPosition(2, 0f, true);

            }
        }), getString(R.string.info).toUpperCase());
        adapter.addFragment(new KlinikDoctorFragment(results), getString(R.string.dokter).toUpperCase());
        adapter.addFragment(new KlinikLayananFragment(results.services), getString(R.string.layanan).toUpperCase());
        adapter.addFragment(new KlinikReviewFragment(results.practice_id, results.practice_name), getString(R.string.title_activity_doctor_review).toUpperCase());
        viewPager.setAdapter(adapter);
    }

    // set adapter view pager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void practiceProfile() {
        NetworkManager.getNetworkService(getApplicationContext()).getPracticeProfile(((AppController) getApplication()).getSessionManager().getToken(),currentLanguage, practiceUri, new Callback<PracticeModel>() {
            @Override
            public void success(PracticeModel practiceModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(practiceModel.messages, response, KlinikProfileActivity.this,new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        practiceProfile();
                    }


                });
                if (isTokenValid) {


                    if (practiceModel.results.photos != null) {
                        if (practiceModel.results.photos.others != null) {
                            practiceModel.results.photos.others.add(0, practiceModel.results.photos.primary);
                            setupViewPagerHeader(viewPagerHeader, practiceModel.results.photos.others);
                            viewPagerIndicator.setViewPager(viewPagerHeader);
                        } else {
                            setupViewPagerHeadersingle(viewPagerHeader, practiceModel.results.photos.primary);
                        }
                    }

                    btn_beri_ulasan.setVisibility(practiceModel.results.review.equals("Y") ? View.VISIBLE : View.INVISIBLE);
                    practiceId = practiceModel.results.practice_id;
                    practicename = practiceModel.results.practice_name;
                    klinikRatingBar.setRating(practiceModel.results.star_rating);
                    tvKlinikName.setText(practiceModel.results.practice_name);
                    tvKlinikCategory.setText(currentLanguage.equals("en")?practiceModel.results.category.en:practiceModel.results.category.id);
                    tvKlinikReview.setText(practiceModel.results.total_review + "");
                    imagelink = practiceModel.results.photos.primary.large_image;
                    setupViewPagerContent(viewPagerContent, practiceModel.results);
                    tabLayoutContent.setupWithViewPager(viewPagerContent);
                    for (int i = 0; i < tabLayoutContent.getTabCount(); i++) {
                        TabLayout.Tab tab = tabLayoutContent.getTabAt(i);
                        RelativeLayout relativeLayout = (RelativeLayout)
                                LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, tabLayoutContent, false);

                        TextView tabTitle = (TextView) relativeLayout.findViewById(R.id.tab);
                        tabTitle.setText(tabLayoutContent.getTabAt(i).getText().toString());
                        tab.setCustomView(relativeLayout);
                        tab.select();
                    }

                    viewPagerContent.setCurrentItem(0, false);
                    tabLayoutContent.setScrollPosition(0, 0f, true);

                    contentLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

    }

}
