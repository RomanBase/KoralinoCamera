package com.ankhrom.base.common.statics;

import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public final class ViewHelper {

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T findParentView(Class<T> clazz, View view) {

        if (view == null) {
            return null;
        }

        if (view.getClass().isAssignableFrom(clazz)) {
            return (T) view;
        }

        return findParentView(clazz, (View) view.getParent());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T findChildView(Class<T> clazz, View root) {

        if (root.getClass().isAssignableFrom(clazz)) {
            return (T) root;
        }

        T view = null;

        if (root instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup) root;
            int childCount = layout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = layout.getChildAt(i);
                if (childView.getClass().isAssignableFrom(clazz)) {
                    view = (T) childView;
                    break;
                } else {
                    view = findChildView(clazz, childView);
                    if (view != null) {
                        break;
                    }
                }
            }
        }

        return view;
    }

    public static RectF getRect(View view, float offset) {

        int[] position = new int[2];

        view.getLocationOnScreen(position);
        position[1] -= offset;

        return new RectF(position[0], position[1], position[0] + view.getWidth(), position[1] + view.getHeight());
    }

    public static int[] getPosition(View view) {

        int[] position = new int[2];

        view.getLocationOnScreen(position);

        return position;
    }

    public static void removeView(View view) {

        ViewParent parent = view.getParent();

        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }
}
