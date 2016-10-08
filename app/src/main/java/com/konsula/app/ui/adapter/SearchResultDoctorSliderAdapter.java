package com.konsula.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.ui.activity.direktori.DoctorProfileActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.SearchDoctorModel;
import com.konsula.app.service.model.SearchKlinikModel;
import com.konsula.app.ui.custom.CustomtextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by SamuelSonny on 02-Feb-16.
 */
public class SearchResultDoctorSliderAdapter extends PagerAdapter {
    private SearchDoctorModel.Doctor data;
    private Context context;
    private ArrayList<View> views;
    public HashMap<Integer, ArrayList<View>> sliderMapping = new HashMap<Integer, ArrayList<View>>();

    public SearchResultDoctorSliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup group = (ViewGroup) views.get(position).getParent();
        if (group != null) {
            group.removeView(views.get(position));
        }
        ((ViewPager) container).addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        // TODO Auto-generated method stub
        return view.equals(obj);
    }

    public void setData(final SearchDoctorModel.Doctor data) {
        this.data = data;
//        if(sliderMapping.containsKey(data.doctor_id)){
//            views = sliderMapping.get(data.doctor_id);
//        }else {
            views = new ArrayList<View>();
       //     sliderMapping.put(data.doctor_id, views);
            for (int i = 0; i < getCount(); i++) {
                View convertView = LayoutInflater.from(context).inflate(R.layout.item_search_doctor_result_slider, null);
                ViewHolder holder = new ViewHolder();
                holder.avatarImageView = (ImageView) convertView.findViewById(R.id.item_result_search_avatar_image_view);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_name_text_view);
                holder.specialistTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_specialis_text_view);
                holder.klinikTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_text_view);
                holder.priceTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_price_text_view);
                holder.totalRatingBar = (RatingBar) convertView.findViewById(R.id.item_result_detail_profile_total_rating);
                holder.location = (CustomtextView) convertView.findViewById(R.id.etLocation);
                holder.totalReviewTextView = (TextView) convertView.findViewById(R.id.item_result_detail_profile_total_review_text_view);
                holder.bookTextView = (LinearLayout) convertView.findViewById(R.id.item_result_detail_profile_book_layout);
                holder.imagecomment = (ImageView) convertView.findViewById(R.id.item_result_detail_profile_review_image_button);
                convertView.setTag(holder);

                AppController.getInstance().displayImage(context,data.photos.primary.medium_image, holder.avatarImageView);
                holder.nameTextView.setText(data.doctor_name);
                String current = convertView.getResources().getConfiguration().locale.getLanguage();
                if (data.specialities == null){
                    if (current.equals("en") || current.equals("EN")) {
                        holder.specialistTextView.setText(data.job_english);
                    }else {
                        holder.specialistTextView.setText(data.job_bahasa);
                    }
                }else {
                    if (current.equals("en") || current.equals("EN")) {
                        holder.specialistTextView.setText(data.specialities != null && data.specialities.size() > 0 ? data.specialities.get(0).specialization.specialization_english : "");
                    } else {
                        holder.specialistTextView.setText(data.specialities != null && data.specialities.size() > 0 ? data.specialities.get(0).specialization.specialization_bahasa : "");
                    }
                }


    /*if(data.doctor_name.length() > 27 && data.practices.get(position).practice_name.length() > 27){
        holder.klinikTextView.setText(data.practices.get(position).practice_name.substring(0, 27) + "...");
    }else{*/
                //holder.klinikTextView.setText(data.practices.get(i).practice_name);
                //}
                String[] s = data.practices.get(i).location.split(",");
                holder.klinikTextView.setText(s[0]+", "+s[1]);
//                holder.klinikTextView.setText( data.area_name+", "+data.city_name);
              //  holder.location.setText(data.practices.get(i).location);
                if (data.practices.get(i).rate.equals("")) {
                    data.practices.get(i).rate = SearchKlinikModel.NA;
                } else if (data.practices.get(i).rate.contains(" ")) {
                    holder.priceTextView.setText(convertView.getResources().getString(R.string.mulai_dari) + " " + convertView.getResources().getString(R.string.gratis_konsultasi));
                } else if ((data.practices.get(i).rate.equals(SearchKlinikModel.NA))) {
                    holder.priceTextView.setText(convertView.getResources().getString(R.string.mulai_dari) + " " + SearchKlinikModel.NA);
                } else {
                    holder.priceTextView.setText(convertView.getResources().getString(R.string.mulai_dari) + " " + AppController.getInstance().getDefaultPriceFormat(data.currency, Double.parseDouble(data.practices.get(i).rate)));
                }
                if (data.star_rating == 0) {
                    holder.totalRatingBar.setVisibility(View.INVISIBLE);
                } else {
                    holder.totalRatingBar.setRating((float) data.star_rating);
                }
                if (data.total_review == 0) {
                    holder.totalReviewTextView.setVisibility(View.INVISIBLE);
                    holder.imagecomment.setVisibility(View.INVISIBLE);
                } else {
                    holder.totalReviewTextView.setText(data.total_review + "");
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (AppController.checkConnection(context)){
                            Intent intent = new Intent(context, DoctorProfileActivity.class);
                            intent.putExtra("doctorUri", data.identity_uri);
                            context.startActivity(intent);
                        }else{
                            Toast.makeText(context, context.getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                views.add(convertView);
            }

    }

    public SearchDoctorModel.Doctor getData() {
        return data;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.practices.size();
    }

    private class ViewHolder {
        ImageView avatarImageView;
        TextView nameTextView;
        TextView specialistTextView;
        TextView klinikTextView;
        TextView priceTextView;
        RatingBar totalRatingBar;
        TextView totalReviewTextView;
        TextView location;
        LinearLayout bookTextView;
        ImageView imagecomment;
    }
}
