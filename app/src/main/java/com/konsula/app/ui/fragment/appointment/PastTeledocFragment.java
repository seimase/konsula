package com.konsula.app.ui.fragment.appointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.UpcomingTeledocModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.teledokter.TeledocDetailActivity;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 17/04/2016.
 */
public class PastTeledocFragment extends Fragment {
    private RecyclerView PastListView;
    private LinearLayout viewNodata;
    String currentLanguage;
    int currentpage = 1;
    private CancelableCallback cancelableCallback;
    private SwipeRefreshLayout swipeContainer;

    private Button refresh;
    private LinearLayout layoutloading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_teledoc_history, container, false);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        PastListView = (RecyclerView) view.findViewById(R.id.help_list_view);
        viewNodata = (LinearLayout) view.findViewById(R.id.view_nodata);
        PastListView.setHasFixedSize(true);
        PastListView.setClickable(true);
        PastListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutloading = (LinearLayout) view.findViewById(R.id.l_loading);
        refresh = (Button) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setVisibility(View.GONE);
                layoutloading.setVisibility(View.VISIBLE);
                getPasTeledocList();
            }
        });
        getPasTeledocList();
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPasTeledocList();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;


    }

    public void getPasTeledocList() {
        cancelableCallback = new CancelableCallback<UpcomingTeledocModel>() {

            @Override
            protected void onSuccess(UpcomingTeledocModel upcomingTeledocModel, Response response) {
                boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).isTokenValid(upcomingTeledocModel.messages, response, getActivity(), new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getPasTeledocList();
                    }

                });
                if (isTokenValid) {
                    swipeContainer.setRefreshing(false);
                    layoutloading.setVisibility(View.GONE);
                    if (upcomingTeledocModel.results.data != null) {
                        PastListView.setAdapter(new PastTeledocAdapter(getActivity(), upcomingTeledocModel.results.data, R.layout.item_past_teledoc));
                    } else {
                        viewNodata.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                swipeContainer.setRefreshing(false);
                refresh.setVisibility(View.VISIBLE);
                layoutloading.setVisibility(View.GONE);

            }
        };
        NetworkManager.getNetworkService(getContext()).getPastTeledoc(((AppController) getActivity().getApplication()).getSessionManager().getToken(), currentLanguage, currentpage, cancelableCallback);

    }


    public class PastTeledocAdapter extends RecyclerView.Adapter<PastTeledocAdapter.ViewHolder> {

        private List<UpcomingTeledocModel.Datum> items;
        private int itemLayout;
        private Context mContext;


        public PastTeledocAdapter(Context context, List<UpcomingTeledocModel.Datum> items, int itemLayout) {
            mContext = context;
            this.items = items;
            this.itemLayout = itemLayout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final UpcomingTeledocModel.Datum item = items.get(position);
            holder.textViewname.setText(item.doctor_name);
            holder.textViewdate.setText(((AppController) AppController.getAppContext()).setDatefull(item.schedule));
            holder.textViewdetail.setText(item.reason);
            holder.textViewStatus.setText(item.tele_status.equals("Close") || item.tele_status.equals("close") ? "DONE" : "CANCELLED");
            holder.textViewStatus.setTextColor(item.tele_status.equals("Close") ? mContext.getResources().getColor(R.color.green_xxl) : mContext.getResources().getColor(R.color.pink));
            holder.item_seedetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AppController) getActivity().getApplication()).getSessionManager().setupcomingteledocdata(item);
                    Intent intent = new Intent(getActivity(), TeledocDetailActivity.class);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            //    return 3;
            return items.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewStatus;
            public TextView textViewname;
            public TextView textViewdate;
            public TextView textViewdetail;
            private RelativeLayout layout;
            private TextView item_seedetail;

            public ViewHolder(View itemView) {
                super(itemView);
                textViewStatus = (TextView) itemView.findViewById(R.id.item_result_past_teledoc_status);
                textViewname = (TextView) itemView.findViewById(R.id.item_result_pastteledoc_detail_name);
                textViewdate = (TextView) itemView.findViewById(R.id.item_result_detail_pastteledoc_date);
                textViewdetail = (TextView) itemView.findViewById(R.id.item_result_detail_past_teledoc);
                item_seedetail = (TextView) itemView.findViewById(R.id.item_seedetail);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelableCallback.cancel();
    }
}
