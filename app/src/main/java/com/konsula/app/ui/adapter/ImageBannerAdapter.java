package com.konsula.app.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.konsula.app.R;
import com.konsula.app.service.model.PracticeModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Konsula on 17/05/2016.
 */
public class ImageBannerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<PracticeModel.Others> mResources;
    int layout;

    public ImageBannerAdapter (Context context,List<PracticeModel.Others>  resource,int layout) {
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
        Picasso picasso = Picasso.with(mContext);
        picasso.setIndicatorsEnabled(false);
        picasso.load(mResources.get(position).medium_image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .fit().centerInside().placeholder(null)
                .into(imageView, new Callback()
                {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(mContext)
                                .load(mResources.get(position).medium_image)
                                .error(R.drawable.ic_homebanner)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso","Could not fetch image");
                                    }
                                });
                    }
                });
        //  ((AppController) mContext).displayImage(mResources.get(position).medium_image, imageView);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
