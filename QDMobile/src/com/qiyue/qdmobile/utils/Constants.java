package com.qiyue.qdmobile.utils;

import com.qiyue.qdmobile.api.SipProfile;

/**
 * Created by simon on 6/23/14.
 */
public class Constants {

    public static final String FRAGMENT_TAG_DIALPAD                    = "com.qiyue.qdmobile.FRAGMENT_TAG_DIALPAD";
    public static final String FRAGMENT_TAG_RECENTS                    = "com.qiyue.qdmobile.FRAGMENT_TAG_CALLLOG";
    public static final String FRAGMENT_TAG_WARNING                    = "com.qiyue.qdmobile.FRAGMENT_TAG_WARNING";
    public static final String FRAGMENT_TAG_SETTINGS                   = "com.qiyue.qdmobile.FRAGMENT_TAG_SETTINGS";
    public static final String FRAGMENT_TAG_CONVERSATIONS_LIST         = "com.qiyue.qdmobile.FRAGMENT_TAG_CONVERSATIONS_LIST";
    public static final String FRAGMENT_TAG_FLASH                      = "com.qiyue.qdmobile.FRAGMENT_TAG_FLASH";

    public static final String FONTS_RBC_LIGHT = "fonts/RobotoCondensed-Light.ttf";

    public final static int PICKUP_SIP_URI = 0;

    public static final String[] ACC_PROJECTION = new String[] {
            SipProfile.FIELD_ID,
            SipProfile.FIELD_ACC_ID, // Needed for default domain
            SipProfile.FIELD_REG_URI, // Needed for default domain
            SipProfile.FIELD_PROXY, // Needed for default domain
            SipProfile.FIELD_DEFAULT_URI_SCHEME, // Needed for default scheme
            SipProfile.FIELD_DISPLAY_NAME,
            SipProfile.FIELD_WIZARD
    };

    public static final String EXTRA_ACCOUNT_JSON = "EXTRA_ACCOUNT_JSON";


}

