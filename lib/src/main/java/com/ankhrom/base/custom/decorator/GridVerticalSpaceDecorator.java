package com.ankhrom.base.custom.decorator;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridVerticalSpaceDecorator extends RecyclerView.ItemDecoration {

    private int count;

    private int rowSpacing;
    private int columnSpacing;

    private boolean includeTopEdge;
    private boolean includeSideEdges;

    public GridVerticalSpaceDecorator(int count) {

        this.count = count;
    }

    public GridVerticalSpaceDecorator count(int count) {

        this.count = count;

        return this;
    }

    public GridVerticalSpaceDecorator spacing(int spacing) {

        this.rowSpacing = spacing;
        this.columnSpacing = spacing;

        return this;
    }

    public GridVerticalSpaceDecorator spacing(int rowSpacing, int columnSpacing) {

        this.rowSpacing = rowSpacing;
        this.columnSpacing = columnSpacing;

        return this;
    }

    public GridVerticalSpaceDecorator includeTopEdge(boolean includeEdge) {

        this.includeTopEdge = includeEdge;

        return this;
    }

    public GridVerticalSpaceDecorator includeSideEdges(boolean includeEdges) {

        this.includeSideEdges = includeEdges;

        return this;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int index = parent.getChildAdapterPosition(view);
        int column = index % count;

        if (includeSideEdges) {
            outRect.left = columnSpacing - column * columnSpacing / count;
            outRect.right = (column + 1) * columnSpacing / count;
        } else {
            outRect.left = column * columnSpacing / count;
            outRect.right = columnSpacing - (column + 1) * columnSpacing / count;
        }

        if (includeTopEdge && index < count) {
            outRect.top = rowSpacing;
        }

        outRect.bottom = rowSpacing;
    }
}
