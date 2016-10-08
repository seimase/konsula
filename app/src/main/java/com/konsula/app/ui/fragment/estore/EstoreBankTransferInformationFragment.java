package com.konsula.app.ui.fragment.estore;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.BankListModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.konsula.app.util.RefreshTokenCallback;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by SamuelSonny on 10-May-16.
 */
public class EstoreBankTransferInformationFragment extends Fragment{
    private LinearLayout linearBankTransfer;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estore_bank_transfer_information, null);
        linearBankTransfer = (LinearLayout) view.findViewById(R.id.linear_bank_transfer);
        initBankList();
        return view;
    }

    public void initBankList() {
        NetworkManager.getNetworkService(getContext()).getBankList(((AppController) ((Activity) getContext()).getApplication()).getSessionManager().getToken(), new Callback<BankListModel>() {
            @Override
            public void success(BankListModel bankListModel, Response response) {
                boolean isTokenValid = ((AppController) ((Activity) getContext()).getApplication()).
                        isTokenValid(bankListModel.messages, response, getContext(), new RefreshTokenCallback() {
                            @Override
                            public void onRefreshTokenComplete() {
                                initBankList();
                            }
                        });
                if (isTokenValid) {
                    linearBankTransfer.removeAllViews();
                    for (BankListModel.Result result: bankListModel.results) {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_bank_list, null);
                        ((TextView)view.findViewById(R.id.txt_bank_name)).setText(result.bank_name);
                        ((TextView)view.findViewById(R.id.txt_account_holder_name)).setText(result.account_holder_name);
                        ((TextView)view.findViewById(R.id.txt_branch_name)).setText(result.branch_name);
                        ((TextView)view.findViewById(R.id.txt_account_number)).setText(result.account_number);
                        linearBankTransfer.addView(view);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void uploadPOP(View view){
        CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(getContext());
        ListView list = new ListView(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.view_simple_list_item);

        adapter.add(getResources().getString(R.string.estore_take_photo));
        adapter.add(getResources().getString(R.string.estore_choose_existing));
        list.setAdapter(adapter);
//        list.setOnItemClickListener(this);

        builder.setView(list);
        builder.setTitle(R.string.estore_upload_proof_of_payment);
        builder.create().show();
    }
}
