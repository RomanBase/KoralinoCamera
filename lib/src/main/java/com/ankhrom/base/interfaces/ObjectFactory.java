package com.ankhrom.base.interfaces;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.ankhrom.base.interfaces.viewmodel.ViewModel;

public interface ObjectFactory {

    void add(Object object);

    void remove(Object object);

    @Nullable
    <T> T get(Class<T> clazz);

    RequestQueue getRequestQueue();

    Context getContext();

    @Nullable
    <T> T construct(Class<T> clazz, Object... args);

    @Nullable
    <T extends ViewModel> T getViewModel(Class<T> viewModelClass, Object... args);
}
