package com.ankhrom.base.viewmodel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.ankhrom.base.custom.listener.AnimationAdapterListener;
import com.ankhrom.base.interfaces.PopupModelAdapter;
import com.ankhrom.base.model.PopupModel;

import java.util.ArrayList;
import java.util.List;

public class BasePopupAdapter implements PopupModelAdapter {

    private Context context;
    private ViewGroup root;

    private final List<PopupModel> stack;
    private boolean stricktMode = true;

    public BasePopupAdapter() {

        stack = new ArrayList<>();
    }

    @Override
    public void init(Context context, ViewGroup root) {
        this.context = context;
        this.root = root;
    }

    @Override
    public void show(PopupModel popupModel) {
        popupModel.setAdapter(this);

        if (stricktMode) {
            if (isActive()) {
                int c = stack.size();
                if (c == 1) {
                    PopupModel cpm = stack.get(0);
                    if (isReplaceable(cpm, popupModel)) {
                        popupModel.init(cpm.getPopup(), cpm.getParent(), cpm.getBinding());
                    }

                    stack.clear();
                    stack.add(popupModel);
                    return;
                } else if (c > 0) {
                    for (PopupModel popup : stack) {
                        removeView(popupModel);
                    }
                    stack.clear();
                }
            }
        }

        stack.add(popupModel);
        popupModel.init(context, root);
    }

    @Override
    public void hide(PopupModel popupModel) {

        stack.remove(popupModel);
        removeView(popupModel);
    }

    private void removeView(PopupModel model) {

        if (model != null) {
            final ViewGroup parent = model.getParent();
            if (parent != null) {
                final View popupView = model.getPopup();
                model.animateHide(popupView)
                        .setAnimationListener(new AnimationAdapterListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                parent.removeView(popupView);
                            }
                        });
            }
        }
    }

    @Override
    public boolean isActive() {
        return !stack.isEmpty();
    }

    @Override
    public PopupModel getCurrentPopupModel() {

        return isActive() ? stack.get(stack.size() - 1) : null;
    }

    public boolean isReplaceable(PopupModel pm1, PopupModel pm2) {

        return pm1.getLayoutResource() == pm2.getLayoutResource() && pm1.getVariableBindingResource() == pm2.getVariableBindingResource();
    }

    public void setStricktMode(boolean stricktMode) {
        this.stricktMode = stricktMode;
    }
}
