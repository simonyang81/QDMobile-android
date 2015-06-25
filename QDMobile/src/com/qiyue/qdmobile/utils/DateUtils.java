package com.qiyue.qdmobile.utils;

import com.github.snowdream.android.util.Log;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateUtils.java
 * <p/>
 * Created by simon on 1/16/15.
 * <p/>
 * All Rights Reserved.
 */
public class DateUtils {

    private static String TAG = DateUtils.class.getSimpleName();

    private static final SimpleDateFormat tvGuideDATE_FORMAT
            = new SimpleDateFormat(Constants.QDMobile_DATE_FORMAT);
    private static final SimpleDateFormat tvGuideTime_FORMAT
            = new SimpleDateFormat(Constants.QDMobile_TIME_FORMAT);

    public static String getDateLabel(Date date, String format) {
        FastDateFormat fdf;
        try {
            fdf = FastDateFormat.getInstance(format);
            return fdf.format(date);
        } catch (Exception  ex) {
            return "";
        }

    }

}
