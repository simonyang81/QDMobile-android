package com.qiyue.qdmobile.utils.audio;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.service.HeadsetButtonReceiver;
import com.qiyue.qdmobile.service.SipService;
import com.qiyue.qdmobile.utils.Compatibility;

@TargetApi(8)
public class AudioFocus8 extends AudioFocusWrapper {

    protected static final String THIS_FILE = AudioFocus8.class.getSimpleName();

    private AudioManager audioManager;
    private SipService service;
    private ComponentName headsetButtonReceiverName;

    private boolean isFocused = false;

    private OnAudioFocusChangeListener focusChangedListener = new OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(THIS_FILE, "Focus changed");
        }
    };

    public void init(SipService aService, AudioManager manager) {
        service = aService;
        audioManager = manager;
        headsetButtonReceiverName = new ComponentName(service.getPackageName(),
                HeadsetButtonReceiver.class.getName());
    }


    public void focus(boolean userWantsBT) {
        Log.d(THIS_FILE, "Focus again " + isFocused);
        if (!isFocused) {
            HeadsetButtonReceiver.setService(service.getUAStateReceiver());
            audioManager.registerMediaButtonEventReceiver(headsetButtonReceiverName);
            audioManager.requestAudioFocus(focusChangedListener,
                    Compatibility.getInCallStream(userWantsBT), AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            isFocused = true;
        }
    }

    public void unFocus() {
        if (isFocused) {
            HeadsetButtonReceiver.setService(null);
            audioManager.unregisterMediaButtonEventReceiver(headsetButtonReceiverName);
            //TODO : when switch to speaker -> failure to re-gain focus then cause music player will wait before reasking focus
            audioManager.abandonAudioFocus(focusChangedListener);
            isFocused = false;
        }
    }

}
