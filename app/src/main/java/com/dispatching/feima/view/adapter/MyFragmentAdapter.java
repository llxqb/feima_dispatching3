package com.dispatching.feima.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by helei on 2017/5/3.
 * MyFragmentAdapter
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mList;
    private  String[] modules;
    public MyFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList,String[] array) {
        super(fm);
        mList = fragmentList;
        modules =array;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return modules[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }


}