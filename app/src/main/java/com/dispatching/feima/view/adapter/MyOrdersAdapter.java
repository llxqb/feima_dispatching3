package com.dispatching.feima.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.widget.ImageView;

import com.dispatching.feima.R;
import com.dispatching.feima.entity.MyOrdersResponse;
import com.dispatching.feima.help.GlideHelper.ImageLoaderHelper;
import com.dispatching.feima.utils.SpannableStringUtils;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.example.mylibrary.adapter.BaseViewHolder;

import java.util.List;


public class MyOrdersAdapter extends BaseQuickAdapter<MyOrdersResponse, BaseViewHolder> {
    private final Context mContext;
    private ImageLoaderHelper mImageLoaderHelper;
    public MyOrdersAdapter(List<MyOrdersResponse> notices, Context context, ImageLoaderHelper imageLoaderHelper) {
        super(R.layout.adapter_my_orders, notices);
        mContext = context;
        mImageLoaderHelper = imageLoaderHelper;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyOrdersResponse item) {
        if (item == null) return;
        ImageView iconView = helper.getView(R.id.adapter_person_icon);
        mImageLoaderHelper.displayRoundedCornerImage(mContext,R.mipmap.neo,iconView,6);
        helper.addOnClickListener(R.id.order_pull_off).addOnClickListener(R.id.order_pull_sure);
        helper.setText(R.id.shop_name, "  "+item.shopName);
        helper.setText(R.id.product_name, item.productName);
        helper.setText(R.id.product_info, item.productDes1);
        helper.setText(R.id.product_price, "￥" + item.productPrice);
        helper.setText(R.id.product_info2, item.productDes2);
        helper.setText(R.id.product_count, "x" + item.productcount);
        String orderPricePartOne = "合计：";
        String orderPricePartTwo = "￥"+item.productPrice * item.productcount;
        SpannableStringBuilder stringBuilder = SpannableStringUtils.getBuilder(orderPricePartTwo)
                .setForegroundColor(ContextCompat.getColor(mContext,R.color.order_price_color))
                .setSize(18,true)
                .create();
        SpannableStringBuilder stringBuilder2 = SpannableStringUtils.getBuilder(orderPricePartOne)
                .setForegroundColor(ContextCompat.getColor(mContext,R.color.light_grey_dark))
                .append(stringBuilder).create();
        helper.setText(R.id.order_price, stringBuilder2);
        helper.setText(R.id.order_count, "共" + item.productcount + "件商品");

    }

}
