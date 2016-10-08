package com.konsula.app.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.konsula.app.R;

public class CustomAlertDialogBuilder extends Builder implements View.OnClickListener{
	private View titleView;
	private LinearLayout alertTitlebackground;
	private AlertDialog alert;
	private ImageView close;
	
	public CustomAlertDialogBuilder(Context context, int color) {
		super(context);
		// TODO Auto-generated constructor stub
		init(color);
	}

	public CustomAlertDialogBuilder(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public void init(){
		titleView = LayoutInflater.from(getContext()).inflate(R.layout.view_alert_dialog_title, null);
		close = (ImageView) titleView.findViewById(R.id.alertClose);
		close.setOnClickListener(this);
		setCustomTitle(titleView);
	}

	public void init(int color){
		titleView = LayoutInflater.from(getContext()).inflate(R.layout.view_alert_dialog_title, null);
		close = (ImageView) titleView.findViewById(R.id.alertClose);
		alertTitlebackground = (LinearLayout) titleView.findViewById(R.id.alertTitlebackground);
		alertTitlebackground.setBackgroundColor(color);
		close.setOnClickListener(this);
		setCustomTitle(titleView);
	}
	
	@Override
	public Builder setTitle(int titleId) {
		// TODO Auto-generated method stub
		((TextView) titleView.findViewById(R.id.alertTitle)).setText(titleId);
		return super.setTitle(titleId);
	}

	@Override
	public Builder setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		((TextView) titleView.findViewById(R.id.alertTitle)).setText(title);
		return super.setTitle(title);
	}

	@Override
	public AlertDialog create() {
		// TODO Auto-generated method stub
		alert = super.create();
		return alert;
	}

	@Override
	public void onClick(View v) {
		alert.dismiss();
	}
}
