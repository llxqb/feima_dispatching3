package com.dispatching.feima.dagger.component;

import android.support.v7.app.AppCompatActivity;

import com.dispatching.feima.dagger.PerActivity;
import com.dispatching.feima.dagger.module.ShopListActivityModule;
import com.dispatching.feima.view.PresenterControl.ShopListControl;
import com.dispatching.feima.view.activity.ShopListActivity;

import dagger.Component;

/**
 * Created by helei on 2017/4/26.
 * LoginActivityComponent
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ShopListActivityModule.class)
public interface ShopListActivityComponent {
    AppCompatActivity activity();
    ShopListControl.ShopListView view();
    void inject(ShopListActivity activity);
}