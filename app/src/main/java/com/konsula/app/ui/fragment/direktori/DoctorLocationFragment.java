package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.direktori.DoctorReviewActivity;
import com.konsula.app.ui.activity.direktori.GoogleMapActivity;
import com.konsula.app.R;
import com.konsula.app.ui.activity.direktori.ScheduleDoctorActivity;
import com.konsula.app.service.model.DoctorModel;
import com.konsula.app.service.model.DoctorReviewModel;
import com.konsula.app.service.model.SearchKlinikModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.DoctorScheduleAdapter;
import com.konsula.app.ui.adapter.ImageLocationAdapter;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/18/2015.
 */
@SuppressWarnings("ValidFragment")
public class DoctorLocationFragment extends Fragment {
    private TextView showScheduleTextView;

    private DoctorModel.Practice practice;
    private int doctorId;
    private String doctorName;
    private CancelableCallback cancelableCallback;
    private boolean isadd = false;

    public DoctorLocationFragment(DoctorModel.Practice practice, int doctorId, String doctorName) {
        this.doctorId = doctorId;
        this.practice = practice;
        this.doctorName = doctorName;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView tvSeeMap;
    TextView locationName;
    TextView locationCategory;
    TextView locationAddress;

    RecyclerView imageRecycler;
    RecyclerView doctorScheduleRecycler;
    TextView locationPrice;
    LinearLayout doctorReviewLayout;
    TextView doctorReviewTextView;
    final public static String AVATAR_IMAGE = "avatarImage";
    private String currentLanguage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_location, null, false);

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        showScheduleTextView = (TextView) view.findViewById(R.id.profile_doctor_location_show_schedule_text_view);
        SpannableString content2 = new SpannableString("" + showScheduleTextView.getText().toString());
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        showScheduleTextView.setText(content2);
        doctorReviewTextView = (TextView) view.findViewById(R.id.doctor_review_other_review_text_view);

