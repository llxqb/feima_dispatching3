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
import com.banshengyuan.feima.dagger.component.DaggerDiscoverFragmentComponent;
import com.banshengyuan.feima.dagger.module.DiscoverFragmentModule;
import com.banshengyuan.feima.dagger.module.MainActivityModule;
import com.banshengyuan.feima.entity.ProductResponse;
import com.banshengyuan.feima.view.PresenterControl.FairControl;
import com.banshengyuan.feima.view.activity.MainActivity;
import com.banshengyuan.feima.view.adapter.FairProductAdapter;

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

public class FairFragment extends BaseFragment implements FairControl.FairView {
    @BindView(R.id.send_fragment_fair)
    RecyclerView mSendFragmentFair;

    public static FairFragment newInstance() {
        return new FairFragment();
    }

    @Inject
    FairControl.PresenterFair mPresenter;

    private Unbinder unbind;
    private FairProductAdapter mAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fair, container, false);
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
        List<ProductResponse> mList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            List<ProductResponse.ProductItemBean> mList1 = new ArrayList<>();
            ProductResponse product = new ProductResponse();
            product.name = "户外运动" + i;
            for (int j = 0; j < 5; j++) {
                ProductResponse.ProductItemBean itemBean = new ProductResponse.ProductItemBean();
                itemBean.content = "魔兽世界" + j;
                itemBean.tip = "少年三国志" + j;
                mList1.add(itemBean);
            }
            product.mList = mList1;
            mList.add(product);
        }
        mAdapter.setNewData(mList);
    }

    private void initView() {
        mSendFragmentFair.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FairProductAdapter(null, getActivity(),true);
        mSendFragmentFair.setAdapter(mAdapter);
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
        DaggerDiscoverFragmentComponent.builder()
                .applicationComponent(((DaggerApplication) getActivity().getApplication()).getApplicationComponent())
                .mainActivityModule(new MainActivityModule((AppCompatActivity) getActivity()))
                .discoverFragmentModule(new DiscoverFragmentModule(this, (MainActivity) getActivity()))
                .build()
                .inject(this);
    }
}
