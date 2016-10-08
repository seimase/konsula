package com.konsula.app.ui.adapter;

/**
 * Created by SamuelSonny on 13-Feb-16.
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.konsula.app.R;
import com.konsula.app.ui.custom.CustomtextView;
import com.konsula.app.util.Converter;

public abstract class MonthAdapter extends BaseAdapter{
    private GregorianCalendar mCalendar;
    private Calendar mCalendarToday;
    private Context mContext;
    private DisplayMetrics mDisplayMetrics;
    private List<String> mItems;
    private AppointmentListAdapter appointmentListAdapter;
    private int mMonth;
    private int mYear;
    private int mDaysShown;
    private int mDaysLastMonth;
    private int mDaysNextMonth;
    private int mTitleHeight, mDayHeight;
    private final String[] mDays = { "S", "S", "R", "K", "J", "S", "M" };
    private final int[] mDaysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    public MonthAdapter(Context c, AppointmentListAdapter appointmentListAdapter, int month, int year, DisplayMetrics metrics) {
        mContext = c;
        mMonth = month;
        mYear = year;
        mCalendar = new GregorianCalendar(mYear, mMonth, 1);
        mCalendarToday = Calendar.getInstance();
        mDisplayMetrics = metrics;
        this.appointmentListAdapter = appointmentListAdapter;
        populateMonth();
    }

    /**
     * @param date - null if day title (0 - dd / 1 - mm / 2 - yy)
     * @param position - position in item list
     * @param item - view for date
     */
    protected abstract void onDate(int[] date, int position, View item);

    private void populateMonth() {
        mItems = new ArrayList<String>();
        for (String day : mDays) {
            mItems.add(day);
            mDaysShown++;
        }

        int firstDay = getDay(mCalendar.get(Calendar.DAY_OF_WEEK));
        int prevDay;
        if (mMonth == 0)
            prevDay = daysInMonth(11) - firstDay + 1;
        else
            prevDay = daysInMonth(mMonth - 1) - firstDay + 1;
        for (int i = 0; i < firstDay; i++) {
            mItems.add(String.valueOf(prevDay + i));
            mDaysLastMonth++;
            mDaysShown++;
        }

        int daysInMonth = daysInMonth(mMonth);
        for (int i = 1; i <= daysInMonth; i++) {
            mItems.add(String.valueOf(i));
            mDaysShown++;
        }

        mDaysNextMonth = 1;
        while (mDaysShown % 7 != 0) {
            mItems.add(String.valueOf(mDaysNextMonth));
            mDaysShown++;
            mDaysNextMonth++;
        }

        mTitleHeight = Converter.dpToPx(mContext, 32);
        int rows = (mDaysShown / 7);
        mDayHeight = mDisplayMetrics.widthPixels / 7;
    }

    private int daysInMonth(int month) {
        int daysInMonth = mDaysInMonth[month];
        if (month == 1 && mCalendar.isLeapYear(mYear))
            daysInMonth++;
        return daysInMonth;
    }

    private int getBarHeight() {
        switch (mDisplayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_HIGH:
                return 48;
            case DisplayMetrics.DENSITY_MEDIUM:
                return 32;
            case DisplayMetrics.DENSITY_LOW:
                return 24;
            default:
                return 48;
        }
    }

    private int getDay(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            case Calendar.SUNDAY:
                return 6;
            default:
                return 0;
        }
    }

    private boolean isToday(int day, int month, int year) {
        if (mCalendarToday.get(Calendar.MONTH) == month
                && mCalendarToday.get(Calendar.YEAR) == year
                && mCalendarToday.get(Calendar.DAY_OF_MONTH) == day) {
            return true;
        }
        return false;
    }

    private int[] getDate(int position) {
        int date[] = new int[3];
        if (position <= 6) {
            return null; // day names
        } else if (position <= mDaysLastMonth + 6) {
            // previous month
            date[0] = Integer.parseInt(mItems.get(position));
            if (mMonth == 0) {
                date[1] = 11;
                date[2] = mYear - 1;
            } else {
                date[1] = mMonth - 1;
                date[2] = mYear;
            }
        } else if (position <= mDaysShown - mDaysNextMonth  ) {
            // current month
            date[0] = position - (mDaysLastMonth + 6);
            date[1] = mMonth;
            date[2] = mYear;
        } else {
            // next month
            date[0] = Integer.parseInt(mItems.get(position));
            if (mMonth == 11) {
                date[1] = 0;
                date[2] = mYear + 1;
            } else {
                date[1] = mMonth + 1;
                date[2] = mYear;
            }
        }
        return date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout view = new LinearLayout(mContext);
        CustomtextView textView = new CustomtextView(mContext);
        LinearLayout notification = new LinearLayout(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams notificationParams = new LinearLayout.LayoutParams(Converter.dpToPx(mContext, 8), Converter.dpToPx(mContext, 8));
        int verticalMargin = Converter.dpToPx(mContext, 11);
        int horizontalMargin = Converter.dpToPx(mContext, 10);

        textView.setFontType(Typeface.createFromAsset(mContext.getAssets(), "Content.ttf"));
        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.addView(textView);
        view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        view.setOrientation(LinearLayout.VERTICAL);
        textView.setLayoutParams(textViewParams);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(12);
        textView.setText(mItems.get(position));
        textView.setTextColor(Color.BLACK);
        notification.setLayoutParams(notificationParams);
        notification.setBackgroundResource(R.drawable.bg_red_circle);
        notificationParams.setMargins(0, 0, 0, 1);

        int[] date = getDate(position);
        if(position % 7 == 5 || position % 7 == 6){
            textView.setTextColor(Color.parseColor("#a0a1a5"));
        }
        if (date != null) {
            textViewParams.setMargins(horizontalMargin, (int) (verticalMargin * 0.1), horizontalMargin, (int) (verticalMargin * 0.1));
            textView.setHeight(mDayHeight - verticalMargin * 2);
            view.setBackgroundResource(R.drawable.border_bottom);
            view.addView(notification);
            notification.setVisibility(View.INVISIBLE);
            if (date[1] != mMonth) {
                // previous or next month
                textView.setText("");
            } else {
                // current month
                textView.setBackgroundColor(0);
                if (isToday(date[0], date[1], date[2] )) {
                    textView.setTextColor(Color.WHITE);
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setBackgroundResource(R.drawable.bg_red_circle);
                }
                for(int i = 0; i < appointmentListAdapter.getCount(); i++){
                    try {
                        Calendar scheduleDate = Calendar.getInstance();
                        scheduleDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(appointmentListAdapter.getItem(i).schedule_date));
                        if(scheduleDate.get(Calendar.DAY_OF_MONTH) == date[0] && scheduleDate.get(Calendar.MONTH) == date[1]){
                            notification.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        } else {
            view.setBackgroundColor(Color.parseColor("#f3f3f5"));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setHeight(mTitleHeight);
        }
        onDate(date, position, textView);
        return view;
    }

    public int getDaysShown() {
        return mDaysShown;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
