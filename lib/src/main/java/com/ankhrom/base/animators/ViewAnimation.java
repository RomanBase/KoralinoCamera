package com.ankhrom.base.animators;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public abstract class ViewAnimation extends Animation {

    private boolean changeBounds = true;
    private final View view;
    private float delta;

    public ViewAnimation(View v) {
        view = v;
        onInit(view);
        view.requestLayout();
    }

    protected abstract void onInit(View view);

    protected abstract void onTransformationStep(View view, float delta);

    protected abstract void onTransformationDone(View view);

    @Override
    public void start() {
        view.startAnimation(this);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        delta = interpolatedTime;
        if (delta < 1.0f) {
            onTransformationStep(view, delta);
        } else {
            onTransformationDone(view);
        }
        view.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return changeBounds;
    }

    public void setChangeBounds(boolean changeBounds) {
        this.changeBounds = changeBounds;
    }

    public float getProgress() {
        return delta;
    }

    public View getView() {
        return view;
    }
}
