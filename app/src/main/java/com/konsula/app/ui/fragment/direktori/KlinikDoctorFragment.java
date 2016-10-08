package com.konsula.app.ui.fragment.direktori;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.ui.activity.direktori.AppointmentDoctorActivity;
import com.konsula.app.ui.activity.direktori.DoctorProfileActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.PracticeModel;

import java.util.List;

/**
 * Created by konsula on 12/16/2015.
 */
@SuppressWarnings("ValidFragment")
public class KlinikDoctorFragment extends Fragment {

    private ListView doctorListView;
    private List<PracticeModel.Doctor> doctorList;
    private PracticeModel.Results practice;
    private String currentLanguage;

    public KlinikDoctorFragment(PracticeModel.Results practice) {
        this.doctorList = practice.doctors;
        this.practice = practice;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_klinik_doctor, null, false);
        doctorListView = (ListView) view.findViewById(R.id.klinik_doctor_list_view);
        doctorListView.setAdapter(new DoctorListAdapter(doctorList));
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        return view;
    }

    // create adapter for filtering
    private class DoctorListAdapter extends ArrayAdapter<PracticeModel.Doctor> {

        private List<PracticeModel.Doctor> list;

        public DoctorListAdapter(List<PracticeModel.Doctor> doctorList) {
            super(getActivity(), R.layout.item_klinik_profile_doctor, doctorList);

            this.list = doctorList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.item_klinik_profile_doctor, null);

                holder = new ViewHolder();
                holder.avatarImageView = (ImageView) convertView.findViewById(R.id.item_klinik_avatar_image_view);
                holder.nameDokterTextView = (TextView) convertView.findViewById(R.id.item_klinik_detail_profile_name_text_view);
                holder.klinikTextView = (TextView) convertView.findViewById(R.id.item_klinik_detail_profile_klinik_text_view);
                holder.totalRatingBar = (RatingBar) convertView.findViewById(R.id.item_klinik_profile_doctor_rating_bar);
                holder.totalReviewTextView = (TextView) convertView.findViewById(R.id.item_klinik_detail_profile_total_review_text_view);
                holder.priceTextView = (TextView) convertView.findViewById(R.id.item_klinik_detail_profile_price_text_view);
                holder.actionReview = (ImageButton) convertView.findViewById(R.id.item_klinik_detail_profile_review_image_button);
                holder.bookActionTextView = (LinearLayout) convertView.findViewById(R.id.item_klinik_detail_profile_book_text_view);
                holder.containerLinearLayout = (LinearLayout) convertView.findViewById(R.id.item_klinik_doctor_content_container_linear_layout);
                holder.layoutrating =(LinearLayout) convertView.findViewById(R.id.item_klinik_detail_profile_middle_container_linear_layout);
                holder.spesialisTextView =(TextView) convertView.findViewById(R.id.item_klinik_detail_profile_specialis_text_view);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final PracticeModel.Doctor result = list.get(position);
            holder.spesialisTextView.setText(currentLanguage.equals("en")?result.specialities.get(0).specialization.specialization_english:result.specialities.get(0).specialization.specialization_bahasa);
            holder.nameDokterTextView.setText(result.doctor_name);
            holder.klinikTextView.setText(result.job_category.id);
            if (result.rate.equals("N/A") || result.rate.equals("Konsultasi Gratis") || result.rate.equals("Free Consultation"))
                holder.priceTextView.setText(getString(R.string.mulai_dari) + " N/A" );
            else
                holder.priceTextView.setText(getString(R.string.mulai_dari) + " " + ((AppController) getActivity().getApplication()).getDefaultPriceFormat(result.currency, Double.parseDouble(result.rate)));
           holder.layoutrating.setVisibility(result.star_rating == 0.00?View.INVISIBLE:View.VISIBLE);
            holder.totalRatingBar.setRating(result.star_rating);
            holder.totalReviewTextView.setText("" + result.total_review);
            ((AppController) getActivity().getApplication()).displayImage(getActivity(),result.photos.primary.medium_image, holder.avatarImageView);
            holder.bookActionTextView.setVisibility(result.online.equals("Y")?View.VISIBLE:View.GONE);
            holder.bookActionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AppointmentDoctorActivity.class);
                    intent.putExtra(AppointmentDoctorActivity.DOCTOR_NAME, result.doctor_name);
                    intent.putExtra(AppointmentDoctorActivity.DOCTOR_IMAGE, result.photos.primary.medium_image);
                    intent.putExtra(AppointmentDoctorActivity.DOCTOR_TITLE, ((AppController) getActivity().getApplication()).getSessionManager().getLanguage().equalsIgnoreCase("en") ? result.job_category.en : result.job_category.id);
                    intent.putExtra(AppointmentDoctorActivity.LOCATION_NAME, practice.practice_name);
                    intent.putExtra(AppointmentDoctorActivity.LOCATION_ADDRESS, practice.location);
                    intent.putExtra(AppointmentDoctorActivity.DOCTOR_ID, result.doctor_id);
                    intent.putExtra(AppointmentDoctorActivity.PRACTICE_ID, practice.practice_id);
                    startActivity(intent);
                }
            });
            holder.avatarImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString(DoctorProfileActivity.AVATAR_IMAGE, result.photos.primary.large_image);
                    android.app.FragmentManager fm = getActivity().getFragmentManager();
                    ImagePreviewFragment powerDialog = new ImagePreviewFragment();
                    powerDialog.setArguments(bundle);
                    powerDialog.show(fm, "fragment_power");
                }
            });

            holder.containerLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), DoctorProfileActivity.class);
                    intent.putExtra("doctorUri", result.doctor_url.substring(result.doctor_url.lastIndexOf("/") + 1));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        TextView nameDokterTextView, priceTextView, klinikTextView, totalReviewTextView;
        LinearLayout bookActionTextView;
        LinearLayout layoutrating;
        ImageButton actionReview;
        ImageView avatarImageView;
        LinearLayout containerLinearLayout;
        RatingBar totalRatingBar;
        TextView spesialisTextView;
    }


}
