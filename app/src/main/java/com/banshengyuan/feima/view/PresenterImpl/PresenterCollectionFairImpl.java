package com.banshengyuan.feima.view.PresenterImpl;

import android.content.Context;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.MyCollectionFairResponse;
import com.banshengyuan.feima.help.RetryWithDelay;
import com.banshengyuan.feima.view.PresenterControl.CollectionFairControl;
import com.banshengyuan.feima.view.model.CollectionModel;
import com.banshengyuan.feima.view.model.ResponseData;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by lei.he on 2017/6/26.
 * PresenterAddAddressImpl
 */

public class PresenterCollectionFairImpl implements CollectionFairControl.PresenterCollectionFair {
    private CollectionFairControl.CollectionFairView mView;
    private Context mContext;
    private CollectionModel mModel;
    private boolean isShow =true;

    @Inject
    public PresenterCollectionFairImpl(Context context, CollectionFairControl.CollectionFairView view, CollectionModel model) {
        mContext = context;
        mView = view;
        mModel = model;
    }


    @Override
    public void onCreate() {

    }


    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void requestCollectionFairList(int page, int pageSize,String token) {
        if (isShow) {
            isShow = false;
            mView.showLoading(mContext.getString(R.string.loading));
        }
        Disposable disposable = mModel.collectionFairRequest(page, pageSize,token).retryWhen(new RetryWithDelay(10, 3000)).compose(mView.applySchedulers())
                .subscribe(this::getCollectionFairSuccess, throwable -> mView.loadFail(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getCollectionFairSuccess(ResponseData responseData) {
        if (responseData.resultCode == 200) {
            responseData.parseData(MyCollectionFairResponse.class);
            MyCollectionFairResponse response = (MyCollectionFairResponse) responseData.parsedData;
            mView.getMyCollectionListSuccess(response);
        } else {
            mView.showToast(responseData.errorDesc);
        }
    }
}
