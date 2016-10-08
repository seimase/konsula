package com.konsula.app.ui.adapter;

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
import com.konsula.app.service.model.EstoreProductCatalogModel;
import com.konsula.app.ui.activity.estore.EstoreProductActivity;

import java.util.ArrayList;

/**
 * Created by hiantohendry on 4/25/16.
 */
    public class CatalogRecyclerAdapter extends RecyclerView.Adapter<CatalogRecyclerAdapter.ViewHolder> {
        private ArrayList<EstoreProductCatalogModel.Product> mDataset;
        private Context mContext;
        private  Boolean isClick;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public TextView txtName;
            public TextView txtPrice;
            public ImageView imageProduct;
            public RatingBar ratingProduct;
            public ViewHolder(View v) {
                super(v);

            }
        }

        public CatalogRecyclerAdapter(Context context,ArrayList<EstoreProductCatalogModel.Product> myDataset) {
            mDataset = myDataset;
            mContext = context;
            isClick = false;
        }


        @Override
        public CatalogRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estore_product, parent, false);

            ViewHolder vh = new ViewHolder(v);
            vh.mView = v;
            vh.txtName = (TextView)v.findViewById(R.id.item_estore_product_name);
            vh.txtPrice = (TextView)v.findViewById(R.id.item_estore_product_price);
            vh.imageProduct = (ImageView) v.findViewById(R.id.item_estore_product_image);
            vh.ratingProduct = (RatingBar) v.findViewById(R.id.item_estore_product_rating);

            return vh;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final EstoreProductCatalogModel.Product item = mDataset.get(position);
            holder.txtName.setText(item.product_name);
            if(item.products_cheapest_price!=null)
            {
                holder.txtPrice.setText(((AppController)mContext).getDefaultPriceFormat(item.products_cheapest_price.currency,Double.parseDouble(item.products_cheapest_price.sell_price)));
                holder.txtPrice.setVisibility(View.VISIBLE);
            }
            else
            holder.txtPrice.setVisibility(View.GONE);

            holder.ratingProduct.setRating(item.practice.star_rating);
            ((AppController)mContext).displayImage(mContext,item.photos.primary.medium_image,holder.imageProduct);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isClick) {
                        isClick = true;
                        Intent a = new Intent(mContext,EstoreProductActivity.class);
                        a.putExtra(EstoreProductActivity.IDENTITY_URI,item.identity_uri);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(a);
                    } else {
                        isClick = false;
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }


}
