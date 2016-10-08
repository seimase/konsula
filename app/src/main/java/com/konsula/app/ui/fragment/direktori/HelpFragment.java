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

import com.konsula.app.AboutUsActivity;
import com.konsula.app.ContactUsActivity;
import com.konsula.app.ui.activity.direktori.PrivacyPolicyActivity;
import com.konsula.app.R;
import com.konsula.app.ui.activity.direktori.TermAndConditionActivity;
import com.konsula.app.service.model.CommonModel;

import java.util.ArrayList;


/**
 * Created by Owner on 11/17/2015.
 */
public class HelpFragment extends Fragment {

    private RecyclerView helpListView;
    private ArrayList<CommonModel> helpList = new ArrayList<CommonModel>();

    public HelpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Resources resrc = getActivity().getResources();
        // Required empty public constructor
        helpList.add(createObject(resrc.getString(R.string.tentangkami)));
        helpList.add(createObject(resrc.getString(R.string.contactus)));
        helpList.add(createObject(resrc.getString(R.string.termcondition)));
        helpList.add(createObject(resrc.getString(R.string.kebijakan_privasi)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        helpListView = (RecyclerView) view.findViewById(R.id.help_list_view);
        helpListView.setHasFixedSize(true);
        helpListView.setClickable(true);
        helpListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (helpList != null && helpList.size() > 0) {
            helpListView.setAdapter(new transactionAdapter(getActivity(), helpList, R.layout.item_help_menu));

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
                            intent = new Intent(getActivity(), AboutUsActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(getActivity(), ContactUsActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(getActivity(), TermAndConditionActivity.class);
                            startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
                            startActivity(intent);
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

