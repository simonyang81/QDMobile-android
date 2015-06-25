package com.qiyue.qdmobile.utils.accessibility;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;

public class Accessibility4 extends AccessibilityWrapper {

	private AccessibilityManager accessibilityManager = null;
	
	@Override
	public void init(Context context) {
		if (accessibilityManager == null) {
			accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		}
	}

	@Override
	public boolean isEnabled() {
		return accessibilityManager.isEnabled();
	}

}
