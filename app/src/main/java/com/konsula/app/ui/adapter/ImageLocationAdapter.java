package com.konsula.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.DoctorModel;

import java.util.List;

/**
 * Created by hiantohendry on 1/30/16.
 */
public class ImageLocationAdapter extends RecyclerView.Adapter<ImageLocationAdapter.ViewHolder> {

    private List<DoctorModel.Others> items;
    private int itemLayout;
    private Context mContext;
    public interface OnImageClicked{
        public void onImageClicked(String imgUrl);
    }
    private OnImageClicked listener;
    public ImageLocationAdapter(Context context,List<DoctorModel.Others> items, int itemLayout,OnImageClicked listener) {
        mContext = context;
        this.items = items;
        this.itemLayout = itemLayout;
        this.listener = listener;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        final DoctorModel.Others item = items.get(position);
        holder.image.setImageBitmap(null);
        ((AppController)mContext).displayImage(mContext,item.thumb_image, holder.image);
        holder.itemView.setTag(item);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageClicked(item.large_image);
            }
        });
    }

    @Override public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.item_image_location);
        }
    }
}