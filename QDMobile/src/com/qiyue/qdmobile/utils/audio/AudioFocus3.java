package com.qiyue.qdmobile.utils.audio;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.api.SipConfigManager;
import com.qiyue.qdmobile.service.HeadsetButtonReceiver;
import com.qiyue.qdmobile.service.SipService;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

public class AudioFocus3 extends AudioFocusWrapper {


    final static String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
    final static String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
    private static final String THIS_FILE = "AudioFocus3";

    private AudioManager audioManager;
    private SipService service;

    private boolean isMusicActive = false;
    private boolean isFocused = false;
    private HeadsetButtonReceiver headsetButtonReceiver;

    public void init(SipService aService, AudioManager manager) {
        service = aService;
        audioManager = manager;
    }

    public void focus(boolean userWantsBT) {
        if (!isFocused) {
            pauseMusic();
            registerHeadsetButton();
            isFocused = true;
        }
    }

    public void unFocus() {
        if (isFocused) {
            restartMusic();
            unregisterHeadsetButton();
            isFocused = false;
        }
    }


    private void pauseMusic() {
        isMusicActive = audioManager.isMusicActive();
        if (isMusicActive &&
                service.getPrefs().getPreferenceBooleanValue(SipConfigManager.INTEGRATE_WITH_NATIVE_MUSIC)) {
            service.sendBroadcast(new Intent(PAUSE_ACTION));
        }
    }

    private void restartMusic() {
        if (isMusicActive &&
                service.getPrefs().getPreferenceBooleanValue(SipConfigManager.INTEGRATE_WITH_NATIVE_MUSIC)) {
            service.sendBroadcast(new Intent(TOGGLEPAUSE_ACTION));
        }
    }

    private void registerHeadsetButton() {
        Log.d(THIS_FILE, "Register media button");
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY + 100);
        if (headsetButtonReceiver == null) {
            headsetButtonReceiver = new HeadsetButtonReceiver();
            HeadsetButtonReceiver.setService(service.getUAStateReceiver());
        }
        service.registerReceiver(headsetButtonReceiver, intentFilter);
    }

    private void unregisterHeadsetButton() {
        try {
            service.unregisterReceiver(headsetButtonReceiver);
            HeadsetButtonReceiver.setService(null);
            headsetButtonReceiver = null;
        } catch (Exception e) {
            Log.e(THIS_FILE, "unregisterHeadsetButton(), " + e.toString());
        }
    }
}
