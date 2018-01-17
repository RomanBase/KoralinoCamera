package com.ankhrom.base.observable;

import android.databinding.BindingAdapter;
import android.os.Build;
import android.widget.SeekBar;

import com.ankhrom.base.interfaces.OnValueChangedListener;

/**
 * Created by R' on 1/8/2018.
 */

public class SeekBarObservable extends BaseObservableField<SeekBar, Integer> {

    private boolean animate = true;

    private OnValueChangedListener<Integer> onPostValueChangedListener;

    public SeekBarObservable() {
    }

    public SeekBarObservable(Integer value) {
        super(value);
    }

    public SeekBarObservable(Float value) {
        super((int) (value * 100.0f));
    }

    public void setOnPostValueChangedListener(OnValueChangedListener<Integer> onPostValueChangedListener) {
        this.onPostValueChangedListener = onPostValueChangedListener;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public float getPercentage() {

        return ((float) get()) / 100.0f;
    }

    public void setPercentage(float value) {

        set((int) (value * 100.0f));
    }

    @Override
    protected void onBindingCreated(SeekBar view) {

        view.setProgress(get());
        view.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                set(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (onPostValueChangedListener != null) {
                    onPostValueChangedListener.onValueChanged(seekBar.getProgress());
                }
            }
        });
    }

    @Override
    protected void onValueChanged(Integer value) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.get().setProgress(value, animate);
        } else {
            view.get().setProgress(value);
        }
    }

    @BindingAdapter({"app:progress"})
    public static void bindEditText(SeekBar view, final SeekBarObservable observable) {

        if (observable == null) {
            return;
        }

        observable.bindToView(view);
    }
}
