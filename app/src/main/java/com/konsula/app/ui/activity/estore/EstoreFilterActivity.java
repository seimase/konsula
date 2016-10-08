package com.konsula.app.ui.activity.estore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.rangebar.RangeBar;
import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreCategoryTagModel;
import com.konsula.app.service.model.EstorePackageModel;
import com.konsula.app.ui.adapter.PackageAdapter;
import com.konsula.app.ui.custom.CustomRadioButton;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class EstoreFilterActivity extends AppCompatActivity {

    public static final int DEFAULT_MIN_RATE = 0;
    public static final int DEFAULT_MAX_RATE = 15000000;
    public static final int RATE_MULTIPLIER = 50000;

    private int minRate = 50000;
    private int maxRate = DEFAULT_MAX_RATE;

    private RangeBar hargaRangeBar;
    private FrameLayout updateBtn;
    private RelativeLayout rLPaketBtn;
    TextView minRateTextView;
    TextView maxRateTextView;
    TextView btnReset;
    ImageView ivArrow;
    EstoreCategoryTagModel estoreCategoryTagModel;
    ImageButton backButton;
    private String selectedTagId;
    private boolean isRadioSpan=true;

    public final static String SELECTED_TAG_ID= "selected_tag_id";
    public final static String MIN_RATE = "min_rate";
    public final static String MAX_RATE= "max_rate";
    Locale locale;
    NumberFormat formatter;
    private RecyclerView packagelist;
    private ArrayList<EstorePackageModel> listpackage;
    private PackageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estore_filter);
        final ProgressDialog loadingDialog = AppController.createProgressDialog(EstoreFilterActivity.this);
        loadingDialog.setMessage(getResources().getString(R.string.dialog_title_please_wait));
        loadingDialog.show();
        Intent a = getIntent();
        selectedTagId = a.getStringExtra(SELECTED_TAG_ID);
        minRate = DEFAULT_MIN_RATE;
        maxRate = a.getIntExtra(MAX_RATE,DEFAULT_MAX_RATE);
        initComponents();
        initListeners();
        loadingDialog.dismiss();
    }


    private void initComponents(){
        locale = new Locale("id", "ID");
        formatter = NumberFormat.getCurrencyInstance(locale);

        backButton = (ImageButton)findViewById(R.id.backButton);
        minRateTextView = (TextView)findViewById(R.id.min_rate_text_view);
        maxRateTextView = (TextView)findViewById(R.id.max_rate_text_view);
        packagelist = (RecyclerView) findViewById(R.id.package_list_recycler_view);
        packagelist.setHasFixedSize(true);
        packagelist.setClickable(true);
        packagelist.setLayoutManager(new LinearLayoutManager(this));
        updateBtn = (FrameLayout)findViewById(R.id.updateBtn);
        rLPaketBtn = (RelativeLayout)findViewById(R.id.rLPaketBtn);
        ivArrow = (ImageView)findViewById(R.id.ivArrow);
        hargaRangeBar = (RangeBar)findViewById(R.id.hargaRangeBar);
        btnReset = (TextView)findViewById(R.id.btnReset);

        minRateTextView.setText(formatter.format(minRate).substring(2));
        maxRateTextView.setText(formatter.format(maxRate).substring(2));

        estoreCategoryTagModel = ((AppController)getApplicationContext()).getSessionManager().getEstoreCategoryTags();


        if(estoreCategoryTagModel.results != null){
            listpackage = new ArrayList<EstorePackageModel>();
            String[] s = null;
            if (!selectedTagId.isEmpty()) {
                s = selectedTagId.split(",");
                Arrays.sort(s);
            }


            for (int i = 0; i < estoreCategoryTagModel.results.size(); i++) {
                EstorePackageModel p = new EstorePackageModel();
                EstoreCategoryTagModel.Result c = estoreCategoryTagModel.results.get(i);
                p.identity_uri = c.identity_uri;
                p.tag_id = c.tag_id;
                p.tag_name = c.tag_name;
                if (s == null) {
                    p.selected = false;
                } else {
                    for (int row = 0; row < s.length; row ++) {
                        if (p.tag_id == Integer.parseInt(s[row])) {
                            p.selected = true;
                            break;
                        } else {
                            p.selected = false;
                        }
                    }
                }


                listpackage.add(p);
            }
            Collections.sort(listpackage, new Comparator<EstorePackageModel>() {
                @Override
                public int compare(EstorePackageModel lhs, EstorePackageModel rhs) {
                    return lhs.tag_name.compareToIgnoreCase(rhs.tag_name);
                }
            });
            adapter = new PackageAdapter(listpackage);
            packagelist.setAdapter(adapter);


        }

    }

    private void initListeners(){

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rLPaketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRadioSpan) {
                    packagelist.setVisibility(View.GONE);
                    ivArrow.setImageResource(R.drawable.arrow_right_grey);
                    isRadioSpan=false;
                }else{
                    ivArrow.setImageResource(R.drawable.arrow_down_grey);
                    packagelist.setVisibility(View.VISIBLE);
                    isRadioSpan=true;
                }
            }
        });
        hargaRangeBar.setTickCount((DEFAULT_MAX_RATE / RATE_MULTIPLIER) + 1);
        try {
            hargaRangeBar.setThumbIndices((minRate/RATE_MULTIPLIER),(maxRate/RATE_MULTIPLIER));
        }catch (Exception e){
            Log.e("HH",(minRate/RATE_MULTIPLIER)+", "+(maxRate/RATE_MULTIPLIER));
        }

        hargaRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                if (leftThumbIndex < 0) leftThumbIndex = 0;
                if (rightThumbIndex < 0) rightThumbIndex = 0;
                int max = DEFAULT_MAX_RATE / RATE_MULTIPLIER;
                if (leftThumbIndex > max) leftThumbIndex = max;
                if (rightThumbIndex > max) rightThumbIndex = max;
                minRate = leftThumbIndex * RATE_MULTIPLIER;
                maxRate = rightThumbIndex * RATE_MULTIPLIER;
                minRateTextView.setText(leftThumbIndex == 0 ? DEFAULT_MIN_RATE+"" : formatter.format(minRate).substring(2));
                maxRateTextView.setText(rightThumbIndex == 0 ? DEFAULT_MIN_RATE+"" : formatter.format(maxRate).substring(2));
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((AppController) getApplication()).setMixpanel("Do Update Filter");

                Intent returnIntent = new Intent();
