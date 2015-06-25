package com.qiyue.qdmobile.utils.clipboard;

import android.content.Context;

import com.qiyue.qdmobile.utils.Compatibility;

public abstract class ClipboardWrapper {

    private static ClipboardWrapper instance;
    
    public static ClipboardWrapper getInstance(Context context) {
        if (instance == null) {
            if (Compatibility.isCompatible(11)) {
                instance = new com.qiyue.qdmobile.utils.clipboard.Clipboard11();
            } else {
                instance = new com.qiyue.qdmobile.utils.clipboard.Clipboard1();
            }
            if (instance != null) {
                instance.setContext(context);
            }
        }
        
        return instance;
    }
    
    protected ClipboardWrapper() {}
    
    protected abstract void setContext(Context context);
    
    public abstract void setText(String description, String text);
}
