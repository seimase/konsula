package com.konsula.app.ui.adapter;

/**
 * Created by Owner on 1/12/2016.
 */
import java.util.ArrayList;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filterable;

import com.konsula.app.ui.filter.BaseFilter;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements
        Filterable {
    private ArrayList<String> fullList;
    private BaseFilter filter;

    public AutoCompleteAdapter(Context context, int resource,
                               int textViewResourceId, ArrayList<String> fullList) {

        super(context, resource, textViewResourceId, fullList);
        this.fullList = fullList;
        this.filter = new BaseFilter(context, fullList){
            @Override
            public void performFiltering(String input, FilterResults results) {

            }
        };
    }

    @Override
    public BaseFilter getFilter() {
        return filter;
    }

    public void setFilter(BaseFilter filter) {
        this.filter = filter;
        filter.setAdapter(this);
    }
}