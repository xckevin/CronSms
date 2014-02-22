package com.xckevin.cronsms;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.xckevin.cronsms.model.SmsInfo;
import com.xckevin.cronsms.service.SendSmsService;
import com.xckevin.cronsms.support.DBHelper;
import com.xckevin.cronsms.util.DateUtils;
import com.xckevin.cronsms.util.NumberUtils;

public class CustomSmsManager {

	public static final String ACTION_SMS_ADD = "com.xckevin.cronsms.INTENT.ACTION_SMS_ADD";
	public static final String ACTION_SMS_SENT = "com.xckevin.cronsms.INTENT.ACTION_SMS_SENT";

	private static final String TAG = CustomSmsManager.class.getSimpleName();

	private static CustomSmsManager instance;

	private Context context;

	private SmsManager smsMgr;

	private AlarmManager alarmMgr;

	private DBHelper dbHelper;

	private CustomSmsManager(Context context) {
		this.context = context;
		smsMgr = SmsManager.getDefault();
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		dbHelper = DBHelper.getInstance(context);
	}

	static synchronized CustomSmsManager getInstance(Context context) {
		if(instance == null) {
			instance = new CustomSmsManager(context);
		}

		return instance;
	}

	public void submitSms(SmsInfo sms) {
		if(sms.getId() == 0) {
			dbHelper.saveSmsInfo(sms);
		} else {
			dbHelper.updateSmsInfo(sms);
		}
		setAlarmSms(sms);
		Bundle args = new Bundle();
		args.setClassLoader(SmsInfo.class.getClassLoader());
		args.putParcelable("sms", sms);
		context.sendBroadcast(new Intent(ACTION_SMS_ADD).putExtras(args));
	}
	
	public void deleteSms(SmsInfo sms) {
		Bundle args = new Bundle();
		args.setClassLoader(SmsInfo.class.getClassLoader());
		args.putParcelable("sms", sms);
		Intent intent = new Intent(context, SendSmsService.class);
		intent.putExtras(args);
		PendingIntent operation = PendingIntent.getService(context, sms.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmMgr.cancel(operation);
		dbHelper.deleteSmsInfo(sms);
	}

	public List<SmsInfo> getSmsInfoByState(int state) {
		return dbHelper.findSmsInfoByState(state);
	}

	public void sendSms(SmsInfo sms) {
		if(sms == null || TextUtils.isEmpty(sms.getSendTo())) {
			return ;
		}
		String[] send = sms.getSendTo().split(";");
		if(send == null || send.length <= 0) {
			return ;
		}
		ArrayList<String> list = smsMgr.divideMessage(sms.getBody());

		ContentResolver resolver = context.getContentResolver();

		for(String id : send) {
			Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
			String to = null;
			if(cursor.moveToNext()) {
				to = cursor.getString(0);
			} else {
				if(NumberUtils.isInteger(id)) {
					to = id;
				} else {
					cursor.close();
					continue;
				}
			}
			cursor.close();
			
			// send message
			for(String msg : list) {
				smsMgr.sendTextMessage(to, null, msg, null, null);
			}
		}
		sms.setState(SmsInfo.STATE_SENT);
		dbHelper.updateSmsInfo(sms);
		Bundle args = new Bundle();
		args.setClassLoader(SmsInfo.class.getClassLoader());
		args.putParcelable("sms", sms);
		context.sendBroadcast(new Intent(ACTION_SMS_SENT).putExtras(args));
	}

	public void resetAllSms() {
		List<SmsInfo> list = dbHelper.findSmsInfoByState(SmsInfo.STATE_SEND);
		if(list == null || list.size() <= 0) {
			return ;
		}
		for(SmsInfo sms : list) {
			setAlarmSms(sms);
		}
	}

	private void setAlarmSms(SmsInfo sms) {
		Log.v(TAG, "sms id: " + sms.getId());
		long sendTime = DateUtils.parseSmsTimeForLong(sms.getSendTime());
		long currentTime = System.currentTimeMillis();
		if(sendTime <= currentTime) {
			// past time
			return ;
		}
		Bundle args = new Bundle();
		args.setClassLoader(SmsInfo.class.getClassLoader());
		args.putParcelable("sms", sms);
		Intent intent = new Intent(context, SendSmsService.class);
		intent.putExtras(args);
		PendingIntent operation = PendingIntent.getService(context, sms.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, sendTime, operation);
	}
}
