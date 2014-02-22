package com.xckevin.cronsms.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.xckevin.cronsms.R;
import com.xckevin.cronsms.adapter.ContactsAdapter;
import com.xckevin.cronsms.util.ContactsQuery;

public class PickContactsActivity extends BaseFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private ListView listView;

	private ProgressBar progressBar;

	private ContactsAdapter adapter;
	
	private ArrayList<ContactsQuery.Data> pickedList;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_pick_contacts);

		init();
	}

	private void init() {
		pickedList = getIntent().getParcelableArrayListExtra("list");
		if(pickedList == null) {
			pickedList = new ArrayList<ContactsQuery.Data>();
		}
		
		setTitle(R.string.title_pick);
		
		listView = (ListView) findViewById(R.id.listview);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		
		adapter = new ContactsAdapter(this);
		listView.setAdapter(adapter);
		
		getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pick, menu);
		return true;
	}
	
	private ArrayList<ContactsQuery.Data> getSelectedItem() {
		ArrayList<ContactsQuery.Data> selected = new ArrayList<ContactsQuery.Data>();
		List<ContactsQuery.Data> all = adapter.getList();
		if(all != null && all.size() > 0) {
			for(ContactsQuery.Data data : all) {
				if(data.isChecked()) {
					selected.add(data);
				}
			}
		}
		
		return selected;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_save) {
			Intent intent = getIntent();
			setResult(RESULT_OK, intent.putParcelableArrayListExtra("list", getSelectedItem()));
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		switch(arg0) {
		case ContactsQuery.QUERY_ID:
			return new CursorLoader(this, ContactsQuery.URI, ContactsQuery.projection, 
					ContactsQuery.selection, null, ContactsQuery.ORDER);
		default:
			break;
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		progressBar.setVisibility(View.GONE);
		ContentResolver contentResolver = getContentResolver();
		List<ContactsQuery.Data> list = new ArrayList<ContactsQuery.Data>();
		ContactsQuery.Data data = null;
		while(arg1.moveToNext()) {
			data = new ContactsQuery.Data();
			String id = arg1.getString(ContactsQuery.INDEX_ID);
			data.setId(id);
			data.setName(arg1.getString(ContactsQuery.INDEX_NAME));
			data.setThumb(arg1.getString(ContactsQuery.INDEX_THUMB));
			Cursor c = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
			String phoneNumber = null;
			if(c.moveToNext()) {
				phoneNumber = c.getString(0);
			}
			c.close();
			data.setNumber(phoneNumber);
			if(pickedList.contains(data)) {
				data.setChecked(true);
			} else {
				data.setChecked(false);
			}
			list.add(data);
		}
		arg1.close();
		
		adapter.setList(list);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.setList(null);
	}

}
