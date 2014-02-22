package com.xckevin.cronsms.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xckevin.cronsms.R;
import com.xckevin.cronsms.fragment.SmsEditFragment;
import com.xckevin.cronsms.model.SmsInfo;

public class EditActivity extends BaseFragmentActivity {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setTitle(R.string.title_edit);
		
		SmsInfo sms = getIntent().getParcelableExtra("sms");
		Bundle args = new Bundle();
		args.putParcelable("sms", sms);
		Fragment fragment = Fragment.instantiate(this, SmsEditFragment.class.getName(), args);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
	}
	
}
