package com.banshengyuan.feima.view.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.ProductListResponse;
import com.banshengyuan.feima.help.GlideHelper.ImageLoaderHelper;
import com.banshengyuan.feima.utils.ValueUtil;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.example.mylibrary.adapter.BaseViewHolder;

import java.util.List;


public class ProductItemAdapter extends BaseQuickAdapter<ProductListResponse.CategoryBean.GoodsBean, BaseViewHolder> {
    private final Context mContext;
    private final ImageLoaderHelper mImageLoaderHelper;


    public ProductItemAdapter(List<ProductListResponse.CategoryBean.GoodsBean> mList, Context context, ImageLoaderHelper imageLoaderHelper) {
        super(R.layout.adapter_recommend_brand, mList);
        mContext = context;
        mImageLoaderHelper = imageLoaderHelper;
    }

    @Override
    protected void convert(BaseViewHolder helper, ProductListResponse.CategoryBean.GoodsBean item) {
        if (item == null) return;
        ImageView imageView = helper.getView(R.id.adapter_common_icon);
        mImageLoaderHelper.displayRoundedCornerImage(mContext,item.cover_img,imageView,6);
        helper.setVisible(R.id.adapter_common_price, true);
        helper.setText(R.id.adapter_common_price,  "￥"+ValueUtil.formatAmount2(item.price));
        helper.setText(R.id.adapter_recommend_text, item.name);
    }

}
