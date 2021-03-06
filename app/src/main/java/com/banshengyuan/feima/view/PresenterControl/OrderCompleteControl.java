package com.banshengyuan.feima.view.PresenterControl;

import com.banshengyuan.feima.entity.MyOrdersResponse;

/**
 * Created by lei.he on 2017/6/28.
 * OrderCompleteControl
 */

public class OrderCompleteControl {
    public interface OrderCompleteView extends LoadDataView {
        void loadFail(Throwable throwable);

        void getMyOrderListSuccess(MyOrdersResponse response);

        void getDeleteOrderSuccess();
    }

    public interface PresenterOrderComplete extends Presenter<OrderCompleteView> {
        void requestMyOrderList(Integer pageNo, Integer pageSize, String status, String token);

        void requestDeleteOrder(String order_sn ,String token);
    }
}
