package com.konsula.app.ui.fragment.direktori;

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

import com.konsula.app.service.model.DoctorAppointmentModel;
import com.konsula.app.service.model.DoctorModel;
import com.konsula.app.ui.activity.direktori.GoogleMapActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.ui.custom.WrappableGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by konsula on 12/16/2015.
 */
@SuppressWarnings("ValidFragment")
public class KlinikInfoFragment extends Fragment {

    public interface OnKlinikInfoListener{
        public void onReviewClicked();
    }

    private OnKlinikInfoListener listener;

    PracticeModel.Results mResource;
    public KlinikInfoFragment(PracticeModel.Results resource,OnKlinikInfoListener listener){
        mResource = resource;
        this.listener =listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    TextView tvSeeMap;
    TextView tvKlinikAddress;
    LinearLayout mListServices;
    RecyclerView mListOperation;
    TextView tvOtherServices;
    String currentLanguage;
//    TextView textViewdayoperasional;
//    TextView textViewhouroperasional;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_klinik_info, null, false);
        tvKlinikAddress = (TextView)view.findViewById(R.id.klinik_info_address_text_view);
        tvOtherServices = (TextView)view.findViewById(R.id.klinik_profile_other_service_text_view);
        SpannableString content = new SpannableString(getString(R.string.lihat_service_lain).replace("{count}", +mResource.services.size() + ""));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvOtherServices.setText(content);
        currentLanguage = getContext().getResources().getConfiguration().locale.getLanguage();
        mListServices = (LinearLayout) view.findViewById(R.id.klinik_info_service_list_recycler_view);
        if (mResource.services!=null){
            int total = mResource.services.size()>4?4:mResource.services.size();
            for (int i = 0; i < total; i++) {
                PracticeModel.Service service1 = mResource.services.get(i);

                View serviceItem = inflater.inflate(R.layout.item_dp_services, null, false);
                TextView tvServiceDesc = (TextView) serviceItem.findViewById(R.id.item_service_name_text_view);
                tvServiceDesc.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? service1.service_english : service1.service_bahasa);
//                }
                mListServices.addView(serviceItem);
            }
        }

        mListOperation = (RecyclerView)view.findViewById(R.id.klinik_info_operasional_list_recycler_view);
        mListOperation.setHasFixedSize(true);
        if (mResource.summary_schedule_arr != null) {
            mListOperation.setAdapter(new OperationsAdapter(mResource.summary_schedule_arr, R.layout.item_klinik_profile_operasional));
        }
        else {
            mResource.summary_schedule_arr = new ArrayList<PracticeModel.SummaryScheduleArr>();
            PracticeModel.SummaryScheduleArr noSchedule = new PracticeModel().new SummaryScheduleArr();
            noSchedule.day = getActivity().getResources().getString(R.string.text_no_schedule);
            noSchedule.hour = "";
            mResource.summary_schedule_arr.add(noSchedule);
            mListOperation.setAdapter(new OperationsAdapter(mResource.summary_schedule_arr, R.layout.item_klinik_profile_operasional));
        }

        mListOperation.setClickable(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListOperation.setLayoutManager(linearLayoutManager);
//        mListServices.setLayoutManager(linearLayoutManager);
     //   mListServices.setLayoutManager(new WrappableGridLayoutManager(getActivity(), 1));
        tvOtherServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReviewClicked();
            }
        });

        tvSeeMap = (TextView)view.findViewById(R.id.tvSeeMap);
        SpannableString seeMapText = new SpannableString("" + tvSeeMap.getText().toString());
        seeMapText.setSpan(new UnderlineSpan(), 0, seeMapText.length(), 0);
        tvSeeMap.setText(seeMapText);
        tvSeeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GoogleMapActivity.class);
                i.putExtra("longitude", Double.parseDouble(mResource.longitude));
                i.putExtra("latitude", Double.parseDouble(mResource.latitude));
                i.putExtra("title", mResource.practice_name);
                startActivity(i);
            }
        });
//        if (mResource.summary_schedule_arr!=null){
//            textViewdayoperasional.setText(mResource.summary_schedule_arr.get(0).day);
//            textViewhouroperasional.setText(mResource.summary_schedule_arr.get(0).hour);
//        }else {
//            textViewdayoperasional.setText(getActivity().getResources().getString(R.string.text_no_schedule));
//        }
        tvKlinikAddress.setText(mResource.location);
        return view;
    }


    public class OperationsAdapter extends RecyclerView.Adapter<OperationsAdapter.ViewHolder> {

        private List<PracticeModel.SummaryScheduleArr> items;
        private int itemLayout;

        public OperationsAdapter(List<PracticeModel.SummaryScheduleArr> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
            return new ViewHolder(v);
        }

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            PracticeModel.SummaryScheduleArr item = items.get(position);
            holder.textViewday.setText(items.get(position).day);
            holder.textViewhour.setText(items.get(position).hour);
            holder.itemView.setTag(item);
        }

        @Override public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewday;
            public TextView textViewhour;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewday = (TextView) itemView.findViewById(R.id.klinik_info_opersional_day_text_view);
                textViewhour = (TextView) itemView.findViewById(R.id.klinik_info_opersional_hour_text_view);
            }
        }
    }
}
