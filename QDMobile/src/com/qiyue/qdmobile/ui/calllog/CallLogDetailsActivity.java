package com.qiyue.qdmobile.ui.calllog;

import android.os.Bundle;

import com.qiyue.qdmobile.BasFragmentActivity;
import com.qiyue.qdmobile.ui.calllog.CallLogDetailsFragment.OnQuitListener;

public class CallLogDetailsActivity extends BasFragmentActivity implements OnQuitListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initBarTintManager();

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            CallLogDetailsFragment detailFragment = new CallLogDetailsFragment();
            detailFragment.setArguments(getIntent().getExtras());
            detailFragment.setOnQuitListener(this);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, detailFragment).commit();
        }
	}
	
//	@Override
//	protected void onStart() {
//		super.onStart();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    if(item.getItemId() == Compatibility.getHomeMenuId()) {
//	         finish();
//	         return true;
//	    }
//
//        return super.onOptionsItemSelected(item);
//	}

	@Override
	public void onQuit() {
		finish();
	}

	@Override
	public void onShowCallLog(long[] callsId) {
		
	}
}
