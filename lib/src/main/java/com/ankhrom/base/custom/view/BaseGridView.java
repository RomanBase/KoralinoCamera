package com.ankhrom.base.custom.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.ankhrom.base.R;
import com.ankhrom.base.custom.decorator.GridVerticalSpaceDecorator;

public class BaseGridView extends RecyclerView {

    private GridLayoutManager manager;
    private GridVerticalSpaceDecorator decorator;

    private int spanCount;
    private int spacing;

    public BaseGridView(Context context) {
        super(context);
        init(context, null);
    }

    public BaseGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseGridView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        boolean includeEdge;
        boolean includeTopEdge;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseGridView, 0, 0);
        try {
            spanCount = ta.getInteger(R.styleable.BaseGridView_spanCount, 2);
            spacing = ta.getDimensionPixelSize(R.styleable.BaseGridView_spacing, 0);
            includeEdge = ta.getBoolean(R.styleable.BaseGridView_includeEdge, true);
            includeTopEdge = ta.getBoolean(R.styleable.BaseGridView_includeTopEdge, true);
        } finally {
            ta.recycle();
        }

        setLayoutManager(manager = new GridLayoutManager(context, spanCount));
        addItemDecoration(decorator = new GridVerticalSpaceDecorator(spanCount).spacing(spacing).includeSideEdges(includeEdge).includeTopEdge(includeTopEdge));
    }

    public void modifyCount(int spanCount) {

        modify(spanCount, spacing);
    }

    public void modifySpacing(int spacing) {

        modify(spanCount, spacing);
    }

    public void modify(int spanCount, int spacing) {

        this.spanCount = spanCount;
        this.spacing = spacing;

        decorator.spacing(spacing).count(spanCount);
        manager.setSpanCount(spanCount);

        getAdapter().notifyItemRangeChanged(manager.findFirstVisibleItemPosition(), manager.findLastVisibleItemPosition());
    }
}
