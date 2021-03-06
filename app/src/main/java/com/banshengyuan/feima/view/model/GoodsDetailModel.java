package com.banshengyuan.feima.view.model;

import com.banshengyuan.feima.entity.AddShoppingCardRequest;
import com.banshengyuan.feima.entity.BuProcessor;
import com.banshengyuan.feima.entity.CollectionRequest;
import com.banshengyuan.feima.network.networkapi.AddShoppingCardApi;
import com.banshengyuan.feima.network.networkapi.GoodsDetailApi;
import com.google.gson.Gson;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * Created by helei on 2017/4/28.
 * LoginModel
 */

public class GoodsDetailModel {
    private final GoodsDetailApi mApi;
    private final AddShoppingCardApi mAddShoppingCardApi;
    private final Gson mGson;
    private final ModelTransform mTransform;
    private final BuProcessor mBuProcessor;

    @Inject
    public GoodsDetailModel(GoodsDetailApi api, AddShoppingCardApi addShoppingCardApi, Gson gson, ModelTransform transform, BuProcessor buProcessor) {
        mApi = api;
        mAddShoppingCardApi = addShoppingCardApi;
        mGson = gson;
        mTransform = transform;
        mBuProcessor = buProcessor;
    }


    public Observable<ResponseData> goodInfoRequest(Integer productId) {
        return mApi.goodInfoRequest(productId,mBuProcessor.getUserToken()).map(mTransform::transformCommon);
    }

    public Observable<ResponseData> goodsCollectionRequest(String shopId, String type) {
        CollectionRequest request = new CollectionRequest();
        request.id = shopId;
        request.type = type;
        request.token = mBuProcessor.getUserToken();
        return mApi.goodsCollectionRequest(mGson.toJson(request)).map(mTransform::transformCommon);
    }

    public Observable<ResponseData> goodInfoSpecificationRequest(Integer productId, String sku) {
        return mApi.goodInfoSpecificationRequest(productId + "", sku).map(mTransform::transformCommon);
    }

    public Observable<ResponseData> requestAddShoppingCard(String productId, String sku, Integer count) {
        AddShoppingCardRequest request = new AddShoppingCardRequest();
        request.goodsId = productId;
        request.goodsSku = sku;
        request.goodsNumber = count+"";
        request.token = mBuProcessor.getUserToken();
        return mAddShoppingCardApi.requestAddShoppingCard(mGson.toJson(request)).map(mTransform::transformCommon);
    }

}
