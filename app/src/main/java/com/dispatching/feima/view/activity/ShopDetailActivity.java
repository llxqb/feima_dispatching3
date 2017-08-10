package com.dispatching.feima.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dispatching.feima.R;
import com.dispatching.feima.dagger.component.DaggerShopDetailActivityComponent;
import com.dispatching.feima.dagger.module.ShopDetailActivityModule;
import com.dispatching.feima.entity.ShopDetailResponse;
import com.dispatching.feima.entity.ShopListResponse;
import com.dispatching.feima.entity.ShopResponse;
import com.dispatching.feima.help.GlideLoader;
import com.dispatching.feima.listener.TabCheckListener;
import com.dispatching.feima.utils.ValueUtil;
import com.dispatching.feima.view.PresenterControl.ShopDetailControl;
import com.dispatching.feima.view.adapter.ShopDetailAdapter;
import com.dispatching.feima.view.customview.ClearEditText;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.jakewharton.rxbinding2.view.RxView;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lei.he on 2017/6/30.
 */

public class ShopDetailActivity extends BaseActivity implements ShopDetailControl.ShopDetailView, BaseQuickAdapter.RequestLoadMoreListener, ClearEditText.setOnMyEditorActionListener {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static Intent getIntent(Context context, ShopListResponse.ListBean item) {
        Intent intent = new Intent(context, ShopDetailActivity.class);
        intent.putExtra("shopInfo", item);
        return intent;
    }

