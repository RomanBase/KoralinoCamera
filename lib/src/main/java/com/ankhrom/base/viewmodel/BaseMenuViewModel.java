package com.ankhrom.base.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ankhrom.base.interfaces.ObjectFactory;
import com.ankhrom.base.interfaces.viewmodel.MenuViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModelNavigation;

public abstract class BaseMenuViewModel<T extends ViewDataBinding> implements MenuViewModel {

    private ViewModelNavigation navigation;
    private final Context context;
    private final ViewGroup container;

    protected boolean autoCloseDrawer = true;

    public BaseMenuViewModel(Context context, @IdRes Integer container) {
        this(context, (ViewGroup) ((Activity) context).findViewById(container));
    }

    public BaseMenuViewModel(Context context, ViewGroup container) {
        this.context = context;
        this.container = container;
    }

    protected <V extends ViewDataBinding> V inflateSubmenu(@IdRes int container, @LayoutRes int layoutResource) {

        return inflateSubmenu((ViewGroup) ((Activity) context).findViewById(container), layoutResource);
    }

    protected <V extends ViewDataBinding> V inflateSubmenu(ViewGroup container, @LayoutRes int layoutResource) {

        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutResource, container, true);
    }

    protected abstract int getLayoutResource();

    @Override
    public void onInit() {

    }

    @Override
    public void onCreate() {

        T binding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutResource(), container, true);

        onBindingCreated(binding);
    }

    @Override
    public void setMenuState(@Nullable Object state) {

    }

    @Override
    public ViewGroup getContainer() {
        return container;
    }

    @Override
    public void setNavigation(@NonNull ViewModelNavigation navigation) {
        this.navigation = navigation;
    }

    public ObjectFactory getFactory() {
        return navigation.getObserver().getFactory();
    }

    protected void onBindingCreated(T binding) {

    }

    protected void navigateFromNewStack(ViewModel vm) {

        navigateTo(vm, true);
    }

    protected <S extends ViewModel> void navigateFromNewStack(Class<S> vmClass, Object... args) {

        navigateTo(getFactory().getViewModel(vmClass, args), true);
    }

    protected void navigateAtCurrentStack(ViewModel vm) {

        navigateTo(vm, false);
    }

    protected <S extends ViewModel> void navigateAtCurrentStack(Class<S> vmClass, Object... args) {

        navigateTo(getFactory().getViewModel(vmClass, args), false);
    }

    protected void navigateTo(ViewModel vm, boolean clearBackStack) {

        navigation.setViewModel(vm, clearBackStack);
    }

    public ViewModelNavigation getNavigation() {
        return navigation;
    }

    public Context getContext() {
        return context;
    }
}
