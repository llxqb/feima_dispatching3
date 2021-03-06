package com.banshengyuan.feima.view.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.MusicListResponse;
import com.banshengyuan.feima.help.GlideHelper.ImageLoaderHelper;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.example.mylibrary.adapter.BaseViewHolder;

import java.util.List;


public class MagicMusicAdapter extends BaseQuickAdapter<MusicListResponse.FairBean, BaseViewHolder> {
    private final Context mContext;
    private final ImageLoaderHelper mImageLoaderHelper;

    public MagicMusicAdapter(List<MusicListResponse.FairBean> mList, Context context, ImageLoaderHelper imageLoaderHelper) {
        super(R.layout.adapter_magic_music, mList);
        mContext =  context;
        mImageLoaderHelper =  imageLoaderHelper;
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicListResponse.FairBean item) {
        ImageView imageView = helper.getView(R.id.adapter_magic_music_background);
        mImageLoaderHelper.displayImage(mContext,item.cover_img,imageView);
    }

}
