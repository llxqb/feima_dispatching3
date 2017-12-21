package com.banshengyuan.feima.view.adapter;

import android.content.Context;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.MyOrdersResponse;
import com.banshengyuan.feima.entity.NoticeResponse;
import com.banshengyuan.feima.help.GlideHelper.ImageLoaderHelper;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.example.mylibrary.adapter.BaseViewHolder;

import java.util.List;


public class OrderCommentAdapter extends BaseQuickAdapter<MyOrdersResponse.ListBean.ProductBean, BaseViewHolder> {
    private final Context mContext;
    private ImageLoaderHelper mImageLoaderHelper;

    public OrderCommentAdapter(List<MyOrdersResponse.ListBean.ProductBean> beanList, Context context , ImageLoaderHelper imageLoaderHelper) {
        super(R.layout.order_comment_adapter, beanList);
        mContext = context;
        mImageLoaderHelper = imageLoaderHelper;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyOrdersResponse.ListBean.ProductBean item) {
        if(item ==null) return;
        mImageLoaderHelper.displayImage(mContext,item.getCover_img(),helper.getView(R.id.goods_img));
        helper.setText(R.id.goods_name,item.getName());


    }

}
