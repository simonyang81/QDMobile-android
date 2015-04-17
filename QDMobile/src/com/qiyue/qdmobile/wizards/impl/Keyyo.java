package com.qiyue.qdmobile.wizards.impl;

import android.text.InputType;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.PreferencesWrapper;

public class Keyyo extends SimpleImplementation {
	
	@Override
	protected String getDomain() {
		return "keyyo.net";
	}
	
	@Override
	protected String getDefaultName() {
		return "Keyyo VoIP";
	}

	
	//Customization
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
	
	
	public SipProfile buildAccount(SipProfile account) {
		account = super.buildAccount(account);
		//Ensure registration timeout value
		account.reg_timeout = 900;
		account.publish_enabled = 1;
		account.transport = SipProfile.TRANSPORT_AUTO;
		account.allow_contact_rewrite = true;
		account.contact_rewrite_method = 1;
        account.try_clean_registers = 0;
		account.vm_nbr = "123";
        account.sip_stun_use = 0;
        account.media_stun_use = 0;
        account.ice_cfg_enable = 1;
        account.ice_cfg_use = 0;
		return account;
	}
	
	public static void setKeyyoDefaultParams(PreferencesWrapper prefs) {
		prefs.setPreferenceBooleanValue(SipConfigManager.ENABLE_DNS_SRV, true);
		prefs.setPreferenceBooleanValue(SipConfigManager.ECHO_CANCELLATION, true);
		prefs.setPreferenceBooleanValue(SipConfigManager.ENABLE_VAD, true);
		prefs.setPreferenceBooleanValue(SipConfigManager.USE_COMPACT_FORM, true);
		
		
		//Only G711a/u and g722 on WB
		prefs.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_WB,"243");
		prefs.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_WB,"245");
		prefs.setCodecPriority("iLBC/8000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_WB,"0");
		prefs.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_WB, "0");
		
		//On NB set for gsm high priority
		prefs.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_NB,"243");
		prefs.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("iLBC/8000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_NB,"0");
		prefs.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_NB, "245");
	}
	
	@Override
	public void setDefaultParams(PreferencesWrapper prefs) {
		super.setDefaultParams(prefs);
		setKeyyoDefaultParams(prefs);
	}

	@Override
	public boolean needRestart() {
		return true;
	}
}
