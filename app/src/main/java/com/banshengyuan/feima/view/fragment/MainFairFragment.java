package com.banshengyuan.feima.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.banshengyuan.feima.DaggerApplication;
import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerMainFragmentComponent;
import com.banshengyuan.feima.dagger.module.MainActivityModule;
import com.banshengyuan.feima.dagger.module.MainFragmentModule;
import com.banshengyuan.feima.entity.FairBottomResponse;
import com.banshengyuan.feima.entity.FairUnderLineResponse;
import com.banshengyuan.feima.entity.RecommendBrandResponse;
import com.banshengyuan.feima.view.PresenterControl.MainFairControl;
import com.banshengyuan.feima.view.activity.BrandFairActivity;
import com.banshengyuan.feima.view.activity.MainActivity;
import com.banshengyuan.feima.view.activity.UnderLineFairActivity;
import com.banshengyuan.feima.view.activity.WorkSummaryActivity;
import com.banshengyuan.feima.view.adapter.MainHotFairAdapter;
import com.banshengyuan.feima.view.adapter.RecommendBrandAdapter;
import com.banshengyuan.feima.view.adapter.UnderLineBrandAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by helei on 2017/5/3.
 * SendingOrderFragment
 */

public class MainFairFragment extends BaseFragment implements MainFairControl.MainFairView, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.fair_offline_hot_recycle_view)
    RecyclerView mFairOfflineHotRecycleView;
    @BindView(R.id.fair_brand_recycle_view)
    RecyclerView mFairBrandRecycleView;
    @BindView(R.id.fair_hot_recycle_view)
    RecyclerView mFairHotRecycleView;
    @BindView(R.id.fair_hot_text_view)
    TextView mFairHotTextView;
    @BindView(R.id.refresh_lay_out)
    SwipeRefreshLayout mRefreshLayOut;

    public static MainFairFragment newInstance() {
        return new MainFairFragment();
    }

    @Inject
    MainFairControl.PresenterFair mPresenter;

    private Unbinder unbind;
    private UnderLineBrandAdapter mUnderLineAdapter;
    private RecommendBrandAdapter mAdapter;
    private MainHotFairAdapter mHotFairAdapter;
    private List<RecommendBrandResponse> mList;
    private List<FairUnderLineResponse> mList2;
    private FairUnderLineResponse mFairUnderLineResponse;
    private boolean firstFlag, secondFlag, thirdFlag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_fair, container, false);
        unbind = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onRefresh() {
        firstFlag = false;
        secondFlag = false;
        thirdFlag = false;
        initData();
    }

    @Override
    public void getRecommendBrandComplete() {
        secondFlag = true;
        dismissLoading();
    }

    @Override
    public void getFairUnderLineComplete() {
        thirdFlag = true;
        dismissLoading();
    }

    @Override
    public void getFairBottomComplete() {
        firstFlag = true;
        dismissLoading();
    }

    @Override
    public void getRecommendBrandSuccess(RecommendBrandResponse recommendBrandResponse) {
        if (recommendBrandResponse.list != null && recommendBrandResponse.list.size() > 0) {
            mList = new ArrayList<>();
            mList.add(recommendBrandResponse);
            mAdapter.setNewData(mList);
        } else {
            mFairBrandRecycleView.setVisibility(View.GONE);
        }

    }

    @Override
    public void getRecommendBrandFail() {
        mFairBrandRecycleView.setVisibility(View.GONE);
    }

    @Override
    public void getFairUnderLineSuccess(FairUnderLineResponse fairUnderLineResponse) {
        if (fairUnderLineResponse.list != null && fairUnderLineResponse.list.size() > 0) {
            mFairUnderLineResponse = fairUnderLineResponse;
            mList2 = new ArrayList<>();
            mList2.add(fairUnderLineResponse);
            mUnderLineAdapter.setNewData(mList2);
        } else {
            mFairBrandRecycleView.setVisibility(View.GONE);
        }

    }

    @Override
    public void getFairUnderLineFail() {
        mFairOfflineHotRecycleView.setVisibility(View.GONE);
    }

    @Override
    public void getFairBottomSuccess(FairBottomResponse fairBottomResponse) {
        List<FairBottomResponse.ListBean> listBean = fairBottomResponse.list;
        if (listBean != null && listBean.size() > 0) {
            mHotFairAdapter.setNewData(listBean);
        }
    }

    @Override
    public void getFairBottomFail() {
        mFairHotRecycleView.setVisibility(View.GONE);
        mFairHotTextView.setVisibility(View.GONE);
    }

    private void initData() {
        //请求线下街区
        if (mLocationInfo != null) {
            mPresenter.requestFairUnderLine(mLocationInfo.getLongitude(), mLocationInfo.getLatitude());
        } else {
            mPresenter.requestFairUnderLine(0, 0);
        }
        //请求品牌布局
        mPresenter.requestRecommendBrand();
        //请求市集列表、
        mPresenter.requestFairBottom();
    }

    private void initView() {
        mRefreshLayOut.setOnRefreshListener(this);
        mFairOfflineHotRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFairBrandRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFairHotRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFairOfflineHotRecycleView.setNestedScrollingEnabled(false);
        mFairBrandRecycleView.setNestedScrollingEnabled(false);
        mFairHotRecycleView.setNestedScrollingEnabled(false);
        mAdapter = new RecommendBrandAdapter(null, getActivity(), mImageLoaderHelper);
        mUnderLineAdapter = new UnderLineBrandAdapter(null, getActivity(), mImageLoaderHelper);
        mHotFairAdapter = new MainHotFairAdapter(null, getActivity(), mImageLoaderHelper);
        mFairOfflineHotRecycleView.setAdapter(mUnderLineAdapter);
        mFairBrandRecycleView.setAdapter(mAdapter);
        mFairHotRecycleView.setAdapter(mHotFairAdapter);


        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.adapter_fair_more:
                    startActivity(BrandFairActivity.getIntent(getActivity()));
                    break;
            }
        });
        mUnderLineAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.adapter_fair_more:
                    startActivity(UnderLineFairActivity.getActivityDetailIntent(getActivity(), mFairUnderLineResponse.list.get(0).id));
                    break;
            }
        });
        mHotFairAdapter.setOnItemClickListener((adapter, view, position) -> {
            FairBottomResponse.ListBean bean = mHotFairAdapter.getItem(position);
            if (bean != null) {
                startActivity(WorkSummaryActivity.getSummaryIntent(getActivity(), bean.id));
            }

        });

    }

    @Override
    public void showLoading(String msg) {
        showDialogLoading(msg);
    }

    @Override
    public void dismissLoading() {
        dismissDialogLoading();
        checkUpDataFinish();
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

    private void checkUpDataFinish() {
        if (firstFlag && secondFlag && thirdFlag) {
            if (mRefreshLayOut.isRefreshing()) {
                mRefreshLayOut.setRefreshing(false);
            }
        }
    }

    private void initialize() {
        DaggerMainFragmentComponent.builder()
                .applicationComponent(((DaggerApplication) getActivity().getApplication()).getApplicationComponent())
                .mainActivityModule(new MainActivityModule((AppCompatActivity) getActivity()))
                .mainFragmentModule(new MainFragmentModule(this, (MainActivity) getActivity()))
                .build()
                .inject(this);
    }
}
