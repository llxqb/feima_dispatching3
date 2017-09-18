package com.banshengyuan.feima.view.PresenterImpl;

import android.content.Context;

import com.banshengyuan.feima.view.PresenterControl.MyOrderControl;
import com.banshengyuan.feima.view.model.MyOrderModel;

import javax.inject.Inject;

/**
 * Created by helei on 2017/5/3.
 * PresenterCompletedImpl
 */

public class PresenterMyOrderImpl implements MyOrderControl.PresenterMyOrder {
    private MyOrderControl.MyOrderView mView;
    private final Context mContext;
    private MyOrderModel mModel;

    @Inject
    public PresenterMyOrderImpl(Context context, MyOrderModel model, MyOrderControl.MyOrderView view) {
        mContext = context;
        mView = view;
        mModel = model;
    }

  /*  @Override
    public void requestMyOrderList(Integer pageNo, Integer pageSize) {
        Disposable disposable = mModel.myOrderListRequest(pageNo, pageSize).compose(mView.applySchedulers())
                .subscribe(this::getMyOrderListSuccess,
                        throwable -> mView.loadFail(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getMyOrderListSuccess(ResponseData responseData) {
        if(responseData.resultCode == 100){
            responseData.parseData(MyOrdersResponse.class);
            MyOrdersResponse response = (MyOrdersResponse) responseData.parsedData;
            mView.getMyOrderListSuccess(response);
        }else {
            mView.showToast(responseData.errorDesc);
        }
    }*/

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }
}