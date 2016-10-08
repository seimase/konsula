package com.konsula.app.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by hiantohendry on 2/4/16.
 */

public class MyWrappableGridView extends GridView {
    public MyWrappableGridView(Context context) {
        super(context);
    }

    public MyWrappableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWrappableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }
        else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}