package com.qiyue.qdmobile.wizards.impl;

import android.text.TextUtils;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.PreferencesWrapper;

public class Sonetel extends SimpleImplementation {
	
	
	@Override
	protected String getDefaultName() {
		return "Sonetel";
	}

	
	//Customization
	@Override
	public void fillLayout(final SipProfile account) {
		super.fillLayout(account);
		
		accountUsername.setTitle(R.string.email_address);
		accountUsername.setDialogTitle(R.string.email_address);
		if( ! TextUtils.isEmpty(account.username) && !TextUtils.isEmpty(account.getSipDomain()) ){
			accountUsername.setText(account.username+"@"+account.getSipDomain());
		}
		
	}
	@Override
	public String getDefaultFieldSummary(String fieldName) {
		if(fieldName.equals(USER_NAME)) {
			return parent.getString(R.string.email_address);
		}
		return super.getDefaultFieldSummary(fieldName);
	}
	
	
	public SipProfile buildAccount(SipProfile account) {
		account = super.buildAccount(account);
		String[] emailParts = getText(accountUsername).trim().split("@");
		if(emailParts.length == 2) {
			account.username = emailParts[0];
			account.acc_id = "<sip:"+ getText(accountUsername).trim() +">";
			
			//account.reg_uri = "sip:"+ emailParts[1];
			// Already done by super, just to be sure and let that modifiable for future if needed re-add there
			// Actually sounds that it also work and that's also probably cleaner :
			account.reg_uri = "sip:"+getDomain();
			account.proxies = new String[] { "sip:"+getDomain() } ;
		}
		
		return account;
	}
	
	@Override
	public void setDefaultParams(PreferencesWrapper prefs) {
		prefs.setPreferenceBooleanValue(SipConfigManager.ENABLE_STUN, true);
		

		//Only G711a/u  on WB & NB
		prefs.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_WB,"245");
		prefs.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_WB,"244");
		prefs.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("iLBC/8000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_WB, "0");
		
		prefs.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_NB,"245");
		prefs.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_NB,"244");
		prefs.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("iLBC/8000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_NB, "0");
	}
	
	public boolean canSave() {
		boolean canSave = super.canSave();
		
		String[] emailParts = getText(accountUsername).split("@");		
		canSave &= checkField(accountUsername, (emailParts.length != 2));
		
		return canSave;
		
	}

	@Override
	public boolean needRestart() {
		return true;
	}


	@Override
	protected String getDomain() {
		return "sonetel.net";
	}
}
