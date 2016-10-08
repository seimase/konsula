package com.konsula.app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.util.Converter;

import java.util.Calendar;

/**
 * Created by SamuelSonny on 02-Apr-16.
 */
public class MonthSliderAdapter extends PagerAdapter{
    private Context context;
    private AppointmentListAdapter appointmentListAdapter;
    private DisplayMetrics metrics = new DisplayMetrics();
    public static final int MAX_MONTH = 12 * 3;

    public MonthSliderAdapter(Context context, AppointmentListAdapter appointmentListAdapter) {
        this.context = context;
        this.appointmentListAdapter = appointmentListAdapter;
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.item_month_slider, null);
        final ViewHolder holder = new ViewHolder();
        holder.calendarGridView = (GridView) convertView.findViewById(R.id.calendar_grid_view);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, position);
        holder.monthAdapter = new MonthAdapter(context, appointmentListAdapter, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), metrics) {
            @Override
            protected void onDate(int[] date, int position, View item) {

            }
        };
        holder.calendarGridView.setAdapter(holder.monthAdapter);
        resizeGridView(holder.calendarGridView, holder.monthAdapter.getDaysShown(), 7);
        convertView.setTag(holder);
        container.addView(convertView);
        return convertView;
    }

    @Override
    public int getCount() {
        return MAX_MONTH;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        // TODO Auto-generated method stub
        return view.equals(obj);
    }

    private void resizeGridView(GridView gridView, int items, int columns) {
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        int oneRowHeight = (metrics.widthPixels / 7);
        int rows = 6;
        params.height = (oneRowHeight * rows - Converter.dpToPx(context, 27)) + Converter.dpToPx(context, 1);
        gridView.setLayoutParams(params);
    }

    private class ViewHolder{
        GridView calendarGridView;
        MonthAdapter monthAdapter;
        private boolean gridViewResized;
    }
}
