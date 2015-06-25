package com.qiyue.qdmobile.utils.clipboard;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

@TargetApi(11)
public class Clipboard11 extends ClipboardWrapper {

    private ClipboardManager clipboardManager;

    @Override
    protected void setContext(Context context) {
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void setText(String description, String text) {
        ClipData clip = ClipData.newPlainText(description, text);
        clipboardManager.setPrimaryClip(clip);
    }

}
