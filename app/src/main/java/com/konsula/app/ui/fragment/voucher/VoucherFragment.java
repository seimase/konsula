package com.konsula.app.ui.fragment.voucher;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.ui.adapter.ViewPagerAdapter;

/**
 * Created by Konsula on 23/05/2016.
 */
public class VoucherFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;


    public VouchervalidFragment validFragment;
    public VoucherUsedFragment expiredFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        initComponents(view);
        setupViewPagerContent(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        initCustomTabLayout();
        return view;
    }

    private void initComponents(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.voucher_tab);
        viewPager = (ViewPager) view.findViewById(R.id.voucher_content_viewpager);
    }

    private void setupViewPagerContent(ViewPager viewPager) {

        validFragment = new VouchervalidFragment();
        expiredFragment = new VoucherUsedFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(validFragment, getString(R.string.text_voucher_valid));
        adapter.addFragment(expiredFragment, getString(R.string.text_voucher_expired));
        viewPager.setAdapter(adapter);

    }

    private void initCustomTabLayout() {
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (i == 0) {
                RelativeLayout relativeLayout = (RelativeLayout)
                        LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_voucher_left, tabLayout, false);

                TextView tabTitle = (TextView) relativeLayout.findViewById(R.id.tab);
                tabTitle.setText(tabLayout.getTabAt(i).getText().toString());
                tab.setCustomView(relativeLayout);
                tab.select();
            } else {
                RelativeLayout relativeLayout = (RelativeLayout)
                        LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_voucher_right, tabLayout, false);

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
    }


}

