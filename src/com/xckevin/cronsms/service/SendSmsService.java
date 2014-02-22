package com.xckevin.cronsms.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.xckevin.cronsms.AppContext;
import com.xckevin.cronsms.model.SmsInfo;

public class SendSmsService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		Bundle args = intent.getExtras();
		if(args == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		SmsInfo sms = args.getParcelable("sms");
		((AppContext) getApplication()).getSmsManager().sendSms(sms);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
