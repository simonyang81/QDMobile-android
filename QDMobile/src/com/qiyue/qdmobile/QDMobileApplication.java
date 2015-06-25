package com.qiyue.qdmobile;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.github.snowdream.android.util.Log;
import com.github.snowdream.android.util.LogFormatter;
import com.qiyue.qdmobile.lbs.LBSBasPO;
import com.qiyue.qdmobile.lbs.LBSCloudService;
import com.qiyue.qdmobile.lbs.LBSLocationListener;
import com.qiyue.qdmobile.utils.Constants;
import com.qiyue.qdmobile.utils.CrashUtils;
import com.qiyue.qdmobile.utils.DateUtils;
import com.qiyue.qdmobile.utils.FileUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

/**
 * Created by Simon on 6/23/15.
 */
public class QDMobileApplication extends Application {

    public LocationClient mLocationClient;

    private static final String TAG = QDMobileApplication.class.getSimpleName();

    /**
     * Keeps application execution context.
     */
    protected static Context sApplicationContext = null;

    public RestAdapter mRestAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        sApplicationContext = getApplicationContext();
        String processName = getProcessName(sApplicationContext, android.os.Process.myPid());

        if (Constants.REAL_PACKAGE_NAME.equalsIgnoreCase(processName)) {

            FileUtil.deleteFilesOneMonthAgo(sApplicationContext);
            initLog();
            CrashUtils.getInstance().init();

            Log.d(TAG, ">--- Initialize Logs ---<");

            if (mRestAdapter == null) {
                mRestAdapter = new RestAdapter.Builder()
                        .setEndpoint(Constants.LBS_CLOUD_API_URL)
                        .build();
            }

            createGeoTable();
        }

        initLBSLocation();
        Log.d(TAG, "onCreate() -> processName: " + processName);

    }

    private void initLBSLocation() {

        if (mRestAdapter == null) {
            mRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.LBS_CLOUD_API_URL)
                    .build();
        }

        Log.d(TAG, ">>---- Start LBS Location Server ----<<");

        if (mLocationClient != null && mLocationClient.isStarted()) {
            Log.d(TAG, "== END ==\n The LBS location is started.");
            return;
        }

        mLocationClient = new LocationClient(this.getApplicationContext());

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(Constants.LBS_SCAN_SPAN);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        mLocationClient.setLocOption(option);
        LBSLocationListener locationListener = new LBSLocationListener();
        mLocationClient.registerLocationListener(locationListener);
        locationListener.setRestAdapter(mRestAdapter);
    }

    private void createGeoTable() {

        mRestAdapter.create(LBSCloudService.class).createGeoTable(
                new TypedString(Constants.LBS_GEO_TABLE_NAME),
                new TypedString("1"),
                new TypedString("1"),
                new TypedString(Constants.LBS_SERVER_AK),

                new Callback<LBSBasPO>() {
                    @Override
                    public void success(LBSBasPO lbsBasPO, Response response) {

                        Log.d(TAG, "createGeoTable() -> success, " + lbsBasPO.toString());
                        createGeoTableColumnSipAccount();
                        createGeoTableColumnTimePeriod();
                        deletePOI();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "createGeoTable() -> failure, " + error.getBody());
                    }
                });
    }

    private void createGeoTableColumnSipAccount() {

        HashMap<String, Object> params = new HashMap<String, Object>() {
            {

                put("name",                 Constants.LBS_GEO_TABLE_COLUMN_SIP_ACCOUNT);
                put("key",                  Constants.LBS_GEO_TABLE_COLUMN_SIP_ACCOUNT);
                put("type",                 3);
                put("max_length",           50);
                put("default_value",        "");
                put("is_sortfilter_field",  0);
                put("is_search_field",      1);
                put("is_index_field",       1);
                put("is_unique_field",      0);
                put("geotable_id",          Constants.LBS_DQMobile_GEO_TABLE_ID);
                put("ak",                   Constants.LBS_SERVER_AK);

            }
        };

        mRestAdapter.create(LBSCloudService.class).createColumn(params,
                new Callback<LBSBasPO>() {
                    @Override
                    public void success(LBSBasPO lbsBasPO, Response response) {
                        Log.d(TAG, "createGeoTableColumnSipAccount() -> success, " + lbsBasPO.toString());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "createGeoTableColumnSipAccount() -> failure, " + error.getBody());
                    }
                });

    }

    private void createGeoTableColumnTimePeriod() {

        HashMap<String, Object> params = new HashMap<String, Object>() {
            {

                put("name",                 Constants.LBS_GEO_TABLE_COLUMN_TIME_PERIOD);
                put("key",                  Constants.LBS_GEO_TABLE_COLUMN_TIME_PERIOD);
                put("type",                 1);
                put("default_value",        0);
                put("is_sortfilter_field",  0);
                put("is_search_field",      0);
                put("is_index_field",       1);
                put("is_unique_field",      0);
                put("geotable_id",          Constants.LBS_DQMobile_GEO_TABLE_ID);
                put("ak",                   Constants.LBS_SERVER_AK);

            }
        };

        mRestAdapter.create(LBSCloudService.class).createColumn(params,
                new Callback<LBSBasPO>() {
                    @Override
                    public void success(LBSBasPO lbsBasPO, Response response) {
                        Log.d(TAG, "createGeoTableColumnTimePeriod() -> success, " + lbsBasPO.toString());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "createGeoTableColumnTimePeriod() -> failure, " + error.getBody());
                    }
                });

    }

    private void deletePOI() {
        HashMap<String, Object> params = new HashMap<String, Object>() {
            {

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -7);
                String currTime = DateUtils.getDateLabel(cal.getTime(), Constants.LBS_DATE_FORMAT);

                put("ak", Constants.LBS_SERVER_AK);
                put("geotable_id", Constants.LBS_DQMobile_GEO_TABLE_ID);
                put(Constants.LBS_GEO_TABLE_COLUMN_TIME_PERIOD, "-," + currTime);

            }
        };

        Log.d(TAG, "params: " + params.toString());

        mRestAdapter.create(LBSCloudService.class).deletePOI(params,
                new Callback<LBSBasPO>() {
                    @Override
                    public void success(LBSBasPO lbsBasPO, Response response) {
                        Log.d(TAG, "deletePOI() -> success, " + lbsBasPO.toString());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "deletePOI() -> failure, " + error.getBody());
                    }
                });
    }

    private void initLog() {

        Log.setEnabled(true);
        Log.setLog2ConsoleEnabled(true);
        Log.setLog2FileEnabled(true);
        Log.setLogFormatter(new LogFormatter.IDEAFormatter("yyyy-MM-dd HH:mm:ss"));

        FileUtil.initQDMobileLogsPath(sApplicationContext);
    }

    public static Context getContextQD() {
        return sApplicationContext;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "onLowMemory");
    }


    public String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }



}
