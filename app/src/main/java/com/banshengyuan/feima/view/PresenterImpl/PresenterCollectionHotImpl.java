package com.banshengyuan.feima.view.PresenterImpl;

import android.content.Context;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.MyCollectionFairResponse;
import com.banshengyuan.feima.view.PresenterControl.CollectionHotControl;
import com.banshengyuan.feima.view.model.CollectionModel;
import com.banshengyuan.feima.view.model.ResponseData;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by lei.he on 2017/6/26.
 * PresenterAddAddressImpl
 */

public class PresenterCollectionHotImpl implements CollectionHotControl.PresenterCollectionHot {
    private CollectionHotControl.CollectionHotView mView;
    private Context mContext;
    private CollectionModel mModel;

    @Inject
    public PresenterCollectionHotImpl(Context context, CollectionHotControl.CollectionHotView view, CollectionModel model) {
        mContext = context;
        mView = view;
        mModel = model;
    }

    @Override
    public void requestCollectionHotList(int page, int pageSize) {
        mView.showLoading(mContext.getString(R.string.loading));
        Disposable disposable = mModel.collectionHotRequest(page, pageSize).compose(mView.applySchedulers())
                .subscribe(this::getCollectionHotSuccess, throwable -> mView.showErrMessage(throwable),
                        () -> mView.dismissLoading());
        mView.addSubscription(disposable);
    }

    private void getCollectionHotSuccess(ResponseData responseData) {
        if (responseData.resultCode == 200) {
            responseData.parseData(MyCollectionFairResponse.class);
            MyCollectionFairResponse response = (MyCollectionFairResponse) responseData.parsedData;
            mView.getMyCollectionListSuccess(response);
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
