package com.banshengyuan.feima.view.PresenterImpl;

import android.content.Context;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.OrderConfirmedResponse;
import com.banshengyuan.feima.entity.PayAccessRequest;
import com.banshengyuan.feima.entity.PayCreateRequest;
import com.banshengyuan.feima.entity.PayResponse;
import com.banshengyuan.feima.view.PresenterControl.PayControl;
import com.banshengyuan.feima.view.model.PayModel;
import com.banshengyuan.feima.view.model.ResponseData;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by lei.he on 2017/6/26.
 * PresenterPayImpl
 */

public class PresenterPayImpl implements PayControl.PresenterPay {
    private PayControl.PayView mView;
    private Context mContext;
    private PayModel mModel;

    @Inject
    public PresenterPayImpl(Context context, PayControl.PayView view, PayModel model) {
        mContext = context;
        mView = view;
        mModel = model;
    }

    @Override
    public void updateOrderStatus(PayAccessRequest request) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.updateOrderStatusRequest(request).compose(mView.applySchedulers())
                .subscribe(this::updateOrderStatusSuccess, throwable -> mView.showErrMessage(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void updateOrderStatusSuccess(ResponseData responseData){
        if (responseData.resultCode == 100) {
            mView.updateOrderStatusSuccess();
        } else {
            mView.showToast(responseData.errorDesc);
        }
    }

    @Override
    public void requestPayInfo(OrderConfirmedResponse response, String payCode) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.payRequest(response,payCode).compose(mView.applySchedulers())
                .subscribe(this::getPayInfoSuccess, throwable -> mView.showErrMessage(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getPayInfoSuccess(ResponseData responseData) {
        if (responseData.resultCode == 100) {
            responseData.parseData(PayResponse.class);
            PayResponse response  = (PayResponse) responseData.parsedData;
            mView.orderPayInfoSuccess(response);
        } else {
            mView.showToast(responseData.errorDesc);
        }
    }

    @Override
    public void requestOrderConfirmed(PayCreateRequest request) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.orderConfirmedRequest(request).compose(mView.applySchedulers())
                .subscribe(this::orderConfirmedSuccess, throwable -> mView.showErrMessage(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void orderConfirmedSuccess(ResponseData responseData) {
        if (responseData.resultCode == 100) {
            responseData.parseData(OrderConfirmedResponse.class);
            OrderConfirmedResponse response  = (OrderConfirmedResponse) responseData.parsedData;
            mView.orderConfirmedSuccess(response);
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