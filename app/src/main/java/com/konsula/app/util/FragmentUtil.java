package com.konsula.app.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.konsula.app.R;

public class FragmentUtil {
	
	public static void changeFragment(FragmentActivity activity, int placeHolderId, Fragment fragment){
		changeFragment(activity, placeHolderId, fragment,true);
	}
	
	public static void changeFragment(FragmentActivity activity, int placeHolderId, Fragment fragment, boolean addToBackStack){
		FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		transaction.replace(placeHolderId, fragment);
		if(addToBackStack)transaction.addToBackStack(null);
		transaction.commit();
	}

	public static boolean backPressed(FragmentActivity activity){
		int count = activity.getFragmentManager().getBackStackEntryCount();
	    if (count > 0) {
	    	activity.getFragmentManager().popBackStack();
	    	return false;
	    }
	    return true;
	}

	public static Fragment getCurrentFragment(FragmentActivity activity, int placeHolderId){
		return activity.getSupportFragmentManager().findFragmentById(placeHolderId);
	}
}
