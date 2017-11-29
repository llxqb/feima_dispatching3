package com.banshengyuan.feima.view.PresenterControl;

import com.banshengyuan.feima.entity.SearchResultResponse;

/**
 * Created by helei on 2017/4/27.
 * LoginControl
 */

public class SearchControl {
    public interface SearchView extends LoadDataView {
        void getSearchFairListSuccess(SearchResultResponse response);
        void getSearchStoreListSuccess(SearchResultResponse response);
        void getSearchStreetListSuccess(SearchResultResponse response);
        void getSearchProductListSuccess(SearchResultResponse response);
        void getSearchFairListFail(String des);
        void getSearchStoreListFail(String des);
        void getSearchStreetListFail(String des);
        void getSearchProductListFail(String des);
    }

    public interface PresenterSearch extends Presenter<SearchView> {
        void requestSearchFairList(String searchName,String searchType);
        void requestSearchProductList(String searchName,String searchType);
        void requestSearchStoreList(String searchName,String searchType);
        void requestSearchStreetList(String searchName,String searchType);
    }

}
