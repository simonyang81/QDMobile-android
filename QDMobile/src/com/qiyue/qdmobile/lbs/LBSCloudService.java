package com.qiyue.qdmobile.lbs;

import com.qiyue.qdmobile.utils.Constants;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.mime.TypedString;

/**
 * Created by Simon on 6/23/15.
 */
public interface LBSCloudService {

    @Multipart
    @POST(Constants.LBS_CLOUD_CREATE_GEOTABLE_API_URL)
    void createGeoTable(@Part("name") TypedString name,
                        @Part("geotype") TypedString geotype,
                        @Part("is_published") TypedString is_published,
                        @Part("ak") TypedString ak,
                        Callback<LBSBasPO> cb);


    @Multipart
    @POST(Constants.LBS_CLOUD_CREATE_COLUMN_API_URL)
    void createColumn(@PartMap Map<String, Object> params, Callback<LBSBasPO> cb);


    @Multipart
    @POST(Constants.LBS_CLOUD_CREATE_POI_API_URL)
    void createPOI(@PartMap Map<String, Object> params, Callback<LBSBasPO> cb);


    @Multipart
    @POST(Constants.LBS_CLOUD_DELETE_POI_API_URL)
    void deletePOI(@PartMap Map<String, Object> params, Callback<LBSBasPO> cb);


}
