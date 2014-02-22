package com.xckevin.cronsms.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xckevin.cronsms.R;
import com.xckevin.cronsms.model.SmsInfo;

public class SmsAdapter extends BaseAdapter {
	
	private Context context;
	
	private List<SmsInfo> list;
	
	public SmsAdapter(Context context) {
		this.context = context;
	}

	public List<SmsInfo> getList() {
		return list;
	}

	public void setList(List<SmsInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_sms_item, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(R.layout.list_sms_item, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(R.layout.list_sms_item);
		}
		convertView.setTag(position);
		
		SmsInfo sms = list.get(position);
		holder.title.setText(sms.getSendName());
		holder.desc.setText(sms.getBody());
		holder.time.setText(sms.getSendTime());
		
		return convertView;
	}
	
	static class ViewHolder {
		ImageView image;
		TextView title;
		TextView desc;
		TextView time;
	}

}
