package com.konsula.app.service.model;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.konsula.app.R;
import com.konsula.app.ui.adapter.SearchResultDoctorAdapter;
import com.konsula.app.ui.custom.CustomtextView;
import com.konsula.app.util.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SamuelSonny on 10-Apr-16.
 */
public class SearchDoctorSliderModel implements ViewPager.OnPageChangeListener{
    private SearchDoctorModel.Doctor doctor;
    private int currentPage;
    private Context context;
    private SearchResultDoctorAdapter.ViewHolder holder;
    private LinearLayout.LayoutParams pageTitleLayout;

    private class PageTextView extends CustomtextView implements View.OnClickListener{
        private int page;

        public PageTextView(Context context) {
            super(context);
            init();
        }

        public PageTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public PageTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init(){
            setOnClickListener(this);
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPage() {
            return page;
        }

        @Override
        public void onClick(View v) {
            holder.getPager().setCurrentItem(page - 1);
        }
    }

    public void setDoctor(SearchDoctorModel.Doctor doctor) {
        this.doctor = doctor;
    }

    public SearchDoctorModel.Doctor getDoctor() {
        return doctor;
    }

    public static List<SearchDoctorSliderModel> convertDoctorsToSliders(List<SearchDoctorModel.Doctor> doctors){
        Log.d("search","masuk slider");
        ArrayList<SearchDoctorSliderModel> sliders = new ArrayList<SearchDoctorSliderModel>();
        for(int i = 0; i < doctors.size(); i++){
            SearchDoctorSliderModel slider = new SearchDoctorSliderModel();
            slider.setDoctor(doctors.get(i));
            sliders.add(slider);
        }
        return sliders;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void renderPageIndex(Context context, final SearchResultDoctorAdapter.ViewHolder holder) {
        this.context = context;
        this.holder = holder;
        holder.getSearchPagingLinearLayout().removeAllViews();
        holder.getSearchPagingLinearLayout().setBackgroundResource(R.drawable.border_bottom_pink_1_dp);
        for(int i = 0; i < doctor.practices.size(); i++){
            final int page = i + 1;
            LinearLayout pageLinearLayout = new LinearLayout(context);
            PageTextView pageTextView = new PageTextView(context);
            pageTextView.setFontType(Typeface.createFromAsset(context.getAssets(), "Content.ttf"));
            pageTextView.setPadding(Converter.dpToPx(context, 9), Converter.dpToPx(context, 5), Converter.dpToPx(context, 9), Converter.dpToPx(context, 5));
            pageTextView.setSingleLine(true);
            pageLinearLayout.addView(pageTextView);
            currentPage = currentPage == 0 ? 1 : currentPage;
            if(currentPage == page){
                if(pageTitleLayout == null) {
                    pageTitleLayout = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                }
                pageLinearLayout.setLayoutParams(pageTitleLayout);
                pageTextView.setBackgroundResource(R.drawable.border_bottom_pink_2_dp);
                pageTextView.setEllipsize(TextUtils.TruncateAt.END);
                try {
                    pageTextView.setText(Html.fromHtml("<b>" + context.getResources().getText(R.string.lokasi) + " " + page + " - </b>" + doctor.practices.get(i).practice_name));
                    pageTextView.setSelected(true);
                }catch (Exception ex){
                    Log.e("error", ex.getMessage());
                }
            }else {
                //  pageTextView.setBackgroundColor(Color.parseColor("#949599"));
                //  pageTextView.setTextColor(Color.WHITE);
                pageTextView.setText(page + "");
                pageTextView.setPage(page);
            }
            holder.getSearchPagingLinearLayout().addView(pageLinearLayout);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPage = position + 1;
        renderPageIndex(context, holder);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
