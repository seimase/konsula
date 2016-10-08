package com.konsula.app.ui.fragment.direktori;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

import com.konsula.app.R;

@SuppressLint("ValidFragment")
public class DummyContentFragment extends Fragment implements TabContentFactory {
	private Context mContext;
	
	public DummyContentFragment(Context context){
		mContext = context;
	}
			

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.fragment_dummy_content);
	}


	@Override
	public View createTabContent(String tag) {
		View v = new View(mContext);
		 
		return v;
	}
	

}
