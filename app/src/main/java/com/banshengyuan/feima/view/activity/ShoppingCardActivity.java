package com.banshengyuan.feima.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerShoppingCardActivityComponent;
import com.banshengyuan.feima.dagger.module.ShoppingCardActivityModule;
import com.banshengyuan.feima.dagger.module.ShoppingCardListResponse;
import com.banshengyuan.feima.utils.SpannableStringUtils;
import com.banshengyuan.feima.utils.ValueUtil;
import com.banshengyuan.feima.view.PresenterControl.ShoppingCardControl;
import com.banshengyuan.feima.view.adapter.ShoppingCardAdapter;
import com.banshengyuan.feima.view.adapter.ShoppingCardItemAdapter;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lei.he on 2017/6/28.
 * AddressActivity
 */

public class ShoppingCardActivity extends BaseActivity implements ShoppingCardControl.ShoppingCardView {


    @BindView(R.id.middle_name)
    TextView mMiddleName;
    @BindView(R.id.toolbar_right_text)
    TextView mToolbarRightText;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_shopping_card_list)
    RecyclerView mActivityShoppingCardList;
    @BindView(R.id.activity_shopping_card_check)
    CheckBox mActivityShoppingCardCheck;
    @BindView(R.id.activity_shopping_card_price)
    TextView mActivityShoppingCardPrice;
    @BindView(R.id.activity_shopping_card_balance)
    TextView mActivityShoppingCardBalance;
    @BindView(R.id.activity_shopping_card_bottom_view)
    LinearLayout mActivityShoppingCardBottomView;

    public static Intent getIntent(Context context) {
        return new Intent(context, ShoppingCardActivity.class);
    }

    @Inject
    ShoppingCardControl.PresenterShoppingCard mPresenter;

    private ShoppingCardAdapter mAdapter;
    private View mEmptyView;
    private View mErrorView;
    private ShoppingCardItemAdapter mShoppingCardItemAdapter;
    private Integer mChildPosition;
    private Integer mPartnerPosition;
    private List<ShoppingCardListResponse.ListBeanX> mBeanXList;
    private ShoppingCardListResponse.ListBeanX.ListBean mChildProduct;
    private Integer originalNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_card);
        ButterKnife.bind(this);
        initializeInjector();
        supportActionBar(mToolbar, true);
        mMiddleName.setText("我的购物车");
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BroConstant.UPDATE_SHOPPING_CARD_INFO));
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setChildAdapter(Integer parentPosition, ShoppingCardItemAdapter itemAdapter, CheckBox partnerCheckBox) {
        mShoppingCardItemAdapter = itemAdapter;
        mPartnerPosition = parentPosition;
        ShoppingCardListResponse.ListBeanX mProduct = mBeanXList.get(parentPosition);
        itemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            mChildPosition = position;
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_shopping_card_check);
            mChildProduct = mProduct.list.get(position);
            switch (view.getId()) {
                case R.id.item_shopping_card_check:
                    if (!checkBox.isChecked()) {
                        mChildProduct.childCheckFlag = false;
                        if (mProduct.checkFlag) {
                            partnerCheckBox.setChecked(false);
                            mProduct.checkFlag = false;
                            if (mActivityShoppingCardCheck.isChecked()) {
                                mActivityShoppingCardCheck.setChecked(false);
                            }
                        }
                    } else {
                        mChildProduct.childCheckFlag = true;
                    }

                    countPrice2(partnerCheckBox, mProduct);
                    itemAdapter.setData(position, mChildProduct);
                    break;
                case R.id.item_shopping_card_reduce:
                    Integer count = mChildProduct.number;
                    if (count - 1 == 0) {
                        showToast("宝贝不能再减少了哦");
                    } else {
                        mChildProduct.number = count - 1;
                        requestProductNumber(mChildProduct, count);
                    }
                    break;
                case R.id.item_shopping_card_add:
                    Integer count2 = mChildProduct.number;
                    mChildProduct.number = count2 + 1;
                    requestProductNumber(mChildProduct, count2);
                    break;
                case R.id.item_shopping_card_delete:
                case R.id.item_shopping_card__slip_delete:
                    requestDeleteProduct(mChildProduct);
                    break;
            }

        });
    }

    private void requestDeleteProduct(ShoppingCardListResponse.ListBeanX.ListBean childProduct) {
        mPresenter.requestDeleteProduct(childProduct.goods_id);
    }

    private void requestProductNumber(ShoppingCardListResponse.ListBeanX.ListBean childProduct, Integer count) {
        originalNumber = count;
        mPresenter.requestChangeProductNumber(childProduct.goods_id, childProduct.goods_sku, childProduct.number);
    }

    /*@Override
    public void deleteProduct(ShoppingCardListResponse.DataBean product, ShoppingCardListResponse.DataBean.ProductsBean childProduct, Integer position) {
        mChildPosition = position;
        requestDeleteProduct(product, childProduct);
    }*/

    @Override
    public void changeProductNumberSuccess() {
        mShoppingCardItemAdapter.setData(mChildPosition, mChildProduct);
        countPrice();
    }

    @Override
    public void changeProductNumberFail(String des) {
        showToast(des);
        mChildProduct.number = originalNumber;
    }

    @Override
    public void deleteProductSuccess() {
        showToast("刪除购物车成功");
        mShoppingCardItemAdapter.remove(mChildPosition);
        if (mShoppingCardItemAdapter.getData().size() == 0) {
            mAdapter.remove(mPartnerPosition);
        }
        if (mAdapter.getData().size() == 0) {
            mActivityShoppingCardBottomView.setVisibility(View.GONE);
            mAdapter.setEmptyView(mEmptyView);
        }
    }

    @Override
    public void shoppingCardListSuccess(ShoppingCardListResponse response) {
        if (response.list != null && response.list.size() > 0) {
            mToolbarRightText.setVisibility(View.VISIBLE);
            mBeanXList = response.list;
            mActivityShoppingCardBottomView.setVisibility(View.VISIBLE);
            mAdapter.setNewData(response.list);
        } else {
            mActivityShoppingCardBottomView.setVisibility(View.GONE);
            mAdapter.setEmptyView(mEmptyView);
            mToolbarRightText.setVisibility(View.GONE);
        }

    }

    @Override
    public void shoppingCardListFail(String des) {
        mActivityShoppingCardBottomView.setVisibility(View.GONE);
        mAdapter.setEmptyView(mErrorView);
    }

    private void initData() {
        mPresenter.requestShoppingCardList();
    }

    private void initView() {
        mToolbarRightText.setText("编辑");
        setAllPriceText(0);
        mEmptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, (ViewGroup) mActivityShoppingCardList.getParent(), false);
        Button mEmptyButton = (Button) mEmptyView.findViewById(R.id.empty_text);
        mErrorView = LayoutInflater.from(this).inflate(R.layout.net_error_view, (ViewGroup) mActivityShoppingCardList.getParent(), false);
        RxView.clicks(mEmptyButton).throttleFirst(1, TimeUnit.SECONDS).subscribe(o -> onBackPressed());
        RxView.clicks(mActivityShoppingCardBalance).throttleFirst(1, TimeUnit.SECONDS).subscribe(o -> goForPayShoppingCard());
        mActivityShoppingCardList.setLayoutManager(new LinearLayoutManager(this));
        RxView.clicks(mActivityShoppingCardCheck).subscribe(o -> checkForAll());
        RxView.clicks(mToolbarRightText).subscribe(o -> editContent());
        mAdapter = new ShoppingCardAdapter(null, this, ShoppingCardActivity.this, mImageLoaderHelper);
        mActivityShoppingCardList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            ShoppingCardListResponse.ListBeanX product = mBeanXList.get(position);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.adapter_shopping_card_check);
            switch (view.getId()) {
                case R.id.adapter_shopping_card_check:
                    if (!checkBox.isChecked()) {
                        product.checkFlag = false;
                        for (ShoppingCardListResponse.ListBeanX.ListBean productsBean : product.list) {
                            productsBean.childCheckFlag = false;
                        }
                        if (mActivityShoppingCardCheck.isChecked()) {
                            mActivityShoppingCardCheck.setChecked(false);
                        }
                    } else {
                        product.checkFlag = true;
                        for (ShoppingCardListResponse.ListBeanX.ListBean productsBean : product.list) {
                            productsBean.childCheckFlag = true;
                        }
                    }
                    countPrice();
                    mAdapter.setData(position, product);
                    break;
            }
        });

    }

    private void goForPayShoppingCard() {

        List<ShoppingCardListResponse.ListBeanX> orderConfirm = new ArrayList<>();
        for (ShoppingCardListResponse.ListBeanX dataBean : mBeanXList) {
            if (dataBean.checkFlag) {
                orderConfirm.add(dataBean);
            } else {
                List<ShoppingCardListResponse.ListBeanX.ListBean> childProduct = new ArrayList<>();
                for (ShoppingCardListResponse.ListBeanX.ListBean listBean : dataBean.list) {
                    if (listBean.childCheckFlag) {
                        childProduct.add(listBean);
                    }
                }
                if (childProduct.size() > 0) {
                    dataBean.list = childProduct;
                    orderConfirm.add(dataBean);
                }
            }
        }


        if (orderConfirm.size() > 0) {
            ShoppingCardListResponse response = new ShoppingCardListResponse();
            response.list = orderConfirm;
            startActivity(PayActivity.getIntent(this, response));
        } else {
            showToast("您还没有选择宝贝哦");
        }
    }

    private void countPrice2(CheckBox partnerCheckBox, ShoppingCardListResponse.ListBeanX mProduct) {
        countPrice();
        boolean isAllCheck = true;
        for (ShoppingCardListResponse.ListBeanX.ListBean product : mProduct.list) {
            if (!product.childCheckFlag) {
                isAllCheck = false;
            }
        }
        partnerCheckBox.setChecked(isAllCheck);
    }

    private void countPrice() {
        Integer allPrice = 0;
        List<ShoppingCardListResponse.ListBeanX> list = mAdapter.getData();
        if (list.size() > 0) {
            for (ShoppingCardListResponse.ListBeanX dataBean : list) {
                for (ShoppingCardListResponse.ListBeanX.ListBean product : dataBean.list) {
                    if (product.childCheckFlag) {
                        allPrice += product.goods_price * product.number;
                    }

                }
            }
        }
        setAllPriceText(allPrice);
    }

    private void setAllPriceText(Integer price) {
        String orderPricePartOne = "合计：";
        String orderPricePartTwo = ValueUtil.formatAmount2(price);
        SpannableStringBuilder stringBuilder = SpannableStringUtils.getBuilder(orderPricePartTwo)
                .setForegroundColor(ContextCompat.getColor(this, R.color.light_red))
                .setSize(18, true)
                .create();
        SpannableStringBuilder stringBuilder2 = SpannableStringUtils.getBuilder(orderPricePartOne)
                .setForegroundColor(ContextCompat.getColor(this, R.color.tab_text_normal))
                .append(stringBuilder)
                .create();
        mActivityShoppingCardPrice.setText(stringBuilder2);
    }

    private void checkForAll() {
        if (!mActivityShoppingCardCheck.isChecked()) {
            for (ShoppingCardListResponse.ListBeanX dataBean : mBeanXList) {
                dataBean.checkFlag = false;
                for (ShoppingCardListResponse.ListBeanX.ListBean product : dataBean.list) {
                    product.childCheckFlag = false;
                }
            }
        } else {
            for (ShoppingCardListResponse.ListBeanX dataBean : mBeanXList) {
                dataBean.checkFlag = true;
                for (ShoppingCardListResponse.ListBeanX.ListBean product : dataBean.list) {
                    product.childCheckFlag = true;
                }
            }
        }
        countPrice();
        mAdapter.setNewData(mBeanXList);
    }

    private void editContent() {
        if (mAdapter.getData().size() > 0) {
            if (mToolbarRightText.getText().toString().trim().equals("编辑")) {
                mToolbarRightText.setText("保存");
                for (ShoppingCardListResponse.ListBeanX listBeanX : mBeanXList) {
                    for (ShoppingCardListResponse.ListBeanX.ListBean listBean : listBeanX.list) {
                        listBean.childEditFlag = true;
                    }
                }
            } else {
                for (ShoppingCardListResponse.ListBeanX listBeanX : mBeanXList) {
                    mToolbarRightText.setText("编辑");
                    for (ShoppingCardListResponse.ListBeanX.ListBean listBean : listBeanX.list) {
                        listBean.childEditFlag = false;
                    }
                }
            }
            mAdapter.setNewData(mBeanXList);
        }else {
            mToolbarRightText.setVisibility(View.GONE);
        }

    }

    private void initializeInjector() {
        DaggerShoppingCardActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .shoppingCardActivityModule(new ShoppingCardActivityModule(ShoppingCardActivity.this, this))
                .build().inject(this);
    }
}
