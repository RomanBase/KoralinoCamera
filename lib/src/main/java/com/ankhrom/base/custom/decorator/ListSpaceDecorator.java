package com.ankhrom.base.custom.decorator;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ListSpaceDecorator extends RecyclerView.ItemDecoration {

    private final int divider;
    private final int sidePadding;

    private boolean includeTopEdge;
    private boolean includeBottomEdge;

    public ListSpaceDecorator(int spacing) {
        divider = spacing;
        sidePadding = spacing;
    }

    public ListSpaceDecorator(int spacing, boolean includeEdges) {
        divider = spacing;
        sidePadding = spacing;
        includeTopEdge = includeEdges;
        includeBottomEdge = includeEdges;
    }

    public ListSpaceDecorator includeTopEdge(boolean includeEdge) {

        this.includeTopEdge = includeEdge;

        return this;
    }

    public ListSpaceDecorator includeBottomEdge(boolean includeEdge) {

        this.includeBottomEdge = includeEdge;

        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int index = parent.getChildAdapterPosition(view);

        if (includeTopEdge && index == 0) {
            outRect.top = divider;
        }

        outRect.left = sidePadding;
        outRect.right = sidePadding;

        if (includeBottomEdge) {
            outRect.bottom = divider;
        } else if (index < parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = divider;
        }
    }
}
