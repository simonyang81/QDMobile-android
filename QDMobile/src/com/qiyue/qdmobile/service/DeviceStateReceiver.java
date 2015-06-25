package com.qiyue.qdmobile.service;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.api.SipProfile;
import com.qiyue.qdmobile.utils.CallHandlerPlugin;
import com.qiyue.qdmobile.utils.ExtraPlugins;
import com.qiyue.qdmobile.utils.NightlyUpdater;
import com.qiyue.qdmobile.utils.PhoneCapabilityTester;
import com.qiyue.qdmobile.utils.PreferencesProviderWrapper;
import com.qiyue.qdmobile.utils.RewriterPlugin;

public class DeviceStateReceiver extends BroadcastReceiver {

    private static final String THIS_FILE = DeviceStateReceiver.class.getSimpleName();

    public static final String APPLY_NIGHTLY_UPLOAD = "com.csipsimple.action.APPLY_NIGHTLY";

    @Override
    public void onReceive(Context context, Intent intent) {

        PreferencesProviderWrapper prefWrapper = new PreferencesProviderWrapper(context);
        String intentAction = intent.getAction();

        //
        // ACTION_DATA_STATE_CHANGED
        // Data state change is used to detect changes in the mobile
        // network such as a switch of network type (GPRS, EDGE, 3G)
        // which are not detected by the Connectivity changed broadcast.
        //
        //
        // ACTION_CONNECTIVITY_CHANGED
        // Connectivity change is used to detect changes in the overall
        // data network status as well as a switch between wifi and mobile
        // networks.
        //
        if (/*intentAction.equals(ACTION_DATA_STATE_CHANGED) ||*/
                intentAction.equals(ConnectivityManager.CONNECTIVITY_ACTION) ||
                intentAction.equals(Intent.ACTION_BOOT_COMPLETED)) {
            
            if (prefWrapper.isValidConnectionForIncoming()
                    &&
                    !prefWrapper
                            .getPreferenceBooleanValue(PreferencesProviderWrapper.HAS_BEEN_QUIT)) {
                Log.d(THIS_FILE, "Try to start service if not already started");
                Intent sip_service_intent = new Intent(context, SipService.class);
                context.startService(sip_service_intent);
            }

        } else if (intentAction.equals(SipManager.INTENT_SIP_ACCOUNT_ACTIVATE)) {
            context.enforceCallingOrSelfPermission(SipManager.PERMISSION_CONFIGURE_SIP, null);

            long accId;
            accId = intent.getLongExtra(SipProfile.FIELD_ID, SipProfile.INVALID_ID);

            if (accId == SipProfile.INVALID_ID) {
                // allow remote side to send us integers.
                // previous call will warn, but that's fine, no worries
                accId = intent.getIntExtra(SipProfile.FIELD_ID, (int) SipProfile.INVALID_ID);
            }

            if (accId != SipProfile.INVALID_ID) {
                boolean active = intent.getBooleanExtra(SipProfile.FIELD_ACTIVE, true);
                ContentValues cv = new ContentValues();
                cv.put(SipProfile.FIELD_ACTIVE, active);
                int done = context.getContentResolver().update(
                        ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, accId), cv,
                        null, null);
                if (done > 0) {
                    if (prefWrapper.isValidConnectionForIncoming()) {
                        Intent sipServiceIntent = new Intent(context, SipService.class);
                        context.startService(sipServiceIntent);
                    }
                }
            }
        } else if (Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(intentAction) ||
                Intent.ACTION_PACKAGE_REMOVED.equalsIgnoreCase(intentAction)) {
            CallHandlerPlugin.clearAvailableCallHandlers();
            RewriterPlugin.clearAvailableRewriters();
            ExtraPlugins.clearDynPlugins();
            PhoneCapabilityTester.deinit();
        } else if (APPLY_NIGHTLY_UPLOAD.equals(intentAction)) {
            NightlyUpdater nu = new NightlyUpdater(context);
            nu.applyUpdate(intent);
        }
    }

}
