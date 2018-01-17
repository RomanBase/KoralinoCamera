package com.ankhrom.base.interfaces;

import android.content.Context;
import android.view.ViewGroup;

import com.ankhrom.base.model.PopupModel;

public interface PopupModelAdapter {

    void init(Context context, ViewGroup root);

    boolean isActive();

    void show(PopupModel popupModel);

    void hide(PopupModel popupModel);

    PopupModel getCurrentPopupModel();
}
