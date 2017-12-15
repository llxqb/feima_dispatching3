package com.banshengyuan.feima.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.banshengyuan.feima.DaggerApplication;
import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerCouponFragmentComponent;
import com.banshengyuan.feima.dagger.module.CoupleActivityModule;
import com.banshengyuan.feima.dagger.module.CouponFragmentModule;
import com.banshengyuan.feima.entity.Constant;
import com.banshengyuan.feima.entity.MyCoupleResponse;
import com.banshengyuan.feima.view.PresenterControl.CouponAvailableControl;
import com.banshengyuan.feima.view.activity.CoupleActivity;
import com.banshengyuan.feima.view.adapter.CouponAdapter;
import com.example.mylibrary.adapter.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by helei on 2017/5/3.
 * SendingOrderFragment
 * 未使用
 */

public class CouponAvailableFragment extends BaseFragment implements CouponAvailableControl.CouponAvailableView, BaseQuickAdapter.RequestLoadMoreListener {

    public static CouponAvailableFragment newInstance() {
        return new CouponAvailableFragment();
    }

    @BindView(R.id.coupon_common_list)
    RecyclerView mCouponCommonList;

    private CouponAdapter mAdapter;
    private Unbinder unbind;
    private List<MyCoupleResponse.ListBean> mList;
    private String state = "1";//券状态 1未使用 2已使用 3已过期
    private int page = 1;
    private int pageSize = 10;
    private String token;

    @Inject
    CouponAvailableControl.PresenterCouponAvailable mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_common, container, false);
        unbind = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
//        token = mBuProcessor.getUserToken();
        token = Constant.TOKEN;
        mPresenter.requestNoUseCouponList(state, page, pageSize, token);

    }

    private void initView() {
        mList = new ArrayList<>();
        mCouponCommonList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new CouponAdapter(null, getActivity());
        mCouponCommonList.setAdapter(mAdapter);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbind.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    private void initialize() {
        DaggerCouponFragmentComponent.builder()
                .applicationComponent(((DaggerApplication) getActivity().getApplication()).getApplicationComponent())
                .coupleActivityModule(new CoupleActivityModule((AppCompatActivity) getActivity()))
                .couponFragmentModule(new CouponFragmentModule(this, (CoupleActivity) getActivity()))
                .build()
                .inject(this);
    }

    @Override
    public void getNoUseCouponListSuccess(MyCoupleResponse myCoupleResponse) {
        if (myCoupleResponse != null) {
            mList = myCoupleResponse.getList();
            mAdapter.setNewData(mList);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        if (mList.size() < pageSize) {
            mAdapter.loadMoreEnd(true);
        } else {
            mPresenter.requestNoUseCouponList(state, ++page, pageSize, token);
        }
    }
}
