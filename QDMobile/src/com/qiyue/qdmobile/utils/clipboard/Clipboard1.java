package com.qiyue.qdmobile.utils.clipboard;

import android.content.Context;
import android.text.ClipboardManager;

@SuppressWarnings("deprecation")
public class Clipboard1 extends ClipboardWrapper {

    private ClipboardManager clipboardManager;

    @SuppressWarnings("deprecation")
    @Override
    protected void setContext(Context context) {
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void setText(String description, String text) {
        clipboardManager.setText(text);
    }

}
