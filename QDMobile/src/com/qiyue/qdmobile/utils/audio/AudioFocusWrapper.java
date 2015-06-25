package com.qiyue.qdmobile.utils.audio;

import android.media.AudioManager;

import com.qiyue.qdmobile.service.SipService;
import com.qiyue.qdmobile.utils.Compatibility;

public abstract class AudioFocusWrapper {
private static AudioFocusWrapper instance;
	
	public static AudioFocusWrapper getInstance() {
		if (instance == null) {
			if (Compatibility.isCompatible(8)) {
				instance = new com.qiyue.qdmobile.utils.audio.AudioFocus8();
			} else {
				instance = new com.qiyue.qdmobile.utils.audio.AudioFocus3();
			}
		}
		
		return instance;
	}
	
	protected AudioFocusWrapper() {}
	
	public abstract void init(SipService service, AudioManager manager);
	public abstract void focus(boolean userWantsBluetooth);
	public abstract void unFocus();
	
}
