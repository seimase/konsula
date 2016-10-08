package com.konsula.app.ui.activity.Schedule;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.ui.fragment.appointment.PastTeledocFragment;
import com.konsula.app.ui.fragment.appointment.UpcomingTeledocFragment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by Konsula on 14/04/2016.
 */
public class HistoryTeledocActivity extends AppCompatActivity implements UpcomingTeledocFragment.OnSuccessLoadMenuListener {

    private ViewPager viewPagerContent;
    private TabLayout tabLayoutContent;
    private ImageButton backbutton;
    private UpcomingTeledocFragment upcomingTeledocFragment;
    private PastTeledocFragment pastTeledocFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_teledoc);

        backbutton = (ImageButton) findViewById(R.id.backButton);
        viewPagerContent = (ViewPager) findViewById(R.id.history_teledoc_viewpager);
        tabLayoutContent = (TabLayout) findViewById(R.id.history_teledoc_tab);
        setupViewPagerContent(viewPagerContent);
        tabLayoutContent.setupWithViewPager(viewPagerContent);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        viewPagerContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                View doctorView;
                TextView doctorTitle;

                View clinicView;
                TextView clinicTitle;

                // color
                int blackColor = AppController.getInstance().getApplicationContext().getResources().getColor(R.color.black);
                int greyColor = AppController.getInstance().getApplicationContext().getResources().getColor(R.color.textColorHint);

                switch (position) {
                    case 0:
                        // set view for doctor
                        doctorView = tabLayoutContent.getTabAt(0).getCustomView();
                        doctorTitle = (TextView) doctorView.findViewById(R.id.tab_doctor_search_menu_title_text_view);
                        if (doctorView != null && doctorTitle != null)
                            doctorTitle.setTextColor(blackColor);

                        // set view for clinic
                        clinicView = tabLayoutContent.getTabAt(1).getCustomView();
                        clinicTitle = (TextView) clinicView.findViewById(R.id.tab_clinic_search_menu_title_text_view);
                        if (clinicView != null && clinicTitle != null)
                            clinicTitle.setTextColor(greyColor);

                        break;
                    case 1:
                        // set view for doctor
                        doctorView = tabLayoutContent.getTabAt(0).getCustomView();
                        doctorTitle = (TextView) doctorView.findViewById(R.id.tab_doctor_search_menu_title_text_view);
                        if (doctorView != null && doctorTitle != null)
                            doctorTitle.setTextColor(greyColor);

                        // set view for clinic
                        clinicView = tabLayoutContent.getTabAt(1).getCustomView();
                        clinicTitle = (TextView) clinicView.findViewById(R.id.tab_clinic_search_menu_title_text_view);
                        if (clinicView != null && clinicTitle != null)
                            clinicTitle.setTextColor(blackColor);

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
        upcomingTeledocFragment = new UpcomingTeledocFragment();
        pastTeledocFragment = new PastTeledocFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(upcomingTeledocFragment, "Upcoming");
        adapter.addFragment(pastTeledocFragment, "Past");
        viewPager.setAdapter(adapter);
    }

    private void customTabLayout(ViewGroup viewGroup){
        View customTabUpcomingLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_appointment_upcoming_tab, viewGroup);
        View customTabPastLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_appointment_past_tab, viewGroup);
        TabLayout.Tab tabDoctor = tabLayoutContent.getTabAt(0);
        TabLayout.Tab tabClinic = tabLayoutContent.getTabAt(1);

        if(customTabUpcomingLayout != null && tabDoctor != null){
            tabDoctor.setCustomView(customTabUpcomingLayout);
        }

        if(customTabPastLayout != null && tabClinic != null){
            tabClinic.setCustomView(customTabPastLayout);
        }
    }

    @Override
    public void onSuccessLoadMenu() {
        pastTeledocFragment.getPasTeledocList();
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



}
