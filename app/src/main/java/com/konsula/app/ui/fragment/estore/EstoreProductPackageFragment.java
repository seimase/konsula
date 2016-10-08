package com.konsula.app.ui.fragment.estore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreProductItemModel;
import com.konsula.app.service.model.EstoreProductModel;
import com.konsula.app.service.model.SummaryEstoreModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.activity.estore.EstoreProductActivity;
import com.konsula.app.ui.activity.estore.EstoreTransactionActivity;
import com.konsula.app.ui.adapter.ProductPackageAdapter;
import com.konsula.app.util.RefreshTokenCallback;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SamuelSonny on 03-May-16.
 */
public class EstoreProductPackageFragment extends Fragment{
    private ListView productPackageList;
    private ProductPackageAdapter adapter;
    private TextView txtEstimatedCost;
    private View view;
    private List<EstoreProductItemModel.ProductsItem> items;
    String currentLanguage;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_estore_product_package, null);
        productPackageList = (ListView) view.findViewById(R.id.product_package_list);
        adapter = new ProductPackageAdapter(getContext());
        txtEstimatedCost = (TextView) view.findViewById(R.id.txt_estimated_cost);
        currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentLanguage = (currentLanguage.equals("en") || currentLanguage.equals("EN")) ? "en" : "id";
        getData();
        if(items != null) {
            adapter.clear();
            adapter.addAll(items);
        }
        productPackageList.setAdapter(adapter);
        renderEstimatedCost();
        return view;
    }

    public void renderEstimatedCost(){
        int itemCount = 0;
        double estimatedCost = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            itemCount += adapter.getItem(i).amount;
            estimatedCost += adapter.getItem(i).amount * adapter.getItem(i).sell_price;
        }
        if (itemCount > 1){
            txtEstimatedCost.setText("(" + itemCount + " " + getString(R.string.estore_items) + ") " + AppController.getInstance().getDefaultPriceFormat("IDR ",estimatedCost));
        }else{
            txtEstimatedCost.setText("(" + itemCount + " " + getString(R.string.estore_item) + ") " + AppController.getInstance().getDefaultPriceFormat("IDR ",estimatedCost));
        }
    }

    public void reviewOrder(View view){
        int totalAmount = 0;
        if (AppController.checkConnection(getActivity())){
            for (EstoreProductItemModel.ProductsItem item : items) {
                totalAmount += item.amount;
            }
            for (int i = 0; i < EstoreProductActivity.getData().products_items.size(); i++) {
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).item_uuid.equalsIgnoreCase(EstoreProductActivity.getData().products_items.get(i).item_uuid)) {
                        EstoreProductActivity.getData().products_items.get(i).amount = items.get(j).amount;
                        EstoreProductActivity.getData().products_items.get(i).sell_price = items.get(j).sell_price;
                        break;
                    }
                }
            }
            if(totalAmount == 0) {
                ((AppController) ((Activity) getContext()).getApplication()).doDialog(getContext(), getResources().getString(R.string.estore_you_must_choose_at_least_one_item_to_order));
            }else{
                ((AppController) getActivity().getApplication()).setMixpanel("Review e-Store Order");
                EstoreTransactionActivity.changeToolbarname();
                ((EstoreTransactionActivity)getActivity()).nextStep();
            }
        }else{
            Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

    }

    private void getData() {
        final ProgressDialog progressDialog = AppController.createProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.show();
        NetworkManager.getNetworkService( getContext()).getProductItems(((AppController) getActivity().getApplication()).getSessionManager().getToken(),currentLanguage, ((EstoreTransactionActivity) getActivity()).product_uuid, new Callback<EstoreProductItemModel>() {
            @Override
            public void success(EstoreProductItemModel estoreProductItemModel, Response response) {
                // set data
                progressDialog.dismiss();
                boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).
                        isTokenValid(estoreProductItemModel.messages, response, getContext(), new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                getData();
                            }
                        });
                if (isTokenValid) {
                    items = estoreProductItemModel.results.data.items;
                    if(items != null) {
                        adapter.clear();
                        adapter.addAll(items);
                    }
                    productPackageList.setAdapter(adapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                ((AppController) ((Activity) getContext()).getApplication()).doDialog(getContext(), error.getMessage());
            }
        });

    }

}
