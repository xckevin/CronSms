package com.xckevin.cronsms;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;


public class AppContext extends Application {
	
	private CustomSmsManager smsManager;

	@Override
	public void onCreate() {
		super.onCreate();
		smsManager = CustomSmsManager.getInstance(this);
	}

	public CustomSmsManager getSmsManager() {
		return smsManager;
	}
	
	public static final void showToast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
	
	public static final void showToast(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}
