package com.konsula.app.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.konsula.app.R;
import com.konsula.app.ui.custom.CustomtextView;

/**
 * Created by SamuelSonny on 16-Apr-16.
 */
public class EstorePaymentStepView extends LinearLayout{
    private int[] stepIds = new int[]{
            R.id.estore_payment_step1,
            R.id.estore_payment_step2,
            R.id.estore_payment_step3,
            R.id.estore_payment_step4
    };
    private CustomtextView[] txtSteps;
    private int currentStep = 1;
    public EstorePaymentStepView(Context context) {
        super(context);
        init();
    }

    public EstorePaymentStepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EstorePaymentStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EstorePaymentStepView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_estore_payment_step, this);
        txtSteps = new CustomtextView[stepIds.length];
        for (int i = 0; i < stepIds.length; i++) {
            txtSteps[i] = (CustomtextView) findViewById(stepIds[i]);
        }
        renderStep();
    }

    public void renderStep(){
        for (int i = 0; i < stepIds.length; i++) {
            if(currentStep == i){
                txtSteps[i].setTextColor(getResources().getColor(R.color.estore_text_color));
            }else{
                txtSteps[i].setTextColor(getResources().getColor(R.color.step_inactive));
            }
        }
    }

    public void setCurrentStep(int currentStep) {
        Log.d("test", "CURRENT STEP " + currentStep);
        this.currentStep = currentStep;
        renderStep();
    }

    public int getCurrentStep() {
        return currentStep;
    }
}
