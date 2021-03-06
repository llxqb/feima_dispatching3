package com.banshengyuan.feima.view.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.amap.api.location.AMapLocation;
import com.banshengyuan.feima.DaggerApplication;
import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.BuProcessor;
import com.banshengyuan.feima.help.DialogFactory;
import com.banshengyuan.feima.help.GlideHelper.ImageLoaderHelper;
import com.banshengyuan.feima.utils.SharePreferenceUtil;
import com.banshengyuan.feima.utils.ToastUtils;
import com.banshengyuan.feima.view.activity.LoginActivity;

import java.net.ConnectException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Created by helei on 2017/5/3.
 * BaseFragment
 */

public class BaseFragment extends Fragment {
    private CompositeDisposable mDisposable;
    private Dialog mProgressDialog;
    final IntentFilter mFilter = new IntentFilter();

    @Inject
    BuProcessor mBuProcessor;
    @Inject
    SharePreferenceUtil mSharePreferenceUtil;
    @Inject
    ImageLoaderHelper mImageLoaderHelper;

    public AMapLocation mLocationInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DaggerApplication) getActivity().getApplication()).getApplicationComponent().inject(this);
        setRetainInstance(true);
        addFilter();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, mFilter);
        mLocationInfo = ((DaggerApplication) getActivity().getApplicationContext()).getMapLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.clear();
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onReceivePro(context, intent);
        }
    };

    void onReceivePro(Context context, Intent intent) {
    }

    void addFilter() {
    }

    public <T> ObservableTransformer<T, T> applySchedulers() {
        return (observable) -> (
                ((Observable) observable).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    public void addSubscription(Disposable subscription) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(subscription);
    }

    public void judgeToken(Integer code) {
        if (code == 100401 || code == 100107) {
            showBaseToast("登入过期,请重新登入");
            clearSwitchToLogin();
            return;
        }
    }

    public void clearSwitchToLogin() {
        mBuProcessor.clearLoginUser();
        startActivity(LoginActivity.getLoginIntent(getActivity()));
    }

    void showDialogLoading(String msg) {
        dismissDialogLoading();
        mProgressDialog = DialogFactory.showLoadingDialog(getActivity(), msg);
        mProgressDialog.show();
    }

    void dismissDialogLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    void showBaseToast(String message) {
        ToastUtils.showShortToast(message);
    }

    public void showErrMessage(Throwable e) {
        dismissDialogLoading();
        String mErrMessage;
        if (e instanceof HttpException || e instanceof ConnectException) {
            mErrMessage = getString(R.string.text_check_internet);
        } else {
            mErrMessage = getString(R.string.text_wait_try);
        }
        showBaseToast(mErrMessage);
    }

    protected void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
