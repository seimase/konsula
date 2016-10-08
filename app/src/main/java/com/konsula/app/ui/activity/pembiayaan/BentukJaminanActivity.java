package com.konsula.app.ui.activity.pembiayaan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.InitVarLoanModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.custom.CustomtextView;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by konsula on 3/4/2016.
 */
public class BentukJaminanActivity extends AppCompatActivity {

    private MyCustomAdapter dataAdapter = null;
    private CustomtextView tvTitle;
    private ImageButton backButton;
    private String temp = "";
    private String title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bentuk_jaminan);
        initData();
        generateListView();
    }

    private void initData(){
        tvTitle = (CustomtextView) findViewById(R.id.tvTitle);
        try {
            title = "" + getIntent().getExtras().getString("title");
            tvTitle.setText(String.valueOf(title));
        } catch (Exception e) {
            Log.e("catch", "catch " + e.toString());
        }

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("text", "" + temp);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void generateListView(){
        if (tvTitle.getText().toString().equals(getString(R.string.tab2c))) {
            Log.e("Bentuk Jaminan", "Bentuk Jaminan");
            displayListViewBentuk();
        } else {
            Log.e("Lama Jaminan", "Lama Jaminan");
            if (getIntent().getExtras().getString("bentuk").equals("None")) {
                displayListViewLama();
            } else if (getIntent().getExtras().getString("bentuk").equals(getString(R.string.text_mobil))) {
                NetworkManager.getNetworkService(this).initVar(((AppController) getApplication()).getSessionManager().getToken(),new Callback<InitVarLoanModel>() {
                    @Override
                    public void success(InitVarLoanModel initVarLoanModel, Response response) {
                        ArrayList<Jaminan> jaminanList = new ArrayList<Jaminan>();
                        int i=0;
                        for (int item : initVarLoanModel.results.car) {
                            Jaminan jaminan = new Jaminan(initVarLoanModel.results.car.get(i) +" "+ getString(R.string.text_bulan), false);
                            jaminanList.add(jaminan);
                            i++;
                        }
                        //create an ArrayAdaptar from the String Array
                        dataAdapter = new MyCustomAdapter(BentukJaminanActivity.this,
                                R.layout.custom_list_data, jaminanList);
                        ListView listView = (ListView) findViewById(R.id.listView1);
                        // Assign adapter to ListView
                        listView.setAdapter(dataAdapter);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            } else if (getIntent().getExtras().getString("bentuk").equals(getString(R.string.text_motor))) {
                NetworkManager.getNetworkService(this).initVar(((AppController) getApplication()).getSessionManager().getToken(), new Callback<InitVarLoanModel>() {
                    @Override
                    public void success(InitVarLoanModel initVarLoanModel, Response response) {
                        ArrayList<Jaminan> jaminanList = new ArrayList<Jaminan>();
                        int i = 0;
                        for (int item : initVarLoanModel.results.motorbike) {
                            Jaminan jaminan = new Jaminan(initVarLoanModel.results.motorbike.get(i) +" "+ getString(R.string.text_bulan), false);
                            jaminanList.add(jaminan);
                            i++;
                        }
                        //create an ArrayAdaptar from the String Array
                        dataAdapter = new MyCustomAdapter(BentukJaminanActivity.this,
                                R.layout.custom_list_data, jaminanList);
                        ListView listView = (ListView) findViewById(R.id.listView1);
                        // Assign adapter to ListView
                        listView.setAdapter(dataAdapter);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            } else if (getIntent().getExtras().getString("bentuk").equals(getString(R.string.text_rumah))) {
                NetworkManager.getNetworkService(this).initVar(((AppController) getApplication()).getSessionManager().getToken(), new Callback<InitVarLoanModel>() {
                    @Override
                    public void success(InitVarLoanModel initVarLoanModel, Response response) {
                        ArrayList<Jaminan> jaminanList = new ArrayList<Jaminan>();
                        int i = 0;
                        for (int item : initVarLoanModel.results.house) {
                            Jaminan jaminan = new Jaminan(initVarLoanModel.results.house.get(i) +" "+getString(R.string.text_bulan), false);
                            jaminanList.add(jaminan);
                            i++;
                        }
                        //create an ArrayAdaptar from the String Array
                        dataAdapter = new MyCustomAdapter(BentukJaminanActivity.this,
                                R.layout.custom_list_data, jaminanList);
                        ListView listView = (ListView) findViewById(R.id.listView1);
                        // Assign adapter to ListView
                        listView.setAdapter(dataAdapter);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        }
    }

    private void displayListViewLama() {

        //Array list of countries
        ArrayList<Jaminan> jaminanList = new ArrayList<Jaminan>();
        String current = getResources().getConfiguration().locale.getLanguage();
        String month = getString(R.string.text_bulan);
        if (current.equals("en")) {
            month = month + "s";
        }
        Jaminan jaminan = new Jaminan("12 "+month, false);
        jaminanList.add(jaminan);
        jaminan = new Jaminan("24 "+month, false);
        jaminanList.add(jaminan);
        jaminan = new Jaminan("36 "+month, false);
        jaminanList.add(jaminan);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.custom_list_data, jaminanList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }

    private void displayListViewBentuk() {

        //Array list of countries
        ArrayList<Jaminan> jaminanList = new ArrayList<Jaminan>();
        Jaminan jaminan = new Jaminan(getString(R.string.text_mobil), false);
        jaminanList.add(jaminan);
        jaminan = new Jaminan(getString(R.string.text_motor), false);
        jaminanList.add(jaminan);
        jaminan = new Jaminan(getString(R.string.text_rumah), false);
        jaminanList.add(jaminan);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.custom_list_data, jaminanList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }

    private class MyCustomAdapter extends ArrayAdapter<Jaminan> {

        private ArrayList<Jaminan> jaminanList;
        private int mSelectedPosition = -1;
        private RadioButton mSelectedRB;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Jaminan> jaminanList) {
            super(context, textViewResourceId, jaminanList);
            this.jaminanList = new ArrayList<Jaminan>();
            this.jaminanList.addAll(jaminanList);
        }

        private class ViewHolder {
            //TextView code;
            RadioButton name;
            LinearLayout rl;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.custom_list_data, null);

                holder = new ViewHolder();
                holder.name = (RadioButton) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (position != mSelectedPosition && mSelectedRB != null) {
                        mSelectedRB.setChecked(false);
                    }

                    mSelectedPosition = position;
                    mSelectedRB = (RadioButton) v;
                    temp = mSelectedRB.getText().toString();
                }
            });

            if (mSelectedPosition != position) {
                holder.name.setChecked(false);
            } else {
                holder.name.setChecked(true);
                if (mSelectedRB != null && holder.name != mSelectedRB) {
                    mSelectedRB = holder.name;
                }
            }

            Jaminan jaminan = jaminanList.get(position);
            holder.name.setText(jaminan.getName());
            holder.name.setTag(jaminan);

            return convertView;

        }
    }
}
