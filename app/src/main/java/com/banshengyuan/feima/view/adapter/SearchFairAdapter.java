package com.banshengyuan.feima.view.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.SearchResultResponse;
import com.banshengyuan.feima.help.GlideHelper.ImageLoaderHelper;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.example.mylibrary.adapter.BaseViewHolder;

import java.util.List;


public class SearchFairAdapter extends BaseQuickAdapter<SearchResultResponse.ListBean, BaseViewHolder> {
    private final Context mContext;
    private final ImageLoaderHelper mImageLoaderHelper;

    public SearchFairAdapter(List<SearchResultResponse.ListBean> mList, Context context, ImageLoaderHelper imageLoaderHelper) {
        super(R.layout.adapter_search_fair, mList);
        mContext = context;
        mImageLoaderHelper = imageLoaderHelper;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchResultResponse.ListBean item) {
        ImageView imageView = helper.getView(R.id.imageView);
        mImageLoaderHelper.displayImage(mContext, item.cover_img, imageView);
        helper.setText(R.id.adapter_search_fair_name,item.name);
        helper.setText(R.id.adapter_search_fair_summary,item.summary);
    }

}
