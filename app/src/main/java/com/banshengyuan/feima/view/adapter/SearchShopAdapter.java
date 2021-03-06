package com.banshengyuan.feima.view.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.SearchResultResponse;
import com.banshengyuan.feima.help.GlideHelper.ImageLoaderHelper;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.example.mylibrary.adapter.BaseViewHolder;

import java.util.List;


public class SearchShopAdapter extends BaseQuickAdapter<SearchResultResponse.ListBean, BaseViewHolder> {
    private final Context mContext;
    private final ImageLoaderHelper mImageLoaderHelper;

    public SearchShopAdapter(List<SearchResultResponse.ListBean> mList, Context context,ImageLoaderHelper imageLoaderHelper) {
        super(R.layout.adapter_store_list_item, mList);
        mContext = context;
        mImageLoaderHelper = imageLoaderHelper;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchResultResponse.ListBean item) {
        if (item == null) return;
        ImageView imageView = helper.getView(R.id.adapter_collection_icon);
        mImageLoaderHelper.displayImage(mContext, item.shop_logo, imageView);
        helper.setText(R.id.adapter_product_name, item.name);
        helper.setText(R.id.adapter_product_summary, item.category);
    }

}
