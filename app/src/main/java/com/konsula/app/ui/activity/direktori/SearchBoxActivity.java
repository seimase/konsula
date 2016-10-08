package com.konsula.app.ui.activity.direktori;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.konsula.app.AppConstant;
import com.konsula.app.R;
import com.konsula.app.service.model.CommonModel;

import java.util.ArrayList;

/**
 * Created by konsula on 12/11/2015.
 */
public class SearchBoxActivity extends Activity {

    private ImageButton backButton;
    private EditText keyFilterEditText;
    private ListView resultListView;
    private SearchKeyAdapter resultAdapter;
    private RadioGroup kategoriRadioGroup;
    private ArrayList<CommonModel> mList = new ArrayList<CommonModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_box);

        keyFilterEditText = (EditText) findViewById(R.id.search_doctor_spesialisasi_box_edit_text);
        resultListView = (ListView) findViewById(R.id.search_doctor_box_list_view);
        backButton = (ImageButton) findViewById(R.id.search_doctor_action_close_image_button);
        kategoriRadioGroup = (RadioGroup) findViewById(R.id.radio_search_box);

        // set listener and adapter
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        kategoriRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_search_box_doctor) {
                    mList.clear();
                    // create dummy data
                    mList.add(createStaticObjectData("Dokter Gigi", "SPESIALISASI"));
                    mList.add(createStaticObjectData("Dokter Saraf", "SPESIALISASI"));

                    // Adding items to listview
                    resultAdapter = new SearchKeyAdapter(R.layout.item_search_filter, mList, true);
                    resultListView.setAdapter(resultAdapter);

                    getSearchResult();
                } else if (checkedId == R.id.radio_search_box_klinik) {
                    mList.clear();
                    mList.add(createStaticObjectData("Denta Medika", "KLINIK"));
                    mList.add(createStaticObjectData("Denta Abadi", "KLINIK"));

                    // Adding items to listview
                    resultAdapter = new SearchKeyAdapter(R.layout.item_search_filter, mList, false);
                    resultListView.setAdapter(resultAdapter);

                    getSearchResult();
                } else {
                    mList.clear();
                    mList.add(createStaticObjectData("Denta Medika", "KLINIK"));
                    mList.add(createStaticObjectData("Denta Abadi", "KLINIK"));

                    // Adding items to listview
                    resultAdapter = new SearchKeyAdapter(R.layout.item_search_filter, mList, true);
                    resultListView.setAdapter(resultAdapter);

                    getSearchResult();
                }
            }
        });

        kategoriRadioGroup.check(R.id.radio_search_box_doctor);
    }

    //  set static data
    private void getSearchResult() {
        //enables filtering for the contents of the given ListView
        resultListView.setTextFilterEnabled(true);

        // Set listener
        keyFilterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int arg1, int arg2, int arg3) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence keyFilter, int arg1, int arg2, int arg3) {
                resultAdapter.getFilter().filter(keyFilter.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /*keyFilterTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                resultAdapter.getFilter().performFiltering()
                return false;
            }
        });*/
    }

    // create adapter for filtering
    private class SearchKeyAdapter extends ArrayAdapter<CommonModel> {

        private ArrayList<CommonModel> originalList;
        private ArrayList<CommonModel> searchList;
        private SearchKeyFilter filter;
        private boolean isDoctor;

        public SearchKeyAdapter(int textViewResourceId, ArrayList<CommonModel> searchList, boolean isDoctor) {
            super(SearchBoxActivity.this, textViewResourceId, searchList);

            this.searchList = new ArrayList<CommonModel>();
            this.searchList.addAll(searchList);
            this.originalList = new ArrayList<CommonModel>();
            this.originalList.addAll(searchList);

            this.isDoctor = isDoctor;
        }

        @Override
        public SearchKeyFilter getFilter() {
            if (filter == null) {
                filter = new SearchKeyFilter();
            }
            return filter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            if (convertView == null) {

                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.item_search_filter, null);

                holder = new ViewHolder();
                holder.container = (LinearLayout) convertView.findViewById(R.id.spesialisasi_item_search_filter_container);
                holder.spesialisasiFilterTextView = (TextView) convertView.findViewById(R.id.name_item_search_filter_text_view);
                holder.kategoriFilterTextView = (TextView) convertView.findViewById(R.id.kategori_item_search_filter_text_view);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final CommonModel result = mList.get(position);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isDoctor){
                        Intent intent = new Intent(SearchBoxActivity.this, SearchResultDoctorActivity.class);
                        intent.putExtra(AppConstant.KEY_EXTRA_SEARCH, result.getProperty("spesialisasi"));
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SearchBoxActivity.this, SearchResultKlinikActivity.class);
                        intent.putExtra(AppConstant.KEY_EXTRA_SEARCH, result.getProperty("spesialisasi"));
                        startActivity(intent);
                        finish();

                    }
                }
            });
            holder.spesialisasiFilterTextView.setText(result.getProperty("spesialisasi"));
            holder.kategoriFilterTextView.setText(result.getProperty("kategroi"));

            return convertView;
        }

        private class ViewHolder {
            TextView spesialisasiFilterTextView, kategoriFilterTextView;
            LinearLayout container;
        }

        // Filter
        private class SearchKeyFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if (constraint != null && constraint.toString().length() > 0) {
                    ArrayList<CommonModel> filteredItems = new ArrayList<CommonModel>();

                    for (int i = 0, l = originalList.size(); i < l; i++) {
                        CommonModel model = originalList.get(i);
                        if (model.getProperty("spesialisasi") != null && model.getProperty("spesialisasi").trim().length() > 0) {
                            if (model.getProperty("spesialisasi").toString().toLowerCase().contains(constraint))
                                filteredItems.add(model);
                        }
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                } else {
                    synchronized (this) {
                        result.values = originalList;
                        result.count = originalList.size();
                    }
                }
                return result;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                searchList = (ArrayList<CommonModel>) filterResults.values;
                notifyDataSetChanged();
                clear();
                for (int i = 0, l = searchList.size(); i < l; i++)
                    add(searchList.get(i));
                notifyDataSetInvalidated();
            }
        }
    }


    /*
    *  todo :  remove this when connect to api
    * */
    // create object data
    private CommonModel createStaticObjectData(String spesialisasi, String kategroi) {
        CommonModel menuModel = new CommonModel();
        menuModel.setProperty("spesialisasi", spesialisasi);
        menuModel.setProperty("kategroi", kategroi);
        return menuModel;
    }
}
