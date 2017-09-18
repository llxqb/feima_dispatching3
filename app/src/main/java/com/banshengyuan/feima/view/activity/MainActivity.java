package com.banshengyuan.feima.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.banshengyuan.feima.R;
import com.banshengyuan.feima.dagger.component.DaggerMainActivityComponent;
import com.banshengyuan.feima.dagger.module.MainActivityModule;
import com.banshengyuan.feima.help.BottomNavigationViewHelper;
import com.banshengyuan.feima.help.DialogFactory;
import com.banshengyuan.feima.view.PresenterControl.MainControl;
import com.banshengyuan.feima.view.adapter.MyFragmentAdapter;
import com.banshengyuan.feima.view.fragment.CommonDialog;
import com.banshengyuan.feima.view.fragment.CompletedOrderFragment;
import com.banshengyuan.feima.view.fragment.PendingOrderFragment;
import com.banshengyuan.feima.view.fragment.SendingOrderFragment;
import com.banshengyuan.feima.view.fragment.ShoppingCardFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainControl.MainView, BottomNavigationView.OnNavigationItemSelectedListener, CommonDialog.CommonDialogListener {

    public static Intent getMainIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static final Integer DIALOG_TYPE_EXIT_OK = 1;

    @BindView(R.id.view_swapper)
    ViewPager mViewSwapper;
    @BindView(R.id.view_bottom_navigation)
    BottomNavigationView mViewBottomNavigation;

    @Inject
    MainControl.PresenterMain mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeInjector();
        initView();
    }

    @Override
    public void showLoading(String msg) {
        showDialogLoading(msg);
    }

    @Override
    public void dismissLoading() {
        dismissDialogLoading();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void showToast(String message) {
        showBaseToast(message);
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void initView() {
        //默认停用滑动效果
        BottomNavigationViewHelper.disableShiftMode(mViewBottomNavigation);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(PendingOrderFragment.newInstance());
        fragments.add(SendingOrderFragment.newInstance());
        fragments.add(CompletedOrderFragment.newInstance());
        fragments.add(ShoppingCardFragment.newInstance());
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
        mViewSwapper.setOffscreenPageLimit(fragments.size());
        mViewSwapper.setAdapter(adapter);
        mViewBottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_one:
                mViewSwapper.setCurrentItem(0);
                break;
            case R.id.action_two:
                mViewSwapper.setCurrentItem(1);
                break;
            case R.id.action_three:
                mViewSwapper.setCurrentItem(2);
                break;
            case R.id.action_four:
                mViewSwapper.setCurrentItem(3);
                break;
        }
        return true;
    }

    @Override
    public void commonDialogBtnOkListener(int type, int position) {
        switch (type) {
            case 1:
                finish();
                System.exit(0);
                break;
        }
    }

    private void showDialog() {
        CommonDialog commonDialog = CommonDialog.newInstance();
        commonDialog.setContent(getString(R.string.main_exit));
        commonDialog.setListener(this, DIALOG_TYPE_EXIT_OK);
        DialogFactory.showDialogFragment(getSupportFragmentManager(), commonDialog, CommonDialog.TAG);
    }

    private void initializeInjector() {
        DaggerMainActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .mainActivityModule(new MainActivityModule(MainActivity.this, this))
                .build().inject(this);
    }

}