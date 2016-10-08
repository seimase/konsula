package com.konsula.app.ui.fragment.direktori;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.konsula.app.R;
import com.konsula.app.service.model.CommonModel;

import java.util.ArrayList;


/**
 * Created by Owner on 11/17/2015.
 */
public class ShareFragment extends Fragment {

    private RecyclerView appointmentListView;
    private ArrayList<CommonModel> shareList = new ArrayList<CommonModel>();

    public ShareFragment() {
        // Required empty public constructor
        shareList.add(createObject("Email"));
        shareList.add(createObject("Facebook"));
        shareList.add(createObject("Twitter"));
        shareList.add(createObject("Google Plus"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        appointmentListView = (RecyclerView) view.findViewById(R.id.help_list_view);
        appointmentListView.setHasFixedSize(true);
        appointmentListView.setClickable(true);
        appointmentListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (shareList != null && shareList.size() > 0) {
            appointmentListView.setAdapter(new transactionAdapter(getActivity(), shareList, R.layout.item_help_menu));
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
                    switch (position) {
                        case 0:
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Konsula Apps");
                            intent.putExtra(Intent.EXTRA_TEXT, "I recommend Konsula app! Download segera aplikasi Konsula via google play store: https://play.google.com/store/apps/details?id=com.konsula.app dan dapatkan akses langsung dengan dokter berkualitas!");
                            intent.setData(Uri.parse("mailto:"));
                            String title = "Pilih Share";
                            Intent chooser = Intent.createChooser(intent, title);
                            startActivity(chooser);
                            break;
                        case 1:
                            Share("https://facebook.com/sharer/sharer.php?u=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.konsula.app");
                            break;
                        case 2:
                            Share("https://twitter.com/share?url=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.konsula.app");
                            break;
                        case 3:
                            Share("https://plus.google.com/share?url=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.konsula.app");
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

    private void Share(String content){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(content));
        String title = "Pilih Share";
        Intent chooser = Intent.createChooser(intent, title);
        startActivity(chooser);
    }
}