        // set listener
        showScheduleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleDoctorActivity.class);
                startActivity(intent);
            }
        });


        doctorReviewLayout = (LinearLayout) view.findViewById(R.id.doctor_location_review_layout);
        locationName = (TextView) view.findViewById(R.id.doctor_location_name_text_view);
        locationCategory = (TextView) view.findViewById(R.id.doctor_location_category_text_view);
        locationAddress = (TextView) view.findViewById(R.id.doctor_location_address_text_view);
        imageRecycler = (RecyclerView) view.findViewById(R.id.practice_image_recycler);
        imageRecycler.setHasFixedSize(true);
        try {
                if (practice.photos.others == null)
                    practice.photos.others = new ArrayList<>();

                if (!isadd ) {
                    practice.photos.others.add(practice.photos.primary);
                    isadd = true;
                }




        } catch (Exception e) {
            e.printStackTrace();
        }
        imageRecycler.setAdapter(new ImageLocationAdapter(getActivity().getApplicationContext(), practice.photos.others, R.layout.item_image_location, new ImageLocationAdapter.OnImageClicked() {
            @Override
            public void onImageClicked(String imgUrl) {

                Bundle bundle = new Bundle();
                bundle.putString(AVATAR_IMAGE, imgUrl);
                android.app.FragmentManager fm = getActivity().getFragmentManager();
                ImagePreviewFragment powerDialog = new ImagePreviewFragment();
                powerDialog.setArguments(bundle);
                powerDialog.show(fm, "fragment_power");
            }
        }));
        imageRecycler.setClickable(true);
        imageRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        doctorScheduleRecycler = (RecyclerView) view.findViewById(R.id.doctor_location_schedule_recycler);
        doctorScheduleRecycler.setHasFixedSize(true);
        if (practice.schedule.summary_schedule_arr != null) {
            doctorScheduleRecycler.setAdapter(new DoctorScheduleAdapter(practice.schedule.summary_schedule_arr, R.layout.item_doctor_schedule));
        }
        doctorScheduleRecycler.setClickable(true);
        //  doctorScheduleRecycler.setLayoutManager(new WrappableGridLayoutManager(getActivity(), 2));

        locationName.setText(practice.practice_name);
        locationAddress.setText(practice.address);
        locationCategory.setText(practice.category_place);

        locationPrice = (TextView) view.findViewById(R.id.doctor_location_price_text_view);
        if (practice.schedule.rate.contains(" ")) {
            locationPrice.setText(getResources().getString(R.string.mulai_dari) + " " + practice.schedule.rate);
        } else if (practice.schedule.rate != null)
            locationPrice.setText(getResources().getString(R.string.mulai_dari) + " " + (!practice.schedule.rate.equals(SearchKlinikModel.NA) ? ((AppController) getActivity().getApplication()).getDefaultPriceFormat(practice.currency, Double.parseDouble(practice.schedule.rate)) : practice.min_rate));

        tvSeeMap = (TextView) view.findViewById(R.id.tvSeeMap);
        SpannableString content = new SpannableString("" + tvSeeMap.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvSeeMap.setText(content);
        tvSeeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GoogleMapActivity.class);
                i.putExtra("longitude", Double.parseDouble(practice.longitude));
                i.putExtra("latitude", Double.parseDouble(practice.latitude));
                i.putExtra("title", practice.practice_name);
                startActivity(i);
            }
        });


        getDoctorReview(doctorId, doctorName, inflater);
        return view;
    }


    private void getDoctorReview(final int doctorId, final String doctorName, final LayoutInflater inflater) {
        cancelableCallback = new CancelableCallback<DoctorReviewModel>() {


            @Override
            protected void onSuccess(DoctorReviewModel doctorReviewModel, Response response) {
                boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(doctorReviewModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getDoctorReview(doctorId, doctorName, inflater);
                    }

                });
                if (isTokenValid) {
                    ((AppController) getActivity().getApplication()).getSessionManager().setDoctorReview(doctorReviewModel.results);
                    if (doctorReviewModel.results != null) {
                        if (doctorReviewModel.results.review_list.size() > 0) {
                            SpannableString content = new SpannableString(getString(R.string.lihat_review_lain).replace("{count}", doctorReviewModel.results.summary.review_total) + "");
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            doctorReviewTextView.setText(content);
                            doctorReviewTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent a = new Intent(getActivity(), DoctorReviewActivity.class);
                                    a.putExtra(DoctorReviewActivity.DOCTOR_NAME, doctorName);
                                    a.putExtra(DoctorReviewActivity.DOCTOR_ID, doctorId);
                                    getActivity().startActivity(a);
                                }
                            });

                            View reviewItem = inflater.inflate(R.layout.item_review_doctor, null, false);
                            View divider = reviewItem.findViewById(R.id.review_divider);
                            divider.setVisibility(View.GONE);
                            TextView reviewMessageTextView = (TextView) reviewItem.findViewById(R.id.item_review_message_text_view);
                            TextView reviewNameTextView = (TextView) reviewItem.findViewById(R.id.item_review_name_text_view);
                            TextView reviewDateTextView = (TextView) reviewItem.findViewById(R.id.item_review_date_text_view);
                            if (doctorReviewModel.results.review_list.get(0).feedback.equals(""))
                                reviewMessageTextView.setText("-");
                            else
                                reviewMessageTextView.setText(doctorReviewModel.results.review_list.get(0).feedback);
                            reviewNameTextView.setText(doctorReviewModel.results.review_list.get(0).fullname);
                            reviewDateTextView.setText(doctorReviewModel.results.review_list.get(0).date_label);

                            doctorReviewLayout.addView(reviewItem);
                        } else
                            doctorReviewTextView.setText(getString(R.string.no_review));

                    } else {
                        doctorReviewTextView.setText(getString(R.string.no_review));
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {

            }
        };
        NetworkManager.getNetworkService(getActivity().getApplicationContext()).getDoctorReview(((AppController) getActivity().getApplication()).getSessionManager().getToken(), currentLanguage, doctorId,0,
                cancelableCallback);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }
}