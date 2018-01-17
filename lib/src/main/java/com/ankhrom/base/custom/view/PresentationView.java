package com.ankhrom.base.custom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ankhrom.base.animators.ViewAnimation;
import com.ankhrom.base.interfaces.OnActivityStateChangedListener;
import com.ankhrom.base.model.PresentationItemModel;

public class PresentationView extends FrameLayout implements OnActivityStateChangedListener {

    private LayoutInflater inflater;
    private PresentationThread thread;

    private List<PresentationItemModel> items = new ArrayList<>();
    private int currentIndex = 0;
    private int nextIndex = 0;

    private View currentView;
    private View nextView;
    private boolean isAnimationInProgress;

    private float xOffset = 1.0f;
    private float yOffset = 0.0f;

    private Animation moveAnimation;

    public PresentationView(Context context) {
        super(context);
        init(context);
    }

    public PresentationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PresentationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PresentationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        isAnimationInProgress = false;
        inflater = LayoutInflater.from(context);
    }

    public void set(@NonNull PresentationItemModel... itemModels) {

        if (isAnimationInProgress) {
            if (moveAnimation != null && !moveAnimation.hasEnded()) {
                moveAnimation.cancel();
                moveAnimation = null;
            }
            isAnimationInProgress = false;
        }

        items.clear();
        if (itemModels.length <= currentIndex) {
            currentIndex = 0;
        }

        Collections.addAll(items, itemModels);

        onDatasetChanged();
    }

    public void clear() {

        items.clear();
    }

    protected void onDatasetChanged() {

        if (items.isEmpty()) {
            return;
        }

        boolean loaded = items.get(currentIndex).isLoaded();
        if (!loaded) {
            int count = items.size();
            for (int i = 0; i < count; i++) {
                if (items.get(i).isLoaded()) {
                    loaded = true;
                    currentIndex = i;
                    break;
                }
            }
        }

        if (!loaded) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    onDatasetChanged();
                }
            }, 500);
            return;
        }

        if (!items.get(currentIndex).isLoaded()) {
            currentIndex = getNextIndex(currentIndex);
        }
        currentView = inflate(currentIndex, true);

        if (items.size() > 1) {
            nextIndex = getNextIndex(currentIndex);
            if (currentIndex != nextIndex) {
                nextView = inflate(nextIndex, false);
            }
        }

        start();
    }

    public void start() {

        if (items.isEmpty()) {
            return;
        }

        items.get(currentIndex).start();
        startThread();
    }

    private void startThread() {

        /*if (items.size() < 2) {
            return;
        }*/

        if (thread != null) {
            thread.run = false;
            thread = null;
        }

        isAnimationInProgress = false;
        thread = new PresentationThread();
        thread.start();
    }

    public void stop() {

        if (thread == null) {
            return;
        }

        thread.run = false;
        thread.interrupt();
        thread = null;
    }

    public void hideItems() {

        if (currentView != null) {
            currentView.setVisibility(GONE);
        }

        if (nextView != null) {
            nextView.setVisibility(GONE);
        }
    }

    private int getNextIndex(int currentIndex) {

        int index = currentIndex + 1;
        if (index >= items.size()) {
            index = 0;
        }

        if (!items.get(index).isLoaded()) {
            return getNextIndex(index);
        }

        if (!items.get(index).isActive()) {
            if (isAnyItemActive()) {
                return getNextIndex(index);
            }
        }

        return index;
    }

    public boolean isAnyItemActive() {

        for (PresentationItemModel item : items) {
            if (item.isActive()) {
                return true;
            }
        }

        return false;
    }

    protected void update() {

        if (isAnimationInProgress) {
            return;
        }

        try {
            if (items.get(currentIndex).isDone()) {
                items.get(currentIndex).stop();

                postMoveToNextView();
            }
        } catch (Exception e) {
            e.printStackTrace();

            postMoveToNextView();
        }
    }

    protected void postMoveToNextView() {

        post(new Runnable() {
            @Override
            public void run() {
                moveToNextView();
            }
        });
    }

    protected void moveToNextView() {

        isAnimationInProgress = true;

        if (currentView == null || nextView == null) {
            onMoveEnd();
            isAnimationInProgress = false;
            return;
        }

        moveAnimation = new BasePresentationAnim(currentView, nextView, -currentView.getWidth(), nextView.getX());
        moveAnimation.setDuration(1000);

        moveAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onMoveEnd();
                isAnimationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        moveAnimation.start();
    }

    protected void onMoveEnd() {

        currentIndex = nextIndex;
        nextIndex = getNextIndex(currentIndex);

        if (currentIndex == nextIndex) {
            if (currentView != nextView && nextView != null) {
                removeView(currentView);
                currentView = nextView;
                nextView = null;
            }
            items.get(currentIndex).start();
            return;
        }

        removeView(currentView);
        currentView = null;
        currentView = nextView;

        items.get(currentIndex).start();

        nextView = inflate(nextIndex, false);
    }

    protected View inflate(int index, boolean isActive) {

        PresentationItemModel model = items.get(index);

        ViewDataBinding binding = DataBindingUtil.inflate(inflater, model.getLayoutResource(), this, true);

        int variable = model.getVariableBindingResource();
        if (variable > 0) {
            binding.setVariable(variable, model);
            binding.executePendingBindings();
        }

        final View view = binding.getRoot();

        if (!isActive) {
            view.setVisibility(View.INVISIBLE);
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setX(xOffset * view.getWidth());
                    view.setY(yOffset * view.getHeight());
                }
            });
        }

        model.onBindingCreated(binding);

        return view;
    }

    public void setOffsets(float x, float y) {
        this.xOffset = x;
        this.yOffset = y;
    }

    public void setCurrentIndex(int index) {
        this.currentIndex = index;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public void onActivityResume() {

        if (currentIndex < items.size()) {
            items.get(currentIndex).resume();
        }

        startThread();
    }

    @Override
    public void onActivityPause() {

        if (currentIndex < items.size()) {
            items.get(currentIndex).pause();
        }

        stop();
    }

    @Override
    public void onActivityDestroy() {

        stop();
    }

    public static class PresentationAnimationGone extends ViewAnimation {

        private boolean goneAfter;
        private float reqX;

        public PresentationAnimationGone(View v, float reqX, boolean goneAfter) {
            super(v);
            this.goneAfter = goneAfter;
            this.reqX = reqX;
        }

        @Override
        protected void onInit(View view) {

            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onTransformationStep(View view, float t) {

            view.setTranslationX(reqX * t);
        }

        @Override
        protected void onTransformationDone(View view) {

            if (goneAfter) {
                view.setVisibility(View.GONE);
            }
        }
    }

    public static class PresentationAnimationCome extends ViewAnimation {

        private boolean goneAfter;
        private float fromX;

        public PresentationAnimationCome(View v, float fromX, boolean goneAfter) {
            super(v);
            this.goneAfter = goneAfter;
            this.fromX = fromX;
        }

        @Override
        protected void onInit(View view) {

            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onTransformationStep(View view, float t) {

            view.setTranslationX(fromX * (1.0f - t));
        }

        @Override
        protected void onTransformationDone(View view) {

            view.setX(0);
            if (goneAfter) {
                view.setVisibility(View.GONE);
            }
        }
    }

    private class BasePresentationAnim extends PresentationViewAnimation {

        private final float outFinalX;
        private final float inCurrentX;

        public BasePresentationAnim(View viewOut, View viewIn, float outFinalX, float inCurrentX) {
            super(viewOut, viewIn);

            this.outFinalX = outFinalX;
            this.inCurrentX = inCurrentX;
        }

        @Override
        protected void onInit(View viewOut, View viewIn) {

            if (viewIn.getVisibility() != View.VISIBLE) {
                viewIn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onTransformationStep(View viewOut, View viewIn, float t) {

            viewOut.setTranslationX(outFinalX * t);
            viewIn.setTranslationX(inCurrentX * (1.0f - t));
        }

        @Override
        protected void onTransformationDone(View viewOut, View viewIn) {

            viewIn.setX(0);
            viewOut.setVisibility(View.GONE);
        }
    }

    public static abstract class PresentationViewAnimation extends Animation {

        private boolean changeBounds = true;
        private final View viewOut;
        private final View viewIn;
        private float delta;

        public PresentationViewAnimation(@NonNull View viewOut, @NonNull View viewIn) {
            this.viewOut = viewOut;
            this.viewIn = viewIn;
            onInit(viewOut, viewIn);
        }

        protected abstract void onInit(View viewOut, View viewIn);

        protected abstract void onTransformationStep(View viewOut, View viewIn, float delta);

        protected abstract void onTransformationDone(View viewOut, View viewIn);

        @Override
        public void start() {
            viewIn.startAnimation(this);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            delta = interpolatedTime;
            if (delta < 1.0f) {
                onTransformationStep(viewOut, viewIn, delta);
            } else {
                onTransformationDone(viewOut, viewIn);
            }
            viewOut.requestLayout();
            viewIn.requestLayout();
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

        public View getViewOut() {
            return viewOut;
        }

        public View getViewIn() {
            return viewIn;
        }
    }

    class PresentationThread extends Thread {

        boolean run = true;

        @Override
        public void run() {

            try {
                sleep(250);
            } catch (InterruptedException e) {
                //no big deal
            }

            while (run) {

                update();

                try {
                    sleep(250);
                } catch (InterruptedException e) {
                    //no big deal
                }
            }
        }
    }
}
