package com.xckevin.cronsms.util;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract.Contacts;

public final class ContactsQuery {

	// An identifier for the loader
	public static final int QUERY_ID = 1;

	public static final Uri URI = Contacts.CONTENT_URI;

	public static final String[] projection = new String[]{
		Contacts._ID,
		Contacts.DISPLAY_NAME_PRIMARY,
		Contacts.PHOTO_THUMBNAIL_URI,
	};

	public static final String selection = Contacts.HAS_PHONE_NUMBER + "=1";

	public static final String ORDER = "sort_key_alt";

	public static final int INDEX_ID = 0;
	public static final int INDEX_NAME = 1;
	public static final int INDEX_THUMB = 2;

	public static final class Data implements Parcelable {

		private String id;

		private String name;

		private String number;

		private String thumb;

		private boolean checked;
		
		public Data() {
			
		}
		
		public Data(Parcel source) {
			id = source.readString();
			name = source.readString();
			number = source.readString();
			thumb = source.readString();
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getThumb() {
			return thumb;
		}

		public void setThumb(String thumb) {
			this.thumb = thumb;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		@Override
		public boolean equals(Object o) {
			if(o == null) {
				return false;
			}
			if(o == this) {
				return true;
			}
			if(o instanceof Data) {
				Data other = (Data) o;
				return other.id.equals(this.id) && other.name.equals(this.name)
						&& other.number.equals(this.number);
			}
			
			return false;
		}

		@Override
		public int hashCode() {
			return id.hashCode() + name.hashCode() + number.hashCode();
		}

		public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {

			@Override
			public Data createFromParcel(Parcel source) {
				return new Data(source);
			}

			@Override
			public Data[] newArray(int size) {
				return new Data[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(id);
			dest.writeString(name);
			dest.writeString(number);
			dest.writeString(thumb);
		}

	}
}
