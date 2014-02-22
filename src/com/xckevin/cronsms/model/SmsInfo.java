package com.xckevin.cronsms.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SmsInfo implements Parcelable {
	
	public static final String TABLE_NAME = "sms_info";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TO = "send_to";
	public static final String COLUMN_NAME = "send_name";
	public static final String COLUMN_BODY = "_body";
	public static final String COLUMN_TIME = "_time";
	public static final String COLUMN_STATE = "_state";
	
	public static final int STATE_SEND = 0;
	public static final int STATE_SENT = 1;
	
	public static final Parcelable.Creator<SmsInfo> CREATOR = new Parcelable.Creator<SmsInfo>() {

		@Override
		public SmsInfo createFromParcel(Parcel source) {
			return new SmsInfo(source);
		}

		@Override
		public SmsInfo[] newArray(int size) {
			return new SmsInfo[size];
		}
	};
	
	private int id;
	
	// consist of contacts id or phone number
	private String sendTo;
	
	private String sendName;
	
	private String body;
	
	private String sendTime;
	
	private int state;
	
	public SmsInfo() {
		
	}

	public SmsInfo(Parcel source) {
		id = source.readInt();
		sendTo = source.readString();
		sendName = source.readString();
		body = source.readString();
		sendTime = source.readString();
		state = source.readInt();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(sendTo);
		dest.writeString(sendName);
		dest.writeString(body);
		dest.writeString(sendTime);
		dest.writeInt(state);
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(this == o) {
			return true;
		}
		if(!(o instanceof SmsInfo)) {
			return false;
		}
		SmsInfo other = (SmsInfo) o;
		
		return this.id == other.id && this.sendTo.equals(other.sendTo)
				&& this.body.equals(other.body) && this.sendTime.equals(other.sendTime)
				&& this.state == other.state;
	}

	@Override
	public int hashCode() {
		return id + sendTo.hashCode() + body.hashCode() + sendTime.hashCode() + state;
	}
}
