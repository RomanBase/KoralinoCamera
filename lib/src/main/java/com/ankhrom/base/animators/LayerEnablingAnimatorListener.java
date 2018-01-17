package com.ankhrom.base.animators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Prevent performance drop.
 * Due to the offscreen rendering phase, hardware layers are generally enabled only during the time frame of the animation.
 * Indeed, keeping hardware layers on when a View invalidates itself, requires the system to redraw its backing layer entirely prior compositing it on screen.
 */
public class LayerEnablingAnimatorListener extends AnimatorListenerAdapter {

    private final View view;

    private int layerType;

    public LayerEnablingAnimatorListener(@NonNull View view) {
        this.view = view;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);

        layerType = view.getLayerType();
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);

        view.setLayerType(layerType, null);
    }
}
