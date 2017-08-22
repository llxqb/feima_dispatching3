package com.dispatching.feima.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dispatching.feima.R;
import com.dispatching.feima.dagger.component.DaggerGoodsClassifyActivityComponent;
import com.dispatching.feima.dagger.module.GoodsClassifyActivityModule;
import com.dispatching.feima.entity.SpecificationResponse;
import com.dispatching.feima.view.PresenterControl.GoodsClassifyControl;
import com.dispatching.feima.view.adapter.GoodsClassifyAdapter;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lei.he on 2017/6/26.
 */

public class GoodsClassifyActivity extends BaseActivity implements GoodsClassifyControl.GoodsClassifyView {

    @BindView(R.id.middle_name)
    TextView mMiddleName;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.goods_classify_all)
    TextView mGoodsClassifyAll;
    @BindView(R.id.goods_classify_list)
    RecyclerView mGoodsClassifyList;

    public static Intent getIntent(Context context) {
        return new Intent(context, GoodsClassifyActivity.class);
    }

    @Inject
    GoodsClassifyControl.PresenterGoodsClassify mPresenter;
    private GoodsClassifyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_classify);
        ButterKnife.bind(this);
        initializeInjector();
        supportActionBar(mToolbar, true);
        mMiddleName.setText("商品分类");
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
        RxView.clicks(mGoodsClassifyAll).throttleFirst(1, TimeUnit.SECONDS).subscribe(v -> onBackPressed());
        mGoodsClassifyList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GoodsClassifyAdapter(null, GoodsClassifyActivity.this, this);
        mGoodsClassifyList.setAdapter(mAdapter);

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                    switch (view.getId()) {
                        case R.id.adapter_goods_classify_all:
                            showToast("查看全部");
                            break;
                    }
                }
        );

    }

    private void initData() {
        List<SpecificationResponse.ProductsBean.SpecificationListBean> searchHistory = new ArrayList<>();
        String[] strings = {"女士", "男士", "男婴(3个月-2岁)", "男婴(3个月-2岁)", "男婴(3个月-2岁)"};
        String[] strings2 = {"夏天的衣服", "春天的衣服", "秋天的裤子", "冬天的棉袄", "老司机"};
        List<String> mList = new ArrayList<>();
        List<String> mList2 = new ArrayList<>();
        Collections.addAll(mList, strings);
        Collections.addAll(mList2, strings2);
        SpecificationResponse.ProductsBean.SpecificationListBean bean = new SpecificationResponse.ProductsBean.SpecificationListBean();
        bean.partName = "本周新品";
        bean.value = mList;
        searchHistory.add(bean);
        SpecificationResponse.ProductsBean.SpecificationListBean bean2 = new SpecificationResponse.ProductsBean.SpecificationListBean();
        bean2.partName = "秋季新品";
        bean2.value = mList2;
        searchHistory.add(bean2);
        mAdapter.setNewData(searchHistory);
    }

    private void initializeInjector() {
        DaggerGoodsClassifyActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .goodsClassifyActivityModule(new GoodsClassifyActivityModule(GoodsClassifyActivity.this, this))
                .build().inject(this);
    }
}