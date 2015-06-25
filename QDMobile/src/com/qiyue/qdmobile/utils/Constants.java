package com.qiyue.qdmobile.utils;

import com.qiyue.qdmobile.api.SipProfile;

/**
 * Created by simon on 6/23/14.
 */
public class Constants {

    public static final String FRAGMENT_TAG_DIALPAD                    = "com.qiyue.qdmobile.FRAGMENT_TAG_DIALPAD";
    public static final String FRAGMENT_TAG_RECENTS                    = "com.qiyue.qdmobile.FRAGMENT_TAG_CALLLOG";
    public static final String FRAGMENT_TAG_SETTINGS                   = "com.qiyue.qdmobile.FRAGMENT_TAG_SETTINGS";
    public static final String FRAGMENT_TAG_CONVERSATIONS_LIST         = "com.qiyue.qdmobile.FRAGMENT_TAG_CONVERSATIONS_LIST";
    public static final String FRAGMENT_TAG_FLASH                      = "com.qiyue.qdmobile.FRAGMENT_TAG_FLASH";

    public static final String FONTS_RBC_LIGHT = "fonts/RobotoCondensed-Light.ttf";

    public final static int PICKUP_SIP_URI = 0;

    public static final String[] ACC_PROJECTION = new String[] {
            SipProfile.FIELD_ID,
            SipProfile.FIELD_ACC_ID,                // Needed for default domain
            SipProfile.FIELD_REG_URI,               // Needed for default domain
            SipProfile.FIELD_PROXY,                 // Needed for default domain
            SipProfile.FIELD_DEFAULT_URI_SCHEME,    // Needed for default scheme
            SipProfile.FIELD_DISPLAY_NAME,
            SipProfile.FIELD_WIZARD
    };

    public static final String EXTRA_ACCOUNT_JSON = "EXTRA_ACCOUNT_JSON";

    public static final boolean USE_VIDEO = true;

    public static final String QDMobile_DATE_FORMAT = "yyyy-MM-dd";
    public static final String QDMobile_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String LBS_DATE_FORMAT = "yyMMddHHmm";

    public static final String DEBUG_CODE = "*3328433284#";

    public static final int LBS_SCAN_SPAN = 1000 * 60; // 60 seconds

    public static final int LBS_OLD_DATA_TIME_PERIOD = 1000 * 60 * 10; // 10 mins

    public static final String LBS_SERVER_AK = "NIHuuEoFPysGihl2CwTOOzVb";

    public static final String LBS_CLOUD_API_URL = "http://api.map.baidu.com";

    public static final String LBS_CLOUD_CREATE_GEOTABLE_API_URL    = "/geodata/v3/geotable/create";
    public static final String LBS_CLOUD_CREATE_COLUMN_API_URL      = "/geodata/v3/column/create";
    public static final String LBS_CLOUD_CREATE_POI_API_URL         = "/geodata/v3/poi/create";
    public static final String LBS_CLOUD_DELETE_POI_API_URL         = "/geodata/v3/poi/delete";

    public static final String LBS_GEO_TABLE_NAME                   = "DQMobile_GEO_TABLE";
    public static final String LBS_GEO_TABLE_COLUMN_SIP_ACCOUNT     = "SIP_ACCOUNT";
    public static final String LBS_GEO_TABLE_COLUMN_TIME_PERIOD     = "TIME_PERIOD";

    public static final String REAL_PACKAGE_NAME = "com.qiyue.qdmobile";

    public static final String SIP_PROCESS = "sipStack";
    public static final String LBS_PROCESS = "lbsStack";

    public static final String LBS_DQMobile_GEO_TABLE_ID = "111180";






}

