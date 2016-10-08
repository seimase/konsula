package com.konsula.app.ui.activity.direktori;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.R;

import javax.annotation.Nullable;

/**
 * Created by konsula on 12/14/2015.
 */
public class PrivacyPolicyActivity extends AppCompatActivity {
    private ImageButton backButton;
    private ScrollView listView;
    private LinearLayout layoutNodata;
    private LinearLayout l_loading;
    private WebView wb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        init();
        wb.loadUrl("https://staging-api.konsula.com/1.0/general/static/get-privacy-policy");
        wb.setWebViewClient(new TermAndConditionViewClient());
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        wb = (WebView) findViewById(R.id.webView1);
        backButton = (ImageButton) findViewById(R.id.close_action_contact_us_image_button);
        listView = (ScrollView) findViewById(R.id.l_view);
        layoutNodata = (LinearLayout) findViewById(R.id.view_nodata);
        l_loading =(LinearLayout) findViewById(R.id.l_loading);
    }

    private class TermAndConditionViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            l_loading.setVisibility(View.GONE);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            l_loading.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            layoutNodata.setVisibility(View.VISIBLE);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            l_loading.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            layoutNodata.setVisibility(View.VISIBLE);
            // Redirect to deprecated method, so you can use it in all SDK versions
            onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
        }

    }
}
