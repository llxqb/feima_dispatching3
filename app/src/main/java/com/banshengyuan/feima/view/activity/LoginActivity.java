package com.banshengyuan.feima.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.banshengyuan.feima.DaggerApplication;
import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerLoginActivityComponent;
import com.banshengyuan.feima.dagger.module.LoginActivityModule;
import com.banshengyuan.feima.entity.BroConstant;
import com.banshengyuan.feima.entity.LoginResponse;
import com.banshengyuan.feima.entity.PersonInfoResponse;
import com.banshengyuan.feima.entity.SpConstant;
import com.banshengyuan.feima.help.DialogFactory;
import com.banshengyuan.feima.listener.MyTextWatchListener;
import com.banshengyuan.feima.utils.AppDeviceUtil;
import com.banshengyuan.feima.utils.ValueUtil;
import com.banshengyuan.feima.view.PresenterControl.LoginControl;
import com.banshengyuan.feima.view.fragment.CommonDialog;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by helei on 2017/4/26.
 * 登录页面
 */

public class LoginActivity extends BaseActivity implements LoginControl.LoginView, CommonDialog.CommonDialogListener {
    private static final int DIALOG_TYPE_ORDER_INVALID = 1;

    @BindView(R.id.login_userName)
    TextInputLayout mLoginUserName;
    @BindView(R.id.login_password)
    TextInputLayout mLoginPassword;
    @BindView(R.id.login_submit)
    Button mLoginSubmit;
    @BindView(R.id.login_sign)
    TextView mLoginSign;
    @BindView(R.id.login_forget_password)
    TextView mLoginForgetPassword;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private static String clearActivity = null;
    @BindView(R.id.middle_name)
    TextView mMiddleName;

    public static Intent getLoginIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    public static void setClearActivity(String clear) {
        clearActivity = clear;
    }

    @Inject
    LoginControl.PresenterLogin mPresenterLogin;
    private String myPhone;
    private String mPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeInjector();
        ButterKnife.bind(this);
        supportActionBar(mToolbar, true);
        mMiddleName.setText(R.string.app_login);
        initView();
    }

    @Override
    public void showLoading(String msg) {
        showDialogLoading(msg);
    }

    @Override
    public void dismissLoading() {
        dismissDialogLoading();
    }

    @Override
    public void showToast(String message) {
        showBaseToast(message);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenterLogin.onDestroy();
    }


    @Override
    public void loginSuccess(LoginResponse response) {
        showToast("登录成功");
        if (clearActivity != null) {
            clearActivity();
        }
        mBuProcessor.setLoginUser(myPhone, response.token);
        mLocationInfo = ((DaggerApplication) getApplicationContext()).getMapLocation();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BroConstant.UPDATE_PERSON_INFO));
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (clearActivity != null) {
            clearActivity();
        } else {
            super.onBackPressed();
        }
    }

    private void clearActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//表示 不创建新的实例activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//表示 移除该activity上面的activity
        startActivity(intent);
    }

    @Override
    public void getPersonInfoSuccess(PersonInfoResponse response) {
        startActivity(MainActivity.getMainIntent(this));
        finish();
    }

    @Override
    public void commonDialogBtnOkListener(int type, int position) {
        switchSetting();
    }

    private void initView() {
        RxView.clicks(mLoginSubmit).throttleFirst(2, TimeUnit.SECONDS).subscribe(v -> requestLogin());
        RxView.clicks(mLoginSign).throttleFirst(2, TimeUnit.SECONDS).subscribe(v -> switchSignActivity());
        RxView.clicks(mLoginForgetPassword).throttleFirst(2, TimeUnit.SECONDS).subscribe(v -> switchForgetActivity());
        EditText editText = mLoginUserName.getEditText();
        if (editText != null)
            editText.addTextChangedListener(new MyTextWatchListener() {
                @Override
                public void onMyTextChanged(CharSequence s, int start, int before, int count) {
                    String phone = s.toString();
                    if (ValueUtil.isMobilePhone(phone)) {
                        mLoginSubmit.setEnabled(false);
                    } else {
                        myPhone = phone;
                        mLoginSubmit.setEnabled(true);
//                        mLoginSubmit.setAlpha(0.8f);
                    }

                }
            });
        String mUserName = mSharePreferenceUtil.getStringValue(SpConstant.USER_NAME);
        if (editText != null && !TextUtils.isEmpty(mUserName)) {
            editText.setText(mUserName);
            editText.setSelection(mUserName.length());
        }
    }

    private void switchSignActivity() {
        startActivity(SignActivity.getSignIntent(this));
    }

    private void switchForgetActivity() {
        startActivity(ForgetActivity.getForgetIntent(this));
    }

    private void requestLogin() {
        EditText editText = mLoginPassword.getEditText();
        if (editText != null) {
            mPassword = editText.getText().toString();
        }

        if (TextUtils.isEmpty(mPassword)) {
            showToast(getString(R.string.login_password_empty));
            return;
        }

        mRxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE).subscribe(permission -> {
            if (permission) {
                if (mLocationInfo == null) {
                    mAMapLocationClient.setLocationOption(mAMapLocationClientOption);
                    mAMapLocationClient.startLocation();
                }
                mPresenterLogin.onRequestLogin(myPhone, mPassword);
            } else {
                showDialog();
            }
        });

    }

    private void showDialog() {
        CommonDialog commonDialog = CommonDialog.newInstance();
        commonDialog.setContent(getString(R.string.login_check_permission));
        commonDialog.setDialogCancleBtnDismiss();
        commonDialog.setListener(this, DIALOG_TYPE_ORDER_INVALID);
        DialogFactory.showDialogFragment(getSupportFragmentManager(), commonDialog, CommonDialog.TAG);
    }

    private void switchSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", AppDeviceUtil.getPackageName(this), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void initializeInjector() {
        DaggerLoginActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .loginActivityModule(new LoginActivityModule(LoginActivity.this, this))
                .build().inject(this);
    }


}
