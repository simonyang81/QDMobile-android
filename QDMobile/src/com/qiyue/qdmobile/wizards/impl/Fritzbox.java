package com.qiyue.qdmobile.wizards.impl;

import android.text.InputType;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.api.SipUri;

public class Fritzbox extends AlternateServerImplementation {
	
	
	@Override
	protected String getDefaultName() {
		return "Fritz!Box";
	}

	
	//Customization
	@Override
	public void fillLayout(final SipProfile account) {
		super.fillLayout(account);
		
		accountUsername.setTitle(R.string.w_fritz_extension);
		accountUsername.setDialogTitle(R.string.w_fritz_extension);
		accountUsername.setDialogMessage(R.string.w_fritz_extension_advise);
		accountUsername.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
		
		accountServer.setTitle(R.string.w_fritz_proxy);
		accountServer.setDialogTitle(R.string.w_fritz_proxy);
		
		if(account != null && account.proxies != null && account.proxies.length > 0) {
		    accountServer.setText(account.proxies[0].replace("sip:", ""));
		}else {
		    accountServer.setText("fritz.box");
		}
		
	}
	@Override
	public String getDefaultFieldSummary(String fieldName) {
		if(fieldName.equals(USER_NAME)) {
			return parent.getString(R.string.w_fritz_extension_desc);
		}else if(fieldName.equals(SERVER)) {
		    return parent.getString(R.string.w_fritz_proxy_desc);
		}
		return super.getDefaultFieldSummary(fieldName);
	}
	
	
	public SipProfile buildAccount(SipProfile account) {
		account = super.buildAccount(account);
		//Ensure registration timeout value
		account.reg_uri = "sip:fritz.box";
        account.acc_id = "<sip:"
                + SipUri.encodeUser(accountUsername.getText().trim()) + "@fritz.box>";
		account.contact_rewrite_method = 1;
        account.try_clean_registers = 0;
		account.allow_contact_rewrite = false;
		return account;
	}
	
	
}
