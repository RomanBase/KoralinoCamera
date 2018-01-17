package com.ankhrom.base.custom.builder;

import android.support.design.widget.AppBarLayout;

public class ScrollFlagsBuilder {

    private int flags;

    public ScrollFlagsBuilder scroll() {

        flags |= AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
        return this;
    }

    public ScrollFlagsBuilder enterAlways() {

        flags |= AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
        return this;
    }

    public ScrollFlagsBuilder enterAlwaysCollapsed() {

        flags |= AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED;
        return this;
    }

    public ScrollFlagsBuilder exitUntilCollapsed() {

        flags |= AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED;
        return this;
    }

    public ScrollFlagsBuilder snap() {

        flags |= AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;
        return this;
    }

    public int build() {

        return flags;
    }

}
