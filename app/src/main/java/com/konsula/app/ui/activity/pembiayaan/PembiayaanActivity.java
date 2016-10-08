package com.konsula.app.ui.activity.pembiayaan;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.konsula.app.R;

/**
 * Created by konsula on 3/3/2016.
 */
public class PembiayaanActivity extends TabActivity {


    private static final LayoutParams params = new LinearLayout.LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0.5f);

    private static TabHost tabHost;
    private static TabHost.TabSpec spec;
    private ImageButton backButton;
    private static Intent intent;
    private static LayoutInflater inflater;

    private View tab;
    private TextView label;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembiayaan);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tabHost = getTabHost();

        tab = inflater.inflate(R.layout.custom_divider_tabhost, getTabWidget(), false);
        tab.setLayoutParams(params);
        label = (TextView) tab.findViewById(R.id.tabLabel);
        label.setText(getResources().getString(R.string.tabspec2));
        intent = new Intent(PembiayaanActivity.this, PembiayaanTanpaJaminanActivity.class);
        spec = tabHost.newTabSpec(getResources().getString(R.string.tabspec2)).setIndicator(tab).setContent(intent);
        tabHost.addTab(spec);


        tab = inflater.inflate(R.layout.custom_divider_tabhost, getTabWidget(), false);
        tab.setLayoutParams(params);
        label = (TextView) tab.findViewById(R.id.tabLabel);
        label.setText(getResources().getString(R.string.tabspec1));
        intent = new Intent(PembiayaanActivity.this, PembiayaanDenganJaminanActivity.class);
        spec = tabHost.newTabSpec(getResources().getString(R.string.tabspec1)).setIndicator(tab).setContent(intent);
        tabHost.addTab(spec);
        getTabWidget().getChildAt(0).findViewById(R.id.tabSelectedDivider).setVisibility(View.VISIBLE);


        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tag) {
                clearTabStyles();
                View tabView = null;
                if (tag.equals(getResources().getString(R.string.tabspec2))) {
                    tabView = getTabWidget().getChildAt(0);
                } else if (tag.equals(getResources().getString(R.string.tabspec1))) {
                    tabView = getTabWidget().getChildAt(1);
                }
                tabView.findViewById(R.id.tabSelectedDivider).setVisibility(View.VISIBLE);
            }
        });
    }

    private void clearTabStyles() {
        for (int i = 0; i < getTabWidget().getChildCount(); i++) {
            tab = getTabWidget().getChildAt(i);
            tab.findViewById(R.id.tabSelectedDivider).setVisibility(View.GONE);
        }
    }
}