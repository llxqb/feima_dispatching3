package com.banshengyuan.feima.view.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.banshengyuan.feima.R;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.example.mylibrary.adapter.BaseViewHolder;

import java.util.List;


public class CollectionShopAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
    private final Context mContext;

    public CollectionShopAdapter(List<Integer> mList, Context context) {
        super(R.layout.adapter_fair_detail_shop, mList);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Integer item) {
        if (item == null) return;
        ImageView imageView = helper.getView(R.id.adapter_store_icon);
        imageView.setImageResource(item);
    }

}
