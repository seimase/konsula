package com.konsula.app.ui.activity.direktori;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.ui.fragment.direktori.SearchMenuDoctorFragment;
import com.konsula.app.ui.fragment.direktori.SearchMenuKlinikFragment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by Konsula on 25/02/2016.
 */
public class DirektoriActivity extends FragmentActivity implements SearchMenuDoctorFragment.OnSuccessLoadMenuListener {

    private RelativeLayout boxSearch;
    private ViewPager viewPagerContent;
    private TabLayout tabLayoutContent;
    private ImageButton backbutton;
    private ImageView searchButton;

    private SearchMenuDoctorFragment searchMenuDoctorFragment;
    private SearchMenuKlinikFragment searchMenuKlinikFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_main_menu);
        boxSearch = (RelativeLayout) findViewById(R.id.search_main_menu_search);
        backbutton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        searchButton = (ImageView) findViewById(R.id.search_action_contact_us_image_button);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchMenuDoctorFragment = new SearchMenuDoctorFragment();
        searchMenuKlinikFragment = new SearchMenuKlinikFragment();
        // set listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DirektoriActivity.this, SearchFilterActivity.class);
                startActivity(intent);

            }
        });
        boxSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DirektoriActivity.this, SearchFilterActivity.class);
                startActivity(intent);

            }
        });

        // set views
        viewPagerContent = (ViewPager) findViewById(R.id.search_main_menu_viewpager);

        // set tablayout
        tabLayoutContent = (TabLayout) findViewById(R.id.search_main_menu_tab);

        // set viewPager
        setupViewPagerContent(viewPagerContent);

        tabLayoutContent.setupWithViewPager(viewPagerContent);

        viewPagerContent.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               /*     int currentPosition = position;
                Toast.makeText(getActivity(),"Current position" + currentPosition, Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onPageSelected(int position) {
                View doctorView;
                TextView doctorTitle;
                ImageView doctorImageView;

                View clinicView;
                TextView clinicTitle;
                ImageView clinicImageView;

                // color
                int redColor =  AppController.getInstance().getApplicationContext().getResources().getColor(R.color.pink);
                int greyColor = AppController.getInstance().getApplicationContext().getResources().getColor(R.color.textColorHint);

                // drawable doctor
                Drawable doctorInactiveDrawable = AppController.getInstance().getApplicationContext().getResources().getDrawable(R.drawable.ic_klinik_grey);
                Drawable doctorActiveDrawable = AppController.getInstance().getApplicationContext().getResources().getDrawable(R.drawable.ic_dokter_aktif);

                // drawable clinic
                Drawable clinicInactiveDrawable = AppController.getInstance().getApplicationContext().getResources().getDrawable(R.drawable.ic_klinik);
                Drawable clinicActiveDrawable = AppController.getInstance().getApplicationContext().getResources().getDrawable(R.drawable.ic_klinik_aktif);
                switch (position){
                    case 0:
                        // set view for doctor
                        doctorView = tabLayoutContent.getTabAt(0).getCustomView();
                        doctorTitle = (TextView)doctorView.findViewById(R.id.tab_doctor_search_menu_title_text_view);
                        doctorImageView = (ImageView) doctorView.findViewById(R.id.tab_doctor_search_menu_image_view);

                        if(doctorView != null && doctorTitle != null) doctorTitle.setTextColor(redColor);
                        if(doctorView != null && doctorImageView != null) doctorImageView.setImageDrawable(doctorActiveDrawable);

                        // set view for clinic
                        clinicView = tabLayoutContent.getTabAt(1).getCustomView();
                        clinicTitle = (TextView)clinicView.findViewById(R.id.tab_clinic_search_menu_title_text_view);
                        clinicImageView = (ImageView)clinicView.findViewById(R.id.tab_clinic_search_menu_image_view);
                        if(clinicView != null && clinicTitle != null) clinicTitle.setTextColor(greyColor);
                        if(clinicView != null && clinicImageView != null) clinicImageView.setImageDrawable(clinicInactiveDrawable);
                        break;
                    case 1:

                        // set view for doctor
                        doctorView = tabLayoutContent.getTabAt(0).getCustomView();
                        doctorTitle = (TextView)doctorView.findViewById(R.id.tab_doctor_search_menu_title_text_view);
                        doctorImageView = (ImageView) doctorView.findViewById(R.id.tab_doctor_search_menu_image_view);
                        if(doctorView != null && doctorTitle != null) doctorTitle.setTextColor(greyColor);
                        if(doctorView != null && doctorImageView != null) doctorImageView.setImageDrawable(doctorInactiveDrawable);

                        // set view for clinic
                        clinicView = tabLayoutContent.getTabAt(1).getCustomView();
                        clinicTitle = (TextView)clinicView.findViewById(R.id.tab_clinic_search_menu_title_text_view);
                        clinicImageView = (ImageView)clinicView.findViewById(R.id.tab_clinic_search_menu_image_view);
                        if(clinicView != null && clinicTitle != null) clinicTitle.setTextColor(redColor);
                        if(clinicView != null && clinicImageView != null) clinicImageView.setImageDrawable(clinicActiveDrawable);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // setup custom view
        customTabLayout(null);
    }

    private void setupViewPagerContent(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(searchMenuDoctorFragment, getResources().getString(R.string.dokter));
        adapter.addFragment(searchMenuKlinikFragment, getResources().getString(R.string.klinik));
        viewPager.setAdapter(adapter);
    }

    private void customTabLayout(ViewGroup viewGroup){
        View customTabDoctorLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_doctor_search_menu_tab, viewGroup);
        View customTabClinicLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_clinic_search_menu_tab, viewGroup);

        TabLayout.Tab tabDoctor = tabLayoutContent.getTabAt(0);
        TabLayout.Tab tabClinic = tabLayoutContent.getTabAt(1);

        if(customTabDoctorLayout != null && tabDoctor != null){
            tabDoctor.setCustomView(customTabDoctorLayout);
        }

        if(customTabClinicLayout != null && tabClinic != null){
            tabClinic.setCustomView(customTabClinicLayout);
        }

    }

    @Override
    public void onSuccessLoadMenu() {
        searchMenuKlinikFragment.getmenu();
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

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            searchMenuDoctorFragment.cancelableCallback.cancel();
            searchMenuKlinikFragment.cancelableCallback.cancel();
        }catch (Exception e){
            Log.d("Log",String.valueOf(e));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
