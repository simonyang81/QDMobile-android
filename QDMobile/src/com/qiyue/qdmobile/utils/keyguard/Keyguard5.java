package com.qiyue.qdmobile.utils.keyguard;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class Keyguard5 extends KeyguardWrapper {

    @Override
    public void initActivity(Activity activity) {
        Window w = activity.getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    public void lock() {
        // Done automatically
    }

    @Override
    public void unlock() {
        // Done automatically
    }
    
}
