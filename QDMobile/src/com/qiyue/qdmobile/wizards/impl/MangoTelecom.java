package com.qiyue.qdmobile.wizards.impl;

import android.text.TextUtils;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.PreferencesWrapper;

public class MangoTelecom extends AlternateServerImplementation {
    
    static final String DEFAULT_DOMAIN = "mangosip.ru";
	
	@Override
	protected String getDomain() {
	    String thirdDomain = accountServer.getText();
	    if(!TextUtils.isEmpty(thirdDomain)) {
	        return thirdDomain.trim() + "." + DEFAULT_DOMAIN;
	    }
		return DEFAULT_DOMAIN;
	}
	
	@Override
	protected String getDefaultName() {
		return "Mango Telecom";
	}
	
    public boolean canSave() {
        boolean isValid = true;
        
        isValid &= checkField(accountDisplayName, isEmpty(accountDisplayName));
        isValid &= checkField(accountPassword, isEmpty(accountPassword));
        isValid &= checkField(accountUsername, isEmpty(accountUsername));

        return isValid;
    }
	
	//Customization
	@Override
	public void fillLayout(final SipProfile account) {
		super.fillLayout(account);
		String sipDomain = account.getSipDomain();
		if(!TextUtils.isEmpty(sipDomain)) {
		    if(!sipDomain.equals(DEFAULT_DOMAIN)) {
		        accountServer.setText(sipDomain.replace("."+DEFAULT_DOMAIN, ""));
		    }else {
		        accountServer.setText("");
		    }
		}
        accountServer.setTitle(R.string.user_personal_domain);
        accountServer.setDialogTitle(R.string.user_personal_domain);
	}
	
    @Override
    public String getDefaultFieldSummary(String fieldName) {
        if(fieldName.equals(SERVER)) {
            return parent.getString(R.string.user_personal_domain);
        }
        return super.getDefaultFieldSummary(fieldName);
    }
	
	/* (non-Javadoc)
	 * @see com.qiyue.qdmobile.wizards.impl.SimpleImplementation#buildAccount(com.qiyue.qdmobile.api.SipProfile)
	 */
	@Override
	public SipProfile buildAccount(SipProfile account) {
	    SipProfile acc = super.buildAccount(account);
        acc.sip_stun_use = 1;
        acc.media_stun_use = 1;
	    return acc;
	}
    @Override
    public void setDefaultParams(PreferencesWrapper prefs) {
        super.setDefaultParams(prefs);
        prefs.setPreferenceStringValue(SipConfigManager.DTMF_MODE, Integer.toString(SipConfigManager.DTMF_MODE_RTP));
        prefs.setPreferenceBooleanValue(SipConfigManager.ENABLE_STUN, true);
        prefs.addStunServer("mangosip.ru:3478");
    }
	
}
