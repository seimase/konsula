package com.konsula.app.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.PracticeModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hiantohendry on 1/31/16.
 */
public class CoverImageAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<PracticeModel.Others>  mResources;
    int layout;

    public CoverImageAdapter(Context context,List<PracticeModel.Others>  resource,int layout) {
        mResources = resource;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(layout, container, false);
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.header_cover_image);
        ((AppController) mContext).displayImage(mContext,mResources.get(position).medium_image, imageView);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
