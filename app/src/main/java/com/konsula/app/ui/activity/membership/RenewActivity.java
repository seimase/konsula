package com.konsula.app.ui.activity.membership;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AllPlanModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.AllMemberShipAdapter;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 3/21/2016.
 */
public class RenewActivity extends AppCompatActivity {
    private ImageButton backButton;
    private RecyclerView recyclerView;
    private TextView texttoolbar;
    public final static String title = "title";
    private RelativeLayout Btnmembership;
    private String currentLanguage;
    private TextView headerPlus, headerBasic;

    private LinearLayout layoutloading;
    private RelativeLayout l_view;
    private Button refresh;
    private String textToolbar;
    //DisplayMetrics displaymetrics;
    //int minHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        texttoolbar = (TextView) findViewById(R.id.tvTitle);
        textToolbar = getResources().getString(R.string.membership) ;
        texttoolbar.setText(WordUtils.capitalizeFully(textToolbar));
        backButton = (ImageButton) findViewById(R.id.backButton);
        Btnmembership = (RelativeLayout) findViewById(R.id.btnmembership);
        Btnmembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RenewActivity.this, MembershipSubActivity.class);
                i.putExtra("title", "Plus");
                startActivity(i);
            }
        });
        headerPlus = (TextView) findViewById(R.id.header_plus);
        headerBasic = (TextView) findViewById(R.id.header_basic);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.list_item_membership);
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        layoutloading = (LinearLayout) findViewById(R.id.l_loading);
        l_view = (RelativeLayout) findViewById(R.id.l_view);
        refresh = (Button) findViewById(R.id.refresh);
        getData();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });



        /*getData();
        recyclerView = (RecyclerView) findViewById(R.id.membership_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);*/


    }


    private void getData() {
        NetworkManager.getNetworkService(getApplication())
                .getAllPlan(((AppController) getApplication())
                        .getSessionManager().getToken(), currentLanguage, new Callback<AllPlanModel>() {
                    @Override
                    public void success(AllPlanModel allPlanModel, Response response) {
                        layoutloading.setVisibility(View.GONE);
                        l_view.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(new AllMemberShipAdapter(getApplicationContext(), allPlanModel.results.body, R.layout.item_list_membership));
                        headerPlus.setText(allPlanModel.results.head.get(0));
                        headerBasic.setText(allPlanModel.results.head.get(1));


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        layoutloading.setVisibility(View.GONE);
                        refresh.setVisibility(View.VISIBLE);
                        Toast.makeText(RenewActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    public void sortpaln(List<AllPlanModel.Result> movies){
//        Collections.sort(movies, new Comparator<AllPlanModel.Result>() {
//            @Override
//            public int compare(AllPlanModel.Result lhs, AllPlanModel.Result rhs) {
//                int ratingLhs = lhs.plan_id;
//                int ratingRhs = rhs.plan_id;
//                if (ratingLhs < ratingRhs) {
//                    return 1;
//                } else if (ratingLhs > ratingRhs) {
//                    return -1;
//                } else {
//                    return 0;
//                }
//            }
//        });
//    }

}