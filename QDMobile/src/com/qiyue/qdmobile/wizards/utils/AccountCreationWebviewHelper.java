package com.qiyue.qdmobile.wizards.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebView;

import com.qiyue.qdmobile.utils.Compatibility;

@TargetApi(Build.VERSION_CODES.FROYO)
public abstract class  AccountCreationWebviewHelper {

private static AccountCreationWebviewHelper instance;
    
    public static AccountCreationWebviewHelper getInstance() {
        if (instance == null) {
            if (Compatibility.isCompatible(8)) {
                instance = new com.qiyue.qdmobile.wizards.utils.AccountCreationWebviewHelper8();
            } else {
                instance = new com.qiyue.qdmobile.wizards.utils.AccountCreationWebviewHelper3();
            }
        }
        
        return instance;
    }
    public abstract void setSSLNoSecure(WebView webView);
    public abstract void setAllowRedirect(WebView webView);
}
