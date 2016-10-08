package com.konsula.app.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.konsula.app.R;

/**
 * Created by hiantohendry on 3/31/16.
 */
public class CustomEditText extends EditText {
    private int defaultDimension = 0;
    private int TYPE_BOLD = 1;
    private int TYPE_ITALIC = 2;
    private int FONT_HEADER = 1;
    private int FONT_HEADER_REGULER = 2;
    private int FONT_CONTENT = 3;
    private int FONT_LABEL = 4;
    private int fontType;
    private int fontName;

    public CustomEditText(Context context) {
        super(context);
        init(null, 0);
    }
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.font, defStyle, 0);
        fontName = a.getInt(R.styleable.font_name, defaultDimension);
        fontType = a.getInt(R.styleable.font_type, defaultDimension);
        a.recycle();
        if (fontName == FONT_HEADER) {
            setFontType(Typeface.createFromAsset(getContext().getAssets(), "Header.ttf"));
        } else if (fontName == FONT_HEADER_REGULER) {
            setFontType(Typeface.createFromAsset(getContext().getAssets(), "Header_Reguler.ttf"));
        }else if (fontName ==FONT_CONTENT){
            setFontType(Typeface.createFromAsset(getContext().getAssets(), "Content.ttf"));
        }else if (fontName == FONT_LABEL){
            setFontType(Typeface.createFromAsset(getContext().getAssets(), "Label.ttf"));
        }
    }
    public void setFontType(Typeface font) {
        if (fontType == TYPE_BOLD) {
            setTypeface(font, Typeface.BOLD);
        } else if (fontType == TYPE_ITALIC) {
            setTypeface(font, Typeface.ITALIC);
        } else {
            setTypeface(font);
        }
    }
}
