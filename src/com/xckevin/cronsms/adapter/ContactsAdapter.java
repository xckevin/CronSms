package com.xckevin.cronsms.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xckevin.cronsms.R;
import com.xckevin.cronsms.util.ContactsQuery;

public class ContactsAdapter extends BaseAdapter implements OnCheckedChangeListener {

	private LayoutInflater inflater;

	private List<ContactsQuery.Data> list;

	public ContactsAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	public List<ContactsQuery.Data> getList() {
		return list;
	}

	public void setList(List<ContactsQuery.Data> list) {
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
			convertView = inflater.inflate(R.layout.list_contacts_item, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
			convertView.setTag(R.layout.list_contacts_item, holder);
		} else {
			holder = (ViewHolder) convertView.getTag(R.layout.list_contacts_item);
		}

		ContactsQuery.Data data = list.get(position);
		holder.name.setText(data.getName());;
		holder.number.setText(data.getNumber());

		String photoUri = data.getThumb();
		
		holder.checkbox.setOnCheckedChangeListener(null);
		holder.checkbox.setTag(position);
		holder.checkbox.setChecked(data.isChecked());
		holder.checkbox.setOnCheckedChangeListener(this);

		return convertView;
	}

	static class ViewHolder {
		ImageView image;
		TextView name;
		TextView number;
		CheckBox checkbox;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Integer position = (Integer) buttonView.getTag();
		if(position == null) {
			return ;
		}
		list.get(position).setChecked(isChecked);
	}

}
