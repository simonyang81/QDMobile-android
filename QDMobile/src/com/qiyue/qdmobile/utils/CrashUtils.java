package com.qiyue.qdmobile.utils;

import com.github.snowdream.android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * CrashUtils.java
 * <p/>
 * Created by simon on 2/28/15.
 * <p/>
 * Copyright (C) 2014, Newsbeat.
 * All Rights Reserved.
 */
public class CrashUtils implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "QDMobile-Crash";

    private static CrashUtils instance = new CrashUtils();

    public static CrashUtils getInstance() {
        return instance;
    }

    private CrashUtils() {
    }

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        handleException(ex);
        mDefaultHandler.uncaughtException(thread, ex);

    }

    private void handleException(Throwable ex) {
        if (ex == null) {
            return;
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        StringBuffer sb = new StringBuffer();

        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        Log.e(TAG, "Exception: " + sb);
    }

}