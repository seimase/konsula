package com.konsula.app.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.konsula.app.ui.activity.BlankActivity;
import com.konsula.app.ui.activity.transaction.MytransactionHistoryActivity;

import java.util.ArrayList;

/**
 * Created by Konsula on 06/04/2016.
 */
public class TransactionFragment extends Fragment {

    private RecyclerView appointmentListView;
    private ArrayList<CommonModel> appointmentList = new ArrayList<CommonModel>();

    public TransactionFragment() {
        // Required empty public constructor
//        appointmentList.add(createObject("My Transactions"));
//        appointmentList.add(createObject("Payment Details"));
//        appointmentList.add(createObject("Cancel Subscription"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources resrc = getActivity().getResources();
        appointmentList.add(createObject(resrc.getString(R.string.text_mytransaction)));
        appointmentList.add(createObject(resrc.getString(R.string.text_paymentdetails)));
        appointmentList.add(createObject(resrc.getString(R.string.text_batal_langganan)));
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
            final String name = result.getProperty("name");
            holder.itemtext.setText(name);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    switch (position) {
                        case 0:
                            if (AppController.checkConnection(getActivity())){
                                intent = new Intent(getActivity(), MytransactionHistoryActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(),getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 1:
                            if (AppController.checkConnection(getActivity())){
                                intent = new Intent(getActivity(), BlankActivity.class);
                                intent.putExtra("title", name);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(),getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 2:
                            if (AppController.checkConnection(getActivity())){
                                intent = new Intent(getActivity(), BlankActivity.class);
                                intent.putExtra("title", name);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getActivity(),getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 3:
                            if (AppController.checkConnection(getActivity())){
                                intent = new Intent(getActivity(), BlankActivity.class);
                                intent.putExtra("title", name);
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

