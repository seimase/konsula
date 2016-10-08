package com.konsula.app.ui.fragment.direktori;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.HomeModel;
import com.konsula.app.service.model.ViewAccountModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.direktori.SearchResultDoctorActivity;
import com.konsula.app.service.model.HomeBaseModel;
import com.konsula.app.ui.activity.transaction.MytransactionHistoryActivity;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 12/15/2015.
 */
public class SearchMenuDoctorFragment extends Fragment {
    private GridView menuGridView = null;
    private ArrayList<HomeBaseModel> menuListDoctor = new ArrayList<HomeBaseModel>();
    private int imageresource = 0;
    MenuAdapter adapter;

    private LinearLayout layoutloading;
    private Button refresh;
    String currentLanguage;
    OnSuccessLoadMenuListener listener;
    public CancelableCallback cancelableCallback;

    public interface OnSuccessLoadMenuListener {
        public void onSuccessLoadMenu();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnSuccessLoadMenuListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement" + OnSuccessLoadMenuListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_menu_doctor, null, false);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        menuListDoctor = new ArrayList<HomeBaseModel>();
        menuGridView = (GridView) view.findViewById(R.id.main_menu_doctor_grid_view);
        getmenu();
        layoutloading = (LinearLayout) view.findViewById(R.id.l_loading);
        refresh = (Button) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutloading.setVisibility(View.VISIBLE);
                getmenu();
            }
        });

        return view;
    }

    private HomeBaseModel getMenu(String link, String resId, String label) {
        HomeBaseModel menuModel = new HomeBaseModel();
        menuModel.setProperty("link", link);
        menuModel.setProperty("icon", resId);
        menuModel.setProperty("label", label);
        return menuModel;
    }

    // create item menu
    private class MenuAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<HomeBaseModel> list;

        // Constructor
        public MenuAdapter(Context context, List<HomeBaseModel> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final HomeBaseModel model = list.get(position);
            final ViewHolder mHolder;
            if (convertView == null) {
                mHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_menu_main, null);
                mHolder.icon = (ImageView) convertView.findViewById(R.id.item_menu_main_icon);
                mHolder.label = (TextView) convertView.findViewById(R.id.item_menu_main_label);
                mHolder.container = (LinearLayout) convertView.findViewById(R.id.item_menu_main_container);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            final String link = model.getProperty("link");
            final String label = model.getProperty("label");

            mHolder.icon.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (AppController.checkConnection(getActivity())){
                                Intent intent = new Intent(getActivity(),SearchResultDoctorActivity.class);
                                intent.putExtra("keyword", link);
                                intent.putExtra("country", "Indonesia");
                                intent.putExtra("location_state", "jakarta");
                                intent.putExtra("locality", "jakarta");
                                intent.putExtra("location_text", "Jakarta");
                                intent.putExtra("keyword_name", label);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
            );

            mHolder.label.setText(label);

            imageresource = getResources().getIdentifier("@drawable/" + model.getProperty("icon"), "drawable", getActivity().getApplicationContext().getPackageName());
            mHolder.icon.setImageDrawable(getResources().getDrawable(imageresource));
            return convertView;
        }

        class ViewHolder {
            LinearLayout container;
            ImageView icon;
            TextView label;
        }
    }
    private void getmenu() {
        cancelableCallback = new CancelableCallback<HomeModel>() {

            @Override
            protected void onSuccess(HomeModel homeModel, Response response) {
                boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(homeModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getmenu();
                    }


                });
                if (isTokenValid) {
                    for (HomeModel.Doctor item : homeModel.results.doctors) {
                        imageresource = getResources().getIdentifier("@drawable/" + item.img_android, "drawable", getActivity().getApplication().getPackageName());
                        if (imageresource != 0) {
                            menuListDoctor.add(getMenu("" + item.keyword, "" + item.img_android, "" + item.label));
                        }
                    }
                    listener.onSuccessLoadMenu();
                    layoutloading.setVisibility(View.GONE);
                    menuGridView.setVisibility(View.VISIBLE);
                    adapter = new MenuAdapter(getActivity(), menuListDoctor);
                    menuGridView.setAdapter(adapter);
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {

                layoutloading.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            }
        };
        NetworkManager.getNetworkService(getActivity()).initHomeDoctor(((AppController) getActivity().getApplication()).getSessionManager().getToken(), currentLanguage, cancelableCallback);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }

}
