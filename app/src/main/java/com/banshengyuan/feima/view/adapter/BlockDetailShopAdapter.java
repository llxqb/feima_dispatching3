package com.banshengyuan.feima.view.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.BlockStoreListResponse;
import com.banshengyuan.feima.help.GlideHelper.ImageLoaderHelper;
import com.banshengyuan.feima.view.activity.ShopProductDetailActivity;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.example.mylibrary.adapter.BaseViewHolder;

import java.util.List;


public class BlockDetailShopAdapter extends BaseQuickAdapter<BlockStoreListResponse, BaseViewHolder> {
    private final Context mContext;
    private final ImageLoaderHelper mImageLoaderHelper;

    public BlockDetailShopAdapter(List<BlockStoreListResponse> mList, Context context, ImageLoaderHelper imageLoaderHelper) {
        super(R.layout.adapter_block_detail, mList);
        mContext = context;
        mImageLoaderHelper = imageLoaderHelper;

    }

    @Override
    protected void convert(BaseViewHolder helper, BlockStoreListResponse item) {
        if (item == null) return;
        helper.addOnClickListener(R.id.adapter_fair_more);
        helper.setText(R.id.adapter_fair_sign, "店铺");
        RecyclerView recyclerView = helper.getView(R.id.adapter_fair_content);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        BlockDetailStoreItemAdapter itemAdapter = new BlockDetailStoreItemAdapter(item.list, mContext, mImageLoaderHelper);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.setOnItemClickListener((adapter, view, position) -> {
            BlockStoreListResponse.ListBean bean  = (BlockStoreListResponse.ListBean) adapter.getItem(position);
            if (bean != null) {
                mContext.startActivity(ShopProductDetailActivity.getActivityDetailIntent(mContext, bean.id));
            }});
    }
}
