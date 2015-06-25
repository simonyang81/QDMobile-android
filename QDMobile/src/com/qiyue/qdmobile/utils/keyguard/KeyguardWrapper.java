package com.qiyue.qdmobile.utils.keyguard;

import android.app.Activity;

import com.qiyue.qdmobile.utils.Compatibility;

public abstract class KeyguardWrapper {

    public static KeyguardWrapper getKeyguardManager(Activity activity) {
        KeyguardWrapper kw;
        if (Compatibility.isCompatible(5)) {
            kw = new Keyguard5();
        } else {
            kw = new Keyguard3();
        }
        kw.initActivity(activity);
        return kw;
    }
    
    public abstract void initActivity(Activity activity);
    public abstract void lock();
    public abstract void unlock();
}
