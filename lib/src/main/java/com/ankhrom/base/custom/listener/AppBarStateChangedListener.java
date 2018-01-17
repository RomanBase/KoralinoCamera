package com.ankhrom.base.custom.listener;

import android.support.design.widget.AppBarLayout;

public abstract class AppBarStateChangedListener implements AppBarLayout.OnOffsetChangedListener {

    public enum AppBarState {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private AppBarState mCurrentState = AppBarState.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != AppBarState.EXPANDED) {
                onStateChanged(appBarLayout, AppBarState.EXPANDED);
            }
            mCurrentState = AppBarState.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != AppBarState.COLLAPSED) {
                onStateChanged(appBarLayout, AppBarState.COLLAPSED);
            }
            mCurrentState = AppBarState.COLLAPSED;
        } else {
            if (mCurrentState != AppBarState.IDLE) {
                onStateChanged(appBarLayout, AppBarState.IDLE);
            }
            mCurrentState = AppBarState.IDLE;
        }
    }

    public void register(AppBarLayout appBarLayout) {

        appBarLayout.addOnOffsetChangedListener(this);
    }

    public void unregister(AppBarLayout appBarLayout) {

        appBarLayout.removeOnOffsetChangedListener(this);
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, AppBarState state);
}
