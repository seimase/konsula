package com.konsula.app.service.net;

/**
 * Created by hiantohendry on 1/27/16.
 */

import android.content.Context;

import com.konsula.app.AppConstant;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;

public class NetworkManager {

    public static NetworkManager instance;
    private Context context;
    public static NetworkManager getInstance()
    {
        if(instance == null) {
            instance = new NetworkManager();
        }

        return instance;
    }


    public RestAdapter getRestAdapter(Context context){
        this.context = context;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(AppConstant.DOMAIN_URL + AppConstant.API_VERSION )
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return restAdapter;
    }



    public String getErrorMessage(RetrofitError error) {
        String errorMessage = "";
        if (error != null) {
            if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                for (Header header : error.getResponse().getHeaders()) {
                    try {
                        if (header.getName().equals("Error-Message")) {
                            if (header.getValue().toString().equals("")) {
                                errorMessage = "Server Error";
                            } else {
                                errorMessage = header.getValue().toString();
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            } else if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                errorMessage = "Timeout error";
            } else if (error.getKind().equals(RetrofitError.Kind.CONVERSION)) {
                errorMessage = error.getMessage();
            } else if (error.getKind().equals(RetrofitError.Kind.UNEXPECTED)) {
                errorMessage = error.getMessage();
            }
        }

        return errorMessage;
    }

    public static NetworkService getNetworkService(Context context){
        return NetworkManager.getInstance().getRestAdapter(context).create(NetworkService.class);
    }

}
