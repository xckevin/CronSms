package com.xckevin.cronsms.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.xckevin.cronsms.R;
import com.xckevin.cronsms.fragment.SmsListFragment;

public class MainActivity extends BaseFragmentActivity implements OnQueryTextListener  {

	public static final String[] tabs = new String[]{"send", "sent"};

	int mSortMode = -1;

	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_USE_LOGO
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

		init();
	}

	private void init() {
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOnPageChangeListener(pagerListener);
		pager.setAdapter(new SmsPagerAdapter(this, getSupportFragmentManager()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actions, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mSortMode != -1) {
			Drawable icon = menu.findItem(mSortMode).getIcon();
			menu.findItem(R.id.action_sort).setIcon(icon);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_edit:
			startActivity(new Intent(this, EditActivity.class));
			break;
		}
		return true;
	}

	// This method is specified as an onClick handler in the menu xml and will
	// take precedence over the Activity's onOptionsItemSelected method.
	// See res/menu/actions.xml for more info.
	public void onSort(MenuItem item) {
		mSortMode = item.getItemId();
		// Request a call to onPrepareOptionsMenu so we can change the sort icon
		invalidateOptionsMenu();
	}

	// The following callbacks are called for the SearchView.OnQueryChangeListener
	// For more about using SearchView, see src/.../view/SearchView1.java and SearchView2.java
	public boolean onQueryTextChange(String newText) {
		newText = newText.isEmpty() ? "" : "Query so far: " + newText;
		return true;
	}

	public boolean onQueryTextSubmit(String query) {
		Toast.makeText(this, "Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
		return true;
	}

	private ViewPager.SimpleOnPageChangeListener pagerListener = new ViewPager.SimpleOnPageChangeListener() {

	};

	private static class SmsPagerAdapter extends FragmentPagerAdapter {

		Context context;

		public SmsPagerAdapter(Context context, FragmentManager fm) {
			super(fm);
			this.context = context;
		}

		@Override
		public Fragment getItem(int position) {
			Bundle args = new Bundle();
			args.putInt("index", position);
			return Fragment.instantiate(context, SmsListFragment.class.getName(), args);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch(position) {
			case 0:
				return context.getString(R.string.tab_send);
			case 1:
				return context.getString(R.string.tab_sent);
			default:
				return "unknown";
			}
		}

		@Override
		public int getCount() {
			return tabs.length;
		}


	}
}
