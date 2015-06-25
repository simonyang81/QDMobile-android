package com.qiyue.qdmobile.pjsip;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.utils.Compatibility;
import com.qiyue.qdmobile.utils.PreferencesProviderWrapper;

import java.io.File;
import java.lang.reflect.Field;

public class NativeLibManager {

    private static final String THIS_FILE = NativeLibManager.class.getSimpleName();

    public static final String STD_LIB_NAME = "stlport_shared";
    public static final String STACK_NAME = "pjsipjni";

    public static final String VIDEO_NAME = "pj_video_android";
    public static final String VPX_NAME = "pj_vpx";

    public static File getBundledStackLibFile(Context ctx, String libName) {
        PackageInfo packageInfo = PreferencesProviderWrapper.getCurrentPackageInfos(ctx);
        if (packageInfo != null) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            File f = getLibFileFromPackage(appInfo, libName, true);
            return f;
        }

        // This is the very last fallback method
        return new File(ctx.getFilesDir().getParent(), "lib" + File.separator + libName);
    }

    public static File getLibFileFromPackage(ApplicationInfo appInfo, String libName, boolean allowFallback) {
        Log.v(THIS_FILE, "Dir " + appInfo.dataDir);
        if (Compatibility.isCompatible(9)) {
            try {
                Field f = ApplicationInfo.class.getField("nativeLibraryDir");
                File nativeFile = new File((String) f.get(appInfo), libName);
                if (nativeFile.exists()) {
                    Log.v(THIS_FILE, "Found native lib using clean way");
                    return nativeFile;
                }
            } catch (Exception e) {
                Log.e(THIS_FILE, "Cant get field for native lib dir", e);
            }
        }
        if (allowFallback) {
            return new File(appInfo.dataDir, "lib" + File.separator + libName);
        } else {
            return null;
        }
    }

    public static String getLibraryPath(Context context, String libraryName) {
        String libraryPath = "";

        PackageManager packageManager = context.getPackageManager();

        PackageInfo packageInfo = null;

        if (packageManager != null) {
            try {
                packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SHARED_LIBRARY_FILES);
            } catch (Exception e) {
                Log.e(THIS_FILE, "getLibraryPath(), " + e.toString());
            }
        }

        File libFile = getLibFileFromPackage(packageInfo.applicationInfo, libraryName, true);

        if (libFile != null) {
            libraryPath = libFile.getAbsolutePath();
        }

        return libraryPath;
    }


//    public static boolean isDebuggableApp(Context ctx) {
//        try {
//            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
//            return ((pinfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
//        } catch (NameNotFoundException e) {
//            // Should not happen....or something is wrong with android...
//            Log.e(THIS_FILE, "Not possible to find self name", e);
//        }
//        return false;
//    }


    /**
     * Return the complete path to stack lib if detectable (2.3 and upper)
     * Return the short name of the library else (2.2 and lower)
     * @param ctx Context
     * @return String library name to load through System.load();
     */
    /*
	public static String getStackLib(Context ctx) {
		
		File f = NativeLibManager.getStackLibFile(ctx);
		if(f != null) {
			return f.getAbsolutePath();
		}
		
		return STACK_FILE_NAME;
	}
	 */
}
