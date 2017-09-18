package com.banshengyuan.feima.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.banshengyuan.feima.BuildConfig;
import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerSearchActivityComponent;
import com.banshengyuan.feima.dagger.module.SearchActivityModule;
import com.banshengyuan.feima.entity.SearchShopListResponse;
import com.banshengyuan.feima.entity.ShopDetailResponse;
import com.banshengyuan.feima.entity.ShopListResponse;
import com.banshengyuan.feima.entity.SpecificationResponse;
import com.banshengyuan.feima.listener.TabCheckListener;
import com.banshengyuan.feima.utils.ValueUtil;
import com.banshengyuan.feima.view.PresenterControl.SearchControl;
import com.banshengyuan.feima.view.adapter.SearchHistoryAdapter;
import com.banshengyuan.feima.view.adapter.ShopDetailAdapter;
import com.banshengyuan.feima.view.adapter.ShopListAdapter;
import com.banshengyuan.feima.view.customview.ClearEditText;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lei.he on 2017/6/5.
 * SearchActivity
 */

public class SearchActivity extends BaseActivity implements SearchControl.SearchView, ClearEditText.setOnMyEditorActionListener, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.search_goods)
    ClearEditText mSearchGoods;
    @BindView(R.id.search_goods_cancel)
    TextView mSearchGoodsCancel;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.search_shops_list_layout)
    RecyclerView mSearchShopsListLayout;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.search_product_recyclerView)
    RecyclerView mSearchProductList;
    @BindView(R.id.search_goods_layout)
    LinearLayout mSearchGoodsLayout;
    @BindView(R.id.search_history_layout)
    RecyclerView mSearchHistoryLayout;

    public static Intent getIntent(Context context, String type, String storeCode) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("searchType", type);
        intent.putExtra("storeCode", storeCode);
        return intent;
    }

    @Inject
    SearchControl.PresenterSearch mPresenter;

    private final String[] modules = {"销量", "价格", "新品"};
    private ImageView mTabItemPriceLow;
    private ImageView mTabItemPriceUp;
    private TextView mTabItemPriceGoods;
    private ShopDetailAdapter mAdapter;
    private ShopListAdapter mShopListAdapter;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private final Integer mPagerSize = 10;
    private String mPartnerId;
    private String mType;
    private List<ShopDetailResponse.ProductsBean> mSaleCountGoodsList;
    private List<ShopDetailResponse.ProductsBean> mPriceUpGoodsList;
    private List<ShopDetailResponse.ProductsBean> mPriceDownGoodsList;
    private List<ShopDetailResponse.ProductsBean> mNewProductGoodsList;
    private Integer mSaleCountPagerNo = 1;
    private Integer mPricePagerDownNo = 1;
    private Integer mPricePagerUpNo = 1;
    private Integer mNewProductPagerNo = 1;
    private List<ShopDetailResponse.ProductsBean> mList;
    private String mSearchName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
            switch (mTabLayout.getSelectedTabPosition()) {
                case 0:
                    //销量
                    mPresenter.requestProductList(mSearchName, mPartnerId, "saleCount", 1, mPagerSize, ++mSaleCountPagerNo);
                    break;
                case 1:
                    //价格
                    TabLayout.Tab tab = mTabLayout.getTabAt(1);
                    if (tab != null) {
                        if (tab.getTag() == null) return;
                        if ((Integer) tab.getTag() == 1) {
                            mPresenter.requestProductList(mSearchName, mPartnerId, "finalPrice", 1, mPagerSize, ++mPricePagerDownNo);
                        } else {
                            mPresenter.requestProductList(mSearchName, mPartnerId, "finalPrice", 2, mPagerSize, ++mPricePagerUpNo);
                        }
                    }
                    break;
                case 2:
                    //新品
                    mPresenter.requestProductList(mSearchName, mPartnerId, "pid", 2, mPagerSize, ++mNewProductPagerNo);
                    break;
            }

        }
    }

    @Override
    public void onMyEditorAction() {
        mSearchName = mSearchGoods.getEditText().trim();

        if (TextUtils.isEmpty(mSearchName)) {
            showToast("搜索栏不能为空");
        } else {
            if ("goods".equals(mType)) {
                mPresenter.requestProductList(mSearchName, mPartnerId, "saleCount", 1, mPagerSize, mSaleCountPagerNo);
            } else {
                //搜索门店
                mPresenter.requestShopList("82133fac-4825-418b-be84-0d6a0310ae73", mSearchName);
            }

        }
    }

    @Override
    public void getShopListSuccess(SearchShopListResponse response) {
        mSearchHistoryLayout.setVisibility(View.GONE);
        mSearchShopsListLayout.setVisibility(View.VISIBLE);
        if (response.data != null && response.data.size() > 0) {
            List<ShopListResponse.ListBean> listBeen = new ArrayList<>();
            for (SearchShopListResponse.DataBean dataBean : response.data) {
                ShopListResponse.ListBean bean = new ShopListResponse.ListBean();
                bean.storeCode = dataBean.storeId;
                bean.address = dataBean.address;
                bean.fullName = dataBean.storeName;
                bean.phone = dataBean.phone;
                bean.remark = (String) dataBean.remark;
                bean.latitude = dataBean.latitude;
                bean.longitude = dataBean.longitude;
                if (dataBean.businessImages != null && dataBean.businessImages.size() > 0) {
                    List<ShopListResponse.ListBean.BusinessImagesBean> list2 = new ArrayList<>();
                    for (SearchShopListResponse.DataBean.BusinessImagesBean businessImage : dataBean.businessImages) {
                        ShopListResponse.ListBean.BusinessImagesBean imageBean = new ShopListResponse.ListBean.BusinessImagesBean();
                        imageBean.imageUrl = businessImage.imageUrl;
                        imageBean.imageTitle = businessImage.imageTitle;
                        imageBean.sortIndex = businessImage.sortIndex;
                        list2.add(imageBean);
                    }
                    bean.businessImages = list2;
                }
                listBeen.add(bean);
            }
            mShopListAdapter.setNewData(listBeen);
        }
    }

    @Override
    public void getProductListSuccess(ShopDetailResponse response) {
        hideSoftInput(mSearchGoods);
        if (response != null && response.products.size() > 0) {
            mList = response.products;
            mSearchHistoryLayout.setVisibility(View.GONE);
            mSearchGoodsLayout.setVisibility(View.VISIBLE);
            switch (mTabLayout.getSelectedTabPosition()) {
                case 0:
                    if (mSaleCountGoodsList.size() == 0) {
                        mAdapter.setNewData(mList);
                    } else {
                        mAdapter.addData(mList);
                    }
                    mSaleCountGoodsList.addAll(mList);
                    break;
                case 1:
                    //价格
                    TabLayout.Tab tab = mTabLayout.getTabAt(1);
                    if (tab != null) {
                        if (tab.getTag() == null) return;
                        if ((Integer) tab.getTag() == 1) {
                            if (mPriceUpGoodsList.size() == 0) {
                                mAdapter.setNewData(mList);
                            } else {
                                mAdapter.addData(mList);
                            }
                            mPriceUpGoodsList.addAll(mList);
                        } else {
                            if (mPriceDownGoodsList.size() == 0) {
                                mAdapter.setNewData(mList);
                            } else {
                                mAdapter.addData(mList);
                            }
                            mPriceDownGoodsList.addAll(mList);
                        }
                    }
                    break;
                case 2:
                    if (mNewProductGoodsList.size() == 0) {
                        mAdapter.setNewData(mList);
                    } else {
                        mAdapter.addData(mList);
                    }
                    mNewProductGoodsList.addAll(mList);
                    break;
            }
            mAdapter.loadMoreComplete();
        } else {
            mAdapter.loadMoreEnd();
            showToast("搜索结果为空,请重新搜索");
        }
    }

    @Override
    public void onMyTouchAction() {

    }

    private void initView() {
        mSearchGoods.requestFouce();
        mSearchHistoryLayout.setLayoutManager(new LinearLayoutManager(this));
        mSearchHistoryAdapter = new SearchHistoryAdapter(null, this);
        mSearchHistoryLayout.setAdapter(mSearchHistoryAdapter);

        mType = getIntent().getStringExtra("searchType");

        if (!TextUtils.isEmpty(mType)) {
            if ("goods".equals(mType)) {
                mSaleCountGoodsList = new ArrayList<>();
                mPriceUpGoodsList = new ArrayList<>();
                mPriceDownGoodsList = new ArrayList<>();
                mNewProductGoodsList = new ArrayList<>();
                String mStoreCode = getIntent().getStringExtra("storeCode");
                mPartnerId = BuildConfig.PARTNER_ID + "_" + mStoreCode;
                mSearchGoods.setEditHint("搜索商品");
                mSearchProductList.setLayoutManager(new GridLayoutManager(this, 2));
                mAdapter = new ShopDetailAdapter(null, this);
                mSearchProductList.setAdapter(mAdapter);
                mAdapter.setOnLoadMoreListener(this, mSearchProductList);
                mAdapter.setOnItemClickListener((adapter, view, position) ->
                        startActivity(GoodDetailActivity.getIntent(this, (ShopDetailResponse.ProductsBean) adapter.getItem(position)))
                );

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
                                sortGoodsBySaleCount();
                                break;
                            case 1:
                                mTabItemPriceGoods.setTextColor(ContextCompat.getColor(getContext(), R.color.light_blue_dark));
                                break;
                            case 2:
                                mTabItemPriceGoods.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                                sortGoodsByNewProduct();
                                break;
                        }
                    }
                });
            } else {
                mSearchGoods.setEditHint("搜索商户");
                mSearchShopsListLayout.setLayoutManager(new LinearLayoutManager(this));
                mShopListAdapter = new ShopListAdapter(null, this, mImageLoaderHelper);
                mSearchShopsListLayout.setAdapter(mShopListAdapter);
            }
        }

        mSearchGoods.setLinearBackgroundResource(R.drawable.shape_line_grey);
        mSearchGoods.setOnMyEditorActionListener(this, false);
        RxView.clicks(mSearchGoodsCancel).subscribe(o -> onBackPressed());
    }

    private void initData() {
        List<SpecificationResponse.ProductsBean.SpecificationListBean> searchHistory = new ArrayList<>();
        String[] strings = {"小梅子店铺", "指甲油幻彩店铺", "木棉落落店", "黑白街", "老铁扎心了"};
        String[] strings2 = {"夏天的衣服", "春天的衣服", "秋天的裤子", "冬天的棉袄", "老司机"};
        List<String> mList = new ArrayList<>();
        List<String> mList2 = new ArrayList<>();
        Collections.addAll(mList, strings);
        Collections.addAll(mList2, strings2);
        SpecificationResponse.ProductsBean.SpecificationListBean bean = new SpecificationResponse.ProductsBean.SpecificationListBean();
        bean.partName = "最近搜索";
        bean.value = mList;
        searchHistory.add(bean);
        SpecificationResponse.ProductsBean.SpecificationListBean bean2 = new SpecificationResponse.ProductsBean.SpecificationListBean();
        bean2.partName = "热门搜索";
        bean2.value = mList2;
        searchHistory.add(bean2);
        mSearchHistoryAdapter.setNewData(searchHistory);
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

    private void changeStatus(TabLayout.Tab tab) {
        if (tab.getTag() == null) return;
        if (tab.isSelected()) {
            //改变状态
            if ((Integer) tab.getTag() == 1) {
                arrowUiUp();
                tab.setTag(2);
                sortGoodsByPrice(1);

            } else {
                arrowUiDown();
                tab.setTag(1);
                sortGoodsByPrice(2);
            }
        } else {
            //不改变状态
            if ((Integer) tab.getTag() == 1) {
                arrowUiDown();
                tab.setTag(1);
                sortGoodsByPrice(2);
            } else {
                arrowUiUp();
                sortGoodsByPrice(1);
            }
        }
    }

    private void arrowUiUp() {
        mTabItemPriceUp.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_up_blue));
        mTabItemPriceLow.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_up_dark));
    }

    private void arrowUiDown() {
        mTabItemPriceUp.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_up));
        mTabItemPriceLow.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.price_low));
    }

    private void sortGoodsBySaleCount() {
        mSaleCountPagerNo = 1;
        mSaleCountGoodsList.clear();
        mPresenter.requestProductList(mSearchName, mPartnerId, "saleCount", 2, mPagerSize, mSaleCountPagerNo);
    }

    private void sortGoodsByNewProduct() {
        mNewProductPagerNo = 1;
        mNewProductGoodsList.clear();
        mPresenter.requestProductList(mSearchName, mPartnerId, "pid", 2, mPagerSize, mNewProductPagerNo);
    }

    private void sortGoodsByPrice(Integer flag) {
        mPricePagerDownNo = 1;
        mPricePagerUpNo = 1;
        mPriceDownGoodsList.clear();
        mPriceUpGoodsList.clear();
        mPresenter.requestProductList(mSearchName, mPartnerId, "finalPrice", flag, mPagerSize, 1);
    }

    private TabLayout.Tab addOtherView() {
        View mView = LayoutInflater.from(this).inflate(R.layout.tab_view, (ViewGroup) mTabLayout.getParent(), false);
        mTabItemPriceGoods = (TextView) mView.findViewById(R.id.good_price);
        mTabItemPriceLow = (ImageView) mView.findViewById(R.id.price_low);
        mTabItemPriceUp = (ImageView) mView.findViewById(R.id.price_up);
        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setCustomView(mView);
        tab.setTag(1);
        return tab;
    }

    private void initializeInjector() {
        DaggerSearchActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .searchActivityModule(new SearchActivityModule(SearchActivity.this, this))
                .build().inject(this);
    }
}