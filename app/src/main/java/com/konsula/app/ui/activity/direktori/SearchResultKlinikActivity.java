package com.konsula.app.ui.activity.direktori;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.SearchKlinikModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.SearchResultKlinikAdapter;
import com.konsula.app.util.RefreshTokenCallback;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/2/2015.
 */
public class SearchResultKlinikActivity extends SearchResultDoctorActivity {
    @Override
    protected void init() {
        super.init();
        adapter = new SearchResultKlinikAdapter(this);
        // sortBy = SearchFilterCriteriaActivity.SORT_BY_OPERATION_DESC;
        sortBy = "";
        from_clinic = true;

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchResultKlinikActivity.this, SearchFilterActivity.class);
                //i.putExtra("location", locationTitleTextView.getText().toString());
                i.putExtra("title", searchTitleTextView.getText().toString());
                i.putExtra("from_clinic", true);
                i.putExtra("from_result", true);
                startActivity(i);
            }
        });
    }

    @Override
    protected void initAttribute() {
        super.initAttribute();
        kategoriRadioGroup.check(R.id.radio_search_filter_klinik);
    }

    @Override
    protected void loadItem(final boolean afterSearch) {
        if (isLoading == false) {
            isLoading = true;
            //itemLoadingView.start();

            Log.d("test", locationText.getText().toString() + " " + keyword + " " + country + " " + loadedPage);
            if (loadedPage == 1) {
                dialog.show();
            } else {
                itemLoadingView.start();
            }
            searchPractice(afterSearch);
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        keyword = multiSearchResultArrayList.get(i).toLowerCase().replace(" ", "-");
//        if(kategoriRadioGroup.getCheckedRadioButtonId() == R.id.radio_search_filter_klinik) {
//            loadedPage = 1;
//            isLoading = false;
//            loadItem(true);
//        }else{
//            Intent intent = new Intent(this, SearchResultDoctorActivity.class);
//            intent.putExtra("location_state", locationText.getText().toString());
//            intent.putExtra("keyword", keyword);
//            intent.putExtra("keyword_name", keywordText.getText().toString());
//            intent.putExtra("country", "Indonesia");
//            startActivity(intent);
//            finish();
//        }
//    }

    private void searchPractice(final boolean afterSearch){
        NetworkManager.getNetworkService(this).searchPractice(((AppController) getApplication()).getSessionManager().getToken(), locationState, locality, keyword, country, latitude, longitude, loadedPage, sortBy, minRate, maxRate, minHour, maxHour.equals("23.59") ? "" : maxHour,minOps,maxOps, days, new Callback<SearchKlinikModel>() {
            @Override
            public void success(SearchKlinikModel searchKlinikModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(searchKlinikModel.messages, response,SearchResultKlinikActivity.this, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        searchPractice(afterSearch);
                    }


                });
                if (isTokenValid) {

                    String current = getResources().getConfiguration().locale.getLanguage();
                    try {
                        if (current.equals("en") || current.equals("EN")) {
                            searchTitleTextView.setText("" + searchKlinikModel.results.label.keyword.en);
                        } else {

                            searchTitleTextView.setText("" + searchKlinikModel.results.label.keyword.id);
                        }
                    } catch (Exception e) {

                    }

                    locationTitleTextView.setText(getIntent().getStringExtra("location_text"));
//                    if(loadedPage == 1) {
//
//                    }else{
                    dialog.dismiss();
//                    }

                    try {
                        if (loadedPage == 1) {
                            adapter.clear();

                            if (searchKlinikModel.results.practices == null) {
                                DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder2 = new AlertDialog.Builder(SearchResultKlinikActivity.this);
                                builder2.setMessage("Sorry, no available search result data practice.").setPositiveButton("Dismiss", dialogClickListener2).show();

                            }
                        }
                        if (searchKlinikModel.results.practices != null) {
                            adapter.addAll(searchKlinikModel.results.practices);
                            isLoading = false;
                            loadedPage++;
                            if (afterSearch) {
                                closeSearch(null);
                            }

                            if (loadedPage == 1 && latitude != 0.0 && locationState.equals("")) {
                                appController.doDialog(SearchResultKlinikActivity.this, "GPS Location is off");
                                dialog.dismiss();
                            }

                        } else {
                            if (loadedPage == 1) {
                                dialog.dismiss();
                            } else {
                                resultListView.removeFooterView(itemLoadingView);
                            }
                        }
                    } catch (Exception ex) {
                        dialog.dismiss();
                        Log.e("error", ex.getMessage().toString());
                    }
                    itemLoadingView.stop();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("error", error.getMessage());
                itemLoadingView.stop();
                dialog.dismiss();
            }
        });
    }
}
