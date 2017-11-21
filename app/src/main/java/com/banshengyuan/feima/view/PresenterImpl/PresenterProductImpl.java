package com.banshengyuan.feima.view.PresenterImpl;

import android.content.Context;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.AllProductSortResponse;
import com.banshengyuan.feima.view.PresenterControl.ProductControl;
import com.banshengyuan.feima.view.model.MainModel;
import com.banshengyuan.feima.view.model.ResponseData;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by helei on 2017/5/3.
 * PresenterPendingImpl
 */

public class PresenterProductImpl implements ProductControl.PresenterProduct {
    private final MainModel mMainModel;
    private ProductControl.ProductView mView;
    private final Context mContext;

    @Inject
    public PresenterProductImpl(Context context, MainModel model, ProductControl.ProductView view) {
        mContext = context;
        mMainModel = model;
        mView = view;
    }

    @Override
    public void requestAllProductSort() {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mMainModel.allProductSortRequest().compose(mView.applySchedulers())
                .subscribe(this::getAllProductSortSuccess
                        , throwable -> mView.showErrMessage(throwable), () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getAllProductSortSuccess(ResponseData responseData) {
        if (responseData.resultCode == 200) {
            responseData.parseData(AllProductSortResponse.class);
            AllProductSortResponse response = (AllProductSortResponse) responseData.parsedData;
            mView.getAllProductSortSuccess(response);
        } else {
            mView.getAllProductSortFail();
        }
    }

    /* @Override
        public void requestShopId(String scoreCode, Integer type) {
            mView.showLoading(mContext.getString(R.string.loading));
            Disposable disposable = mShopListModel.shopIdRequest(scoreCode, type).compose(mView.applySchedulers())
                    .subscribe(this::getShopSuccess
                            , throwable -> mView.showErrMessage(throwable), () -> mView.dismissLoading());
            mView.addSubscription(disposable);
        }

        private void getShopSuccess(ResponseData responseData) {
            if (responseData.resultCode == 0) {
                responseData.parseData(ShopResponse.class);
                ShopResponse response = (ShopResponse) responseData.parsedData;
                mView.getShopSuccess(response);
            } else {
                mView.showToast(responseData.errorDesc);
            }
        }
    */
    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }
}
