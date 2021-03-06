package com.banshengyuan.feima.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mylibrary.entity.MultiItemEntity;

import java.util.List;

/**
 * Created by lei.he on 2017/6/28.
 * MyOrdersResponse
 */

public class MyOrdersResponse implements Parcelable {


    /**
     * list : [{"order_sn":"Z2017120106111625062","store_name":"test的店铺","store_id":8,"product":[{"id":3,"name":"测试商品","goods_id":1,"price":12000,"number":1,"cover_img":"http://ssapp.jixuanjk.com/upload/example.jpg"},{"id":6,"name":"测试商品","goods_id":1,"price":15000,"number":1,"cover_img":"http://ssapp.jixuanjk.com/upload/example.jpg"},{"id":7,"name":"测试商品","goods_id":1,"price":15000,"number":1,"cover_img":"http://ssapp.jixuanjk.com/upload/example.jpg"}],"pay_status":1,"pay_status_name":"等待买家付款","total_fee":42000,"freight":0}]
     * current_page : 1
     * has_next_page : true
     */

    private int current_page;
    private boolean has_next_page;
    private List<ListBean> list;

    protected MyOrdersResponse(Parcel in) {
        current_page = in.readInt();
        has_next_page = in.readByte() != 0;
        list = in.createTypedArrayList(ListBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(current_page);
        dest.writeByte((byte) (has_next_page ? 1 : 0));
        dest.writeTypedList(list);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyOrdersResponse> CREATOR = new Creator<MyOrdersResponse>() {
        @Override
        public MyOrdersResponse createFromParcel(Parcel in) {
            return new MyOrdersResponse(in);
        }

        @Override
        public MyOrdersResponse[] newArray(int size) {
            return new MyOrdersResponse[size];
        }
    };

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public boolean isHas_next_page() {
        return has_next_page;
    }

    public void setHas_next_page(boolean has_next_page) {
        this.has_next_page = has_next_page;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Parcelable ,MultiItemEntity {
        public static final int LAYOUT_ONE = 1;
        public static final int LAYOUT_TWO = 2;

        @Override
        public int getItemType() {
            if(getOrder_type()==3){
                return LAYOUT_TWO;
            }else{
                return LAYOUT_ONE;
            }
        }
        /**
         * order_sn : Z2017120106111625062
         * store_name : test的店铺
         * store_id : 8
         * cover_image
         * product : [{"id":3,"name":"测试商品","goods_id":1,"price":12000,"number":1,"cover_img":"http://ssapp.jixuanjk.com/upload/example.jpg"},{"id":6,"name":"测试商品","goods_id":1,"price":15000,"number":1,"cover_img":"http://ssapp.jixuanjk.com/upload/example.jpg"},{"id":7,"name":"测试商品","goods_id":1,"price":15000,"number":1,"cover_img":"http://ssapp.jixuanjk.com/upload/example.jpg"}]
         * pay_status : 1
         * pay_status_name : 等待买家付款
         * total_fee : 42000
         * freight : 0
         * is_evaluate  1、已评价 0、未评价
         */

        private String order_sn;
        private String store_name;
        private int store_id;
        private String cover_image;
        private int pay_status;
        private String pay_status_name;
        private int total_fee;
        private int freight;
        private int order_type;
        private int is_evaluate;
        private List<ProductBean> product;

        protected ListBean(Parcel in) {
            order_sn = in.readString();
            store_name = in.readString();
            cover_image = in.readString();
            store_id = in.readInt();
            pay_status = in.readInt();
            pay_status_name = in.readString();
            total_fee = in.readInt();
            freight = in.readInt();
            is_evaluate = in.readInt();
            product = in.createTypedArrayList(ProductBean.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(order_sn);
            dest.writeString(store_name);
            dest.writeString(cover_image);
            dest.writeInt(store_id);
            dest.writeInt(pay_status);
            dest.writeString(pay_status_name);
            dest.writeInt(total_fee);
            dest.writeInt(freight);
            dest.writeInt(is_evaluate);
            dest.writeTypedList(product);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ListBean> CREATOR = new Creator<ListBean>() {
            @Override
            public ListBean createFromParcel(Parcel in) {
                return new ListBean(in);
            }

            @Override
            public ListBean[] newArray(int size) {
                return new ListBean[size];
            }
        };

        public int getOrder_type() {
            return order_type;
        }

        public void setOrder_type(int order_type) {
            this.order_type = order_type;
        }

        public String getOrder_sn() {
            return order_sn;
        }

        public void setOrder_sn(String order_sn) {
            this.order_sn = order_sn;
        }

        public String getStore_name() {
            return store_name;
        }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
        }

        public int getStore_id() {
            return store_id;
        }

        public void setStore_id(int store_id) {
            this.store_id = store_id;
        }

        public int getPay_status() {
            return pay_status;
        }

        public void setPay_status(int pay_status) {
            this.pay_status = pay_status;
        }

        public String getPay_status_name() {
            return pay_status_name;
        }

        public void setPay_status_name(String pay_status_name) {
            this.pay_status_name = pay_status_name;
        }

        public int getTotal_fee() {
            return total_fee;
        }

        public void setTotal_fee(int total_fee) {
            this.total_fee = total_fee;
        }

        public int getFreight() {
            return freight;
        }

        public void setFreight(int freight) {
            this.freight = freight;
        }

        public int getIs_evaluate() {
            return is_evaluate;
        }

        public void setIs_evaluate(int is_evaluate) {
            this.is_evaluate = is_evaluate;
        }

        public List<ProductBean> getProduct() {
            return product;
        }

        public String getCover_image() {
            return cover_image;
        }

        public void setCover_image(String cover_image) {
            this.cover_image = cover_image;
        }

        public void setProduct(List<ProductBean> product) {
            this.product = product;
        }

        public static class ProductBean implements Parcelable {
            /**
             * id : 3
             * name : 测试商品
             * goods_id : 1
             * price : 12000
             * number : 1
             * cover_img : http://ssapp.jixuanjk.com/upload/example.jpg
             */

            private int id;
            private String name;
            private int goods_id;
            private int price;
            private int number;
            private String cover_img;
            private String content;//content 用来订单评价用

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public ProductBean(Parcel in) {
                id = in.readInt();
                name = in.readString();
                goods_id = in.readInt();
                price = in.readInt();
                number = in.readInt();
                cover_img = in.readString();
            }
            public ProductBean() {
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(id);
                dest.writeString(name);
                dest.writeInt(goods_id);
                dest.writeInt(price);
                dest.writeInt(number);
                dest.writeString(cover_img);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<ProductBean> CREATOR = new Creator<ProductBean>() {
                @Override
                public ProductBean createFromParcel(Parcel in) {
                    return new ProductBean(in);
                }

                @Override
                public ProductBean[] newArray(int size) {
                    return new ProductBean[size];
                }
            };

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(int goods_id) {
                this.goods_id = goods_id;
            }

            public int getPrice() {
                return price;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public String getCover_img() {
                return cover_img;
            }

            public void setCover_img(String cover_img) {
                this.cover_img = cover_img;
            }
        }
    }
}
