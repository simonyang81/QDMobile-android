package com.qiyue.qdmobile.wizards.impl;

import android.text.InputType;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.PreferencesWrapper;

public class Telemega extends SimpleImplementation {
	
	@Override
	protected String getDomain() {
		return "proxy.siptele.net";
	}
	
	@Override
	protected String getDefaultName() {
		return "Telemega";
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
	
	
	@Override
	public void setDefaultParams(PreferencesWrapper prefs) {
		super.setDefaultParams(prefs);
        // only gsm
        prefs.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_WB,"0");
        prefs.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_WB,"0");
        prefs.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_WB,"0");
        prefs.setCodecPriority("iLBC/8000/1", SipConfigManager.CODEC_WB,"0");
        prefs.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_WB,"0");
        prefs.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_WB,"0");
        prefs.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_WB,"0");
        prefs.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_WB, "240");
        prefs.setCodecPriority("SILK/8000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("SILK/12000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("SILK/16000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("SILK/24000/1", SipConfigManager.CODEC_WB, "0");

        // only gsm
        prefs.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_NB,"0");
        prefs.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_NB,"0");
        prefs.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_NB,"0");
        prefs.setCodecPriority("iLBC/8000/1", SipConfigManager.CODEC_NB,"0");
        prefs.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_NB,"0");
        prefs.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_NB,"0");
        prefs.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_NB,"0");
        prefs.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_NB, "240");
        prefs.setCodecPriority("SILK/8000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("SILK/12000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("SILK/16000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("SILK/24000/1", SipConfigManager.CODEC_NB, "0");
	}
	
	@Override
	public boolean needRestart() {
	    return true;
	}
}
