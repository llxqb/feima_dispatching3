package com.banshengyuan.feima.view.PresenterImpl;

import android.content.Context;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.ShopDetailBannerResponse;
import com.banshengyuan.feima.entity.ShopListResponse;
import com.banshengyuan.feima.view.PresenterControl.ShopListControl;
import com.banshengyuan.feima.view.model.ResponseData;
import com.banshengyuan.feima.view.model.ShopListModel;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by lei.he on 2017/6/26.
 * PresenterShopListImpl
 */

public class PresenterShopListImpl implements ShopListControl.PresenterShopList {
    private Context mContext;
    private ShopListControl.ShopListView mView;
    private ShopListModel mModel;

    @Inject
    public PresenterShopListImpl(Context context, ShopListControl.ShopListView view, ShopListModel model) {
        mContext = context;
        mView = view;
        mModel = model;
    }

    @Override
    public void requestShopListBanner(String partnerId) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.shopListBannerRequest(partnerId).compose(mView.applySchedulers())
                .subscribe(this::shopListBannerRequestSuccess, throwable -> mView.loadFail(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void shopListBannerRequestSuccess(ResponseData responseData) {
        if (responseData.resultCode == 100) {
            responseData.parseData(ShopDetailBannerResponse.class);
            ShopDetailBannerResponse response = (ShopDetailBannerResponse) responseData.parsedData;
            mView.getShopListBannerSuccess(response);
        } else {
            mView.showToast(responseData.errorDesc);
        }
    }

    @Override
    public void requestShopList(Integer pagerNo, Integer pagerSize) {
        Disposable disposable = mModel.shopListRequest(pagerNo, pagerSize).compose(mView.applySchedulers())
                .subscribe(this::shopListRequestSuccess, throwable -> mView.loadFail(throwable));
        mView.addSubscription(disposable);
    }


    private void shopListRequestSuccess(ResponseData responseData) {
        if (responseData.resultCode == 0) {
            responseData.parseData(ShopListResponse.class);
            ShopListResponse response = (ShopListResponse) responseData.parsedData;
            mView.getShopListSuccess(response.list);
        } else {
            mView.showToast(responseData.errorDesc);
        }
    }

    @Override
    public void onCreate() {

    }


    @Override
    public void onDestroy() {
        mView = null;
    }
}