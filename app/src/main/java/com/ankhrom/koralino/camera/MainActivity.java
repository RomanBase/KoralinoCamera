package com.ankhrom.koralino.camera;

import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.ankhrom.base.Base;
import com.ankhrom.base.BaseActivity;
import com.ankhrom.base.BaseFactory;
import com.ankhrom.base.interfaces.viewmodel.ViewModelObserver;
import com.ankhrom.base.viewmodel.BaseViewModelObserver;
import com.ankhrom.koralino.camera.databinding.ActivityMainBinding;
import com.ankhrom.koralino.camera.viewmodel.CameraViewModel;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    public static MainActivity ref;

    @Override
    protected boolean onPreInit() {

        Base.debug = true;
        ref = this;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        return super.onPreInit();
    }

    @Override
    protected ViewModelObserver init() {

        return BaseViewModelObserver.with(BaseFactory.init(this), R.id.root_container)
                .setScreenRootContainerId(R.id.main_container)
                .setViewModel(CameraViewModel.class)
                .build();
    }

    @Override
    protected int getMainLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }
}
