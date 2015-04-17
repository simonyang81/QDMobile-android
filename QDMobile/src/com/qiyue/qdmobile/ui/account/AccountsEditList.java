package com.qiyue.qdmobile.ui.account;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.utils.Compatibility;

public class AccountsEditList extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.accounts_view);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == Compatibility.getHomeMenuId()) {
			finish();
			return true;
		}
		return false;
	}

}
