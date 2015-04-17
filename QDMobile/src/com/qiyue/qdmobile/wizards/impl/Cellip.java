package com.qiyue.qdmobile.wizards.impl;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.PreferencesWrapper;

public class Cellip extends SimpleImplementation {
	
	@Override
	protected String getDomain() {
		return "sip1.cellip.com";
	}
	
	@Override
	protected String getDefaultName() {
		return "Cellip";
	}

	
	//Customization
	@Override
	public void fillLayout(final SipProfile account) {
		super.fillLayout(account);
		
		accountUsername.setTitle(R.string.w_common_phone_number);
		accountUsername.setDialogTitle(R.string.w_common_phone_number);
		// Some users use a letter at the end of the number - so allow numbers
		//accountUsername.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
		
	}
	@Override
	public String getDefaultFieldSummary(String fieldName) {
		if(fieldName.equals(USER_NAME)) {
			return parent.getString(R.string.w_common_phone_number_desc);
		}
		return super.getDefaultFieldSummary(fieldName);
	}
	
	
	public SipProfile buildAccount(SipProfile account) {
		account = super.buildAccount(account);
		account.proxies = new String[] {"sip:193.105.226.30"};
		account.transport = SipProfile.TRANSPORT_UDP;
		return account;
	}
	
	@Override
	public void setDefaultParams(PreferencesWrapper prefs) {
		super.setDefaultParams(prefs);
		// Add stun server
		prefs.setPreferenceBooleanValue(SipConfigManager.ENABLE_STUN, true);
		prefs.addStunServer("stun.mysecretary.net");
	}
	

	@Override
	public boolean needRestart() {
		return true;
	}
}
