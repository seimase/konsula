package com.konsula.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.ui.activity.direktori.KlinikProfileActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.SearchKlinikModel;

/**
 * Created by SamuelSonny on 30-Jan-16.
 */
public class SearchResultKlinikAdapter extends ArrayAdapter<SearchKlinikModel.Practice>{

    public SearchResultKlinikAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String currentLanguage = getContext().getResources().getConfiguration().locale.getLanguage();
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_klinik_result, null);
            holder = new ViewHolder();
            holder.avatarImageView = (ImageView) convertView.findViewById(R.id.item_result_search_avatar_image_view);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_name_text_view);
            holder.specialistTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_specialis_text_view);
            holder.klinikTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_text_view);
            holder.priceTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_price_text_view);
            holder.totalRatingBar = (RatingBar) convertView.findViewById(R.id.item_result_detail_profile_total_rating);
            holder.totalReviewTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_total_review_text_view);
            holder.bookTextView = (LinearLayout) convertView.findViewById(R.id.item_result_detail_profile_book_layout);
            holder.ratinglayout =(LinearLayout) convertView.findViewById(R.id.item_result_detail_profile_middle_container_linear_layout);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final SearchKlinikModel.Practice item = getItem(position);
        AppController.getInstance().displayImage(getContext(),item.photos.primary.medium_image, holder.avatarImageView);
        holder.nameTextView.setText(item.practice_name);
        holder.klinikTextView.setText(item.area_name+", "+item.city_name);
        if (currentLanguage.equals("en") || currentLanguage.equals("EN")){
            holder.specialistTextView.setText(item.place_english);
        }else {
            holder.specialistTextView.setText(item.place_bahasa);
        }
        try {
            holder.priceTextView.setText(item.min_rate.equals(SearchKlinikModel.NA) ? "" : getContext().getResources().getString(R.string.mulai_dari) + " "+ AppController.getInstance().getDefaultPriceFormat(item.currency, Double.parseDouble(item.min_rate)));
        }catch (Exception e){
            holder.priceTextView.setText(item.min_rate);
        }

        if (item.min_rate.equals(SearchKlinikModel.NA)){
            holder.priceTextView.setText(getContext().getResources().getString(R.string.mulai_dari) + " " + SearchKlinikModel.NA);
        }

        holder.ratinglayout.setVisibility(item.star_rating == 0.00?View.INVISIBLE:View.VISIBLE);
        //holder.priceTextView.setVisibility(item.min_rate.equals(SearchKlinikModel.NA) ? View.GONE : View.VISIBLE);
        holder.totalRatingBar.setRating((float) item.star_rating);
        holder.totalReviewTextView.setText(item.total_review + "");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppController.checkConnection(getContext())){
                    Intent intent = new Intent(getContext(), KlinikProfileActivity.class);
                    intent.putExtra("praticeUri", item.identity_uri);
                    getContext().startActivity(intent);
                }else{
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }

            }
        });
        return convertView;
    }

    private class ViewHolder{
        ImageView avatarImageView;
        TextView nameTextView;
        TextView specialistTextView;
        TextView klinikTextView;
        TextView priceTextView;
        RatingBar totalRatingBar;
        TextView totalReviewTextView;
        LinearLayout bookTextView;
        LinearLayout ratinglayout;
    }
}