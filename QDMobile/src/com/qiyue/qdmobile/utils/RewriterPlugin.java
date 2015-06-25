package com.qiyue.qdmobile.utils;

import android.Manifest.permission;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.api.SipManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RewriterPlugin {

    private static final String THIS_FILE = "RewriterPlugin";
    public static final String EXTRA_REMOTE_INTENT_TOKEN = "android.intent.extra.remote_intent_token";

    private static Map<String, String> AVAILABLE_REWRITERS = null;

    /**
     * Rewrite a number using a given plugin.
     * Warning this should never be done on main thread otherwise will always fail due to thread issues.
     * 
     * @param context The application context to use to talk to plugin
     * @param componentName The fully qualified component name of the plugin
     * @param number The number to rewrite
     */
    public static String rewriteNumber(Context context, final String componentName, String number) {
        ComponentName cn = ComponentName.unflattenFromString(componentName);

        Intent it = new Intent(SipManager.ACTION_REWRITE_NUMBER);
        it.putExtra(Intent.EXTRA_PHONE_NUMBER, number);
        it.setComponent(cn);
        
        OnRewriteReceiver resultTreater = new OnRewriteReceiver(number);
        context.sendOrderedBroadcast(it, permission.PROCESS_OUTGOING_CALLS, resultTreater, null,
                Activity.RESULT_OK, null, null);
        
        return resultTreater.getResult();
    }


    /**
     * Retrieve rewriter available as plugin for csipsimple.
     * 
     * @param ctxt context of application
     * @return A map of package name => Fancy name of rewriter
     */
    public static Map<String, String> getAvailableRewriters(Context ctxt) {

        if (AVAILABLE_REWRITERS == null) {
            AVAILABLE_REWRITERS = new HashMap<String, String>();

            PackageManager packageManager = ctxt.getPackageManager();
            Intent it = new Intent(SipManager.ACTION_REWRITE_NUMBER);

            List<ResolveInfo> availables = packageManager.queryBroadcastReceivers(it, 0);
            for (ResolveInfo resInfo : availables) {
                ActivityInfo actInfos = resInfo.activityInfo;
                Log.d(THIS_FILE, "Found rewriter " + actInfos.packageName + " " + actInfos.name);
                if (packageManager.checkPermission(permission.PROCESS_OUTGOING_CALLS,
                        actInfos.packageName) == PackageManager.PERMISSION_GRANTED) {
                    String packagedActivityName
                            = (new ComponentName(actInfos.packageName, actInfos.name)).flattenToString();
                    AVAILABLE_REWRITERS.put(packagedActivityName, (String) resInfo.loadLabel(packageManager));
                }
            }
        }

        return AVAILABLE_REWRITERS;
    }

    /**
     * Reset cache of outgoing call handlers
     */
    public static void clearAvailableRewriters() {
        AVAILABLE_REWRITERS = null;
    }


    /**
     * Interface for listener about load state of remote call handler plugin
     */
    public static class OnRewriteReceiver extends BroadcastReceiver {
        String result;
        private Semaphore runSemaphore;
        

        public OnRewriteReceiver(String defaultResult) {
            super();
            result = defaultResult;
            runSemaphore = new Semaphore(0);
        }
        
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(THIS_FILE, "Rewriter receive");
            Bundle resolvedInfos = getResultExtras(true);
            result = resolvedInfos.getString(Intent.EXTRA_PHONE_NUMBER);
            Log.d(THIS_FILE, "Rewriter receive : " + result);
            runSemaphore.release();
        }

        public String getResult() {
            try {
                runSemaphore.tryAcquire(5L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.e(THIS_FILE, "Can't acquire run semaphore... problem...");
            }
            return result;
        }
    }

}
