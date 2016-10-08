package com.konsula.app.ui.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.konsula.app.R;

/**
 * Created by SamuelSonny on 30-Jan-16.
 */
public class LoadingMenu extends LinearLayout{
    private View view;
    private ProgressBar itemLoadingProgressBar;
    private ObjectAnimator animation;

    public LoadingMenu(Context context) {
        super(context);
        init();
    }

    public LoadingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        view = LayoutInflater.from(getContext()).inflate(R.layout.loading_menu, this);
        itemLoadingProgressBar = (ProgressBar) findViewById(R.id.item_loading_progress_bar);
        animation = ObjectAnimator.ofInt (itemLoadingProgressBar, "progress", 0, 100);

        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new DecelerateInterpolator());
        stop();
    }

    public void start(){
        setVisibility(VISIBLE);
        setPadding(0, 0, 0, 0);
        animation.start();
    }

    public void stop(){
        setVisibility(GONE);
        setPadding(-100, -100 * getHeight()-10000, -100, -100);
        itemLoadingProgressBar.clearAnimation();
    }
}
