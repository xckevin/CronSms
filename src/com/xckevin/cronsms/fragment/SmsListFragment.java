package com.xckevin.cronsms.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.xckevin.cronsms.AppContext;
import com.xckevin.cronsms.CustomSmsManager;
import com.xckevin.cronsms.R;
import com.xckevin.cronsms.adapter.SmsAdapter;
import com.xckevin.cronsms.model.SmsInfo;
import com.xckevin.cronsms.ui.EditActivity;

public class SmsListFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

	private ListView listView;

	private ProgressBar progressBar;

	private SmsAdapter adapter;

	private GetDataTask getDataTask;

	private SmsReceiver smsReceiver;

	private int type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			type = args.getInt("index");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frament_sms_list, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
		adapter = new SmsAdapter(getActivity());
		listView.setAdapter(adapter);

		smsReceiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CustomSmsManager.ACTION_SMS_ADD);
		filter.addAction(CustomSmsManager.ACTION_SMS_SENT);
		getActivity().registerReceiver(smsReceiver, filter);

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);

		getDataTask = new GetDataTask();
		getDataTask.execute(type);
		return view;
	}

	@Override
	public void onDestroyView() {
		if(smsReceiver != null) {
			getActivity().unregisterReceiver(smsReceiver);
			smsReceiver = null;
		}
		super.onDestroyView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SmsInfo sms = (SmsInfo) parent.getAdapter().getItem(position);
		if(sms != null) {
			startActivity(new Intent(getActivity(), EditActivity.class).putExtra("sms", sms));
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final SmsInfo sms = (SmsInfo) parent.getAdapter().getItem(position);
		if(sms == null) {
			return false;
		}
		new AlertDialog.Builder(getActivity())
		.setTitle(R.string.dialog_title_operate)
		.setItems(R.array.list_sms_operate, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case 0:
					// edit
					startActivity(new Intent(getActivity(), EditActivity.class).putExtra("sms", sms));
					break;
				case 1:
					// delete
					((AppContext) getActivity().getApplication()).getSmsManager().deleteSms(sms);
					adapter.getList().remove(sms);
					adapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}

		}).show();
		return true;
	}

	private class GetDataTask extends AsyncTask<Integer, Integer, List<SmsInfo>> {

		@Override
		protected List<SmsInfo> doInBackground(Integer... params) {
			if(isAdded()) {
				return ((AppContext) getActivity().getApplication()).getSmsManager().getSmsInfoByState(params[0]);
			} else {
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}

		@Override
		protected void onPostExecute(List<SmsInfo> result) {
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			adapter.setList(result);
		}

	}

	private void handAddSmsAction(SmsInfo sms) {
		if(type == SmsInfo.STATE_SEND) {
			List<SmsInfo> list = adapter.getList();
			int index = list.indexOf(sms);
			if(index == -1) {
				list.add(sms);
			} else {
				list.set(index, sms);
			}
		} else {
			List<SmsInfo> list = adapter.getList();
			list.remove(sms);
		}
		adapter.notifyDataSetChanged();
	}

	private void handSendSmsAction(SmsInfo sms) {
		if(type == SmsInfo.STATE_SEND) {
			adapter.getList().remove(sms);
		} else {
			adapter.getList().add(0, sms);
		}
		adapter.notifyDataSetChanged();
	}

	private class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(CustomSmsManager.ACTION_SMS_ADD.equals(action)) {
				Bundle args = intent.getExtras();
				SmsInfo sms = args.getParcelable("sms");
				if(sms != null) {
					handAddSmsAction(sms);
				}
			} else if(CustomSmsManager.ACTION_SMS_SENT.equals(action) && type == SmsInfo.STATE_SENT) {
				Bundle args = intent.getExtras();
				SmsInfo sms = args.getParcelable("sms");
				if(sms != null) {
					handSendSmsAction(sms);
				}
			} else {

			}
		}

	}

}
