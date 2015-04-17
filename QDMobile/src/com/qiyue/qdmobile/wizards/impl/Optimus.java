package com.qiyue.qdmobile.wizards.impl;

import android.text.InputType;
import android.text.TextUtils;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.PreferencesWrapper;

public class Optimus extends SimpleImplementation {
	
	@Override
	protected String getDomain() {
		return "sip.optimus.pt";
	}
	
	@Override
	protected String getDefaultName() {
		return "Optimus";
	}

    private final static String USUAL_PREFIX = "351";

	
	//Customization
	@Override
	public void fillLayout(final SipProfile account) {
		super.fillLayout(account);
		
		accountUsername.setTitle(R.string.w_common_phone_number);
		accountUsername.setDialogTitle(R.string.w_common_phone_number);
		accountUsername.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);

        if(TextUtils.isEmpty(account.username)){
            accountUsername.setText(USUAL_PREFIX);
        }
		
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSave() {
        boolean ok = super.canSave();
        ok &= checkField(accountUsername, accountUsername.getText().trim().equalsIgnoreCase(USUAL_PREFIX));
        return ok;
    }
    
	@Override
	public String getDefaultFieldSummary(String fieldName) {
		if(fieldName.equals(USER_NAME)) {
            return USUAL_PREFIX + "+" + parent.getString(R.string.w_common_phone_number_desc);
		}
		return super.getDefaultFieldSummary(fieldName);
	}
	
	
	public SipProfile buildAccount(SipProfile account) {
		account = super.buildAccount(account);
		account.proxies = new String[] {"sip:asbg.sip.optimus.pt;hide"};
		account.transport = SipProfile.TRANSPORT_UDP;
		return account;
	}
	
	@Override
	public void setDefaultParams(PreferencesWrapper prefs) {
		super.setDefaultParams(prefs);
		prefs.setPreferenceStringValue(SipConfigManager.USER_AGENT, "Optimus-SoftPhone/7.0.1.4124");
	}
	
	@Override
	public boolean needRestart() {
		return true;
	}
}
