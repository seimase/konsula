package com.konsula.app.ui.activity.estore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.BannerHomeModel;
import com.konsula.app.service.model.EstoreProductCatalogModel;
import com.konsula.app.service.model.PracticeModel;
import com.konsula.app.ui.adapter.CatalogRecyclerAdapter;
import com.konsula.app.ui.custom.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class EstoreSearchCategoryActivity extends AppCompatActivity {

    ArrayList<EstoreProductCatalogModel.Item> mValues;
    ArrayList<EstoreProductCatalogModel.Item> mOrigValues;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ImageButton closeButton;
    RecyclerView mRecyclerView;
    String currentLanguage = "";
    EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_estore_search_category);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        closeButton = (ImageButton) findViewById(R.id.estore_search_category_action_close_image_button);
        searchBox = (EditText) findViewById(R.id.estore_search_category_edit_text);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mValues.clear();
                Log.e("SearchBox", s.toString());
                for (int i = 0; i < mOrigValues.size(); i++) {
                    if (currentLanguage.equals("id")) {
                        if (mOrigValues.get(i).category_bahasa.toLowerCase().contains(s.toString().toLowerCase()))
                            mValues.add(mOrigValues.get(i));
                    } else {
                        if (mOrigValues.get(i).category_english.toLowerCase().contains(s.toString().toLowerCase()))
                            mValues.add(mOrigValues.get(i));
                    }


                }
                if (mValues.size() == 0) {
                    Log.e("SearchBox", "masuk");
                    createNewSearch(s.toString());
                }

                mAdapter.notifyDataSetChanged();
            }
        });

        mOrigValues = (ArrayList<EstoreProductCatalogModel.Item>) ((AppController) getApplication())
                .getSessionManager().getEstoreCatalog().items;
        mValues = (ArrayList<EstoreProductCatalogModel.Item>) ((AppController) getApplication())
                .getSessionManager().getEstoreCatalog().items;
        if (mValues != null) {

            initRecyclerView();

        }

    }

    private void initRecyclerView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.estore_search_category_list_view);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ListCategoriesAdapter(mValues);
        mRecyclerView.setAdapter(mAdapter);

    }

    public class ListCategoriesAdapter extends RecyclerView.Adapter<ListCategoriesAdapter.ViewHolder> {
        private ArrayList<EstoreProductCatalogModel.Item> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txtCategoryName;

            public ViewHolder(View v) {
                super(v);
                txtCategoryName = (TextView) v.findViewById(R.id.item_estore_category_name);
            }
        }

        public ListCategoriesAdapter(ArrayList<EstoreProductCatalogModel.Item> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public ListCategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_category, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final EstoreProductCatalogModel.Item item = mDataset.get(position);

            if (currentLanguage.equals("id"))
                holder.txtCategoryName.setText(item.category_bahasa);
            else
                holder.txtCategoryName.setText(item.category_english);

            holder.txtCategoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle params = new Bundle();
                    params.putString("search_string", String.valueOf(mDataset.get(position).category_id));
                    ((AppController)getApplication()).setFacebookEvent("Search", params);
                    if (mDataset.get(position).category_id == 0){
                        Log.d("keyword",mDataset.get(position).keyword);
                        Intent a = new Intent(EstoreSearchCategoryActivity.this, EstoreCategoryActivity.class);
                        a.putExtra(EstoreCategoryActivity.CATEGORY_URI, mDataset.get(position).identity_uri);
                        a.putExtra(EstoreCategoryActivity.TITLE, "Quick Search");
                        a.putExtra(EstoreCategoryActivity.TAG_KEYWORD,mDataset.get(position).keyword);
                        a.putExtra(EstoreCategoryActivity.TAG_FROM_USER_SEARCH,true);
                        startActivity(a);
                        finish();
                    }else {
                        Intent a = new Intent(EstoreSearchCategoryActivity.this, EstoreCategoryActivity.class);
                        a.putExtra(EstoreCategoryActivity.CATEGORY_URI, mDataset.get(position).identity_uri);
                        a.putExtra(EstoreCategoryActivity.TITLE, holder.txtCategoryName.getText().toString());
                        a.putExtra(EstoreCategoryActivity.TAG_KEYWORD,"");
                        startActivity(a);
                        finish();
                    }
//                    Toast.makeText(EstoreSearchCategoryActivity.this, item.label, Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }

    private void createNewSearch(String init) {
        ArrayList<EstoreProductCatalogModel.Item> items = new ArrayList<EstoreProductCatalogModel.Item>();
        EstoreProductCatalogModel model = new EstoreProductCatalogModel();
        EstoreProductCatalogModel.Item temp = model.new Item();
        temp.category_bahasa = "Cari \""  + init+"\"";
        temp.category_english = "Find \""  + init+"\"";
        temp.category_id = 0;
        temp.identity_uri = null;
        temp.keyword = init;
        items.add(temp);
        mValues = items;
        initRecyclerView();
    }
}
