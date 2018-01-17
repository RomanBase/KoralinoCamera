package com.ankhrom.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ankhrom.base.animators.BaseAnim;
import com.ankhrom.base.common.statics.ViewHelper;
import com.ankhrom.base.interfaces.viewmodel.CloseableViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModel;

public abstract class BaseActivityDrawer<T extends ViewDataBinding> extends BaseActivity<T> {

    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate() {
        super.onCreate();

        drawerLayout = ViewHelper.findChildView(DrawerLayout.class, binding.getRoot());

        if (drawerLayout == null) {
            throw new RuntimeException("Drawer layout not found !");
        }

        int ds = getDrawerShadow();
        if (ds > 0) {
            drawerLayout.setDrawerShadow(ds, GravityCompat.START);
        }
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                getToolbar(),
                R.string.open_menu,  /* "open drawer" description */
                R.string.close_menu  /* "close drawer" description */
        );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setToolbarNavigationClickListener(navigationIconClickListener);

        toggleToolbarNavigation(true, false);
    }

    private final View.OnClickListener navigationIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!drawerToggle.isDrawerIndicatorEnabled()) {
                onBackPressed();
            }
        }
    };

    @Override
    protected void onToolbarSetup(Toolbar toolbar, ActionBar bar) {

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (drawerToggle == null) {
            return;
        }

        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (drawerToggle == null) {
            return;
        }

        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return drawerToggle != null && drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewModelChanged(ViewModel viewModel) {

        drawerLayout.closeDrawers();

        if (viewModel instanceof CloseableViewModel) {
            toggleToolbarNavigation(!((CloseableViewModel) viewModel).isCloseable(), true);
        } else {
            toggleToolbarNavigation(true);
        }

        title = viewModel.getTitle();
        super.setToolbarTitle(title);
    }

    @Override
    protected void toggleToolbarNavigation(boolean enable) {

        toggleToolbarNavigation(enable, true);
    }

    protected void toggleToolbarNavigation(boolean enable, boolean animate) {

        if (enable == drawerToggle.isDrawerIndicatorEnabled()) {
            return;
        }

        drawerLayout.setDrawerLockMode(enable ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        if (animate) {
            float[] values = enable ? new float[]{1.0f, 0.0f} : new float[]{0.0f, 1.0f}; //1.0 burger, 0.0 arrow
            ValueAnimator animator = new ValueAnimator();
            animator.setFloatValues(values);
            animator.setDuration(BaseAnim.DURATION);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = (float) animation.getAnimatedValue();
                    drawerToggle.onDrawerSlide(drawerLayout, t);
                }
            });
            if (enable) {
                drawerToggle.setDrawerIndicatorEnabled(true);
            } else {
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        drawerToggle.setDrawerIndicatorEnabled(false);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        drawerToggle.setDrawerIndicatorEnabled(false);
                    }
                });
            }
            animator.start();
        } else {
            drawerToggle.setDrawerIndicatorEnabled(enable);
        }
    }

    public void setToolbarNavigationEnable(boolean enable) {

        super.toggleToolbarNavigation(enable);
    }

    protected int getDrawerShadow() {

        return R.drawable.drawer_shadow;
    }
}
