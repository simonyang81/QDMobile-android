package com.qiyue.qdmobile.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CallLog;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.api.SipCallSession;
import com.qiyue.qdmobile.api.SipManager;
import com.qiyue.qdmobile.models.CallerInfo;
import com.qiyue.qdmobile.models.Filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallLogHelper {

    private static final String ACTION_ANNOUNCE_SIP_CALLLOG = "de.ub0r.android.callmeter.SAVE_SIPCALL";
    // Uri of call log entry
    private static final String EXTRA_CALL_LOG_URI = "uri";
    // Provider name
    public static final String EXTRA_SIP_PROVIDER = "provider";
    private static final String THIS_FILE = "CallLogHelper";


    public static void addCallLog(Context context, ContentValues values, ContentValues extraValues) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri result = null;
        try {
            result = contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
        } catch (IllegalArgumentException e) {
            Log.w(THIS_FILE, "Cannot insert call log entry. Probably not a phone", e);
        }

        if (result != null) {
            // Announce that to other apps
            final Intent broadcast = new Intent(ACTION_ANNOUNCE_SIP_CALLLOG);
            broadcast.putExtra(EXTRA_CALL_LOG_URI, result.toString());
            String provider = extraValues.getAsString(EXTRA_SIP_PROVIDER);
            if (provider != null) {
                broadcast.putExtra(EXTRA_SIP_PROVIDER, provider);
            }
            context.sendBroadcast(broadcast);
        }
    }

    public static ContentValues logValuesForCall(Context context, SipCallSession call, long callStart) {
        ContentValues cv = new ContentValues();
        String remoteContact = call.getRemoteContact();

        cv.put(CallLog.Calls.NUMBER, remoteContact);

        Pattern p = Pattern.compile("^(?:\")?([^<\"]*)(?:\")?[ ]*(?:<)?sip(?:s)?:([^@]*@[^>]*)(?:>)?", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(remoteContact);
        String number = remoteContact;
        if (m.matches()) {
            number = m.group(2);
        }

        cv.put(CallLog.Calls.DATE, (callStart > 0) ? callStart : System.currentTimeMillis());
        int type = CallLog.Calls.OUTGOING_TYPE;
        int nonAcknowledge = 0;
        if (call.isIncoming()) {
            type = CallLog.Calls.MISSED_TYPE;
            nonAcknowledge = 1;
            if (callStart > 0) {
                // Has started on the remote side, so not missed call
                type = CallLog.Calls.INCOMING_TYPE;
                nonAcknowledge = 0;
            } else if (call.getLastStatusCode() == SipCallSession.StatusCode.DECLINE ||
                    call.getLastStatusCode() == SipCallSession.StatusCode.BUSY_HERE ||
                    call.getLastReasonCode() == 200) {
                // We have intentionally declined this call or replied elsewhere
                type = CallLog.Calls.INCOMING_TYPE;
                nonAcknowledge = 0;
            }
        }


        int hasBeenAutoanswered = Filter.isAutoAnswerNumber(context, call.getAccId(), number, null);
        if (hasBeenAutoanswered == call.getLastStatusCode()) {
            nonAcknowledge = 0;
        }
        cv.put(CallLog.Calls.TYPE, type);
        cv.put(CallLog.Calls.NEW, nonAcknowledge);
        cv.put(CallLog.Calls.DURATION,
                (callStart > 0) ? (System.currentTimeMillis() - callStart) / 1000 : 0);
        cv.put(SipManager.CALLLOG_PROFILE_ID_FIELD, call.getAccId());
        cv.put(SipManager.CALLLOG_STATUS_CODE_FIELD, call.getLastStatusCode());
        cv.put(SipManager.CALLLOG_STATUS_TEXT_FIELD, call.getLastStatusComment());

        CallerInfo callerInfo = CallerInfo.getCallerInfoFromSipUri(context, remoteContact);
        if (callerInfo != null) {
            cv.put(CallLog.Calls.CACHED_NAME, callerInfo.name);
            cv.put(CallLog.Calls.CACHED_NUMBER_LABEL, callerInfo.numberLabel);
            cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, callerInfo.numberType);
        }

        return cv;
    }
}
