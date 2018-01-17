package com.ankhrom.base.observable;

import android.databinding.BaseObservable;
import android.support.annotation.Nullable;
import android.view.View;

import com.ankhrom.base.common.statics.ObjectHelper;
import com.ankhrom.base.common.statics.StringHelper;
import com.ankhrom.base.interfaces.OnValueChangedListener;

import java.lang.ref.WeakReference;

public abstract class BaseObservableField<T extends View, S> extends BaseObservable {

    protected WeakReference<T> view;
    protected S value;

    private OnValueChangedListener<S> listener;

    public BaseObservableField() {
    }

    public BaseObservableField(S value) {
        set(value);
    }

    protected abstract void onBindingCreated(T view);

    protected abstract void onValueChanged(S value);

    public S get() {
        return value;
    }

    public void set(S value) {

        if (!ObjectHelper.equals(this.value, value) && !(StringHelper.isEmpty(value) && StringHelper.isEmpty(this.value))) {
            this.value = value;
            if (view != null) {
                onValueChanged(value);
                if (listener != null) {
                    listener.onValueChanged(value);
                }
            }
            notifyChange();
        }
    }

    public void setValue(S value) {

        if (!ObjectHelper.equals(this.value, value)) {
            this.value = value;
            if (view != null) {
                onValueChanged(value);
            }
            notifyChange();
        }
    }

    public void setOnValueChangedListener(@Nullable OnValueChangedListener<S> listener) {
        this.listener = listener;
    }

    protected void bindToView(T view) {

        if (view == null) {
            return;
        }

        if (ObjectHelper.equals(this.view, view)) {
            return;
        }

        this.view = new WeakReference<T>(view);
        onBindingCreated(view);
    }
}
