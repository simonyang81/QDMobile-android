package com.qiyue.qdmobile.utils.accessibility;

import android.content.Context;

import com.qiyue.qdmobile.utils.Compatibility;

public abstract class AccessibilityWrapper {
	
	private static AccessibilityWrapper instance;
	
	public static AccessibilityWrapper getInstance() {
		if (instance == null) {
			if (Compatibility.isCompatible(4)) {
				instance = new com.qiyue.qdmobile.utils.accessibility.Accessibility4();
			} else {
				instance = new com.qiyue.qdmobile.utils.accessibility.Accessibility3();
			}
		}
		
		return instance;
	}
	
	protected AccessibilityWrapper() {}

	public abstract void init(Context context);
	public abstract boolean isEnabled();

}
