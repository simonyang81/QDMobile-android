package com.qiyue.qdmobile.service;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.pjsip.UAStateReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class HeadsetButtonReceiver extends BroadcastReceiver {

    private static final String THIS_FILE = "HeadsetButtonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(THIS_FILE, "onReceive");
        if (uaReceiver == null) {
            return;
        }
        //	abortBroadcast();
        //
        // Headset button has been pressed by user. Normally when
        // the UI is active this event will never be generated instead
        // a headset button press will be handled as a regular key
        // press event.
        //
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Log.d(THIS_FILE, "Key : " + event.getKeyCode());
            if (event != null &&
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                    event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {

                if (uaReceiver.handleHeadsetButton()) {
                    abortBroadcast();
                }
            }
        }

    }

    private static UAStateReceiver uaReceiver = null;

    public static void setService(UAStateReceiver aUAReceiver) {
        uaReceiver = aUAReceiver;
    }

}
