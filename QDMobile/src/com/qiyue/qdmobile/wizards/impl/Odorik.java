package com.qiyue.qdmobile.wizards.impl;

import android.text.InputType;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;

public class Odorik extends SimpleImplementation {
	
	@Override
	protected String getDomain() {
		return "sip.odorik.cz";
	}
	
	@Override
	public void fillLayout(final SipProfile account) {
		super.fillLayout(account);
		
		accountUsername.setTitle(R.string.w_common_phone_number);
		accountUsername.setDialogTitle(R.string.w_common_phone_number);
		accountUsername.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
		
	}

	@Override
	public String getDefaultFieldSummary(String fieldName) {
		if(fieldName.equals(USER_NAME)) {
			return parent.getString(R.string.w_common_phone_number_desc);
		}
		return super.getDefaultFieldSummary(fieldName);
	}

	@Override
	protected String getDefaultName() {
		return "Odorik.cz";
	}
	
	@Override
	public SipProfile buildAccount(SipProfile account) {
		SipProfile acc = super.buildAccount(account);
		acc.allow_contact_rewrite = false;
		acc.allow_via_rewrite = false;
		return acc;
	}
}