    @BindView(R.id.search_goods)
    ClearEditText mSearchGoods;
    @BindView(R.id.shop_detail_tool_right)
    ImageView mShopDetailToolRight;
    @BindView(R.id.shop_detail_shop_icon)
    ImageView mShopDetailShopIcon;
    @BindView(R.id.shop_detail_shop_name)
    TextView mShopDetailShopName;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.shop_detail_recyclerView)
    RecyclerView mShopDetailRecyclerView;
    @BindView(R.id.banner)
    Banner mBanner;
    @Inject
    ShopDetailControl.PresenterShopDetail mPresenter;

    private View mView;
    private ImageView mTabItemPriceLow;
    private ImageView mTabItemPriceUp;
    private TextView mTabItemPriceGoods;
    private ShopDetailAdapter mAdapter;
    private List<Integer> mImageList;
    private Integer mPagerNo = 1;
    private final Integer mPagerSize = 10;
    private ShopListResponse.ListBean mShopInfo;
    private ShopResponse mShopInfo2;
    private List<ShopDetailResponse.ProductsBean> mList;
    private final String[] modules = {"销量", "价格", "新品"};
    private List<ShopDetailResponse.ProductsBean> mAllGoodsList;
    private String mStoreCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);
        supportActionBar(mToolbar, true);
        initializeInjector();
        initView();
        initData();
    }

    @Override
    public void onLoadMoreRequested() {
        if (mList.size() < mPagerSize) {
            mAdapter.loadMoreEnd(true);
        } else {
            mPresenter.requestShopGoodsList(mStoreCode, ++mPagerNo, mPagerSize);
        }
    }

    @Override
    public void transformShopGoodsListSuccess(List<ShopDetailResponse.ProductsBean> products) {
        mList = products;
        mAllGoodsList.addAll(products);
        if (products.size() > 0) {
            mAdapter.addData(mList);
            mAdapter.loadMoreComplete();
        } else {
            mAdapter.loadMoreEnd();
        }
    }

    @Override
    public void loadFail(Throwable throwable) {
        showErrMessage(throwable);
        mAdapter.loadMoreFail();
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
    public void onResume() {
        super.onResume();
        mBanner.startAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBanner.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onMyEditorAction() {


    }

    @Override
    public void onMyTouchAction() {
        hideSoftInput(mSearchGoods);
        startActivity(SearchActivity.getIntent(this, "goods"));
    }

    private void initView() {
        mSearchGoods.setOnMyEditorActionListener(this, true);
        mImageList = new ArrayList<>();
        mShopInfo = (ShopListResponse.ListBean) getIntent().getSerializableExtra("shopInfo");
        mShopInfo2 = (ShopResponse) getIntent().getSerializableExtra("ShopResponse");
        if (mShopInfo2 != null) {
            mBuProcessor.setShopResponse(mShopInfo2);
            mImageLoaderHelper.displayRoundedCornerImage(this, mShopInfo2.businessImages.get(0).imageUrl, mShopDetailShopIcon, 6);
            mStoreCode = mShopInfo2.storeCode;
            mShopDetailShopName.setText(mShopInfo2.fullName);
            if (mShopInfo2.storeCode.equals("107")) {
                mImageList.add(R.mipmap.main_right_second);
            } else {
                mImageList.add(R.mipmap.activities_second);
            }
        }

        if (mShopInfo != null) {
            mBuProcessor.setShopInfo(mShopInfo);
            mStoreCode = mShopInfo.storeCode;
            List<ShopListResponse.ListBean.BusinessImagesBean> shopItemInfo = mShopInfo.businessImages;
            if (shopItemInfo.size() != 0) {
                mImageLoaderHelper.displayRoundedCornerImage(this, shopItemInfo.get(0).imageUrl, mShopDetailShopIcon, 6);
            } else {
                mImageLoaderHelper.displayRoundedCornerImage(this, R.mipmap.freemud_logo, mShopDetailShopIcon, 6);
            }
            mShopDetailShopName.setText(mShopInfo.fullName == null ? "未知" : mShopInfo.fullName);
            if (mShopInfo.storeCode.equals("107")) {
                mImageList.add(R.mipmap.main_right_second);
            } else {
                mImageList.add(R.mipmap.activities_second);
            }
        }

        mBanner.setImages(mImageList).setImageLoader(new GlideLoader()).start();
        mList = new ArrayList<>();
        mAllGoodsList = new ArrayList<>();
        mShopDetailRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new ShopDetailAdapter(null, this);
        mShopDetailRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this, mShopDetailRecyclerView);
        mAdapter.setOnItemClickListener((adapter, view, position) ->
                switchToGoodsDetail((ShopDetailResponse.ProductsBean) adapter.getItem(position))
        );

        mSearchGoods.setLinearBackgroundResource(R.drawable.shape_line_grey);
        mSearchGoods.setEditHint("搜索商品");

        RxView.clicks(mShopDetailToolRight).throttleFirst(1, TimeUnit.SECONDS).subscribe(v -> startActivity(GoodsClassifyActivity.getIntent(this)));

        mTabLayout.addTab(mTabLayout.newTab().setText(modules[0]));
        mTabLayout.addTab(addOtherView());
        mTabLayout.addTab(mTabLayout.newTab().setText(modules[2]));
        ValueUtil.setIndicator(mTabLayout, 40, 40);
        TabLayout.Tab tab = mTabLayout.getTabAt(1);
        if (tab != null) {
            if (tab.getCustomView() != null) {
                View view = (View) tab.getCustomView().getParent();
                view.setOnClickListener(v -> changeStatus(tab));
            }
        }
        mTabLayout.addOnTabSelectedListener(new TabCheckListener() {
            @Override
            public void onMyTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mTabItemPriceGoods.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        sortGoodsBySaleCount(mAllGoodsList);
                        break;
                    case 1:
                        mTabItemPriceGoods.setTextColor(ContextCompat.getColor(getContext(), R.color.light_blue_dark));
                        break;
                    case 2:
                        mTabItemPriceGoods.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        break;
                }
            }
        });

    }

    private void sortGoodsBySaleCount(List<ShopDetailResponse.ProductsBean> list) {
        Collections.sort(list, (o1, o2) -> {
            if (o1.saleCount >= o2.saleCount) {
                return 1;
            } else {
                return -1;
            }
        });
        mAdapter.setNewData(list);
    }

    private void sortGoodsByPriceUp(List<ShopDetailResponse.ProductsBean> list) {
        Collections.sort(list, (o1, o2) -> {
            if (o1.finalPrice > o2.finalPrice) {
                return 1;
            } else {
                return -1;
            }
        });
        mAdapter.setNewData(list);
    }

    private void sortGoodsByPriceDown(List<ShopDetailResponse.ProductsBean> list) {
        Collections.sort(list, (o1, o2) -> {
            if (o1.finalPrice > o2.finalPrice) {
                return -1;
            } else {
                return 1;
            }
        });
        mAdapter.setNewData(list);
    }

    private void switchToGoodsDetail(ShopDetailResponse.ProductsBean goodsInfo) {
        startActivity(GoodDetailActivity.getIntent(this, goodsInfo));
    }

    private void changeStatus(TabLayout.Tab tab) {
        if (tab.getTag() == null) return;
        if (tab.isSelected()) {
            //改变状态
            if ((Integer) tab.getTag() == 1) {
                mTabItemPriceUp.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_up_blue));
                mTabItemPriceLow.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_up_dark));
                tab.setTag(2);
                sortGoodsByPriceDown(mAllGoodsList);

            } else {
                mTabItemPriceUp.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_up));
                mTabItemPriceLow.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_low));
                tab.setTag(1);
                sortGoodsByPriceUp(mAllGoodsList);
            }
        } else {
            //不改变状态
            if ((Integer) tab.getTag() == 1) {
                mTabItemPriceUp.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_up));
                mTabItemPriceLow.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_low));
                tab.setTag(1);
                sortGoodsByPriceUp(mAllGoodsList);
            } else {
                mTabItemPriceUp.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_up_blue));
                mTabItemPriceLow.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_up_dark));
                tab.setTag(2);
            }
        }
    }

    private TabLayout.Tab addOtherView() {
        mView = LayoutInflater.from(this).inflate(R.layout.tab_view, (ViewGroup) mTabLayout.getParent(), false);
        mTabItemPriceGoods = (TextView) mView.findViewById(R.id.good_price);
        mTabItemPriceLow = (ImageView) mView.findViewById(R.id.price_low);
        mTabItemPriceUp = (ImageView) mView.findViewById(R.id.price_up);
        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setCustomView(mView);
        tab.setTag(1);
        return tab;
    }

    private void initData() {
        mPresenter.requestShopGoodsList(mStoreCode, mPagerNo, mPagerSize);
    }

    private void initializeInjector() {
        DaggerShopDetailActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .shopDetailActivityModule(new ShopDetailActivityModule(ShopDetailActivity.this, this))
                .build().inject(this);
    }
}
