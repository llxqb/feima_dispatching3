package com.banshengyuan.feima.network.networkapi;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by helei on 2017/4/27.
 * LoginApi
 */

public interface GoodsDetailApi {
    @GET("Query/Product/GetDetail")
    Observable<String> goodInfoRequest(@Query("productId") String request);

    @GET("Query/Product/ListBaseInfos")
    Observable<String> goodInfoSpecificationRequest(@Query("shopId") String shopId, @Query("productId") String request);

}