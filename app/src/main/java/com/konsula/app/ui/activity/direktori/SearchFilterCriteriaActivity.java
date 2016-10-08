package com.konsula.app.ui.activity.direktori;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;
import com.konsula.app.R;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by konsula on 12/11/2015.
 */
public class SearchFilterCriteriaActivity extends Activity implements RangeBar.OnRangeBarChangeListener {
    private TextView sortByPengalamanButton;
    private TextView sortByHargaButton;
    private RangeBar biayaKonsultasiRangeBar;
    private RangeBar jadwalKonsultasiRangeBar;
    private RangeBar yearExperienceRangebar;
    private RangeBar yearOperationRangebar;
    private TextView minRateTextView;
    private TextView maxRateTextView;
    private TextView minHourTextView;
    private TextView maxHourTextView;
    private TextView maxExpTextView;
    private TextView minExpTextView;
    private LinearLayout layoutExp;
    private LinearLayout layoutOps;
    private TextView maxOpsTextView;
    private TextView minOpsTextView;
    private TextView[] jadwalPraktekTextView = new TextView[8];

    private String sortBy;
    private int minExp = 0;
    private int maxExp = MAX_RATE;
    private int minOps = 0;
    private int maxOps = MAX_RATE;
    private int minRate = 0;
    private int maxRate = MAX_EXP;
    private String minHour = "00.00";
    private String maxHour = "23.45";
    private String days = "";
    private boolean from_clinic = false;

