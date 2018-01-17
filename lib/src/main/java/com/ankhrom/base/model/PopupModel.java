package com.ankhrom.base.model;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.ankhrom.base.animators.BaseAnim;
import com.ankhrom.base.interfaces.PopupModelAdapter;
import com.ankhrom.base.viewmodel.BasePopupAdapter;

public abstract class PopupModel<T extends ViewDataBinding> extends ItemModel {

    private PopupModelAdapter adapter;
    private ViewGroup parent;
    private View popup;

    protected T binding;

    public void init(Context context, @NonNull ViewGroup root) {

        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutResource(), root, true);

        init(binding.getRoot(), root, binding);
    }

    public void init(@NonNull View popup, @NonNull ViewGroup root, ViewDataBinding binding) {
        this.popup = popup;
        this.parent = root;

        animateShow(popup);

        int variable = getVariableBindingResource();
        if (variable > 0 && binding != null) {
            binding.setVariable(variable, this);
        }

        initBinding(binding);
    }

    @SuppressWarnings("unchecked")
    protected void initBinding(ViewDataBinding binding) {

        this.binding = (T) binding;
        onBindingCreated(this.binding);
    }

    protected void onBindingCreated(T binding) {

    }

    public Animation animateShow(View popup){

        return BaseAnim.show(popup);
    }

    public Animation animateHide(View popup){

        return BaseAnim.fade(popup);
    }

    public T getBinding() {
        return binding;
    }

    public void hide() {

        if (adapter != null) {
            adapter.hide(this);
            return;
        }

        if (popup != null) {
            parent.removeView(popup);
        }
    }

    public ViewGroup getParent() {
        return parent;
    }

    public View getPopup() {
        return popup;
    }

    public void setAdapter(BasePopupAdapter adapter) {
        this.adapter = adapter;
    }

    public PopupModelAdapter getAdapter() {
        return adapter;
    }
}
