package com.ankhrom.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ankhrom.base.common.statics.ScreenHelper;
import com.ankhrom.base.common.statics.StringHelper;
import com.ankhrom.base.interfaces.ActivityEventListener;
import com.ankhrom.base.interfaces.OnActivityStateChangedListener;
import com.ankhrom.base.interfaces.viewmodel.CloseableViewModel;
import com.ankhrom.base.interfaces.viewmodel.MenuItemableViewModel;
import com.ankhrom.base.interfaces.viewmodel.OnBaseChangedListener;
import com.ankhrom.base.interfaces.viewmodel.SearchableViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModelNavigation;
import com.ankhrom.base.interfaces.viewmodel.ViewModelObserver;
import com.ankhrom.base.model.ToolbarItemModel;
import com.ankhrom.base.viewmodel.BaseViewModelObserver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements OnBaseChangedListener {

    private List<OnActivityStateChangedListener> stateListeners;
    private ViewModelObserver mvm;

    protected Toolbar toolbar;
    protected T binding;
    protected String title;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!onPreInit()) {
            return;
        }

        Base.log(BuildConfig.VERSION_NAME, String.format(Locale.US, "(%d)", BuildConfig.VERSION_CODE), Build.VERSION.SDK_INT);

        int appTheme = getAppTheme();
        if (appTheme > 0) {
            setTheme(appTheme);
        }

        onGlideInit(Glide.get(this));

        try {
            onCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        onPostInit(savedInstanceState, binding);

        if (mvm instanceof BaseViewModelObserver) {
            ((BaseViewModelObserver) mvm).onInitialized();
        }
    }

    @CallSuper
    void onCreate() {

        binding = DataBindingUtil.setContentView(this, getMainLayout());

        mvm = init();

        if (mvm == null) {
            throw new RuntimeException("Failed to init ViewModelObserver");
        }

        if (mvm instanceof BaseViewModelObserver) {
            ((BaseViewModelObserver) mvm).setRootViewContainer((ViewGroup) binding.getRoot().getRootView());
            ((BaseViewModelObserver) mvm).setOnBaseChangedListener(this);
            ((BaseViewModelObserver) mvm).onInitialized();
        }

        toolbar = getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setToolbarTitle(getInitialPageTitle());

            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                onToolbarSetup(toolbar, bar);
            }
        }

        invalidateOptionsMenu();
    }

    protected boolean onPreInit() {
        return true;
    }

    protected abstract ViewModelObserver init();

    protected void onPostInit(Bundle state, T binding) {

    }

    protected abstract int getMainLayout();

    protected abstract Toolbar getToolbar();

    @StyleRes
    protected int getAppTheme() {
        return 0;
    }

    protected void onToolbarSetup(Toolbar toolbar, ActionBar bar) {

        ViewModel cvm = mvm.getCurrentViewModel();
        if (cvm instanceof CloseableViewModel) {
            boolean closeable = ((CloseableViewModel) cvm).isCloseable();
            bar.setDisplayHomeAsUpEnabled(closeable);
            bar.setHomeButtonEnabled(closeable);
        } else {
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setHomeButtonEnabled(false);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected String getInitialPageTitle() {

        return getString(R.string.app_name);
    }

    protected void onGlideInit(Glide glide) {

        glide.setMemoryCategory(MemoryCategory.HIGH);
    }

    public void addActivityStateListener(OnActivityStateChangedListener listener) {

        if (stateListeners == null) {
            stateListeners = new ArrayList<>();
        }

        stateListeners.add(listener);
    }

    public void removeActivityStateListener(OnActivityStateChangedListener listener) {

        if (stateListeners == null) {
            return;
        }

        stateListeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends ViewModelObserver> T getViewModelObserver() {

        return (T) mvm;
    }

    public ViewModelNavigation getNavigation() {

        if (mvm == null) {
            return null;
        }

        return mvm.getNavigation();
    }

    public ActivityEventListener getEventListener() {

        if (mvm == null) {
            return null;
        }

        return mvm.getEventListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ActivityEventListener eventListener = getEventListener();
        if (eventListener != null) {
            eventListener.onResume();
        }

        if (stateListeners != null) {
            for (OnActivityStateChangedListener listener : stateListeners) {
                listener.onActivityResume();
            }
        }
    }

    @Override
    protected void onPause() {

        ActivityEventListener eventListener = getEventListener();
        if (eventListener != null) {
            eventListener.onPause();
        }

        if (stateListeners != null) {
            for (OnActivityStateChangedListener listener : stateListeners) {
                listener.onActivityPause();
            }
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (stateListeners != null) {
            for (OnActivityStateChangedListener listener : stateListeners) {
                listener.onActivityDestroy();
            }
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ActivityEventListener eventListener = getEventListener();
        if (eventListener != null) {
            if (!eventListener.onBaseActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (ScreenHelper.isSoftKeyboardVisible(this, binding.getRoot())) {
            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                bar.invalidateOptionsMenu();
            }

            ScreenHelper.hideSoftKeyboard(this);

            return;
        }

        ActivityEventListener eventListener = getEventListener();
        if (eventListener != null) {
            if (!eventListener.onBaseBackPressed()) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        ViewModel currentViewModel = mvm.getCurrentViewModel();

        if (currentViewModel instanceof SearchableViewModel) {

            final SearchableViewModel viewModel = (SearchableViewModel) currentViewModel;
            final SearchView sv = new SearchView(this);
            final MenuItem search = menu.add(R.string.search_label);

            search.setActionView(sv);

            int icon = getSearchIcon();
            if (icon > 0) {
                search.setIcon(icon);
            }
            search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    viewModel.performSearch(s, false);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return viewModel.performSearch(s, false);
                }
            });
        }

        if (currentViewModel instanceof MenuItemableViewModel) {

            final MenuItemableViewModel viewModel = (MenuItemableViewModel) currentViewModel;

            ToolbarItemModel[] menuItemModels = viewModel.getMenuItems();
            if (menuItemModels == null) {
                return true;
            }

            for (final ToolbarItemModel model : menuItemModels) {
                final MenuItem item = menu.add(model.getTitle());

                if (model.getImageResourceId() > 0) {
                    item.setIcon(model.getImageResourceId());
                }

                if (model.isShowAsAction()) {
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }

                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        model.onClick(menuItem);
                        return true;
                    }
                });
            }
        }

        return true;
    }

    @Override
    public void onViewModelChanged(ViewModel viewModel) {

        if (viewModel instanceof CloseableViewModel) {
            toggleToolbarNavigation(((CloseableViewModel) viewModel).isCloseable());
        } else {
            toggleToolbarNavigation(false);
        }

        title = viewModel.getTitle();
        setToolbarTitle(title);
    }

    protected void setToolbarTitle(String title) {

        if (StringHelper.isEmpty(title)) {
            title = getString(R.string.empty);
        }

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(title.toUpperCase());
            invalidateOptionsMenu();
        }
    }

    protected void toggleToolbarNavigation(boolean enable) {

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(enable);
            bar.setHomeButtonEnabled(enable);
        }
    }

    protected void toggleToolbarVisibility(boolean visible, boolean animate) {

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setShowHideAnimationEnabled(animate);
            if (visible) {
                bar.show();
            } else {
                bar.hide();
            }
        }
    }

    @Override
    public void onScreenOptionsChanged(int options) {

    }

    @Override
    public void onReceiveArgs(int requestCode, Object[] args) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        ViewModel vm = getViewModelObserver().getCurrentViewModel();

        if (vm != null) {
            Fragment fragment = vm.getFragment();

            if (fragment != null) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected int getSearchIcon() {
        return 0;
    }

    /**
     * rotate screen to portrait orientation
     */
    protected void setScreenOrientationPortrait() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * rotate screen to portrait orientation with reversed portrait rotation possibility
     */
    protected void setScreenOrientationSensorPortrait() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    /**
     * rotate screen to landscape orientation
     */
    protected void setScreenOrientationLandscape() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * rotate screen to landscape orientation with reversed landscepe rotation possibility
     */
    protected void setScreenOrientationSensorLandscape() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    /**
     * enable all screen orientatiions based on sensor
     */
    protected void setScreenOrientationSensor() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    /**
     * remove status bar and sets activity to whole screen
     */
    protected void setFullScreen() {

        if (Build.VERSION.SDK_INT < 16) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    /**
     * hides devices soft keys
     */
    protected void hideVirtualUI() {

        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    /**
     * sets screen brightness
     *
     * @param reqScreenBrightness 0.0 - 1.0
     */
    protected void setScreenBrightness(float reqScreenBrightness) {

        final Window window = getWindow();
        final WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
        windowLayoutParams.screenBrightness = reqScreenBrightness;
        window.setAttributes(windowLayoutParams);
    }

    /**
     * prevent device to go sleep or dim display
     */
    protected void preventSleep() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected boolean isLayoutLandscape() {

        return Configuration.ORIENTATION_LANDSCAPE == getScreenOrientation();
    }

    protected int getScreenOrientation() {

        return getResources().getConfiguration().orientation;
    }

    protected void disableSreenOrientation(boolean forceSensor) {

        switch (getScreenOrientation()) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation(forceSensor ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation(forceSensor ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
        }
    }

    protected void enableScreenSensor() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
