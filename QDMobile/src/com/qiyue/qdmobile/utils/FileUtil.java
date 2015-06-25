package com.qiyue.qdmobile.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.github.snowdream.android.util.FilePathGenerator;
import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.QDMobileApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * FileUtil.java
 * <p/>
 * Created by simon on 12/25/14.
 * <p/>
 * All Rights Reserved.
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static boolean isStorageAccessible() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getTempImageFileName() {
        return "_temp_qdmobile_3328433284.png";
    }

    public static void initQDMobileLogsPath(Context context) {

        String logPath = getQDMobileLogsFilePath(context);
        String date = DateUtils.getDateLabel(new Date(), "yyyyMMdd");
        Log.setFilePathGenerator(new FilePathGenerator.DefaultFilePathGenerator(logPath, "QDMobile_" + date, ".log"));

    }

    public static String getQDMobileLogsFilePath(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_ALARMS).getPath();
    }

    public static void writeFileWithBitmap(Bitmap bm, File file) {

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "writeFileWithBitmap: " + e.toString());
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (Exception e) {
            }
        }

    }

    public static void deleteFilesOneMonthAgo (Context context) {

        Calendar cd_month = Calendar.getInstance();
        cd_month.add(Calendar.MONTH, -1);
        Date d = cd_month.getTime();

        final String logPath = getQDMobileLogsFilePath(QDMobileApplication.getContextQD());
        final String[] filesName = new File(logPath).list();

        for (String fileName : filesName) {
            File file = new File(logPath + File.separator + fileName);
            Long time =file.lastModified();
            Calendar cd = Calendar.getInstance();
            cd.setTimeInMillis(time);
            Date fileDate = cd.getTime();
            if (fileDate.before(d)) {
                file.delete();
            }
        }

    }



}
