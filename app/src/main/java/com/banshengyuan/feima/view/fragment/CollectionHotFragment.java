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
import com.banshengyuan.feima.dagger.component.DaggerCollectionFragmentComponent;
import com.banshengyuan.feima.dagger.module.CollectionActivityModule;
import com.banshengyuan.feima.dagger.module.CollectionFragmentModule;
import com.banshengyuan.feima.entity.ExChangeResponse;
import com.banshengyuan.feima.view.PresenterControl.CollectionHotControl;
import com.banshengyuan.feima.view.activity.MyCollectionActivity;
import com.banshengyuan.feima.view.adapter.ExChangeAdapter;
import com.example.mylibrary.adapter.BaseQuickAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by helei on 2017/5/3.
 * 我的收藏-热闹
 */

public class CollectionHotFragment extends BaseFragment implements CollectionHotControl.CollectionHotView, BaseQuickAdapter.RequestLoadMoreListener {

    public static CollectionHotFragment newInstance() {
        return new CollectionHotFragment();
    }

    @BindView(R.id.coupon_common_list)
    RecyclerView mCouponCommonList;

    private Unbinder unbind;
    private ExChangeAdapter mAdapter;//HotFairAdapter
    private Integer mPagerSize = 10;
    private Integer mPagerNo = 1;

    @Inject
    CollectionHotControl.PresenterCollectionHot mPresenter;

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
        mPresenter.requestCollectionHotList(mPagerNo, mPagerSize);
    }

    private void initView() {
        mCouponCommonList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ExChangeAdapter(null, getActivity(), mImageLoaderHelper);
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
        DaggerCollectionFragmentComponent.builder()
                .applicationComponent(((DaggerApplication) getActivity().getApplication()).getApplicationComponent())
                .collectionActivityModule(new CollectionActivityModule((AppCompatActivity) getActivity()))
                .collectionFragmentModule(new CollectionFragmentModule(this, (MyCollectionActivity) getActivity()))
                .build()
                .inject(this);
    }

    @Override
    public void getMyCollectionListSuccess(ExChangeResponse response) {
        mAdapter.setNewData(response.getList());
    }

    @Override
    public void onLoadMoreRequested() {

    }
}
