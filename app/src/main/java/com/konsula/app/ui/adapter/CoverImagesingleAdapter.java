package com.konsula.app.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.PracticeModel;

import java.util.List;

/**
 * Created by Konsula on 21/03/2016.
 */
public class CoverImagesingleAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    PracticeModel.Primary mResources;

    public CoverImagesingleAdapter(Context context, PracticeModel.Primary resource) {
        mResources = resource;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResources != null ? 1 : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.fragment_klinik_header_cover, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.header_cover_image);
        ((AppController) mContext).displayImage(mContext,mResources.large_image, imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}