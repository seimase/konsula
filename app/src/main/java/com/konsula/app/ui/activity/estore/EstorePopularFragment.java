package com.konsula.app.ui.activity.estore;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreCategoryModel;
import com.konsula.app.service.model.EstoreSearchProductModel;
import com.konsula.app.ui.custom.EndlessRecyclerOnScrollListener;
import com.konsula.app.ui.custom.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

public class EstorePopularFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private EstoreProductRecyclerViewAdapter.OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private EstoreProductRecyclerViewAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<EstoreSearchProductModel.Item> estoreCategoryModels;
    private TextView messageTextView;
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    public interface OnLoadMoreListener{
        public void onLoadMore(int currentPage);
    }

    private OnLoadMoreListener listener;

    public void setListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

    @SuppressWarnings("unused")
    public static EstorePopularFragment newInstance(int columnCount) {
        EstorePopularFragment fragment = new EstorePopularFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }


    public EstorePopularFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estorepopular_list, container, false);

        messageTextView = (TextView)view.findViewById(R.id.estore_popular_message_text_view);
        recyclerView   = (RecyclerView) view.findViewById(R.id.list);
        gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity().getApplicationContext(), R.dimen.space_10);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setHasFixedSize(true);

        mListener = new EstoreProductRecyclerViewAdapter.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(EstoreSearchProductModel.Item item) {
                Intent a =new Intent(getActivity(),EstoreProductActivity.class);
                a.putExtra(EstoreProductActivity.IDENTITY_URI,item.identity_uri);
                startActivity(a);
            }
        };
        estoreCategoryModels = new ArrayList<>();
        adapter = new EstoreProductRecyclerViewAdapter(getContext().getApplicationContext(),estoreCategoryModels, mListener);
        recyclerView.setAdapter(adapter);

         endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                listener.onLoadMore(current_page);
            }
        };
        recyclerView.setOnScrollListener(endlessRecyclerOnScrollListener);

        ((AppController) getActivity().getApplication()).setMixpanel("View Most Popular Product");
        return view;
    }

    public void clearItems(){
        this.estoreCategoryModels.clear();
        adapter.notifyDataSetChanged();
        endlessRecyclerOnScrollListener.reset();
    }
    public void refreshItems(List<EstoreSearchProductModel.Item> estoreCategoryModels){
        if(estoreCategoryModels != null) {
            this.recyclerView.setVisibility(View.VISIBLE);
            messageTextView.setVisibility(View.GONE);
            this.estoreCategoryModels.addAll(estoreCategoryModels);
            adapter.notifyDataSetChanged();
        }else
        {
            nodata();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void nodata(){
        this.recyclerView.setVisibility(View.GONE);
        messageTextView.setVisibility(View.VISIBLE);
        messageTextView.setText(getString(R.string.estore_no_product));
    }


}
