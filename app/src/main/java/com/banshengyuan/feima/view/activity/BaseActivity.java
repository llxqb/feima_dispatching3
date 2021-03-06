package com.banshengyuan.feima.view.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.banshengyuan.feima.DaggerApplication;
import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.ApplicationComponent;
import com.banshengyuan.feima.dagger.component.BaseActivityComponent;
import com.banshengyuan.feima.dagger.component.DaggerBaseActivityComponent;
import com.banshengyuan.feima.dagger.module.BaseActivityModule;
import com.banshengyuan.feima.entity.BuProcessor;
import com.banshengyuan.feima.gen.DaoSession;
import com.banshengyuan.feima.help.DialogFactory;
import com.banshengyuan.feima.help.GlideHelper.ImageLoaderHelper;
import com.banshengyuan.feima.utils.SharePreferenceUtil;
import com.banshengyuan.feima.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.net.ConnectException;

import javax.inject.Inject;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Created by helei on 2017/4/26.
 * BaseActivity
 */

public class BaseActivity extends AppCompatActivity implements Handler.Callback {
    @Inject
    protected SharePreferenceUtil mSharePreferenceUtil;
    @Inject
    protected DaoSession mDaoSession;
    @Inject
    protected BuProcessor mBuProcessor;
    @Inject
    protected AMapLocationClient mAMapLocationClient;
    @Inject
    protected AMapLocationClientOption mAMapLocationClientOption;
    @Inject
    protected ImageLoaderHelper mImageLoaderHelper;

    protected RxPermissions mRxPermissions;
    private Dialog mProgressDialog;
    private CompositeDisposable mDisposable;
    public AMapLocation mLocationInfo;
    protected final IntentFilter mFilter = new IntentFilter();
    protected Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivityComponent component = DaggerBaseActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .baseActivityModule(new BaseActivityModule(BaseActivity.this))
                .build();
        mRxPermissions = component.rxPermissions();
        component.inject(this);
        mHandler = new Handler(this);
        addFilter();
        mLocationInfo = ((DaggerApplication) getApplicationContext()).getMapLocation();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);
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

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.clear();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
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

    ApplicationComponent getApplicationComponent() {
        return ((DaggerApplication) getApplication()).getApplicationComponent();
    }

    public void addSubscription(Disposable subscription) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(subscription);
    }

    void supportActionBar(Toolbar toolbar, boolean isShowIcon) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        if (isShowIcon) {
            toolbar.setNavigationIcon(R.drawable.vector_arrow_left);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    void showDialogLoading(String msg) {
        dismissDialogLoading();
        mProgressDialog = DialogFactory.showLoadingDialog(this, msg);
        mProgressDialog.show();
    }

    void dismissDialogLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    public void clearSwitchToLogin() {
        mBuProcessor.clearLoginUser();
        switchToLogin();
    }

    protected void switchToLogin() {
        startActivity(LoginActivity.getLoginIntent(this));
    }

    public <T> ObservableTransformer<T, T> applySchedulers() {
        return (observable) -> (
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    public void judgeToken(Integer code) {
        if (code == 100401 || code == 100107) {
            showBaseToast("登入过期,请重新登入");
            clearSwitchToLogin();
            return;
        }
    }

    public Intent getSingTaskMainIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    protected void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
