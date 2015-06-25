package com.qiyue.qdmobile.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.github.snowdream.android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * EmailSender.java
 * <p/>
 * Created by simon on 1/27/15.
 * <p/>
 * All Rights Reserved.
 */
public class EmailSender {

    private static final String TAG = EmailSender.class.getSimpleName();

    private static final String PLAIN_TEXT = "plain/text";
    private Context mContext;

    public EmailSender(Context ctx) {
        mContext = ctx;
    }

    /**
     * Send <code>ACTION_SEND</code> request.
     * 
     * 
     * @param to to
     * @param subject subject
     * @param body body
     * @param attachmentFilePaths attachment file path or <code>null</code>
     */
    public boolean sendEmail(String to[], String subject, String body, String[] attachmentFilePaths) {

        ArrayList<Uri> attachmentUris = new ArrayList<Uri>();
        if (attachmentFilePaths != null && attachmentFilePaths.length > 0) {
            for (String attachment : attachmentFilePaths) {
                try {
                    File file = new File(attachment);
                    if (file == null) {
                        Log.w(TAG, "File error: " + attachment);
                    } else if (!file.exists()) {
                        Log.w(TAG, "File does not exist: " + attachment);
                    } else if (!file.canRead()) {
                        Log.w(TAG, "File can't be read: " + attachment);
                    } else if (!file.isFile()) {
                        Log.w(TAG, "Invalid file: " + attachment);
                    } else {
                        Uri uri = Uri.fromFile(file);
                        Log.i(TAG, "Attachement path[size=" + file.length() + "]: " + attachment);
                        Log.i(TAG, "Attachement URI: " + uri.toString());
                        attachmentUris.add(uri);
                    }
                } catch (Throwable ex) {
                    Log.w(TAG, "Error: " + ex.toString());
                }
            }
        }

        boolean multiple = attachmentUris == null ? false : attachmentUris.isEmpty() == false;
        final Intent emailIntent = new Intent(multiple ? Intent.ACTION_SEND_MULTIPLE
                : Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL,    to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,  subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT,     body);

        if (attachmentUris != null && attachmentUris.isEmpty() == false) {
            if (multiple) {
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentUris);
            } else {
                emailIntent.putExtra(Intent.EXTRA_STREAM, attachmentUris.get(0));
            }
        }

        emailIntent.setType(PLAIN_TEXT);
        List<ResolveInfo> availableSoft = mContext.getPackageManager()
                .queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (availableSoft.size() <= 0) {
        	return false;
        }
        mContext.startActivity(Intent.createChooser(emailIntent, "Send Email"));
        
        return true;
    }
}
