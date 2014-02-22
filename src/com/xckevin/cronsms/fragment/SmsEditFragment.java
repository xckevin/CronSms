package com.xckevin.cronsms.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xckevin.cronsms.AppContext;
import com.xckevin.cronsms.R;
import com.xckevin.cronsms.model.SmsInfo;
import com.xckevin.cronsms.ui.PickContactsActivity;
import com.xckevin.cronsms.util.ContactsQuery;
import com.xckevin.cronsms.util.ContactsQuery.Data;
import com.xckevin.cronsms.util.DateUtils;
import com.xckevin.cronsms.widget.LabelView;
import com.xckevin.cronsms.widget.LabelView.OnLabelActionListener;

public class SmsEditFragment extends Fragment implements OnClickListener, OnFocusChangeListener, OnLabelActionListener {

	public static final int REQUEST_PICK_CONTACTS = 0;

	private SmsInfo sms;

	private Calendar sendCal;

	private EditText toEdit;
	private EditText bodyEdit;

	private TextView numTxt;

	private LabelView labelView;

	private Button dateBtn;
	private Button timeBtn;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

	private boolean operateTos = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			sms = args.getParcelable("sms");
		}
		if(sms == null) {
			sms = new SmsInfo();
			sendCal = Calendar.getInstance();
		} else {
			sendCal = DateUtils.parseSmsTimeForCalendar(sms.getSendTime());
		}
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sms_edit, container, false);
		toEdit = (EditText) view.findViewById(R.id.to);
		bodyEdit = (EditText) view.findViewById(R.id.body);
		numTxt = (TextView) view.findViewById(R.id.num);
		labelView = (LabelView) view.findViewById(R.id.labels);
		dateBtn = (Button) view.findViewById(R.id.btn_date);
		timeBtn = (Button) view.findViewById(R.id.btn_time);

		String to = sms.getSendTo();
		if(TextUtils.isEmpty(to)) {
			labelView.setVisibility(View.GONE);
		} else {
			String name = sms.getSendName();
			toEdit.setText(sms.getSendName());
			initLabelView(to.split(";"), name.split(";"));
		}
		bodyEdit.setText(sms.getBody());
		if(TextUtils.isEmpty(sms.getSendTime())) {

		} else {
			notifySendTimeChanged();
		}

		toEdit.setOnFocusChangeListener(this);
		toEdit.addTextChangedListener(toTextWatcher);
		labelView.setOnLabelActionListener(this);
		bodyEdit.addTextChangedListener(bodyTextWatcher);
		dateBtn.setOnClickListener(this);
		timeBtn.setOnClickListener(this);
		view.findViewById(R.id.add).setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.edit, menu);
		return ;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_send) {
			tryToSaveSms();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void tryToSaveSms() {
		SmsInfo sms = getSms();
		Activity activity = getActivity();
		if(TextUtils.isEmpty(sms.getSendTo())) {
			AppContext.showToast(activity, R.string.edit_to_null);
		} else if(TextUtils.isEmpty(sms.getBody())) {
			AppContext.showToast(activity, R.string.edit_body_null);
		} else if(TextUtils.isEmpty(sms.getSendTime())) {
			AppContext.showToast(activity, R.string.edit_time_null);
		} else {
			((AppContext) activity.getApplication()).getSmsManager().submitSms(sms);
			activity.finish();
		}
	}

	public SmsInfo getSms() {
		List<ContactsQuery.Data> list = labelView.getContacts();
		if(list != null && list.size() > 0) {
			StringBuffer tos = new StringBuffer();
			StringBuffer names = new StringBuffer();
			for(ContactsQuery.Data data : list) {
				tos.append(data.getId()).append(";");
				names.append(data.getName()).append(";");
			}
			sms.setSendTo(tos.toString());
			sms.setSendName(names.toString());
		}
		sms.setSendTime(DateUtils.formatSmsTime(sendCal));
		sms.setBody(bodyEdit.getText().toString().trim());
		sms.setState(SmsInfo.STATE_SEND);
		return sms;
	}

	/**
	 * init label view to fill with contacts' name
	 * @param tos
	 */
	private void initLabelView(String[] tos, String[] names) {
		if(tos.length <= 0 || names.length <= 0) {
			return ;
		}
		ContentResolver contentResolver = getActivity().getContentResolver();
		ArrayList<ContactsQuery.Data> list = new ArrayList<ContactsQuery.Data>();
		ContactsQuery.Data data = null;
		for(int i = 0; i < tos.length; i ++) {
			data = new ContactsQuery.Data();
			data.setId(tos[i]);
			data.setName(names[i]);
			Cursor c = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{data.getId()}, null);
			String phoneNumber = null;
			if(c.moveToNext()) {
				phoneNumber = c.getString(0);
			}
			c.close();
			data.setNumber(phoneNumber);
			list.add(data);
		}
		labelView.setContacts(list);
	}
	
	private void resetTos() {
		if(toEdit.hasFocus()) {
			return ;
		}
		List<ContactsQuery.Data> list = labelView.getContacts();
		if(list == null || list.size() <= 0) {
			toEdit.getEditableText().clear();
		} else {
			StringBuffer sendName = new StringBuffer();
			for(ContactsQuery.Data data : list) {
				sendName.append(data.getName()).append(";");
			}
			operateTos = true;
			toEdit.setText(sendName);
		}
	}

	/**
	 * notify send time has changed, update datetime buttons
	 */
	private void notifySendTimeChanged() {
		Date date = sendCal.getTime();
		dateBtn.setText(dateFormat.format(date));
		timeBtn.setText(timeFormat.format(date));
	}

	/**
	 * pick send date
	 */
	private void showSelectDateDialog() {
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				sendCal.set(Calendar.YEAR, year);
				sendCal.set(Calendar.MONTH, monthOfYear);
				sendCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				notifySendTimeChanged();
			}
		}, sendCal.get(Calendar.YEAR) , sendCal.get(Calendar.MONTH), sendCal.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	/**
	 * pick send time
	 */
	private void showSelectTimeDialog() {
		TimePickerDialog dialog = new TimePickerDialog(getActivity(), new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				sendCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
				sendCal.set(Calendar.MINUTE, minute);
				sendCal.set(Calendar.SECOND, 0);
				notifySendTimeChanged();
			}
		}, sendCal.get(Calendar.HOUR_OF_DAY), sendCal.get(Calendar.MINUTE), true);
		dialog.show();
	}

	/**
	 * handle pick contacts' info
	 * @param contactsIds
	 */
	private void onPickContactsSuccessed(ArrayList<ContactsQuery.Data> list) {
		if(list == null || list.size() <= 0) {
			return ;
		}
		StringBuffer sendName = new StringBuffer();
		for(ContactsQuery.Data data : list) {
			sendName.append(data.getName()).append(";");
		}
		labelView.setContacts(list);
		operateTos = true;
		toEdit.setText(sendName);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_PICK_CONTACTS:
			if(resultCode == Activity.RESULT_OK) {
				ArrayList<ContactsQuery.Data> list = data.getParcelableArrayListExtra("list");
				onPickContactsSuccessed(list);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_date:
			showSelectDateDialog();
			break;
		case R.id.btn_time:
			showSelectTimeDialog();
			break;
		case R.id.add:
			Intent intent = new Intent(getActivity(), PickContactsActivity.class).putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) labelView.getContacts());;
			startActivityForResult(intent , REQUEST_PICK_CONTACTS);
			break;
		default:
			break;
		}
	}

	private TextWatcher toTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			if(operateTos) {
				operateTos = false;
				return ;
			}
			int length = s.length();
			if(length <= 1) {
				return ;
			}
			if(s.charAt(length - 1) == ';') {
				String temp = s.subSequence(0, length - 1).toString();
				String phoneNumber = null;
				int index = temp.lastIndexOf(';');
				if(index < 0) {
					phoneNumber = temp;
				} else {
					phoneNumber = temp.substring(index + 1);
				}
				ContactsQuery.Data data = new ContactsQuery.Data();
				data.setId(phoneNumber);
				data.setName(phoneNumber);
				data.setNumber(phoneNumber);
				labelView.appendLabel(data);
			}
		}

	};

	private TextWatcher bodyTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			int[] length = SmsMessage.calculateLength(s.toString(), false);
			String text = s.toString().length() + "/" + length[0];
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			style.setSpan(new ForegroundColorSpan(Color.RED), text.indexOf("/") + 1, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			numTxt.setText(style);
		}

	};

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch(v.getId()) {
		case R.id.to:
			if(hasFocus) {
				toEdit.getEditableText().clear();
				labelView.setVisibility(View.VISIBLE);
			} else {
//				resetTos();
				labelView.setVisibility(View.GONE);
			}
			break;
			default:
				break;
		}
	}

	@Override
	public void onDeleted(Data data) {
		resetTos();
	}

}
