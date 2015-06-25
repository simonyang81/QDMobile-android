package com.qiyue.qdmobile.utils;

import java.lang.reflect.Method;

import android.os.HandlerThread;

import com.github.snowdream.android.util.Log;

public class Threading {

	private static final String THIS_FILE = "Threading";

	public final static void stopHandlerThread(HandlerThread handlerThread, boolean wait) {
		if (handlerThread == null) {
			return;
		}
		boolean fails = true;
		
		if (Compatibility.isCompatible(5)) {
			try {
				Method method = handlerThread.getClass().getDeclaredMethod("quit");
				method.invoke(handlerThread);
				fails = false;
			} catch (Exception e) {
				Log.d(THIS_FILE, "Something is wrong with api level declared use fallback method");
			}
		}
		if (fails && handlerThread.isAlive() && wait) {
			try {
				handlerThread.join(500);
			} catch (Exception e) {
				Log.e(THIS_FILE, "Can t finish handler thread....", e);
			}
		}
	}
}
