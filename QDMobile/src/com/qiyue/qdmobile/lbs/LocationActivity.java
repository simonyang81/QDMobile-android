package com.qiyue.qdmobile.lbs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.github.snowdream.android.util.Log;
import com.qiyue.qdmobile.BasActivity;
import com.qiyue.qdmobile.QDMobileApplication;
import com.qiyue.qdmobile.R;
import com.qiyue.qdmobile.utils.Constants;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Simon on 7/1/15.
 */
public class LocationActivity extends BasActivity {

    private static final String TAG = LocationActivity.class.getSimpleName();

    private LocationClient mLocClient;

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private boolean isFirstLoc = true;  // 是否首次定位

    private BitmapDescriptor mMarkerRes
            = BitmapDescriptorFactory.fromResource(R.drawable.icon_location_marker);
    private BitmapDescriptor mRemoteMarkerRes
            = BitmapDescriptorFactory.fromResource(R.drawable.icon_remote_location_marker);

    private Observable<LatLng> mObservable;

//    private LatLng[] mArrays
//        = {new LatLng(24.447405, 118.152257), new LatLng(24.503183, 118.155131), new LatLng(24.541582, 118.143633)};
//    private int mCount = 0;

    private MyLocationData mLocalData;

    private Action1<LatLng> mRemoteSubscriber = latLng -> {

        try {

            if (mBaiduMap != null) {

                if (mLocalData != null) {
                    Log.d(Constants.LOCATION_TAG, "LocationActivity local location -> ("
                            + mLocalData.latitude + "," + mLocalData.longitude + ")");
                }

                if (latLng != null) {
                    Log.d(Constants.LOCATION_TAG, "LocationActivity remote location -> ("
                            + latLng.latitude + "," + latLng.longitude + ")");
                }

                if (isFirstLoc && mLocalData != null) {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(mLocalData.latitude, mLocalData.longitude);
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                    mBaiduMap.animateMapStatus(u);
                }

                mBaiduMap.clear();

                if (mLocalData != null) {
                    LatLng ll1 = new LatLng(mLocalData.latitude, mLocalData.longitude);
                    mBaiduMap.addOverlay(new MarkerOptions().icon(mMarkerRes).position(ll1));
                }
                if (latLng != null) {
                    LatLng ll2 = new LatLng(latLng.latitude, latLng.longitude);
                    mBaiduMap.addOverlay(new MarkerOptions().icon(mRemoteMarkerRes).position(ll2));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    };

    private Action1<MyLocationData> mLocalSubscriber = myLocationData -> {
        mLocalData = myLocationData;
        if (mObservable != null) {
            mObservable.subscribe(mRemoteSubscriber);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "--->> onCreate() <<---");

        setContentView(R.layout.fragment_location);
        initBarTintManager();

        Intent i = getIntent();
        if (i != null) {
            String sipAccount = i.getStringExtra(Constants.EXTRA_SIP_ACCOUNT);

            Log.d(Constants.LOCATION_TAG, "LocationActivity sipAccount: " + sipAccount);

            if (mObservable == null) {
                createLocationObservable(sipAccount);
            }
        }

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mLocClient = new LocationClient(this);

        ((QDMobileApplication) getApplication()).registerLocationSubscriber(mLocalSubscriber);
    }

    private void createLocationObservable(final String sipAccount) {

        mObservable = Observable.create(subscriber -> {

            RestAdapter restAdapter = ((QDMobileApplication) getApplication()).mRestAdapter;
            if (restAdapter != null) {
                restAdapter.create(LBSCloudService.class).listPOI(Constants.LBS_SERVER_AK,
                        Constants.LBS_DQMobile_GEO_TABLE_ID, sipAccount, new Callback<LBSListPO>() {
                            @Override
                            public void success(LBSListPO lbsListPO, Response response) {

                                Log.d(Constants.LOCATION_TAG, "llistPOI() -> success, bsListPO: " + lbsListPO.toString());

                                scan : {
                                    if (lbsListPO == null || lbsListPO.status != 0) {
                                        subscriber.onNext(null);
                                        break scan;
                                    }

                                    if (lbsListPO.pois == null || lbsListPO.pois.isEmpty()) {
                                        subscriber.onNext(null);
                                        break scan;
                                    }

                                    LBSPoisPO poisPO = lbsListPO.pois.get(0);
                                    if (poisPO == null || poisPO.location == null || poisPO.location.length != 2) {
                                        subscriber.onNext(null);
                                        break scan;
                                    }

                                    LatLng ll = new LatLng(poisPO.location[1], poisPO.location[0]);
                                    subscriber.onNext(ll);
                                }

                                subscriber.onCompleted();

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e(Constants.LOCATION_TAG, "llistPOI() -> failure, " + error.toString());
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                            }
                        });
            } else {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }

        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();

        ((QDMobileApplication) getApplication()).registerLocationSubscriber(null);

        mMapView = null;
        mObservable = null;
    }


}
