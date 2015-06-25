package com.qiyue.qdmobile.pjsip.sipclf;

import android.content.Context;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.pjsip.PjSipService.PjsipModule;

import org.pjsip.pjsua.pjsua;

public class SipClfModule implements PjsipModule {

    private static final String THIS_FILE = "SipClfModule";
    private boolean enableModule = false;

    public SipClfModule() {
    }

    @Override
    public void setContext(Context ctxt) {
    }

    @Override
    public void onBeforeStartPjsip() {
        if (enableModule) {
            int status = pjsua.sipclf_mod_init();
            Log.d(THIS_FILE, "SipClfModule module added with status " + status);
        }
    }

    @Override
    public void onBeforeAccountStartRegistration(int pjId, SipProfile acc) {
    }

}