//                if(estoreCategoryTagModel.results != null){
//                    if(getSelectedTag(selectedTagId)!=null)
//                    {
//                        returnIntent.putExtra("tags",getSelectedTag(selectedTagId).identity_uri);
//                        returnIntent.putExtra("tag_id",getSelectedTag(selectedTagId).tag_id);
//                    }
//                }
                if (listpackage != null) {
                    returnIntent.putExtra("tags",getSelectedTag());
                    returnIntent.putExtra("tag_id",getSelectedTagId());

                    JSONObject props = new JSONObject();
                    try{
                        props.put("SOURCE",getSelectedTag() );
                        ((AppController) getApplication()).setMixpanel("Get param data for filter", props);
                    }catch (Exception e){

                    }
                }
                returnIntent.putExtra("max_price",maxRate);
                returnIntent.putExtra("min_price",minRate);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hargaRangeBar.setThumbIndices(0,(DEFAULT_MAX_RATE/RATE_MULTIPLIER));
                if(estoreCategoryTagModel.results != null) {
                    for (int i = 0; i < listpackage.size(); i++) {
                        listpackage.get(i).selected = false;
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private EstoreCategoryTagModel.Result getSelectedTag(int id)
    {
        for (int i = 0; i < estoreCategoryTagModel.results.size(); i++) {
            if(id == estoreCategoryTagModel.results.get(i).tag_id)
                return estoreCategoryTagModel.results.get(i);
        }
        return null;
    }

    private String getSelectedTag()
    {
        String tag = "";
        int count = 0;
        for (int i = 0; i < listpackage.size(); i++) {
            if (listpackage.get(i).selected) {
                if (count == 0) {
                    tag = listpackage.get(i).identity_uri;
                    count++;
                } else {
                    tag = tag + "," + listpackage.get(i).identity_uri;
                    count++;
                }

            }
        }
        return tag;
    }

    private String getSelectedTagId()
    {
        String tag = "";
        int count = 0;
        for (int i = 0; i < listpackage.size(); i++) {
            if (listpackage.get(i).selected) {
                if (count == 0) {
                    tag = String.valueOf(listpackage.get(i).tag_id);
                    count++;
                } else {
                    tag = tag + "," + String.valueOf(listpackage.get(i).tag_id);
                    count++;
                }

            }
        }
        return tag;
    }
}
