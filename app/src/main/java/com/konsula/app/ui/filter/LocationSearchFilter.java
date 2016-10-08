package com.konsula.app.ui.filter;

import android.app.Activity;
import android.content.Context;

import com.konsula.app.AppController;
import com.konsula.app.service.model.LocationSearchModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SamuelSonny on 29-Jan-16.
 */
public class LocationSearchFilter extends BaseFilter{
    private Context context1;

    public LocationSearchFilter(Context context, ArrayList<String> fullList) {
        super(context, fullList);
        this.context1 = context;

    }

    @Override
    public void performFiltering(final String input, final FilterResults results) {
        String currentLanguage = context.getResources().getConfiguration().locale.getLanguage();
        currentLanguage= (currentLanguage.equals("en") || currentLanguage.equals("EN")) ?"en":"id";
        NetworkManager.getNetworkService(context).locationSearch((((AppController) context.getApplicationContext())).getSessionManager().getToken(), currentLanguage,input.toString(), new Callback<LocationSearchModel>() {
            @Override
            public void success(LocationSearchModel locationSearchModel, Response response) {
                boolean isTokenValid = ((AppController) ((Activity)context1).getApplication()).isTokenValid(locationSearchModel.messages, response,context, new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        performFiltering(input,results);
                    }


                });
                if (isTokenValid) {

                    fullList.clear();
                    for (LocationSearchModel.Result item : locationSearchModel.results) {
                        fullList.add(item.location_name);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
