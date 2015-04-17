package com.qiyue.qdmobile.wizards.impl;

import android.text.InputType;

import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.PreferencesWrapper;

public class DeltaTelecom extends SimpleImplementation {

    @Override
    protected String getDomain() {
        return "sip.skylink.ru";
    }

    @Override
    protected String getDefaultName() {
        return "JSC - DeltaTelecom";
    }

    // Customization
    @Override
    public void fillLayout(final SipProfile account) {
        super.fillLayout(account);

        accountUsername.setTitle(R.string.w_common_phone_number);
        accountUsername.setDialogTitle(R.string.w_common_phone_number);
        accountUsername.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);

    }

    @Override
    public String getDefaultFieldSummary(String fieldName) {
        if (fieldName.equals(USER_NAME)) {
            return parent.getString(R.string.w_common_phone_number_desc);
        }
        return super.getDefaultFieldSummary(fieldName);
    }

    @Override
    public boolean needRestart() {
        return true;
    }

    @Override
    public void setDefaultParams(PreferencesWrapper prefs) {
        super.setDefaultParams(prefs);
        prefs.setPreferenceBooleanValue(SipConfigManager.ENABLE_STUN, true);

        prefs.addStunServer("stun.skylink.ru");
    }

}
