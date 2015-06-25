package com.qiyue.qdmobile.plugins.telephony;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.utils.CallHandlerPlugin;
import com.qiyue.qdmobile.utils.PhoneCapabilityTester;

import java.util.List;

public class CallHandler extends BroadcastReceiver {

    private static final String THIS_FILE = CallHandler.class.getSimpleName();

    private static Bitmap sPhoneAppBmp = null;
    private static boolean sPhoneAppInfoLoaded = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SipManager.ACTION_GET_PHONE_HANDLERS.equals(intent.getAction())) {

            PendingIntent pendingIntent = null;
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            // We must handle that clean way cause when call just to
            // get the row in account list expect this to reply correctly
            if (number != null && PhoneCapabilityTester.isPhone(context)) {
                // Build pending intent
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.fromParts("tel", number, null));
                pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
            }

            // Retrieve and cache infos from the phone app
            if (!sPhoneAppInfoLoaded) {
                List<ResolveInfo> callers = PhoneCapabilityTester.resolveActivitiesForPriviledgedCall(context);
                if (callers != null) {
                    for (final ResolveInfo caller : callers) {
                        if (caller.activityInfo.packageName.startsWith("com.android")) {
                            PackageManager pm = context.getPackageManager();
                            Resources remoteRes;
                            try {
                                // We load the resource in the context of the remote app to have a bitmap to return.
                                remoteRes = pm.getResourcesForApplication(caller.activityInfo.applicationInfo);
                                sPhoneAppBmp = BitmapFactory.decodeResource(remoteRes, caller.getIconResource());
                            } catch (NameNotFoundException e) {
                                Log.e(THIS_FILE, "Impossible to load ", e);
                            }
                            break;
                        }
                    }
                }
                sPhoneAppInfoLoaded = true;
            }


            //Build the result for the row (label, icon, pending intent, and excluded phone number)
            Bundle results = getResultExtras(true);
            if (pendingIntent != null) {
                results.putParcelable(CallHandlerPlugin.EXTRA_REMOTE_INTENT_TOKEN, pendingIntent);
            }
            results.putString(Intent.EXTRA_TITLE, context.getResources().getString(R.string.use_pstn));
            if (sPhoneAppBmp != null) {
                results.putParcelable(Intent.EXTRA_SHORTCUT_ICON, sPhoneAppBmp);
            }

            // This will exclude next time tel:xxx is raised from csipsimple treatment which is wanted
            results.putString(Intent.EXTRA_PHONE_NUMBER, number);

        }
    }

}
