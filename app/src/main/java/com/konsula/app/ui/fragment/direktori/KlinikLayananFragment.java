package com.konsula.app.ui.fragment.direktori;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konsula.app.R;
import com.konsula.app.service.model.PracticeModel;

import java.util.List;

/**
 * Created by konsula on 12/16/2015.
 */
@SuppressWarnings("ValidFragment")
public class KlinikLayananFragment extends Fragment {

    private RecyclerView layananListView;
    private List<PracticeModel.Service> listService;
    String currentLanguage;

    public KlinikLayananFragment(List<PracticeModel.Service> services ) {
        // create data
        listService = services;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_klinik_layanan, null, false);
        layananListView = (RecyclerView) view.findViewById(R.id.klinik_info_service_list_recycler_view);
        // set adapter
        layananListView.setAdapter(new ServicesAdapter(listService, R.layout.item_klinik_profile_general));
        layananListView.setHasFixedSize(true);
        layananListView.setClickable(true);
        layananListView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        return view;
    }

    // create adapter for filtering
    public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {

        private List<PracticeModel.Service> items;
        private int itemLayout;

        public ServicesAdapter(List<PracticeModel.Service> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
            return new ViewHolder(v);
        }

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            PracticeModel.Service item = items.get(position);
            if (currentLanguage.equals("id")){
                holder.textView.setText(items.get(position).service_bahasa);
            }else{
                holder.textView.setText(items.get(position).service_english);
            }

            holder.itemView.setTag(item);
        }

        @Override public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.tv_klinik_profile_general);
            }
        }
    }
}
