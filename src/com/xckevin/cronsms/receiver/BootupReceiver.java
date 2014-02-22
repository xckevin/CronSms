package com.xckevin.cronsms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xckevin.cronsms.AppContext;

public class BootupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			((AppContext) context.getApplicationContext()).getSmsManager().resetAllSms();
		}
	}

}
