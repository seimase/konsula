package com.konsula.app.ui.activity.estore;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreSearchProductModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class EstoreProductRecyclerViewAdapter extends RecyclerView.Adapter<EstoreProductRecyclerViewAdapter.ViewHolder> {

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(EstoreSearchProductModel.Item item);
    }

    private final List<EstoreSearchProductModel.Item> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    public EstoreProductRecyclerViewAdapter(Context context, List<EstoreSearchProductModel.Item> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_estore_category_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.txtName.setText(holder.mItem.product_name);
        if(holder.mItem.product_min_price!=null)
        {
            holder.txtPrice.setText(((AppController)mContext).getDefaultPriceFormat("IDR",holder.mItem.product_min_price));
            holder.txtPrice.setVisibility(View.VISIBLE);
        }
        else
            holder.txtPrice.setVisibility(View.GONE);

        holder.ratingProduct.setRating(holder.mItem.practice.star_rating);
        Picasso.with(mContext).load(holder.mItem.photos.primary.medium_image).into(holder.imageProduct);
        //  ((AppController)mContext).displayImage(mContext,holder.mItem.photos.primary.medium_image,holder.imageProduct);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public  View mView;
        public TextView txtName;
        public TextView txtPrice;
        public ImageView imageProduct;
        public RatingBar ratingProduct;

        public EstoreSearchProductModel.Item mItem;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            txtName = (TextView)v.findViewById(R.id.item_estore_product_name);
            txtPrice = (TextView)v.findViewById(R.id.item_estore_product_price);
            imageProduct = (ImageView) v.findViewById(R.id.item_estore_product_image);
            ratingProduct = (RatingBar) v.findViewById(R.id.item_estore_product_rating);
        }

        @Override
        public String toString() {
            return super.toString() + "";
        }
    }
}