    public static final String SORT_BY_PRICE_ASC = "price_asc";
    public static final String SORT_BY_EXPERIENCE_DESC = "experience_desc";
    public static final String SORT_BY_OPERATION_DESC = "operation_desc";
    public static final int MAX_RATE = 1500000;
    public static final int MAX_EXP = 60;
    public static final int RATE_MULTIPLIER = 50000;
    public static final int MAX_TIME = 24 * 60;
    public static final int TIME_MINUTE_MULTIPLIER = 15;
    private LinearLayout ll22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter_criteria);

        sortByPengalamanButton = (TextView) findViewById(R.id.sort_by_pengalaman_button);
        sortByHargaButton = (TextView) findViewById(R.id.sort_by_harga_button);
        biayaKonsultasiRangeBar = (RangeBar) findViewById(R.id.filter_criteria_biaya_konsultasi_rangebar);
        yearExperienceRangebar = (RangeBar) findViewById(R.id.filter_criteria_year_experience_rangebar);
        yearOperationRangebar = (RangeBar) findViewById(R.id.filter_criteria_year_operation_rangebar);
        ll22 = (LinearLayout) findViewById(R.id.ll22);
        layoutExp = (LinearLayout) findViewById(R.id.layout_filter_exp);
        layoutOps = (LinearLayout) findViewById(R.id.layout_filter_ops);

        try {
            from_clinic = getIntent().getExtras().getBoolean("from_clinic");
            if (from_clinic) {
                ll22.setVisibility(View.GONE);
                layoutExp.setVisibility(View.GONE);
            }else {
                layoutOps.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }

        jadwalKonsultasiRangeBar = (RangeBar) findViewById(R.id.filter_criteria_jadwal_konsultasi_rangebar);
        minRateTextView = (TextView) findViewById(R.id.min_rate_text_view);
        maxRateTextView = (TextView) findViewById(R.id.max_rate_text_view);
        minHourTextView = (TextView) findViewById(R.id.min_hour_text_view);
        maxHourTextView = (TextView) findViewById(R.id.max_hour_text_view);
        maxExpTextView = (TextView) findViewById(R.id.max__year_experience_view);
        minExpTextView = (TextView) findViewById(R.id.min_year_experience_view);
        maxOpsTextView = (TextView) findViewById(R.id.max__year_operation_view);
        minOpsTextView = (TextView) findViewById(R.id.min_year_operation_view);
        jadwalPraktekTextView[0] = (TextView) findViewById(R.id.jadwal_praktek_any_text_view);
        jadwalPraktekTextView[1] = (TextView) findViewById(R.id.jadwal_praktek_senin_text_view);
        jadwalPraktekTextView[2] = (TextView) findViewById(R.id.jadwal_praktek_selasa_text_view);
        jadwalPraktekTextView[3] = (TextView) findViewById(R.id.jadwal_praktek_rabu_text_view);
        jadwalPraktekTextView[4] = (TextView) findViewById(R.id.jadwal_praktek_kamis_text_view);
        jadwalPraktekTextView[5] = (TextView) findViewById(R.id.jadwal_praktek_jumat_text_view);
        jadwalPraktekTextView[6] = (TextView) findViewById(R.id.jadwal_praktek_sabtu_text_view);
        jadwalPraktekTextView[7] = (TextView) findViewById(R.id.jadwal_praktek_minggu_text_view);

        biayaKonsultasiRangeBar.setTickCount((MAX_RATE / RATE_MULTIPLIER) + 1);
        jadwalKonsultasiRangeBar.setTickCount((24 * 60 / TIME_MINUTE_MULTIPLIER) + 1);
        yearExperienceRangebar.setTickCount((MAX_EXP / 1) + 1);
        yearOperationRangebar.setTickCount((MAX_EXP / 1) + 1);
        biayaKonsultasiRangeBar.setOnRangeBarChangeListener(this);
        jadwalKonsultasiRangeBar.setOnRangeBarChangeListener(this);
        yearExperienceRangebar.setOnRangeBarChangeListener(this);
        yearOperationRangebar.setOnRangeBarChangeListener(this);

        if (getIntent().getExtras() != null) {
            sortBy = getIntent().getStringExtra("sort_by");
            minRate = getIntent().getIntExtra("min_rate", 0);
            maxRate = getIntent().getIntExtra("max_rate", MAX_RATE);
            minHour = getIntent().getStringExtra("min_hour");
            maxHour = getIntent().getStringExtra("max_hour");
            minExp = getIntent().getIntExtra("min_exp", 0);
            maxExp = getIntent().getIntExtra("max_exp", MAX_EXP);
            minOps = getIntent().getIntExtra("min_ops", 0);
            maxOps = getIntent().getIntExtra("max_ops", MAX_EXP);
            days = getIntent().getStringExtra("days");


            initAttribute();
        }
    }

    private void initAttribute() {
        setActive(sortByPengalamanButton, false);
        setActive(sortByHargaButton, false);
        if (sortBy.equals(SORT_BY_EXPERIENCE_DESC) || sortBy.equals(SORT_BY_OPERATION_DESC)) {
            sortBy = SORT_BY_EXPERIENCE_DESC;
            setActive(sortByPengalamanButton, true);
        } else {
            sortBy = SORT_BY_PRICE_ASC;
            setActive(sortByHargaButton, true);
        }
        biayaKonsultasiRangeBar.setThumbIndices(minRate / RATE_MULTIPLIER, maxRate==0?MAX_RATE/ RATE_MULTIPLIER:maxRate / RATE_MULTIPLIER);
        int minTime = (Integer.parseInt(minHour.split("\\.")[0]) * 60) + Integer.parseInt(minHour.split("\\.")[1]);
        int maxTime = (Integer.parseInt(maxHour.split("\\.")[0]) * 60) + Integer.parseInt(maxHour.split("\\.")[1]);
        jadwalKonsultasiRangeBar.setThumbIndices(minTime / TIME_MINUTE_MULTIPLIER, maxTime / TIME_MINUTE_MULTIPLIER);
        yearExperienceRangebar.setThumbIndices(minExp , maxExp==0?MAX_EXP:maxExp);
        yearOperationRangebar.setThumbIndices(minOps , maxOps==0?MAX_EXP:maxOps);
        if (!days.equals("")) {
            String[] days = this.days.split(",");
            for (int i = 0; i < jadwalPraktekTextView.length; i++) {
                boolean available = false;
                for (String day : days) {
                    if (day.equals(i + "")) {
                        available = true;
                    }
                }
                if (available) {
                    setActive(jadwalPraktekTextView[i], true);
                } else {
                    setActive(jadwalPraktekTextView[i], false);
                }
            }
        } else {
            setActive(jadwalPraktekTextView[0], true);
            for (TextView textView : jadwalPraktekTextView) {
                if (textView != jadwalPraktekTextView[0]) {
                    setActive(textView, false);
                }
            }
        }
    }

    public void sortBy(View view) {
        setActive(sortByPengalamanButton, false);
        setActive(sortByHargaButton, false);

        setActive((TextView) view, true);

        if (view == sortByPengalamanButton) {
            sortBy = getIntent().getStringExtra("type").equals(SearchResultDoctorActivity.class.getName()) ? SORT_BY_EXPERIENCE_DESC : SORT_BY_OPERATION_DESC;
        } else if (view == sortByHargaButton) {
            sortBy = SORT_BY_PRICE_ASC;
        }
    }

    public void setActive(TextView textView, boolean active) {
        textView.setBackgroundResource(active ? R.drawable.shape_edit_text_rounded_pink : 0);
        textView.setTextColor(getResources().getColor(active ? R.color.white : R.color.textColorBase));
    }

    public void setJadwal(View view) {
        days = "";
        if (view == jadwalPraktekTextView[0]) {
            setActive(jadwalPraktekTextView[0], true);
            for (TextView textView : jadwalPraktekTextView) {
                if (textView != jadwalPraktekTextView[0]) {
                    setActive(textView, false);
                }
            }
        } else {
            int index = 0;
            setActive(jadwalPraktekTextView[0], false);
            for (TextView textView : jadwalPraktekTextView) {
                if (view == textView) {
                    setActive(textView, !(textView.getTextColors().getDefaultColor() == getResources().getColor(R.color.white)));
                } else if (textView.getTextColors().getDefaultColor() != getResources().getColor(R.color.white)) {
                    setActive(textView, false);
                }

                if (textView.getTextColors().getDefaultColor() == getResources().getColor(R.color.white)) {
                    days += index + ",";
                }
                index++;
            }
            days = days.length() > 0 ? days.substring(0, days.length() - 1) : "";
        }
    }

    public void updateFilter(View view) {
        Intent intent = new Intent();
        if (sortBy == null) {
            sortBy = getIntent().getStringExtra("type").equals(SearchResultDoctorActivity.class.getName()) ? SORT_BY_EXPERIENCE_DESC : SORT_BY_OPERATION_DESC;
        }
        intent.putExtra("sort_by", sortBy);
        intent.putExtra("min_rate", minRate);
        intent.putExtra("max_rate", maxRate);
        intent.putExtra("min_hour", minHour.replace(":", "."));
        intent.putExtra("max_hour", maxHour.replace(":", "."));
        intent.putExtra("min_exp", minExp);
        intent.putExtra("max_exp", maxExp);
        intent.putExtra("min_ops", minOps);
        intent.putExtra("max_ops", maxOps);
        intent.putExtra("days", days);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void resetFilter(View view) {
        sortBy = getIntent().getStringExtra("type").equals(SearchResultDoctorActivity.class.getName()) ? SORT_BY_EXPERIENCE_DESC : SORT_BY_OPERATION_DESC;
        minRate = 0;
        maxRate = MAX_RATE;
        minHour = "00.00";
        maxHour = "23.59";
        minExp = 0;
        maxExp = MAX_EXP;
        minOps = 0;
        maxOps = MAX_EXP;
        days = "";
        initAttribute();
    }

    public void close(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
        if (rangeBar == biayaKonsultasiRangeBar) {
            Locale locale = new Locale("id", "ID");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
            minRate = leftThumbIndex * RATE_MULTIPLIER;
            maxRate = rightThumbIndex * RATE_MULTIPLIER;
            minRateTextView.setText(leftThumbIndex == 0 ? getResources().getText(R.string.free_con) : formatter.format(minRate).substring(2));
            maxRateTextView.setText(rightThumbIndex == 0 ? getResources().getText(R.string.free_con) : formatter.format(maxRate).substring(2));
        } else if (rangeBar == jadwalKonsultasiRangeBar) {
            int minTime = leftThumbIndex * TIME_MINUTE_MULTIPLIER;
            int maxTime = rightThumbIndex * TIME_MINUTE_MULTIPLIER;
            minTime = minTime == MAX_TIME ? minTime - 1 : minTime;
            maxTime = maxTime == MAX_TIME ? maxTime - 1 : maxTime;
            minHour = String.format("%02d", minTime / 60) + ":" + String.format("%02d", minTime % 60);
            maxHour = String.format("%02d", maxTime / 60) + ":" + String.format("%02d", maxTime % 60);
            minHourTextView.setText(minHour);
            maxHourTextView.setText(maxHour + " WIB");
        } else if (rangeBar == yearExperienceRangebar) {
            minExp = leftThumbIndex * 1;
            maxExp = rightThumbIndex * 1;
            maxExpTextView.setText(getString(R.string.direktori_total_year_experience).replace("{year}", String.valueOf(maxExp)));
            minExpTextView.setText(getString(R.string.direktori_total_year_experience).replace("{year}", String.valueOf(minExp)));
        }else if (rangeBar == yearOperationRangebar) {
            minOps = leftThumbIndex * 1;
            maxOps = rightThumbIndex * 1;
            maxOpsTextView.setText(getString(R.string.direktori_total_year_experience).replace("{year}", String.valueOf(maxOps)));
            minOpsTextView.setText(getString(R.string.direktori_total_year_experience).replace("{year}", String.valueOf(minOps)));
        }
    }
}