package com.konsula.app.ui.activity.estore;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.MainActivity;
import com.konsula.app.R;
import com.konsula.app.service.model.EstoreProductModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.fragment.estore.EstoreBankTransferInformationFragment;
import com.konsula.app.ui.fragment.estore.EstoreConfirmFragment;
import com.konsula.app.ui.fragment.estore.EstorePaymentFragment;
import com.konsula.app.ui.fragment.estore.EstoreProductPackageFragment;
import com.konsula.app.ui.fragment.estore.EstoreReviewOrderFragment;
import com.konsula.app.ui.view.EstorePaymentStepView;
import com.konsula.app.util.FragmentUtil;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EstoreTransactionActivity extends AppCompatActivity {
    private ImageView backButton;
    private ImageView closeButton;
    private EstorePaymentStepView paymentStepView;
    private int currentStep = 0;
    private int[] titles = {
            R.string.estore_product_package,
            R.string.estore_review_order,
            R.string.estore_payment,
            R.string.estore_confirm
    };
    private int[] paymentSteps = {0, 1, 2, 2, 3};
    private Class[] fragments = new Class[]{
            EstoreProductPackageFragment.class,
            EstoreReviewOrderFragment.class,
            EstorePaymentFragment.class,
            EstoreBankTransferInformationFragment.class,
            EstoreConfirmFragment.class
    };
    private static TextView title;
    public String product_uuid;
    public final static String TAG_PRODUCT_UUID = "PRODUCT_UUID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estore_transaction);
        backButton = (ImageView) findViewById(R.id.estore_back_image_button);
        closeButton = (ImageView) findViewById(R.id.estore_close_image_button);
        paymentStepView = (EstorePaymentStepView) findViewById(R.id.payment_step_view);
        title = (TextView) findViewById(R.id.txt_title);

        product_uuid = getIntent().getStringExtra(TAG_PRODUCT_UUID);
        nextStep();
    }

    private void setCurrentFragment(int currentStep) {
        try {
            Fragment currentFragment = (Fragment) fragments[currentStep].newInstance();
            title.setText(titles[currentStep]);
            if (currentStep > 0) {
                FragmentUtil.changeFragment(this, R.id.fragment_container, currentFragment);
            } else {
                FragmentUtil.changeFragment(this, R.id.fragment_container, currentFragment, false);
            }
            paymentStepView.setCurrentStep(paymentSteps[currentStep]);
            if (paymentSteps[currentStep] == 3) {
                backButton.setVisibility(View.GONE);
                closeButton.setVisibility(View.VISIBLE);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void renderEstimatedCost() {
        ((EstoreProductPackageFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).renderEstimatedCost();
    }

    public void nextStep() {
        nextStep(null);
    }

    public void nextStep(View view) {
        setCurrentFragment(currentStep);
        currentStep++;
    }



    public void showTOS(View view){
        ((EstoreReviewOrderFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).showTOS(view);
    }

    public void showPrivacyPolicy(View view){
        ((EstoreReviewOrderFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).showPrivacyPolicy(view);
    }

    public void checkAgreement(View view){
        ((EstoreReviewOrderFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).checkAgreement(view);
    }

    public void cancelPromoCode(View view){
        ((EstoreReviewOrderFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).cancelPromoCode(view);
    }

    public void createTransaction(View view) {
        if (AppController.checkConnection(this)){
            ((EstoreReviewOrderFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).createTransaction(view);
        }else{
            Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }


    }

    public void reviewOrder(View view) {
        ((EstoreProductPackageFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).reviewOrder(view);
    }

    public void seeDetail(View view) {
        ((EstorePaymentFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).seeDetail(view);
    }

    public void uploadPOP(View view) {
        ((EstoreBankTransferInformationFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).uploadPOP(view);
    }

    public void closeDetail(View view) {
        ((EstorePaymentFragment) FragmentUtil.getCurrentFragment(this, R.id.fragment_container)).closeDetail(view);
    }

    public void close(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (paymentSteps[currentStep] == 1) {
            super.onBackPressed();
        } else if (paymentSteps[currentStep] < 4) {
            if (AppController.checkConnection(getBaseContext())){
                title.setText(R.string.estore_product_package);
                currentStep -= 2;
                paymentStepView.setCurrentStep(paymentSteps[currentStep]);
                currentStep++;
                if (FragmentUtil.backPressed(this)) {
                    super.onBackPressed();
                }
            }else{
                Toast.makeText(getBaseContext(), getResources().getText(R.string.no_connection),Toast.LENGTH_LONG).show();
            }

        } else {
            close(null);
        }
    }

    public static void changeToolbarname(){
        title.setText(R.string.estore_product_package);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}
