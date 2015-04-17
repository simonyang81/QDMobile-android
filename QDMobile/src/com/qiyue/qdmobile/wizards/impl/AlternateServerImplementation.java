package com.qiyue.qdmobile.wizards.impl;

import android.preference.EditTextPreference;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;

public abstract class AlternateServerImplementation extends SimpleImplementation {

	protected static String SERVER = "server";
	protected EditTextPreference accountServer;
	
	@Override
	protected void bindFields() {
		super.bindFields();
		accountServer = (EditTextPreference) findPreference(SERVER);
	}
	
	@Override
	public void fillLayout(SipProfile account) {
		super.fillLayout(account);
		accountServer.setText(account.getSipDomain());
	}
	
	@Override
	protected String getDomain() {
		return accountServer.getText();
	}
	
	@Override
	public int getBasePreferenceResource() {
		return R.xml.w_alternate_server_preferences;
	}
	
	public boolean canSave() {
		boolean isValid = true;
		
		isValid &= super.canSave();
		isValid &= checkField(accountServer, isEmpty(accountServer));

		return isValid;
	}

	@Override
	public void updateDescriptions() {
		super.updateDescriptions();
		setStringFieldSummary(SERVER);
	}
	
	@Override
	public String getDefaultFieldSummary(String fieldName) {
		if(fieldName.equals(SERVER)) {
			return parent.getString(R.string.w_common_server_desc);
		}
		return super.getDefaultFieldSummary(fieldName);
	}
}
