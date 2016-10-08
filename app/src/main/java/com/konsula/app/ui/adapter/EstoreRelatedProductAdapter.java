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
import com.konsula.app.service.model.EstoreProductModel;
import com.konsula.app.ui.activity.estore.EstoreProductActivity;

import java.util.ArrayList;

/**
 * Created by hiantohendry on 5/25/16.
 */
public class EstoreRelatedProductAdapter extends RecyclerView.Adapter<EstoreRelatedProductAdapter.ViewHolder>{

    private ArrayList<EstoreProductModel.SimilarProduct> mDataset;
    private Context mContext;

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

    public EstoreRelatedProductAdapter(Context context,ArrayList<EstoreProductModel.SimilarProduct> myDataset) {
        mDataset = myDataset;
        mContext = context;
    }


    @Override
    public EstoreRelatedProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
        final EstoreProductModel.SimilarProduct item = mDataset.get(position);
        holder.txtName.setText(item.product_name);
        holder.txtPrice.setText(AppController.getInstance().getDefaultPriceFormat("IDR",item.price));

        holder.ratingProduct.setRating(Float.parseFloat(item.rating_score));
        ((AppController)mContext).displayImage(mContext,item.primary_photo,holder.imageProduct);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a =new Intent(mContext,EstoreProductActivity.class);
                a.putExtra(EstoreProductActivity.IDENTITY_URI,item.identity_uri);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(a);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
