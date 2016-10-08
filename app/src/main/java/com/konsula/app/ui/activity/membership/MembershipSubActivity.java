package com.konsula.app.ui.activity.membership;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AllPlanModel;
import com.konsula.app.service.model.MembershipSubModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.AllMemberShipAdapter;
import com.konsula.app.ui.adapter.MembershipSubAdapter;
import com.konsula.app.ui.custom.MyLinearLayoutManager;

import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 06/04/2016.
 */
public class MembershipSubActivity extends AppCompatActivity {
    private ImageButton backButton;
    private RecyclerView recyclerView;
    private TextView textViewToolbar;
    private int plan_id;
    private LinearLayout layoutloading;
    private Button refresh;
    private String textToolbar;
    private String currentLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submembership);
        textViewToolbar = (TextView) findViewById(R.id.tvTitle);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        textViewToolbar = (TextView) findViewById(R.id.tvTitle);
        textToolbar =  getResources().getString(R.string.membership)+ " Plus";
        textViewToolbar.setText(WordUtils.capitalizeFully(textToolbar));
        // plan_id = getIntent().getExtras().getInt("title");
        plan_id = 2;
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.membership_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        refresh = (Button) findViewById(R.id.refresh);
        getData();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        getData();

    }

    private void getData() {
        NetworkManager.getNetworkService(getApplication())
                .getSubPlan(((AppController) getApplication())
                        .getSessionManager().getToken(), plan_id, new Callback<MembershipSubModel>() {
                    @Override
                    public void success(MembershipSubModel membershipSubModel, Response response) {
                        layoutloading.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(new MembershipSubAdapter(getApplicationContext(), membershipSubModel.results.subplan, membershipSubModel.results, R.layout.item_membership_sub));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        layoutloading.setVisibility(View.GONE);
                        refresh.setVisibility(View.VISIBLE);
                        Toast.makeText(MembershipSubActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
