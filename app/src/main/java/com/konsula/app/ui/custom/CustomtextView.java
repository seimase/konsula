package com.konsula.app.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.konsula.app.R;

/**
 * Created by Konsula on 12/02/2016.
 */
public class CustomtextView extends TextView{
    private int defaultDimension = 0;
    private int TYPE_BOLD = 1;
    private int TYPE_ITALIC = 2;
    private int FONT_HEADER = 1;
    private int FONT_HEADER_REGULER = 2;
    private int FONT_CONTENT = 3;
    private int FONT_LABEL = 4;
    private int FONT_KLAVIKA = 5;
    private int FONT_ROBOTO = 6;
    private int fontType;
    private int fontName;

    public CustomtextView(Context context) {
        super(context);
        init(null, 0);
    }
    public CustomtextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public CustomtextView(Context context, AttributeSet attrs, int defStyle) {
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
        }else if (fontName == FONT_KLAVIKA){
            setFontType(Typeface.createFromAsset(getContext().getAssets(), "Klavika.ttf"));
        }else if (fontName == FONT_ROBOTO){
            setFontType(Typeface.createFromAsset(getContext().getAssets(), "Roboto.ttf"));
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
