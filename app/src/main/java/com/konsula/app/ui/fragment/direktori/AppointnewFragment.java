package com.konsula.app.ui.fragment.direktori;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.CommonModel;
import com.konsula.app.ui.activity.Schedule.HistoryTeledocActivity;
import com.konsula.app.ui.activity.direktori.AppointmentActivity;
import com.konsula.app.ui.activity.direktori.AppointmentHistoryActivity;
import com.konsula.app.ui.activity.direktori.AppointmentNewActivity;

import java.util.ArrayList;

/**
 * Created by Konsula on 29/03/2016.
 */
public class AppointnewFragment extends Fragment {

    private RecyclerView appointmentListView;
    private ArrayList<CommonModel> appointmentList = new ArrayList<CommonModel>();

    public AppointnewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources resrc = getActivity().getResources();
        appointmentList.add(createObject(resrc.getString(R.string.title_appointment)));
        appointmentList.add(createObject(resrc.getString(R.string.title_teledokter)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        appointmentListView = (RecyclerView) view.findViewById(R.id.help_list_view);
        appointmentListView.setHasFixedSize(true);
        appointmentListView.setClickable(true);
        appointmentListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (appointmentList != null && appointmentList.size() > 0) {
            appointmentListView.setAdapter(new transactionAdapter(getActivity(), appointmentList, R.layout.item_help_menu));

        }
        return view;
    }

    private CommonModel createObject(String name) {
        CommonModel commonModel = new CommonModel();
        commonModel.setProperty("name", name);
        return commonModel;
    }

    public class transactionAdapter extends RecyclerView.Adapter<transactionAdapter.ViewHolder> {

        private ArrayList<CommonModel> items;
        private int itemLayout;
        private Context mContext;


        public transactionAdapter(Context context, ArrayList<CommonModel> items, int itemLayout) {
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
            final CommonModel result = items.get(position);
            String name = result.getProperty("name");
            holder.itemtext.setText(name);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    switch (position) {
                        case 0:
                            if (AppController.checkConnection(getActivity())){
                                intent = new Intent(getActivity(), AppointmentNewActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(),getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                            }

                            break;
                        case 1:
                            if (AppController.checkConnection(getActivity())){
                                intent = new Intent(getActivity(), HistoryTeledocActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(),getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                            }
                            break;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return items.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView itemtext;
            private RelativeLayout layout;

            public ViewHolder(View itemView) {
                super(itemView);
                itemtext = (TextView) itemView.findViewById(R.id.item_help_menu_name_text_view);
                layout = (RelativeLayout) itemView.findViewById(R.id.item_help_menu_container);
            }
        }
    }
}

