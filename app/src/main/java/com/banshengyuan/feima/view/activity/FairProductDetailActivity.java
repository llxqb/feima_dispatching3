package com.banshengyuan.feima.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.aries.ui.view.radius.RadiusTextView;
import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerFairProductDetailActivityComponent;
import com.banshengyuan.feima.dagger.module.FairProductDetailActivityModule;
import com.banshengyuan.feima.entity.Constant;
import com.banshengyuan.feima.entity.HotFairDetailResponse;
import com.banshengyuan.feima.entity.HotFairStateResponse;
import com.banshengyuan.feima.entity.HotFariJoinActionRequest;
import com.banshengyuan.feima.entity.HotFariJoinActionResponse;
import com.banshengyuan.feima.entity.HotFariStateRequest;
import com.banshengyuan.feima.entity.IntentConstant;
import com.banshengyuan.feima.help.DialogFactory;
import com.banshengyuan.feima.view.PresenterControl.FairProductDetailControl;
import com.banshengyuan.feima.view.fragment.CommonDialog;
import com.banshengyuan.feima.view.fragment.JoinActionDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lei.he on 2017/6/28.
 * AddAddressActivity
 */

public class FairProductDetailActivity extends BaseActivity implements FairProductDetailControl.FairProductDetailView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.join)
    RadiusTextView join;

    public static Intent getIntent(Context context, String fId) {
        Intent intent = new Intent(context, FairProductDetailActivity.class);
        intent.putExtra("fId", fId);
        return intent;
    }

    @Inject
    FairProductDetailControl.PresenterFairProductDetail mPresenter;

    private String fId;
    private HotFairDetailResponse hotFairDetailResponse = null;//热闹详情
    private HotFairStateResponse hotFairStateResponse = null;
    private String token;
    private String order_sn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fairproduct_detail);
        ButterKnife.bind(this);
        initializeInjector();
       /* supportActionBar(mToolbar, true);
        mMiddleName.setText("新增收货地址");*/
        initView();
        initData();
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
        mPresenter.onDestroy();
    }

    private void initView() {
        if (getIntent() != null) {
            fId = getIntent().getStringExtra("fId");
        }
    }

    private void initData() {
//        token = mBuProcessor.getUserToken();
        token = Constant.TOKEN;
        if (mBuProcessor.isValidLogin()) {
            mPresenter.requestHotFairDetail(fId, token);//根据id查看热闹详情
        } else {
            mPresenter.requestHotFairDetail(fId, null);//根据id查看热闹详情
        }


    }

    /**
     * 报名参加
     * 查看二维码
     */
    private void join() {
        if (!mBuProcessor.isValidLogin()) {
            switchToLogin2();
            return;
        }
        if (hotFairStateResponse != null) {
            if(hotFairStateResponse.getStatus().equals("2")){//付款完成
                startActivity(ActionCodeActivity.getIntent(FairProductDetailActivity.this, hotFairDetailResponse, hotFairStateResponse.getQrcode()));
            } else {
                JoinActionDialog joinActionDialog = JoinActionDialog.newInstance();
                joinActionDialog.setData(mPresenter, fId, token, hotFairDetailResponse);

                DialogFactory.showDialogFragment(getSupportFragmentManager(), joinActionDialog, CommonDialog.TAG);
            }
        } else {
            showToast("热闹报名状态获取失败");
        }
    }

    private void initializeInjector() {
        DaggerFairProductDetailActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .fairProductDetailActivityModule(new FairProductDetailActivityModule(FairProductDetailActivity.this, this))
                .build().inject(this);
    }


    @Override
    public void getHotFairDetailSuccess(HotFairDetailResponse response) {
        if (response != null) {
            hotFairDetailResponse = response;
            if (!TextUtils.isEmpty(response.getInfo().getOrder_sn())) {
                order_sn = response.getInfo().getOrder_sn();
                if (mBuProcessor.isValidLogin()) {
                    mPresenter.requestHotFairState(fId, order_sn, token); //热闹-报名订单状态查询
                }
//                join.setText("报名参加");
//                join.setBackgroundResource(R.drawable.button_style_blue6_rightangle);
//                join.setTextColor(Color.parseColor("#212121"));
            }
        }
    }

    @Override
    public void getHotFairStateSuccess(HotFairStateResponse response) {
        if (response != null) {
            hotFairStateResponse = response;
            if (response.getStatus().equals("2")) {//付款完成
                join.setText("查看二维码");
//                join.setBackgroundColor(Color.parseColor("#212121"));
//                join.setTextColor(Color.WHITE);
            } else {
                join.setText("报名参加");
//                join.setBackgroundResource(R.drawable.button_style_blue6_rightangle);
//                join.setTextColor(Color.parseColor("#212121"));
            }
        }
    }

    @Override
    public void getHotFairJoinActionSuccess(HotFariJoinActionResponse response) {
        if (response != null) {
            showToast("报名成功");
            if (!TextUtils.isEmpty(response.getOrder_sn())) {//判断订单号是否为空
                //直接唤起支付
                mPresenter.requestHotFairState(fId, response.getOrder_sn(), token); //热闹-报名订单状态查询
            }
        }
    }

    @OnClick({R.id.join, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.join:
                join();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void switchToLogin2() {
        startActivityForResult(LoginActivity.getLoginIntent(FairProductDetailActivity.this), IntentConstant.ORDER_POSITION_ONE);
    }
}
