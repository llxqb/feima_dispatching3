package com.banshengyuan.feima.view.PresenterControl;

import com.banshengyuan.feima.entity.BlockDetailFairListResponse;
import com.banshengyuan.feima.entity.BlockDetailProductListResponse;
import com.banshengyuan.feima.entity.BlockDetailResponse;
import com.banshengyuan.feima.entity.BlockStoreListResponse;
import com.banshengyuan.feima.entity.FairUnderLineResponse;

/**
 * Created by lei.he on 2017/6/28.
 * AddAddressControl
 */

public class UnderLineFairControl {
    public interface UnderLineFairView extends LoadDataView {
        void getBlockDetailSuccess(BlockDetailResponse response);

        void getBlockDetailFail(String des);

        void getBlockFairListSuccess(BlockDetailFairListResponse response);

        void getBlockFairListFail(String des);

        void getStoreListSuccess(BlockStoreListResponse response);

        void getStoreListFail();

        void getProductListSuccess(BlockDetailProductListResponse response);

        void getProductListFail(String des);

        void loadError(Throwable throwable);

        void getFairUnderLineSuccess(FairUnderLineResponse fairUnderLineResponse);

        void getFairUnderLineFail();
    }

    public interface PresenterUnderLineFair extends Presenter<UnderLineFairView> {
        void requestBlockDetail(Integer blockId);

        void requestBlockFairList(Integer blockId, Integer page, Integer pageSize);

        void requestBlockStoreList(Integer blockId, Integer page, Integer pageSize);

        void requestBlockProductList(Integer blockId, Integer page, Integer pageSize);

        void requestFairUnderLine(double longitude, double latitude, Integer page, Integer pageSize);
    }
}
