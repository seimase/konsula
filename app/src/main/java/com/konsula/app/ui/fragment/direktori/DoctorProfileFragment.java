package com.konsula.app.ui.fragment.direktori;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.DoctorModel;

import javax.annotation.Nullable;

/**
 * Created by konsula on 12/18/2015.
 */
@SuppressLint("ValidFragment")
public class DoctorProfileFragment extends Fragment {

    private DoctorModel.Results results = null;
    String[] parts;
    public DoctorProfileFragment(DoctorModel.Results results) {
        this.results = results;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    LinearLayout layoutEducation;
    LinearLayout layoutExperience;
    LinearLayout layoutMembership;
    LinearLayout layoutAwards;
    LinearLayout layoutServices;
    TextView seeOtherServiceTextView;
    boolean reviewSpan = true;
    String currentLanguage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profil, null, false);
        layoutEducation = (LinearLayout) view.findViewById(R.id.layout_dp_education);
        layoutExperience = (LinearLayout) view.findViewById(R.id.layout_dp_exp);
        layoutMembership = (LinearLayout) view.findViewById(R.id.layout_dp_membership);
        layoutAwards = (LinearLayout) view.findViewById(R.id.layout_dp_awards);
        layoutServices = (LinearLayout) view.findViewById(R.id.layout_dp_services);
        currentLanguage = getContext().getResources().getConfiguration().locale.getLanguage();
        seeOtherServiceTextView = (TextView) view.findViewById(R.id.doctor_review_other_review_text_view);
        if (results.educations != null) {
            for (DoctorModel.Education education : results.educations) {
                View eduItem = inflater.inflate(R.layout.item_estore_contact_detail, null, false);
                TextView eduProgram = (TextView) eduItem.findViewById(R.id.tv_estore_contact);
                eduProgram.setText(education.education);
                layoutEducation.addView(eduItem);
            }
        }

        if (results.experiences != null) {
            for (DoctorModel.Experience experience : results.experiences) {
                View expItem = inflater.inflate(R.layout.item_dp_experience, null, false);
                TextView expYear = (TextView) expItem.findViewById(R.id.tv_dp_exp_year);
                TextView exp = (TextView) expItem.findViewById(R.id.tv_dp_exp);
                if (experience.start.equals("") || experience.end.equals(""))
                    expYear.setText("");
                else
                    expYear.setText(experience.start + " - " + experience.end + ", ");

                try {
                    parts = experience.experience.split(", ");
                    String string = experience.experience;

                    if (parts[parts.length - 1].equals(experience.start + " - " + experience.end))
                        //if ((string.substring(string.lastIndexOf(", ") + 1).equals(" " + experience.start + " - " + experience.end)))
                        exp.setText(parts[0]);
                    else
                        exp.setText(experience.experience);
                } catch (Exception e) {
                    exp.setText(experience.experience);
                }

                layoutExperience.addView(expItem);
            }
        }

        if (results.memberships != null) {
            for (DoctorModel.Membership membership : results.memberships) {
                View memberItem = inflater.inflate(R.layout.item_dp_general, null, false);
                TextView memberShip = (TextView) memberItem.findViewById(R.id.tv_dp_general);
                memberShip.setText(membership.organization_name);
                layoutMembership.addView(memberItem);
            }
        }
        if (results.awards != null) {
            for (DoctorModel.Award award : results.awards) {
                View awardItem = inflater.inflate(R.layout.item_dp_general, null, false);
                TextView tvAward = (TextView) awardItem.findViewById(R.id.tv_dp_general);
                tvAward.setText(award.award + ((award.year == null) ? "" : " - " + award.year));
                layoutAwards.addView(awardItem);
            }
        }


        if (results.services != null) {
            for (int i = 0; i < results.services.size(); i++) {
                DoctorModel.Service service1 = results.services.get(i);

                View serviceItem = inflater.inflate(R.layout.item_dp_services, null, false);
                // TextView tvServiceName = (TextView) serviceItem.findViewById(R.id.item_service_name_text_view);
                //  tvServiceName.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? service1.service_english : service1.service_bahasa);
                // tvServiceName.setText(service1.service_bahasa);
//                if ((i + 1) < results.services.size()) {
                    TextView tvServiceDesc = (TextView) serviceItem.findViewById(R.id.item_service_name_text_view);
                    DoctorModel.Service service2 = results.services.get(i);
                    tvServiceDesc.setText((currentLanguage.equals("en") || currentLanguage.equals("EN")) ? service2.service_english : service2.service_bahasa);
//                }
                layoutServices.addView(serviceItem);
            }

            seeOtherServiceTextView.setText(getString(R.string.lihat_service_lain).replace("{count}", results.services.size() + ""));

            seeOtherServiceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!reviewSpan) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        layoutServices.setLayoutParams(lp);
                        reviewSpan = true;
                        seeOtherServiceTextView.setText(getString(R.string.lihat_sedikit));

                    } else {
                        int height = (int) (layoutServices.getHeight() * 0.4f);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                        layoutServices.setLayoutParams(lp);
                        reviewSpan = false;
                        seeOtherServiceTextView.setText(getString(R.string.lihat_service_lain).replace("{count}", results.services.size() + ""));

                    }
                }
            });

            layoutServices.post(new Runnable() {
                @Override
                public void run() {
                    int height = (int) (layoutServices.getHeight() * 0.4f);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                    layoutServices.setLayoutParams(lp);
                    reviewSpan = false;
                }
            });

        } else {
            seeOtherServiceTextView.setText("-");
        }


        return view;
    }



}