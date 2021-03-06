package com.banshengyuan.feima.view.PresenterImpl;

import android.content.Context;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.BlockDetailFairListResponse;
import com.banshengyuan.feima.entity.BlockDetailProductListResponse;
import com.banshengyuan.feima.entity.BlockDetailResponse;
import com.banshengyuan.feima.entity.BlockStoreListResponse;
import com.banshengyuan.feima.entity.FairUnderLineResponse;
import com.banshengyuan.feima.view.PresenterControl.UnderLineFairControl;
import com.banshengyuan.feima.view.model.ResponseData;
import com.banshengyuan.feima.view.model.UnderLineFairModel;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by lei.he on 2017/6/26.
 * PresenterAddAddressImpl
 */

public class PresenterUnderLineFairImpl implements UnderLineFairControl.PresenterUnderLineFair {
    private UnderLineFairControl.UnderLineFairView mView;
    private Context mContext;
    private UnderLineFairModel mModel;

    @Inject
    public PresenterUnderLineFairImpl(Context context, UnderLineFairControl.UnderLineFairView view, UnderLineFairModel model) {
        mContext = context;
        mView = view;
        mModel = model;
    }

    @Override
    public void requestFairUnderLine(double longitude, double latitude, Integer page, Integer pageSize) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.vistaListRequest(longitude, latitude, page, pageSize).compose(mView.applySchedulers())
                .subscribe(this::getFairUnderLineSuccess
                        , throwable -> mView.loadError(throwable), () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getFairUnderLineSuccess(ResponseData responseData) {
        if (responseData.resultCode == 200) {
            responseData.parseData(FairUnderLineResponse.class);
            FairUnderLineResponse response = (FairUnderLineResponse) responseData.parsedData;
            mView.getFairUnderLineSuccess(response);
        } else {
            mView.getFairUnderLineFail();
        }
    }

    @Override
    public void requestBlockProductList(Integer blockId, Integer page, Integer pageSize) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.productListRequest(blockId, page, pageSize).compose(mView.applySchedulers())
                .subscribe(this::getProductListSuccess, throwable -> mView.loadError(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getProductListSuccess(ResponseData responseData) {
        if (responseData.resultCode == 200) {
            responseData.parseData(BlockDetailProductListResponse.class);
            BlockDetailProductListResponse response = (BlockDetailProductListResponse) responseData.parsedData;
            mView.getProductListSuccess(response);
        } else {
            mView.getProductListFail(responseData.errorDesc);
        }
    }

    @Override
    public void requestBlockStoreList(Integer blockId, Integer page, Integer pageSize) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.storeListRequest(blockId, page, pageSize).compose(mView.applySchedulers())
                .subscribe(this::getStoreListSuccess, throwable -> mView.loadError(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getStoreListSuccess(ResponseData responseData) {
        if (responseData.resultCode == 200) {
            responseData.parseData(BlockStoreListResponse.class);
            BlockStoreListResponse response = (BlockStoreListResponse) responseData.parsedData;
            mView.getStoreListSuccess(response);
        } else {
            mView.getStoreListFail();
        }
    }

    @Override
    public void requestBlockFairList(Integer blockId, Integer page, Integer pageSize) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.blockFairListRequest(blockId, page, pageSize).compose(mView.applySchedulers())
                .subscribe(this::getBlockFairListSuccess, throwable -> mView.loadError(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getBlockFairListSuccess(ResponseData responseData) {
        if (responseData.resultCode == 200) {
            responseData.parseData(BlockDetailFairListResponse.class);
            BlockDetailFairListResponse response = (BlockDetailFairListResponse) responseData.parsedData;
            mView.getBlockFairListSuccess(response);
        } else {
            mView.getBlockFairListFail(responseData.errorDesc);
        }
    }

    @Override
    public void requestBlockDetail(Integer blockId) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.blockDetailRequest(blockId).compose(mView.applySchedulers())
                .subscribe(this::getBlockDetailSuccess, throwable -> mView.showErrMessage(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);

    }

    private void getBlockDetailSuccess(ResponseData responseData) {
        if (responseData.resultCode == 200) {
            responseData.parseData(BlockDetailResponse.class);
            BlockDetailResponse response = (BlockDetailResponse) responseData.parsedData;
            mView.getBlockDetailSuccess(response);
        } else {
            mView.getBlockDetailFail(responseData.errorDesc);
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
