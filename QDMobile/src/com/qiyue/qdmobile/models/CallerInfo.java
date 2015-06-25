package com.qiyue.qdmobile.models;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.api.SipUri;
import com.qiyue.qdmobile.api.SipUri.ParsedSipContactInfos;
import com.qiyue.qdmobile.utils.contacts.ContactsWrapper;

/**
 * Looks up caller information for the given phone number.
 */
public class CallerInfo {

    private static final String THIS_FILE = "CallerInfo";

    public static final CallerInfo EMPTY = new CallerInfo();

    public boolean contactExists;

    public long personId;
    public String name;

    public String phoneNumber;
    public String phoneLabel;
    public int numberType;
    public String numberLabel;
    /** The photo for the contact, if available. */
    public long photoId;
    /** The high-res photo for the contact, if available. */
    public Uri photoUri;

    // fields to hold individual contact preference data,
    // including the send to voicemail flag and the ringtone
    // uri reference.
    public Uri contactRingtoneUri;
    public Uri contactContentUri;
    
    private static LruCache<String, CallerInfo> callerCache;

    private static class CallerInfoLruCache extends LruCache<String, CallerInfo> {
        final Context mContext;
        public CallerInfoLruCache(Context context) {
            super(4 * 1024 * 1024);
            mContext = context;
        }
        
        @Override
        protected CallerInfo create(String sipUri) {
            CallerInfo callerInfo = null;
            ParsedSipContactInfos uriInfos = SipUri.parseSipContact(sipUri);
            String phoneNumber = SipUri.getPhoneNumber(uriInfos);
            if (!TextUtils.isEmpty(phoneNumber)) {
                Log.d(THIS_FILE, "Number found " + phoneNumber + ", try People lookup");
                callerInfo = ContactsWrapper.getInstance().findCallerInfo(mContext, phoneNumber);
            }

            if (callerInfo == null || !callerInfo.contactExists) {
                // We can now search by sip uri
                callerInfo = ContactsWrapper.getInstance().findCallerInfoForUri(mContext,
                        uriInfos.getContactAddress());
            }
            
            if(callerInfo == null) {
                callerInfo = new CallerInfo();
                callerInfo.phoneNumber = sipUri;
            }
            
            return callerInfo;
        }
        
    }
    

    /**
     * Build and retrieve caller infos from contacts based on the caller sip uri
     * 
     * @param context Current application context
     * @param sipUri The remote contact sip uri
     * @return The caller info as CallerInfo object
     */
    public static CallerInfo getCallerInfoFromSipUri(Context context, String sipUri) {
        if (TextUtils.isEmpty(sipUri)) {
            return EMPTY;
        }
        if(callerCache == null) {
            callerCache = new CallerInfoLruCache(context);
        }
        synchronized (callerCache) {
            return callerCache.get(sipUri);
        }
    }

    public static CallerInfo getCallerInfoForSelf(Context context) {
        return ContactsWrapper.getInstance().findSelfInfo(context);
    }

    @Override
    public String toString() {
        return "CallerInfo{" +
                "contactExists=" + contactExists +
                ", personId=" + personId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", phoneLabel='" + phoneLabel + '\'' +
                ", numberType=" + numberType +
                ", numberLabel='" + numberLabel + '\'' +
                ", photoId=" + photoId +
                ", photoUri=" + photoUri +
                ", contactRingtoneUri=" + contactRingtoneUri +
                ", contactContentUri=" + contactContentUri +
                '}';
    }
}
