package com.banshengyuan.feima.view.model;

import com.banshengyuan.feima.network.networkapi.UnderLineFairApi;
import com.google.gson.Gson;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by helei on 2017/4/28.
 * LoginModel
 */

public class UnderLineFairModel {
    private final UnderLineFairApi mApi;
    private final Gson mGson;
    private final ModelTransform mTransform;

    @Inject
    public UnderLineFairModel(UnderLineFairApi api, Gson gson, ModelTransform transform) {
        mApi = api;
        mGson = gson;
        mTransform = transform;
    }


    public Observable<ResponseData> blockDetailRequest(Integer blockId) {
        return mApi.blockDetailRequest(blockId).map(mTransform::transformCommon);
    }

    public Observable<ResponseData> blockFairListRequest(Integer blockId, Integer page, Integer pageSize) {
        return mApi.blockFairListRequest(blockId + "", page, pageSize).map(mTransform::transformCommon);
    }

    public Observable<ResponseData> storeListRequest(Integer blockId, Integer page, Integer pageSize) {
        return mApi.storeListRequest(blockId, page, pageSize).map(mTransform::transformCommon);
    }

    public Observable<ResponseData> productListRequest(Integer blockId, Integer page, Integer pageSize) {
        return mApi.productListRequest(blockId, page, pageSize).map(mTransform::transformCommon);
    }

    public Observable<ResponseData> vistaListRequest(double longitude, double latitude, Integer page, Integer pageSize) {
        return mApi.vistaListRequest(longitude + "", latitude + "", page, pageSize).map(mTransform::transformCommon);
    }


}
