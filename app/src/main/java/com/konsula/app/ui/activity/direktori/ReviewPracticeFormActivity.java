package com.konsula.app.ui.activity.direktori;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.service.model.SimpleModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.Converter;
import com.konsula.app.util.RefreshTokenCallback;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SamuelSonny on 16-Feb-16.
 */
public class ReviewPracticeFormActivity extends ReviewDoctorFormActivity{
    @Override
    protected void init() {
        reviewUri = getIntent().getStringExtra("practice_uri");
        getpracticeProfile();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        doctorProvileAvatarImageView.setVisibility(View.GONE);
        params.setMargins(Converter.dpToPx(this, 20), 0, Converter.dpToPx(this, 20), 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.profile_cover_gape_linear_layout);
        profileIdentityLinearLayout.setLayoutParams(params);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try{
            doctorId = Integer.parseInt(reviewAdapter.getItem(i).id);
        }catch(Exception e){

        }
    }

    private void getpracticeProfile(){
        NetworkManager.getNetworkService(this).getPracticeProfile(((AppController) getApplication()).getSessionManager().getToken(),currentLanguage, reviewUri, new Callback<PracticeModel>() {
            @Override
            public void success(PracticeModel practiceModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(practiceModel.messages, response,ReviewPracticeFormActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getpracticeProfile();
                    }


                });
                if (isTokenValid) {
                    layoutloading.setVisibility(View.GONE);
                    l_view.setVisibility(View.VISIBLE);
                    practiceId = practiceModel.results.practice_id;
                    try {
                        doctorId = practiceModel.results.doctors.get(0).doctor_id;
                    }catch(Exception e){
                        btnBook.setVisibility(View.GONE);
                    }
                    AppController.getInstance().displayImage(ReviewPracticeFormActivity.this,practiceModel.results.photos.primary.large_image, profileCoverPageImageView);
                    reviewNameTitleTextView.setText(practiceModel.results.practice_name);
                    reviewNameTextView.setText(practiceModel.results.practice_name);
                    reviewDescTextView.setText(practiceModel.results.category.id);
                    reviewAdapter.clear();
                    reviewAdapter.add(new SimpleModel(0 + "",getResources().getString(R.string.dialog_choose)));
                    for(PracticeModel.Doctor item : practiceModel.results.doctors){
                        reviewAdapter.add(new SimpleModel(item.doctor_id + "", item.doctor_name));
                    }
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ReviewPracticeFormActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                layoutloading.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            }
        });
    }
}
