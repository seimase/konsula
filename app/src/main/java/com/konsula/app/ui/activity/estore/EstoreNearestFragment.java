package com.konsula.app.ui.activity.estore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.konsula.app.AppController;
import com.konsula.app.GPSTracker;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreSearchProductModel;
import com.konsula.app.ui.custom.EndlessRecyclerOnScrollListener;
import com.konsula.app.ui.custom.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class EstoreNearestFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private GoogleMap map;
    private EstoreProductRecyclerViewAdapter.OnListFragmentInteractionListener mListener;
    private EstoreProductRecyclerViewAdapter adapter;

    public EstoreNearestFragment() {
    }

    @SuppressWarnings("unused")
    public static EstoreNearestFragment newInstance(int columnCount) {
        EstoreNearestFragment fragment = new EstoreNearestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView;
    private ArrayList<EstoreSearchProductModel.Item> estoreCategoryModels;
    private ArrayList<EstoreSearchProductModel.Map> estoreCategoryMapModels;
    private TextView messageTextView;
    private GPSTracker gpsTracker;
    GridLayoutManager gridLayoutManager;
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private Boolean isExpand = false;

    public interface OnLoadMoreListener {
        public void onLoadMore(int currentPage);
    }

    private OnLoadMoreListener listener;

    public void setListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estore_nearest, container, false);

        gpsTracker = new GPSTracker(getContext().getApplicationContext());

        appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
        messageTextView = (TextView) view.findViewById(R.id.estore_nearest_message_text_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (!isExpand) {
                        appBarLayout.setExpanded(true);
                        isExpand = true;
                    } else {
                        isExpand = false;
                    }
                }
            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior(getContext(), null);
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity().getApplicationContext(), R.dimen.space_10);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setHasFixedSize(true);

        mListener = new EstoreProductRecyclerViewAdapter.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(EstoreSearchProductModel.Item item) {

                Intent a = new Intent(getActivity(), EstoreProductActivity.class);
                a.putExtra(EstoreProductActivity.IDENTITY_URI, item.identity_uri);
                startActivity(a);
            }
        };
        estoreCategoryMapModels = new ArrayList<>();
        estoreCategoryModels = new ArrayList<>();
        adapter = new EstoreProductRecyclerViewAdapter(getContext().getApplicationContext(), estoreCategoryModels, mListener);
        recyclerView.setAdapter(adapter);

        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                listener.onLoadMore(current_page);
            }
        };
        recyclerView.setOnScrollListener(endlessRecyclerOnScrollListener);

        ((AppController) getActivity().getApplication()).setMixpanel("View Nearest Product");
        return view;
    }


    public void settoTop() {
        recyclerView.smoothScrollToPosition(0);
        appBarLayout.setExpanded(true);
    }

    private void initilizeMap() {
        if (map == null) {
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(
                    R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    EstoreNearestFragment.this.map = googleMap;
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.setMyLocationEnabled(true);
                    setMarkers();
                }
            });
        } else {
            map.clear();
            setMarkers();
        }
    }

    public void setMarkers() {

        for (int i = 0; i < estoreCategoryMapModels.size(); i++) {
            double lat = estoreCategoryMapModels.get(i).latitude;
            double lon = estoreCategoryMapModels.get(i).longitude;
            String title = estoreCategoryMapModels.get(i).practice_name;
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_estore_marker_64))
                    .title(title));

            if (i == 0) {

                if (gpsTracker.canGetLocation()) {
                    CameraUpdate point = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 11);
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_estore_mylocation))
                            .title(title));
                    map.moveCamera(point);
                } else {
                    CameraUpdate point = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lat, lon), 11);
                    map.moveCamera(point);

                }
            }

        }
    }

    public void clearItems() {
        this.estoreCategoryModels.clear();
        this.estoreCategoryMapModels.clear();
        adapter.notifyDataSetChanged();
        endlessRecyclerOnScrollListener.reset();
    }

    public void refreshItems(List<EstoreSearchProductModel.Item> estoreCategoryModels, List<EstoreSearchProductModel.Map> estoeMapModels) {

        if (estoreCategoryModels != null) {
            this.recyclerView.setVisibility(View.VISIBLE);
            messageTextView.setVisibility(View.GONE);
            this.estoreCategoryModels.addAll(estoreCategoryModels);
            this.estoreCategoryMapModels.addAll(estoeMapModels);
            adapter.notifyDataSetChanged();
            try {
                oninitilizeMap();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                oninitilizeMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
            nodata();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void oninitilizeMap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getActivity())) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2909);
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2909);
            }
        } else {
            initilizeMap();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @Nonnull String[] permissions,
                                           @Nonnull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2909:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    initilizeMap();
                }
                break;
        }
    }


    public void nodata(){
        this.recyclerView.setVisibility(View.GONE);
        messageTextView.setVisibility(View.VISIBLE);
        messageTextView.setText(getString(R.string.estore_no_product));
    }
}
