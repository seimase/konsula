package com.konsula.app.ui.activity.estore;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreCategoryModel;
import com.konsula.app.service.model.EstoreProductCatalogModel;
import com.konsula.app.service.model.EstoreProductModel;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.adapter.CatalogRecyclerAdapter;
import com.konsula.app.ui.adapter.CoverImageAdapter;
import com.konsula.app.ui.adapter.ImageBannerAdapter;
import com.konsula.app.ui.custom.MyLinearLayoutManager;
import com.konsula.app.ui.custom.widget.CircleIndicator;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EstoreMainActivity extends AppCompatActivity {

    ImageButton backButton;
    ArrayList<EstoreProductCatalogModel.Item> estoreProductCatalog;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private MyLinearLayoutManager mLayoutManager;
    CircleIndicator viewPagerIndicator;
    ViewPager viewPagerHeader;
    String currentLanguage;
    TextView tv;
    ProgressBar progressBar;
    RelativeLayout mainContentLayout, refreshLayout;
    ImageButton searchBtn;
    Button btnRefresh;

    private Handler handler;
    private Runnable runnable;
    int position=1;
    CoverImageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estore_main);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";

        btnRefresh = (Button)findViewById(R.id.btn_refresh);
        refreshLayout = (RelativeLayout)findViewById(R.id.estore_main_refresh);
        searchBtn = (ImageButton)findViewById(R.id.estore_search_image_button);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        mainContentLayout = (RelativeLayout)findViewById(R.id.estore_main_content);
        viewPagerHeader = (ViewPager) findViewById(R.id.estore_main_header_viewpager);
        viewPagerIndicator = (CircleIndicator) findViewById(R.id.indicator);
        backButton = (ImageButton)findViewById(R.id.estore_header_cover_back_image_button);
        tv = (TextView) findViewById(R.id.estore_main_text);
        handler = new Handler();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppController) getApplication()).setMixpanel("Do Search e-Store");
                Intent a = new Intent(EstoreMainActivity.this,EstoreSearchCategoryActivity.class);
                startActivity(a);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.checkConnection(getBaseContext())){
                    progressBar.setVisibility(View.VISIBLE);
                    getProductCatalog();
                }else{
                    Toast.makeText(EstoreMainActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                }
            }
        });

        getProductCatalog();
    }

    private void getProductCatalog(){
        refreshLayout.setVisibility(View.GONE);
        String token = ((AppController)getApplication())
                .getSessionManager().getToken();
        NetworkManager.getNetworkService(getApplicationContext()).getProductCatalog(token, new Callback<EstoreProductCatalogModel>() {
            @Override
            public void success(EstoreProductCatalogModel estoreProductCatalogModel, Response response) {
                boolean isTokenValid = ((AppController) getApplication()).isTokenValid(estoreProductCatalogModel.messages, response, EstoreMainActivity.this,new RefreshTokenCallback() {
                    @Override
                    public void onRefreshTokenComplete() {
                        getProductCatalog();
                    }

                });

                if (isTokenValid) {
                    progressBar.setVisibility(View.GONE);
                    if(estoreProductCatalogModel.results!=null) {
                        ((AppController)getApplication())
                                .getSessionManager().setEstoreCatalog(estoreProductCatalogModel.results);
                        mainContentLayout.setVisibility(View.VISIBLE);
                        estoreProductCatalog = new ArrayList<EstoreProductCatalogModel.Item>();
                        for(int i = 0; i< estoreProductCatalogModel.results.items.size();i++)
                        {
                            if(estoreProductCatalogModel.results.items.get(i).products != null)
                                estoreProductCatalog.add(estoreProductCatalogModel.results.items.get(i));
                        }

                        if(estoreProductCatalogModel.results.banners != null)
                        {
                            if(estoreProductCatalogModel.results.banners.size()>0)
                                setupViewPagerHeader(viewPagerHeader,estoreProductCatalogModel.results.banners);
                        }
                        initRecyclerView();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("err",NetworkManager.getInstance().getErrorMessage(error));
                Toast.makeText(EstoreMainActivity.this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                //finish();
                progressBar.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setupViewPagerHeader(ViewPager viewPager, List<EstoreProductCatalogModel.Banner> resources) {
        ArrayList<PracticeModel.Others> coverImagesList = new ArrayList<>();
        for(EstoreProductCatalogModel.Banner data : resources) {
            PracticeModel model = new PracticeModel();
            PracticeModel.Others temp = model.new Others();
            temp.medium_image = data.image_url;
            coverImagesList.add(temp);
        }
        adapter = new CoverImageAdapter(getApplicationContext(), coverImagesList,R.layout.item_estore_photos);
        viewPager.setAdapter(adapter);
        viewPagerIndicator.setViewPager(viewPagerHeader);
        for (int i = 0; i < adapter.getCount() - 1; i++)
            viewPagerHeader.setCurrentItem(i, true);
        runnable = new Runnable() {
            public void run() {
                if (position >= adapter.getCount()) {
                    position = 0;
                } else {
                    position = position + 1;
                }
                viewPagerHeader.setCurrentItem(position, true);

            }
        };
        handler.postDelayed(runnable, 5000);
    }



    private void initRecyclerView(){

        mRecyclerView = (RecyclerView) findViewById(R.id.estore_main_content_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new MyLinearLayoutManager(this);
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ListCategoriesAdapter(estoreProductCatalog);
        mRecyclerView.setAdapter(mAdapter);

        tv.setVisibility(View.VISIBLE);
    }

    public class ListCategoriesAdapter extends RecyclerView.Adapter<ListCategoriesAdapter.ViewHolder> {
        private ArrayList<EstoreProductCatalogModel.Item> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txtCategoryName;
            public TextView txtSeeAll;
            public RecyclerView recyclerCatalog;
            public LinearLayoutManager linearLayoutManager;
            public ViewHolder(View v) {
                super(v);
                txtCategoryName = (TextView) v.findViewById(R.id.item_estore_category_name);
                txtSeeAll = (TextView) v.findViewById(R.id.item_estore_btn_see_all);
                recyclerCatalog = (RecyclerView)v.findViewById(R.id.item_estore_list_product);
                recyclerCatalog.setHasFixedSize(true);

                linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerCatalog.setLayoutManager(linearLayoutManager);
            }
        }

        public ListCategoriesAdapter(ArrayList<EstoreProductCatalogModel.Item> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public ListCategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estore_category, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            EstoreProductCatalogModel.Item item = mDataset.get(position);

            final String sCategory = item.category_english;
            if(currentLanguage.equals("id"))
                holder.txtCategoryName.setText(item.category_bahasa);
            else
                holder.txtCategoryName.setText(item.category_english);

            holder.txtSeeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.txtSeeAll.setClickable(false);
                    ((AppController)getApplication()).setMixpanel("View All " + sCategory);
                    Intent a = new Intent(EstoreMainActivity.this,EstoreCategoryActivity.class);
                    a.putExtra(EstoreCategoryActivity.CATEGORY_URI,mDataset.get(position).identity_uri);
                    a.putExtra(EstoreCategoryActivity.TITLE,holder.txtCategoryName.getText().toString());
                    a.putExtra(EstoreCategoryActivity.TAG_KEYWORD,"");
                    startActivity(a);
                    holder.txtSeeAll.setClickable(true);
                }
            });

            CatalogRecyclerAdapter catalogRecyclerAdapter = new CatalogRecyclerAdapter(getApplicationContext(),(ArrayList) item.products);
            holder.recyclerCatalog.setAdapter(catalogRecyclerAdapter);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 5000);
    }
}
