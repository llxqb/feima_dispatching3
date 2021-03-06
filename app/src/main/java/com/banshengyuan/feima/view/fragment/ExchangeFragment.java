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

import com.banshengyuan.feima.DaggerApplication;
import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerFragmentComponent;
import com.banshengyuan.feima.dagger.module.FragmentModule;
import com.banshengyuan.feima.dagger.module.MainActivityModule;
import com.banshengyuan.feima.entity.ExChangeResponse;
import com.banshengyuan.feima.view.PresenterControl.ExChangeControl;
import com.banshengyuan.feima.view.activity.FairProductDetailActivity;
import com.banshengyuan.feima.view.activity.MainActivity;
import com.banshengyuan.feima.view.adapter.ExChangeAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by helei on 2017/5/3.
 * SendingOrderFragment
 * 热闹
 */

public class ExchangeFragment extends BaseFragment implements ExChangeControl.ExChangeView,SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.hot_fragment_activities)
    RecyclerView mHotFragmentActivities;
    @Inject
    ExChangeControl.PresenterExChange mPresenter;
    @BindView(R.id.hot_fragment_refresh)
    SwipeRefreshLayout mHotFragmentRefresh;
    private Unbinder unbind;
    private ExChangeAdapter mHotFairAdapter;
    private String streetId = "0";//街区ID

    public static ExchangeFragment newInstance() {
        return new ExchangeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbind.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    private void initData() {
        mPresenter.requestHotFairInfo(streetId);
    }

    private void initView() {
        mHotFragmentRefresh.setOnRefreshListener(this);
        mHotFragmentActivities.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHotFairAdapter = new ExChangeAdapter(null, getActivity(), mImageLoaderHelper);
        mHotFragmentActivities.setAdapter(mHotFairAdapter);
        mHotFragmentActivities.setNestedScrollingEnabled(false);
        mHotFairAdapter.setOnItemClickListener((adapter, view, position) -> {
            ExChangeResponse.ListBean bean = mHotFairAdapter.getItem(position);
            if (bean != null) {
                startActivity(FairProductDetailActivity.getIntent(getActivity(), String.valueOf(bean.getId())));
            }
        });
    }

    private void initialize() {
        DaggerFragmentComponent.builder()
                .applicationComponent(((DaggerApplication) getActivity().getApplication()).getApplicationComponent())
                .mainActivityModule(new MainActivityModule((AppCompatActivity) getActivity()))
                .fragmentModule(new FragmentModule(this, (MainActivity) getActivity())).build()
                .inject(this);
    }

    @Override
    public void getHotFairInfoSuccess(ExChangeResponse response) {
        if(mHotFragmentRefresh.isRefreshing()){
            mHotFragmentRefresh.setRefreshing(false);
        }
        mHotFairAdapter.setNewData(response.getList());
    }
}
