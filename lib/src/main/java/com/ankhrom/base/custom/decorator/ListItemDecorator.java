package com.ankhrom.base.custom.decorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ListItemDecorator extends RecyclerView.ItemDecoration {

    private final Drawable divider;

    private boolean includeBottomEdge;

    public ListItemDecorator(Context context, @DrawableRes int resId) {
        divider = ContextCompat.getDrawable(context, resId);
    }

    public ListItemDecorator(Drawable drawable) {
        divider = drawable;
    }

    public ListItemDecorator includeBottomEdge(boolean includeEdge) {

        this.includeBottomEdge = includeEdge;

        return this;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (includeBottomEdge) {
            outRect.bottom = divider.getIntrinsicHeight();
        } else if (parent.getChildAdapterPosition(view) <= parent.getAdapter().getItemCount()) {
            outRect.bottom = divider.getIntrinsicHeight();
        }
    }
}
