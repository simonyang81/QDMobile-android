package com.qiyue.qdmobile.pjsip.reghandler;

import android.content.Context;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.pjsip.PjSipService.PjsipModule;

import org.pjsip.pjsua.pjsua;

public class RegHandlerModule implements PjsipModule {

    private static final String THIS_FILE = "RegHandlerModule";
    private RegHandlerCallback regHandlerReceiver;

    public RegHandlerModule() {
    }

    @Override
    public void setContext(Context ctxt) {
        regHandlerReceiver = new RegHandlerCallback(ctxt);
    }

    @Override
    public void onBeforeStartPjsip() {
        int status = pjsua.mobile_reg_handler_init();
        pjsua.mobile_reg_handler_set_callback(regHandlerReceiver);
        Log.d(THIS_FILE, "Reg handler module added with status " + status);
    }

    @Override
    public void onBeforeAccountStartRegistration(int pjId, SipProfile acc) {
        regHandlerReceiver.set_account_cleaning_state(pjId, acc.try_clean_registers);
    }

}
