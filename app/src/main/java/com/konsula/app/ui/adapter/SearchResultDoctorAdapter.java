package com.konsula.app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.konsula.app.R;
import com.konsula.app.service.model.SearchDoctorModel;
import com.konsula.app.service.model.SearchDoctorSliderModel;
import com.konsula.app.ui.custom.CustomtextView;
import com.konsula.app.ui.view.WrapContentViewPager;
import com.konsula.app.util.Converter;

/**
 * Created by SamuelSonny on 30-Jan-16.
 */
public class SearchResultDoctorAdapter extends ArrayAdapter<SearchDoctorSliderModel>{
    public SearchResultDoctorAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final SearchDoctorSliderModel item = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_doctor_result, null);
            holder = new ViewHolder();
            holder.searchPagingLinearLayout = (LinearLayout) convertView.findViewById(R.id.item_result_search_paging_linear_layout);
            holder.pager = (WrapContentViewPager) convertView.findViewById(R.id.pager);
            holder.sliderAdapter = new SearchResultDoctorSliderAdapter(getContext());
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.sliderAdapter.setData(item.getDoctor());
        holder.pager.setAdapter(holder.sliderAdapter);
        holder.pager.setOnPageChangeListener(item);
        item.setCurrentPage(holder.pager.getCurrentItem());
        item.renderPageIndex(getContext(), holder);
        return convertView;
    }

    public class ViewHolder{
        LinearLayout searchPagingLinearLayout;
        SearchResultDoctorSliderAdapter sliderAdapter;
        WrapContentViewPager pager;

        public LinearLayout getSearchPagingLinearLayout() {
            return searchPagingLinearLayout;
        }

        public WrapContentViewPager getPager() {
            return pager;
        }
    }
}