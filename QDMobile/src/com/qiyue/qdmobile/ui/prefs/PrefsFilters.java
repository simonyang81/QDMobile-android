package com.qiyue.qdmobile.ui.prefs;

import android.content.Intent;
import android.os.Bundle;

import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.ui.account.AccountsChooserListActivity;
import com.qiyue.qdmobile.ui.filters.AccountFilters;

public class PrefsFilters extends AccountsChooserListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
	@Override
	protected boolean showInternalAccounts() {
		return true;
	}

    @Override
    public void onAccountClicked(long id, String displayName, String wizard) {
        if(id != SipProfile.INVALID_ID){
            Intent it = new Intent(this, AccountFilters.class);
            it.putExtra(SipProfile.FIELD_ID,  id);
            it.putExtra(SipProfile.FIELD_DISPLAY_NAME, displayName);
            it.putExtra(SipProfile.FIELD_WIZARD, wizard);
            startActivity(it);
        }
    }
    
}
