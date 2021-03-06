package com.banshengyuan.feima.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.banshengyuan.feima.DaggerApplication;
import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerShopProductDetailFragmentComponent;
import com.banshengyuan.feima.dagger.module.ShopProductDetailActivityModule;
import com.banshengyuan.feima.dagger.module.ShopProductDetailFragmentModule;
import com.banshengyuan.feima.entity.CollectionResponse;
import com.banshengyuan.feima.entity.ShopDetailCouponListResponse;
import com.banshengyuan.feima.entity.ShopDetailProductListResponse;
import com.banshengyuan.feima.entity.StoreDetailResponse;
import com.banshengyuan.feima.view.PresenterControl.ShopProductDetailControl;
import com.banshengyuan.feima.view.activity.GoodDetailActivity;
import com.banshengyuan.feima.view.activity.ShopProductDetailActivity;
import com.banshengyuan.feima.view.adapter.FairDetailNewAdapter;
import com.banshengyuan.feima.view.adapter.ProductAnotherAdapter;
import com.example.mylibrary.adapter.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by helei on 2017/5/3.
 * TrendsFragment
 */

public class ProductListFragment extends BaseFragment implements ShopProductDetailControl.ShopProductDetailView, BaseQuickAdapter.RequestLoadMoreListener {
    @BindView(R.id.fragment_trends_list_last)
    RecyclerView mFragmentTrendsListLast;
    @BindView(R.id.fragment_trends_list_another)
    RecyclerView mFragmentTrendsListAnother;

    public static ProductListFragment newInstance() {
        return new ProductListFragment();
    }

    @Inject
    ShopProductDetailControl.PresenterShopProductDetail mPresenter;

    private Unbinder unbind;
    private FairDetailNewAdapter mAdapter;
    private ProductAnotherAdapter mAnotherAdapter;
    private Integer mShopId;
    private Integer mPage = 1;
    private Integer mPageSize = 10;
    private ShopDetailProductListResponse mResponse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        mShopId = ((ShopProductDetailActivity) getActivity()).getShopId();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trends, container, false);
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
    public void onLoadMoreRequested() {
        if (mResponse.has_next_page) {
            mPresenter.requestStoreProductList(mShopId, ++mPage, mPageSize);
        } else {
            mAdapter.loadMoreEnd(true);
        }
    }

    @Override
    public void getStoreProductListSuccess(ShopDetailProductListResponse response) {
        mResponse = response;
        List<ShopDetailProductListResponse.ListBean> listBeen = response.list;
        if (listBeen != null) {
            if (listBeen.size() == 1 && listBeen.get(0).summary_img != null) {
                if (!TextUtils.isEmpty(listBeen.get(0).summary_img)) {
                    List<String> list = new ArrayList<>();
                    list.add(listBeen.get(0).summary_img);
                    mAnotherAdapter.setNewData(list);
                }
            } else {
                if (listBeen.size() > 0) {
                    mAdapter.addData(response.list);
                    mAdapter.loadMoreComplete();
                } else {
                    mAdapter.loadMoreEnd();
                }
            }
        }

    }

    public void changeView(boolean flag) {
        mFragmentTrendsListAnother.setVisibility(flag ? View.VISIBLE : View.GONE);
        mFragmentTrendsListLast.setVisibility(flag ? View.GONE : View.VISIBLE);
    }

    @Override
    public void getStoreProductListFail() {
        mPage--;
        mAdapter.loadMoreFail();
    }

    @Override
    public void loadError(Throwable throwable) {
        mPage--;
        showErrMessage(throwable);
        mAdapter.loadMoreFail();
    }

    private void initData() {
        mPresenter.requestStoreProductList(mShopId, mPage, mPageSize);
    }

    private void initView() {
        mFragmentTrendsListLast.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mFragmentTrendsListAnother.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FairDetailNewAdapter(null, getActivity(), mImageLoaderHelper);
        mAnotherAdapter = new ProductAnotherAdapter(null, getActivity(), mImageLoaderHelper);
        mFragmentTrendsListLast.setAdapter(mAdapter);
        mFragmentTrendsListAnother.setAdapter(mAnotherAdapter);
        mAdapter.setOnLoadMoreListener(this, mFragmentTrendsListLast);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            ShopDetailProductListResponse.ListBean bean = (ShopDetailProductListResponse.ListBean) adapter.getItem(position);
            if (bean != null) {
                startActivity(GoodDetailActivity.getIntent(getActivity(), bean.id));
            }
        });
    }

    @Override
    public void getCouponInfoSuccess() {

    }

    @Override
    public void getStoreCouponListSuccess(ShopDetailCouponListResponse response) {

    }

    @Override
    public void getStoreCouponListFail() {

    }

    @Override
    public void getStoreDetailSuccess(StoreDetailResponse response) {

    }

    @Override
    public void getStoreDetailFail() {

    }

    @Override
    public void getCollectionSuccess(CollectionResponse response) {

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
        DaggerShopProductDetailFragmentComponent.builder()
                .applicationComponent(((DaggerApplication) getActivity().getApplication()).getApplicationComponent())
                .shopProductDetailActivityModule(new ShopProductDetailActivityModule((AppCompatActivity) getActivity(), this))
                .shopProductDetailFragmentModule(new ShopProductDetailFragmentModule((ShopProductDetailActivity) getActivity())).build()
                .inject(this);
    }
}