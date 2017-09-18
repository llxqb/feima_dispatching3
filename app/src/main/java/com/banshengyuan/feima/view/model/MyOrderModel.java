package com.banshengyuan.feima.view.model;

import com.banshengyuan.feima.entity.BuProcessor;
import com.banshengyuan.feima.network.networkapi.MyOrderApi;
import com.google.gson.Gson;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by helei on 2017/4/28.
 * LoginModel
 */

public class MyOrderModel {
    private final MyOrderApi mApi;
    private final Gson mGson;
    private final ModelTransform mTransform;
    private BuProcessor mBuProcessor;

    @Inject
    public MyOrderModel(MyOrderApi api, Gson gson, ModelTransform transform, BuProcessor buProcessor) {
        mApi = api;
        mGson = gson;
        mTransform = transform;
        mBuProcessor = buProcessor;
    }


    public Observable<ResponseData> myOrderListRequest(Integer pageNo, Integer pageSize) {
        return mApi.orderListRequest(pageNo, pageSize, mBuProcessor.getUserId()).map(mTransform::transformTypeTwo);
    }

    public Observable<ResponseData> orderStatusListRequest(Integer status, Integer pageNo, Integer pageSize) {
        return mApi.orderStatusListRequest(mBuProcessor.getUserId(), status, pageNo, pageSize).map(mTransform::transformTypeTwo);
    }
}