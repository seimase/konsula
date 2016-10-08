package com.konsula.app.ui.filter;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by SamuelSonny on 29-Jan-16.
 */
public class MultiSearchFilter extends BaseFilter{
    private String searchType;
    private String location;
    public MultiSearchFilter(Context context, ArrayList<String> fullList) {
        super(context, fullList);
    }

    @Override
    public void performFiltering(final String input, final FilterResults results) {
//        String currentLanguage = context.getResources().getConfiguration().locale.getLanguage();
//        currentLanguage= (currentLanguage.equals("en") || currentLanguage.equals("EN")) ?"en":"id";
//        NetworkManager.getNetworkService(context).multiSearch((((AppController) context.getApplicationContext())).getSessionManager().getToken(), currentLanguage,searchType, input.toString(), location, new Callback<MultiSearchModel>() {
//            @Override
//            public void success(MultiSearchModel multiSearchModel, Response response) {
//                boolean isTokenValid = ((AppController) ((Activity)context).getApplication()).isTokenValid(multiSearchModel.messages, response,context, new RefreshTokenCallback() {
//                    @Override
//                    public void onRefreshTokenComplete() {
//                        performFiltering(input,results);
//                    }
//
//
//                });
//                if (isTokenValid) {
//
//                    fullList.clear();
//                    for (MultiSearchModel.Result item : multiSearchModel.results) {
//                        if(item.target.equals(MultiSearchModel.TARGET_SEARCH_PAGE)) {
//                            fullList.add(item.bahasa_name);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}