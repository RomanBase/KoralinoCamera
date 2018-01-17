package com.ankhrom.base.animators;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.ankhrom.base.common.statics.ColorHelper;
import com.ankhrom.base.common.statics.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class PageAnimation extends AnimatorListenerAdapter {

    private final static long DURATION = 350;

    public boolean inProgress;

    private final List<Item> items;

    private long duration = DURATION;

    public PageAnimation() {

        items = new ArrayList<>();
    }

    public void setDuration(long duration) {

        this.duration = duration;
    }

    public void addItem(PageAnimation.Item item) {

        items.add(item);
    }

    public void startShowAnimation(@Nullable Animator.AnimatorListener listener) {

        setInitialState();

        final ValueAnimator animator = new ValueAnimator();
        animator.setDuration(duration);
        animator.setFloatValues(0.0f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animationStep((float) valueAnimator.getAnimatedValue());
            }
        });

        animator.addListener(this);
        if (listener != null) {
            animator.addListener(listener);
        }

        animator.start();
    }

    public void startHideAnimation(@Nullable Animator.AnimatorListener listener) {

        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(duration);
        animator.setFloatValues(1.0f, 0.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animationStep((float) valueAnimator.getAnimatedValue());
            }
        });

        animator.addListener(this);
        if (listener != null) {
            animator.addListener(listener);
        }

        animator.start();
    }

    protected void setInitialState() {

        for (Item item : items) {

            View view = item.view;

            view.setPivotX(0);
            view.setPivotY(0);
            view.setScaleX(item.fromScaleX);
            view.setScaleY(item.fromScaleY);
            view.setX(item.fromRect.left);
            view.setY(item.fromRect.top);
        }
    }

    protected void animationStep(float t) {

        for (Item item : items) {

            View view = item.view;

            float scaleX = MathHelper.interpolate(item.fromScaleX, item.toScaleX, t);
            float scaleY = MathHelper.interpolate(item.fromScaleY, item.toScaleY, t);

            float posX = MathHelper.interpolate(item.fromRect.left, item.toRect.left, t);
            float posY = MathHelper.interpolate(item.fromRect.top, item.toRect.top, t);

            view.setX(posX);
            view.setY(posY);
            view.setScaleX(scaleX);
            view.setScaleY(scaleY);

            if (item.animateBackgroundColor) {
                view.setBackgroundColor(ColorHelper.interpolate(item.fromColor, item.toColor, t));
            }

            view.requestLayout();
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

        inProgress = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {

        inProgress = false;
    }

    public static class Item { // TODO: 27/09/16 builder

        final View view;

        final RectF fromRect;
        final RectF toRect;

        final float fromScaleX;
        final float fromScaleY;
        final float toScaleX;
        final float toScaleY;

        boolean animateBackgroundColor;
        int fromColor;
        int toColor;


        public Item(@NonNull View view) {
            this.view = view;

            fromRect = toRect = new RectF();

            fromScaleX = toScaleX = 1.0f;
            fromScaleY = toScaleY = 1.0f;
        }

        public Item(@NonNull View view, @NonNull View from, @NonNull View to) {
            this(view, getRect(from), getRect(to));
        }

        public Item(@NonNull View view, @NonNull View from, @NonNull RectF to) {
            this(view, getRect(from), to);
        }

        public Item(@NonNull View view, @NonNull RectF from, @NonNull RectF to) {

            this.view = view;

            fromRect = from;
            toRect = to;

            fromScaleX = fromRect.width() / toRect.width();
            fromScaleY = fromRect.height() / toRect.height();

            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams((int) to.width(), (int) to.height());
                view.setLayoutParams(params);
            } else {
                params.width = (int) to.width();
                params.height = (int) to.height();
            }

            toScaleX = 1.0f;
            toScaleY = 1.0f;
        }

        public Item setBackground(int from, int to) {

            this.animateBackgroundColor = true;
            this.fromColor = from;
            this.toColor = to;

            return this;
        }

        static RectF getRect(View view) {

            RectF r = new RectF();

            r.left = (float) view.getLeft();
            r.top = (float) view.getTop();
            r.right = (float) view.getRight();
            r.bottom = (float) view.getBottom();

            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof View) {
                float left = ((View) parent).getPaddingLeft();
                float top = ((View) parent).getPaddingTop();

                r.left += left;
                r.top += top;
                r.right += left;
                r.bottom += top;
            }

            return r;
        }
    }
}
