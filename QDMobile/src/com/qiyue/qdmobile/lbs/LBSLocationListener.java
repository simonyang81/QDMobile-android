package com.qiyue.qdmobile.lbs;

import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MyLocationData;
import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.QDMobileApplication;
import com.qiyue.qdmobile.utils.AccountUtils;
import com.qiyue.qdmobile.utils.Constants;
import com.qiyue.qdmobile.utils.DateUtils;

import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Simon on 6/23/15.
 */
public class LBSLocationListener implements BDLocationListener {

    private static final String TAG = LBSLocationListener.class.getSimpleName();

    private QDMobileApplication mApplication;
    public void setApplication(QDMobileApplication application) {
        mApplication = application;
    }

    private RestAdapter mRestAdapter;
    public void setRestAdapter(RestAdapter restAdapter) {
        this.mRestAdapter = restAdapter;
    }

    private MyLocationData myLocationData;

    private Observable<MyLocationData> observable = Observable.create(subscriber -> {

        Log.d(Constants.LOCATION_TAG, "LBSLocationListener myLocationData -> ("
                + myLocationData.latitude + "," + myLocationData.longitude + ")");

        subscriber.onNext(myLocationData);
        subscriber.onCompleted();

    });


    @Override
    public void onReceiveLocation(BDLocation location) {

//        StringBuffer sb = new StringBuffer(256);
//        sb.append("time : ");
//        sb.append(location.getTime());
//        sb.append("\n");
////        sb.append("\nerror code : ");
////        sb.append(location.getLocType());
////        sb.append("\nlatitude : ");
//        sb.append(location.getLatitude());
//        sb.append(",");
//        sb.append(location.getLongitude());
////        sb.append("\nradius : ");
////        sb.append(location.getRadius());
//        if (location.getLocType() == BDLocation.TypeGpsLocation){
//            sb.append("\nspeed : ");
//            sb.append(location.getSpeed());
////            sb.append("\nsatellite : ");
////            sb.append(location.getSatelliteNumber());
////            sb.append("\ndirection : ");
//            sb.append("\naddr : ");
//            sb.append(location.getAddrStr());
////            sb.append(location.getDirection());
//        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//            sb.append("\naddr : ");
//            sb.append(location.getAddrStr());
//
////            sb.append("\noperationers : ");
////            sb.append(location.getOperators());
//        }
//        Log.i(TAG, sb.toString());

        if (location != null) {
            myLocationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
        } else {
            myLocationData = null;
        }

        Action1<MyLocationData> subscriber = mApplication.mSubscriber;
        Log.d(Constants.LOCATION_TAG, "subscriber: " + subscriber);

        if (observable != null && subscriber != null) {
            Log.d(Constants.LOCATION_TAG, "subscriber --->>> ");
            observable.subscribe(subscriber);
        }

        if (location != null) {
            String lbsAccountID = AccountUtils.getLBSAccountID();
            if (TextUtils.isEmpty(lbsAccountID) == false) {
                createPOI(location, lbsAccountID);
            }
        }

    }

    private HashMap<String, Object> params = new HashMap<>();

    private void createPOI(BDLocation location, String sipAccount) {
        params.put("title",         "Address_" + sipAccount);
        params.put("latitude",      location.getLatitude());
        params.put("longitude",     location.getLongitude());
        params.put("address",       location.getAddrStr());
        params.put("coord_type",    1);
        params.put("geotable_id",   Constants.LBS_DQMobile_GEO_TABLE_ID);
        params.put("ak",            Constants.LBS_SERVER_AK);
        params.put("tags",          location.getTime());
        params.put(Constants.LBS_GEO_TABLE_COLUMN_SIP_ACCOUNT, sipAccount);

        Calendar cal = Calendar.getInstance();
        String currTime = DateUtils.getDateLabel(cal.getTime(), Constants.LBS_DATE_FORMAT);
        params.put(Constants.LBS_GEO_TABLE_COLUMN_TIME_PERIOD, Integer.parseInt(currTime));

        mRestAdapter.create(LBSCloudService.class).createPOI(params,
                new Callback<LBSBasPO>() {
                    @Override
                    public void success(LBSBasPO lbsBasPO, Response response) {
                        Log.d(TAG, "createPOI() -> success, " + lbsBasPO.toString());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "createPOI() -> failure, " + error.getBody());
                    }
                });


    }


}
