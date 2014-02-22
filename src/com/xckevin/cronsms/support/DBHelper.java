package com.xckevin.cronsms.support;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xckevin.cronsms.Env;
import com.xckevin.cronsms.model.SmsInfo;

public class DBHelper extends SQLiteOpenHelper {

	private static DBHelper instance;

	private DBHelper(Context context) {
		super(context, Env.DB_NAME, null, Env.DB_VERSION);
	}

	public static synchronized DBHelper getInstance(Context context) {
		if(instance == null) {
			instance = new DBHelper(context);
		}

		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE TABLE IF NOT EXISTS ").append(SmsInfo.TABLE_NAME);
		buffer.append("(");
		buffer.append("`").append(SmsInfo.COLUMN_ID).append("` INTEGER PRIMARY KEY AUTOINCREMENT,");
		buffer.append("`").append(SmsInfo.COLUMN_TO).append("` VARCHAR,");
		buffer.append("`").append(SmsInfo.COLUMN_NAME).append("` VARCHAR,");
		buffer.append("`").append(SmsInfo.COLUMN_BODY).append("` VARCHAR,");
		buffer.append("`").append(SmsInfo.COLUMN_TIME).append("` DATETIME,");
		buffer.append("`").append(SmsInfo.COLUMN_STATE).append("` INT");
		buffer.append(")");
		db.execSQL(buffer.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO
	}

	public List<SmsInfo> findAll() {
		List<SmsInfo> list = new ArrayList<SmsInfo>();
		SmsInfo s = null;
		Cursor cursor = null;
		try {
			cursor = getReadableDatabase().query(SmsInfo.TABLE_NAME, null, null, null, null, null, SmsInfo.COLUMN_STATE + " ASC ," + SmsInfo.COLUMN_ID + " DESC");
			int idIndex = cursor.getColumnIndex(SmsInfo.COLUMN_ID);
			int toIndex = cursor.getColumnIndex(SmsInfo.COLUMN_TO);
			int nameIndex = cursor.getColumnIndex(SmsInfo.COLUMN_NAME);
			int bodyIndex = cursor.getColumnIndex(SmsInfo.COLUMN_BODY);
			int timeIndex = cursor.getColumnIndex(SmsInfo.COLUMN_TIME);
			int stateIndex = cursor.getColumnIndex(SmsInfo.COLUMN_STATE);
			while(cursor.moveToNext()) {
				s = new SmsInfo();
				s.setId(cursor.getInt(idIndex));
				s.setSendTo(cursor.getString(toIndex));
				s.setSendName(cursor.getString(nameIndex));
				s.setBody(cursor.getString(bodyIndex));
				s.setSendTime(cursor.getString(timeIndex));
				s.setState(cursor.getInt(stateIndex));
				list.add(s);
			}
		} finally {
			if(cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}

		return list;
	}

	public List<SmsInfo> findSmsInfoByState(int state) {
		List<SmsInfo> list = new ArrayList<SmsInfo>();
		SmsInfo s = null;
		Cursor cursor = getReadableDatabase().query(SmsInfo.TABLE_NAME, null, SmsInfo.COLUMN_STATE + "=?", new String[]{String.valueOf(state)}, null, null, SmsInfo.COLUMN_ID + " desc");
		int idIndex = cursor.getColumnIndex(SmsInfo.COLUMN_ID);
		int toIndex = cursor.getColumnIndex(SmsInfo.COLUMN_TO);
		int nameIndex = cursor.getColumnIndex(SmsInfo.COLUMN_NAME);
		int bodyIndex = cursor.getColumnIndex(SmsInfo.COLUMN_BODY);
		int timeIndex = cursor.getColumnIndex(SmsInfo.COLUMN_TIME);
		int stateIndex = cursor.getColumnIndex(SmsInfo.COLUMN_STATE);
		while(cursor.moveToNext()) {
			s = new SmsInfo();
			s.setId(cursor.getInt(idIndex));
			s.setSendTo(cursor.getString(toIndex));
			s.setSendName(cursor.getString(nameIndex));
			s.setBody(cursor.getString(bodyIndex));
			s.setSendTime(cursor.getString(timeIndex));
			s.setState(cursor.getInt(stateIndex));
			list.add(s);
		}
		cursor.close();
		cursor = null;

		return list;
	}

	public void saveSmsInfo(SmsInfo sms) {
		if(sms == null) {
			return ;
		}
		ContentValues values = new ContentValues();
		values.put(SmsInfo.COLUMN_TO, sms.getSendTo());
		values.put(SmsInfo.COLUMN_BODY, sms.getBody());
		values.put(SmsInfo.COLUMN_NAME, sms.getSendName());
		values.put(SmsInfo.COLUMN_TIME, sms.getSendTime());
		values.put(SmsInfo.COLUMN_STATE, sms.getState());
		getWritableDatabase().insert(SmsInfo.TABLE_NAME, null, values);
		Cursor cursor = getReadableDatabase().rawQuery("select last_insert_rowid() from " + SmsInfo.TABLE_NAME,null);
		if(cursor.moveToNext()) {
			sms.setId(cursor.getInt(0));
		}
		cursor.close();
	}

	public void updateSmsInfo(SmsInfo sms) {
		if(sms == null) {
			return ;
		}
		ContentValues values = new ContentValues();
		values.put(SmsInfo.COLUMN_TO, sms.getSendTo());
		values.put(SmsInfo.COLUMN_BODY, sms.getBody());
		values.put(SmsInfo.COLUMN_NAME, sms.getSendName());
		values.put(SmsInfo.COLUMN_TIME, sms.getSendTime());
		values.put(SmsInfo.COLUMN_STATE, sms.getState());
		getWritableDatabase().update(SmsInfo.TABLE_NAME, values, SmsInfo.COLUMN_ID + "=?", new String[]{String.valueOf(sms.getId())});
	}
	
	public void deleteSmsInfo(SmsInfo sms) {
		if(sms == null) {
			return ;
		}
		getWritableDatabase().delete(SmsInfo.TABLE_NAME, SmsInfo.COLUMN_ID + "=?", new String[]{String.valueOf(sms.getId())});
	}

}
