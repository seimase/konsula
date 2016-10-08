package com.konsula.app.ui.filter;

import android.content.Context;
import android.widget.Filter;

import com.konsula.app.ui.adapter.AutoCompleteAdapter;

import java.util.ArrayList;

/**
 * Created by SamuelSonny on 29-Jan-16.
 */
public abstract class BaseFilter extends Filter{
    protected Context context;
    protected AutoCompleteAdapter adapter;
    protected ArrayList<String> fullList = new ArrayList<String>();
    protected ArrayList<String> defaultList = new ArrayList<String>();

    public BaseFilter(Context context, ArrayList<String> fullList, ArrayList<String> defaultList) {
        this.context = context;
        this.fullList = fullList;
        this.defaultList = defaultList;
    }

    public BaseFilter(Context context, ArrayList<String> fullList) {
        this.context = context;
        this.fullList = fullList;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<String> getFullList() {
        return fullList;
    }

    public void setDefaultList(ArrayList<String> defaultList) {
        this.defaultList = defaultList;
    }

    public ArrayList<String> getDefaultList() {
        return defaultList;
    }

    public void setAdapter(AutoCompleteAdapter adapter) {
        this.adapter = adapter;
    }

    public AutoCompleteAdapter getAdapter() {
        return adapter;
    }

    public void showDefaultList(){
        fullList.clear();
        fullList.addAll(defaultList);
    }

    @Override
    protected final FilterResults performFiltering(CharSequence input) {
        final FilterResults results = new FilterResults();
        if(input != null) {
            performFiltering(input.toString(), results);
        }else{
            showDefaultList();
        }
        results.values = fullList;
        results.count = fullList.size();
        return results;
    }

    public abstract void performFiltering(String input, FilterResults results);

    @Override
    protected final void publishResults(CharSequence input, FilterResults filterResults) {
        if (filterResults.count > 0) {
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetInvalidated();
        }
    }
}
