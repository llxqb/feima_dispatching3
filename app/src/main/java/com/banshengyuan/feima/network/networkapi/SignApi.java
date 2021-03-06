package com.banshengyuan.feima.network.networkapi;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by helei on 2017/4/27.
 * LoginApi
 */

public interface SignApi {
    @GET("api/user/register-sms/{mobile}")
    Observable<String> verityCodeRequest(@Path("mobile") String phone);

    @POST("api/user/register")
    Observable<String> signUpRequest(@Body String response);

}
