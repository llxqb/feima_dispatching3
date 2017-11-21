package com.banshengyuan.feima.view.PresenterImpl;

import android.content.Context;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.RecommendBrandResponse;
import com.banshengyuan.feima.view.PresenterControl.FairControl;
import com.banshengyuan.feima.view.model.MainModel;
import com.banshengyuan.feima.view.model.ResponseData;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by helei on 2017/5/3.
 * PresenterPendingImpl
 */

public class PresenterFairImpl implements FairControl.PresenterFair {
    private final MainModel mMainModel;
    private FairControl.FairView mView;
    private final Context mContext;

    @Inject
    public PresenterFairImpl(Context context, MainModel model, FairControl.FairView view) {
        mContext = context;
        mMainModel = model;
        mView = view;
    }

    @Override
    public void requestFairBrand() {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mMainModel.requestRecommendBrand().compose(mView.applySchedulers())
                .subscribe(this::getFairBrandSuccess
                        , throwable -> mView.showErrMessage(throwable), () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getFairBrandSuccess(ResponseData responseData) {
        if (responseData.resultCode == 200) {
            responseData.parseData(RecommendBrandResponse.class);
            RecommendBrandResponse response = (RecommendBrandResponse) responseData.parsedData;
            mView.getFairBrandSuccess(response);
        } else {
            mView.getFairBrandFail();
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
