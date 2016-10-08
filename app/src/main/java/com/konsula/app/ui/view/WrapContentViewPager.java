package com.konsula.app.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.konsula.app.util.Converter;

/**
 * Created by SamuelSonny on 02-Feb-16.
 */
public class WrapContentViewPager extends ViewPager{
    private float xDown;
    private float xMove;
    private float yDown;
    private float yMove;
    private boolean viewpagersroll = false;

    public WrapContentViewPager(Context context) {
        super(context);
    }

    public WrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = Converter.dpToPx(getContext(), 120);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            xDown = ev.getRawX();
            yDown = ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            xMove = ev.getRawX();
            yMove = ev.getRawY();
            if (viewpagersroll) {
                getParent().requestDisallowInterceptTouchEvent(true);
                return super.dispatchTouchEvent(ev);
            }
            if (Math.abs(yMove - yDown) < 5 && Math.abs(xMove - xDown) > 20) {
                viewpagersroll = true;
            } else {
                return false;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            viewpagersroll = false;
        }
        return super.dispatchTouchEvent(ev);
    }
}
