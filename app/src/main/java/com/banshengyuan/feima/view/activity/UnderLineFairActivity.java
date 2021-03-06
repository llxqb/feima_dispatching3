package com.banshengyuan.feima.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerUnderLineFairActivityComponent;
import com.banshengyuan.feima.dagger.module.UnderLineFairActivityModule;
import com.banshengyuan.feima.entity.BlockDetailFairListResponse;
import com.banshengyuan.feima.entity.BlockDetailProductListResponse;
import com.banshengyuan.feima.entity.BlockDetailResponse;
import com.banshengyuan.feima.entity.BlockStoreListResponse;
import com.banshengyuan.feima.entity.BroConstant;
import com.banshengyuan.feima.entity.FairUnderLineResponse;
import com.banshengyuan.feima.listener.AppBarStateChangeListener;
import com.banshengyuan.feima.utils.ValueUtil;
import com.banshengyuan.feima.view.PresenterControl.UnderLineFairControl;
import com.banshengyuan.feima.view.adapter.ActivityUnderLineFairAdapter;
import com.banshengyuan.feima.view.adapter.MyOrderFragmentAdapter;
import com.banshengyuan.feima.view.fragment.UnderLineFairFragment;
import com.banshengyuan.feima.view.fragment.UnderLineProductListFragment;
import com.banshengyuan.feima.view.fragment.UnderLineShopFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lei.he on 2017/6/5.
 * WelcomeActivity
 */

public class UnderLineFairActivity extends BaseActivity implements UnderLineFairControl.UnderLineFairView {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.fair_under_line_tab_layout)
    TabLayout mFairUnderLineTabLayout;
    @BindView(R.id.fair_under_line_view_pager)
    ViewPager mFairUnderLineViewPager;
    @BindView(R.id.fair_under_line_recyclerView)
    RecyclerView mFairUnderLineRecyclerView;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.fair_under_line_background)
    ImageView mFairUnderLineBackground;

    public static Intent getActivityDetailIntent(Context context, Integer mStreetId) {
        Intent intent = new Intent(context, UnderLineFairActivity.class);
        intent.putExtra("mStreetId", mStreetId);
        return intent;
    }

    @Inject
    UnderLineFairControl.PresenterUnderLineFair mPresenter;

    private final String[] modules = {"市集", "商家", "产品"};
    private ActivityUnderLineFairAdapter mAdapter;
    private FairUnderLineResponse mFairUnderLineResponse;
    private Integer mPosition;
    private FairUnderLineResponse.ListBean mListBean;
    private Integer mBlockId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_line_fair);
        initializeInjector();
        ButterKnife.bind(this);
        supportActionBar(mToolbar, true);
        initView();
        initData();

    }

    @Override
    public void getBlockDetailSuccess(BlockDetailResponse response) {
        BlockDetailResponse.InfoBean infoBean = response.info;
        if (infoBean != null) {
            mImageLoaderHelper.displayImage(this, response.info.cover_img, mFairUnderLineBackground);
            mCollapsingToolbarLayout.setTitle(TextUtils.isEmpty(infoBean.name) ? "街区详情" : infoBean.name);
        }
    }

    @Override
    public void getBlockDetailFail(String des) {
        showToast(des);

    }

    @Override
    public void getFairUnderLineSuccess(FairUnderLineResponse fairUnderLineResponse) {
        if (fairUnderLineResponse.list != null && fairUnderLineResponse.list.size() > 0) {
            mFairUnderLineResponse = fairUnderLineResponse;
            mAdapter.setNewData(mFairUnderLineResponse.list);

            for (int i = 0; i < fairUnderLineResponse.list.size(); i++) {
                if (fairUnderLineResponse.list.get(i).id == mBlockId) {
                    fairUnderLineResponse.list.get(i).select_position = true;
                    mPosition = i;
                }
            }
            mFairUnderLineRecyclerView.getLayoutManager().smoothScrollToPosition(mFairUnderLineRecyclerView, null, mPosition);
        } else {
            mFairUnderLineRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void getFairUnderLineFail() {
        mFairUnderLineRecyclerView.setVisibility(View.GONE);
    }

    private void initData() {
        //请求街区详情
        mPresenter.requestBlockDetail(mBlockId);
        //请求线下街区
        if (mLocationInfo != null) {
            mPresenter.requestFairUnderLine(mLocationInfo.getLongitude(), mLocationInfo.getLatitude(), 1, 10);
        } else {
            mPresenter.requestFairUnderLine(0, 0, 1, 10);
        }
    }

    private void initView() {
        //选中的街区
        mBlockId = getIntent().getIntExtra("mStreetId", 0);

        mCollapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.tab_text_normal));
        mCollapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);

        mFairUnderLineRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new ActivityUnderLineFairAdapter(null, this, mImageLoaderHelper);
        mFairUnderLineRecyclerView.setAdapter(mAdapter);
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(UnderLineFairFragment.newInstance());
        mFragments.add(UnderLineShopFragment.newInstance());
        mFragments.add(UnderLineProductListFragment.newInstance());
        MyOrderFragmentAdapter adapter = new MyOrderFragmentAdapter(getSupportFragmentManager(), mFragments, modules);
        mFairUnderLineViewPager.setOffscreenPageLimit(mFragments.size() - 1);
        mFairUnderLineViewPager.setAdapter(adapter);
        mFairUnderLineTabLayout.setupWithViewPager(mFairUnderLineViewPager);
        ValueUtil.setIndicator(mFairUnderLineTabLayout, 40, 40);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    mToolbar.setNavigationIcon(R.mipmap.arrow_left);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    mToolbar.setNavigationIcon(R.drawable.vector_arrow_left);
                }
            }
        });

        mAdapter.setOnItemClickListener((adapter1, view, position) -> {
            mPosition = position;
            mListBean = mFairUnderLineResponse.list.get(mPosition);
            mBlockId = mListBean.id;
            /*for (int i = 0; i < mFairUnderLineResponse.list.size(); i++) {
                mFairUnderLineResponse.list.get(i).select_position = i == position;
            }*/
            mFairUnderLineRecyclerView.getLayoutManager().smoothScrollToPosition(mFairUnderLineRecyclerView, null, mPosition);
            mPresenter.requestBlockDetail(mBlockId);
            Intent intent = new Intent(BroConstant.BLOCKDETAIL_UPDATE);
            intent.putExtra("blockId", mBlockId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        });
    }

    public Integer getBlockId() {
        return mBlockId;
    }

    @Override
    public void getBlockFairListSuccess(BlockDetailFairListResponse response) {

    }

    @Override
    public void getBlockFairListFail(String des) {

    }

    @Override
    public void getStoreListSuccess(BlockStoreListResponse response) {

    }

    @Override
    public void loadError(Throwable throwable) {

    }

    @Override
    public void getStoreListFail() {

    }

    @Override
    public void getProductListSuccess(BlockDetailProductListResponse response) {

    }

    @Override
    public void getProductListFail(String des) {

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

    private void initializeInjector() {
        DaggerUnderLineFairActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .underLineFairActivityModule(new UnderLineFairActivityModule(UnderLineFairActivity.this, this))
                .build().inject(this);
    }
}
