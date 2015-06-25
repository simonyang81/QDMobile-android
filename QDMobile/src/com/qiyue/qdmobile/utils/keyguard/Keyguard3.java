package com.qiyue.qdmobile.utils.keyguard;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;

@SuppressWarnings("deprecation")
public class Keyguard3 extends KeyguardWrapper {
    
    private Context context;
    
    // Keygard for incoming call
    private boolean manageKeyguard = false;
    private KeyguardManager keyguardManager;
    private KeyguardManager.KeyguardLock keyguardLock;
    

    @Override
    public void initActivity(Activity activity) {
        context = activity;
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("com.csipsimple.inCallKeyguard");
    }
    
    @Override
    public void lock() {
        if (manageKeyguard) {
            keyguardLock.reenableKeyguard();
        }
    }

    @Override
    public void unlock() {
        manageKeyguard = true;
        keyguardLock.disableKeyguard();
    }

    
}
