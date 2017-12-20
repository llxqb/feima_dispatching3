package com.banshengyuan.feima.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.entity.MyCoupleResponse;
import com.banshengyuan.feima.utils.TimeUtil;
import com.banshengyuan.feima.utils.ValueUtil;
import com.example.mylibrary.adapter.BaseQuickAdapter;
import com.example.mylibrary.adapter.BaseViewHolder;

import java.util.List;


public class ChoiceCouponAdapter extends BaseQuickAdapter<MyCoupleResponse.ListBean, BaseViewHolder> {
    private final Context mContext;

    public ChoiceCouponAdapter(List<MyCoupleResponse.ListBean> mList, Context context) {
        super(R.layout.adapter_coupon_item, mList);
        mContext = context;

    }

    @Override
    protected void convert(BaseViewHolder helper, MyCoupleResponse.ListBean item) {
        helper.addOnClickListener(R.id.adapter_couple_check);
        helper.setVisible(R.id.adapter_couple_check, true);
        ImageView imageView = helper.getView(R.id.adapter_coupon_image);
        imageView.setImageResource(R.mipmap.nouse_couple_bg);

        helper.setText(R.id.adapter_couple_name, item.getName());
        helper.setText(R.id.adapter_couple_usetime, "使用期限：" + TimeUtil.transferLongToDate(TimeUtil.TIME_MMDD_CH, (long) item.getExpire_start_time()) +
                "-" + TimeUtil.transferLongToDate(TimeUtil.TIME_MMDD_CH, (long) item.getExpire_end_time()));
        helper.setText(R.id.adapter_couple_limit, "仅可在店铺" + item.getStore_name() + "中使用");

        if (item.getType() == 1 && !TextUtils.isEmpty(item.getValue())) {
            //券类型 1满减 2折扣
            helper.setText(R.id.adapter_couple_price, "￥" + ValueUtil.formatAmount(Double.parseDouble(item.getValue().split(",")[1])));
        } else if (item.getType() == 2 && !TextUtils.isEmpty(item.getValue())) {
            if (!TextUtils.isEmpty(item.getValue().split(",")[1])) {
                helper.setText(R.id.adapter_couple_price, Double.parseDouble(item.getValue().split(",")[1]) * 10 + "折");
            }
        }
    }
}
