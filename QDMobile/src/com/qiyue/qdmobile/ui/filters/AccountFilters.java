package com.qiyue.qdmobile.ui.filters;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;

public class AccountFilters extends FragmentActivity {

    private static final String THIS_FILE = "AccountFilters";
    private long accountId = SipProfile.INVALID_ID;
    private AccountFiltersListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String accountName = null;
        String wizard = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountId = extras.getLong(SipProfile.FIELD_ID, -1);
            accountName = extras.getString(SipProfile.FIELD_DISPLAY_NAME);
            wizard = extras.getString(SipProfile.FIELD_WIZARD);
        }

        if (accountId == -1) {
            Log.e(THIS_FILE, "You provide an empty account id....");
            finish();
        }
        if (!TextUtils.isEmpty(accountName)) {
            setTitle(getResources().getString(R.string.filters) + " : " + accountName);
        }

        setContentView(R.layout.account_filters_view);
        listFragment = (AccountFiltersListFragment) getSupportFragmentManager().findFragmentById(R.id.list);
        listFragment.setAccountId(accountId);
    }

}
